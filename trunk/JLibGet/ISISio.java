/*---------------------------------------------------------------------------
 * *** CCLRC ISIS Facility GET Routines ***
 * *** Original routines by Kevin Knowles, modifications by Freddie Akeroyd
 *
 * DEC/CMS REPLACEMENT HISTORY, Element BYTE_REL_EXPN.FOR
 * 1    11-AUG-1989 09:28:08 KJK "New routine coming in at 2.4.5"
 * DEC/CMS REPLACEMENT HISTORY, Element BYTE_REL_EXPN.FOR
 *
 * Converted to Java by Chris M. Bouzek, February 2004
 *
 * $Log$
 * Revision 1.1  2004/04/30 00:02:09  bouzekc
 * Initial revision
 *
 */
package ISIS.JLibGet;

/**
 * Main I/O class to read ISIS RAW files.
 */
public class ISISio {
  //~ Instance fields **********************************************************

  //crpt_SPECIALS common block start
  private String fileName;
  private int ver1;
  private int[] iFormat = new int[10];
  private int[] iVer = new int[10];
  private int nDet;
  private int nMon;
  private int nEff;
  private int nSep;
  private int nTRG;
  private int NSP1;
  private int NTC1;
  private int uLen;
  private int nPer;
  private int[] dataHeader = new int[32];
  private IntConvert intConvert = new IntConvert(  );
  private FloatConvert fConvert = new FloatConvert(  );
  private GetSect getSect       = new GetSect(  );

  //crpt_SPECIALS common block end
  //peculiar to crpt_SPECIALS common block from OPEN_DATA_FILE start
  private int nDet1;

  //~ Methods ******************************************************************

  //crpt_WORK common block end

  /**
   * Gets data from a RAW file one or more spectra at a time. ERROR CODES <br>
   * 0 = all OK<br>
   * 1 = file runID not currently open<br>
   * 2 = file number is '00000' and so no access available<br>
   * 3 = asked for non-existent parameter<br>
   * 4 = TOO MANY SPECTRA ASKED FOR<br>
   * 5 = error in byte unpacking<br>
   * 6 = cannot understand data section<br>
   */
  public int getDat( String runID, int IFSN, int NOS, int[] iData ) {
    //note that the length parameter is the length of iData
    //note also that there was an EQUIVALENCE(iWork, work) in Fortran
    int iErr;
    int iBase;
    int iLong;
    int j;
    int i;
    int iStart;
    int status;
    int iCompress;
    int[] iBuffer = new int[33000];
    int errCode   = 0;

    if( runID.substring( 3, 7 ).equals( "00000" ) ) {
      errCode = 2;

      //note no return statement in original Fortran
    }

    //  check name is valid & open file according to runID
    /*if( ( !fileName.equals( runID ) ) || ( fileName.equals( " " ) ) ) {
      String msg = "Access requested to " + runID + " when " + fileName +
        " open.";
      FErrorAddDummy.fErrorAdd( "getDat", msg, " " );
      errCode = 1;

      return errCode;
    }*/

    //  read data into iDat ....remembering there are NTC1+1 time channels and
    //  NSP1+1 spectra and now also nPer periods
    if( ( IFSN < 0 ) || ( IFSN > ( ( ( NSP1 + 1 ) * nPer ) - 1 ) ) ) {
      errCode = 4;

      return errCode;
    }

    if( ( ( IFSN + NOS ) - 1 ) > ( ( ( NSP1 + 1 ) * nPer ) - 1 ) ) {
      errCode = 4;

      return errCode;
    }

    if( ver1 == 1 ) {
      iBase = iFormat[5];
    } else {
      iBase = iFormat[6];
    }

    if( ( iVer[6] <= 1 ) ) {
      // Original version of data section
      iBase   = iBase + 1 + ( IFSN * ( NTC1 + 1 ) );
      iLong   = NOS * ( NTC1 + 1 );
      iErr    = getSect.getSect( iBase, iLong, iData, 49 );
      intConvert.VAXToLocalInts( iData );
    } else {
      // New version of data section (may be compressed and not necessarily consecutive
      // First pick up data section header, in particular compression type.
      iCompress = dataHeader[0];

      // The CRPT has the "compress" flag set, even though it isn't really compressed
      if( ( iCompress == 0 ) ) {
        // uncompressed
        iBase   = iBase + dataHeader[2] + ( IFSN * ( NTC1 + 1 ) );
        iLong   = NOS * ( NTC1 + 1 );
        iErr    = getSect.getSect( iBase, iLong * 4, iData, 49 );
        intConvert.VAXToLocalInts( iData );
      } else if( iCompress == 1 ) {
        // byte relative compression
        j = 0;

        int[] someInt = new int[1];

        //put bytes from iBuffer into a byte array...this may not be done right
        byte[] bArr = new byte[iBuffer.length * 4];

        for( i = IFSN - 1; i < ( ( IFSN + NOS ) - 1 ); i++ ) {
          iStart   = iBase + dataHeader[2] + ( 2 * i );
          iLong    = 2;
          iErr     = getSect.getSect( iStart, iLong, iBuffer, 49 );
          intConvert.VAXToLocalInts( iBuffer );
          iStart       = iBase + iBuffer[1];
          iLong        = iBuffer[0];
          iErr         = getSect.getSect( iStart, iLong, iBuffer, 49 );

          //We do not need to  intConvert.VAXToLocalInts() as handled in byte_rel_expn()
          someInt[0]   = iData[j];
          arrayCopyIntToByte( iBuffer, bArr );
          status     = byteRelExpn( bArr, iLong, 1, someInt, NTC1 + 1 );
          iData[j]   = someInt[0];

          //odd number error codes are OK
          if( ( status % 2 ) == 0 ) {
            errCode = 5;

            return errCode;
          }
          j = j + NTC1 + 1;
        }
      } else {
        errCode = 6;

        return errCode;
      }
    }

    //note no return in original Fortran code
    return errCode;
  }

  /**
   * Copies the values in fArray to iArray.
   *
   * @param fArray The float array to copy
   * @param iArray The int array to copy to
   */
  public static void arrayCopyFloatToInt( float[] fArray, int[] iArray ) {
    //copy it over.  getParr wants an int[]
    for( int m = 0; m < fArray.length; m++ ) {
      iArray[m] = ( int )fArray[m];
    }
  }

  /**
   * Copies the values in iArray to fArray.
   *
   * @param iArray The int array to copy.
   * @param fArray The float array to copy to.
   */
  public static void arrayCopyIntToFloat( int[] iArray, float[] fArray ) {
    for( int m = 0; m < iArray.length; m++ ) {
      fArray[m] = ( float )iArray[m];
    }
  }

  /**
   * Takes care of the EQUIVALENCE originally present in some subroutines.
   *
   * @param iTemp The "equivalent" integer array
   *
   * @return The String concatenation of the elements of iTemp
   */
  public static String copyIntsToString( int[] iTemp ) {
    StringBuffer cTemp = new StringBuffer(  );

    for( int i = 0; i < iTemp.length; i++ ) {
      cTemp.append( iTemp[i] );
    }

    return cTemp.toString(  );
  }

  /**
   * Gets character parameter(s) from RAW data file.\
   *
   * @param runID
   * @param name
   * @param cValue
   *
   * @return Error code:<br>
   *         <ul><li>0 = all OK</li> <li>1 = file runID not currently open</li>
   *         <li>2 = file number is '00000' and so no access available</li>
   *         <li>3 = asked for non-existent parameter</li> <li>4 = other
   *         error</li> </ul>
   */
  public int getParc( 
    String runID, String name, String[] cValue ) {
    //note that lengthIn is the length of cValue[]
    //note also an original Fortran EQUIVALENCE(iTemp, cTemp)

    String cTemp;
    int[] iTemp  = new int[33];
    int noteSect;
    int lengthOut = 0;
    int k;
    int i;
    int nLines   = 0;
    int iLines   = 0;
    int ver9     = 0;
    int offset;
    int iLlen;
    int lLen     = 0;
    int iErr     = 0;
    int errCode  = 0;

    //  decide whether it's CRPT or just a file
    if( runID.substring( 3, 7 ).equals( "00000" ) ) {
      errCode = 2;
      //note no return in original Fortran code
    }

    //  check name is valid & open file according to runID
    /*if( ( !fileName.equals( runID ) ) || ( fileName.equals( " " ) ) ) {
      String msg = "Access requested to " + runID + " when " + fileName +
        " open.";
      FErrorAddDummy.fErrorAdd( "getParc", msg, " " );
      errCode = 1;

      return errCode;
    }*/

    //  read variables into cValue
    //  Format section
    if( name.equals( "HDR" ) ) {
      iErr        = getSect.getSect( 1, 20, iTemp, 49 );

      //EQUIVALENCE
      cTemp       = copyIntsToString( iTemp );
      cValue[1]   = cTemp;
      //  Run section
    } else if( name.equals( "TITL" ) ) {
      iErr        = getSect.getSect( iFormat[0] + 2, 20, iTemp, 49 );

      //EQUIVALENCE
      cTemp       = copyIntsToString( iTemp );
      cValue[1]   = cTemp;
    } else if( name.equals( "USER" ) ) {
      iErr        = getSect.getSect( iFormat[0] + 22, 20, iTemp, 49 );

      //EQUIVALENCE
      cTemp       = copyIntsToString( iTemp );
      cValue[0]   = cTemp.substring( 0, 19 );
      cValue[1]   = cTemp.substring( 20, 39 );
      cValue[2]   = cTemp.substring( 40, 59 );
      cValue[3]   = cTemp.substring( 60, 79 );
      iErr        = getSect.getSect( iFormat[0] + 42, 20, iTemp, 49 );
      cValue[4]   = cTemp.substring( 0, 19 );
      cValue[5]   = cTemp.substring( 20, 39 );
      cValue[6]   = cTemp.substring( 40, 59 );
      cValue[7]   = cTemp.substring( 60, 79 );
      //  Instrument section
    } else if( name.equals( "NAME" ) ) {
      iErr        = getSect.getSect( iFormat[1] + 1, 2, iTemp, 49 );

      //EQUIVALENCE
      cTemp       = copyIntsToString( iTemp );
      cValue[1]   = cTemp.substring( 0, 7 );
      // LOG / Notes section
    } else if( name.equals( "NOTE" ) ) {
      if( ver1 == 1 ) {
        noteSect = 7;
      } else {
        noteSect = 8;
      }

      int[] ver9Arr = { ver9 };
      iErr   = getSect.getSect( iFormat[noteSect - 1], 1, ver9Arr, 49 );
      ver9   = ver9Arr[0];
      ver9   = intConvert.VAXToLocalInt( ver9 );

      if( ver9 == 0 ) {
        iLines      = ( iFormat[noteSect] - iFormat[noteSect - 1] ) / 20;
        nLines      = iLines;
        offset      = iFormat[noteSect - 1] + 2;

        //what is this?????
        iLlen       = 20;  //! 20*4 characters
        lLen        = 80;
        cTemp       = " ";
        lengthOut   = Math.min( nLines, cValue.length );
        k           = 0;

        for( i = 0; i < lengthOut; i++ ) {
          k           = offset + ( ( i - 1 ) * iLlen );
          iErr        = getSect.getSect( k, iLlen, iTemp, 49 );

          //EQUIVALENCE
          cTemp       = copyIntsToString( iTemp );
          cValue[i]   = cTemp.substring( 0, lLen - 1 );
        }
      } else if( ver9 == 2 ) {
        int[] lineArr = { nLines };
        iErr        = getSect.getSect( 
            iFormat[noteSect - 1] + 1, 1, lineArr, 49 );
        nLines      = lineArr[0];
        nLines      = intConvert.VAXToLocalInt( nLines );
        offset      = iFormat[noteSect - 1] + 2;

        // Each line stored as a line length + data
        lengthOut   = Math.min( nLines, cValue.length );

        for( i = 0; i < lengthOut; i++ ) {
          lineArr[0]   = lLen;
          iErr         = getSect.getSect( offset, 1, lineArr, 49 );
          lLen         = lineArr[0];
          lLen         = intConvert.VAXToLocalInt( lLen );
          iLlen        = ( ( lLen - 1 ) / 4 ) + 1;
          iErr         = getSect.getSect( offset + 1, iLlen, iTemp, 49 );

          //EQUIVALENCE
          cTemp        = copyIntsToString( iTemp );
          cValue[i]    = cTemp.substring( 0, lLen - 1 );
          offset       = offset + iLlen + 1;
        }
      } else {
        int[] lineArr = { nLines };
        iErr        = getSect.getSect( 
            iFormat[noteSect - 1] + 1, 1, lineArr, 49 );
        nLines      = lineArr[0];
        nLines      = intConvert.VAXToLocalInt( nLines );
        iLlen       = 20;  //  ! 20*4 characters per line
        lLen        = 80;
        offset      = iFormat[noteSect - 1] + 2;
        cTemp       = " ";
        lengthOut   = Math.min( nLines, cValue.length );
        k           = 0;

        for( i = 0; i < lengthOut; i++ ) {
          k           = offset + ( ( i - 1 ) * iLlen );
          iErr        = getSect.getSect( k, iLlen, iTemp, 49 );

          //EQUIVALENCE
          cTemp       = copyIntsToString( iTemp );
          cValue[i]   = cTemp.substring( 0, lLen - 1 );
        }
      }

      if( nLines <= 0 ) {
        cValue[0] = " No notes were made";
      }

      if( lengthOut < nLines ) {
        String msg = "Not enough space to return all of NOTES section";
        FErrorAddDummy.fErrorAdd( "getParc", msg, " " );
      }

      //  non existent requests
    } else {
      errCode = 3;

      String msg = "No such char parameter as " + name;
      FErrorAddDummy.fErrorAdd( "getParc", msg, " " );

      return errCode;
    }

    return errCode;
  }

  //GET routines modified to cope with periods Feb89 !!

  /**
   * ERROR CODES <br>
   * 0 = all OK<br>
   * 1 = file runID not currently open<br>
   * 2 = file number is '00000' and so no access available<br>
   * 3 = asked for non-existent parameter<br>
   * 4 = other error<br>
   * Gets named integer paramter(s) from a RAW data file Whole sections may
   * also be requested
   */
  public int getPari( String runID, String name, int[] iValue ) {
    //note that lengthIn is the length of iValue[]
    //note also that there were two equivalence statements:
    //EQUIVALENCE(iWork, work)
    //EQUIVALENCE(iJunk, bJunk)
    //there was also a INTRINSIC CHAR statement...I am not sure what to
    //do with that
    int iFrom;
    int i        = 0;
    int j        = 0;
    int k        = 0;
    int seNum    = 0;
    int noteSect = 0;
    RefInt NTC      = new RefInt(  );
    RefInt NDETY    =  new RefInt();
    RefInt nUse     = new RefInt();

    //int iJunk    = 0;
    int offset = 0;
    int lLen   = 0;
    int iLlen  = 0;

    //byte[] bJunk = new byte[4];
    int errCode = 0;
    int iErr    = 0;

    //  decide whether it's CRPT or just a file
    if( runID.substring( 3, 7 ).equals( "00000" ) ) {
      FErrorAddDummy.fErrorAdd( "getPari", "runID is 00000", " " );
      errCode = 2;

      return errCode;
    }

    //  check name is valid & open file according to runID\
    errCode = openDataFile( runID, NTC, NDETY, nUse );

    if( errCode == 1 ) {
      return errCode;
    }

    //  read variables into iValue
    //  From now on just decide what has been requested and return it
    if( name.equals( "VER1" ) ) {
      iValue[0] = ver1;
      //length      = 1;
    } else if( name.equals( "SFMT" ) ) {
      iErr = getSect.getSect( 1, 31, iValue, 49 );

      //length   = 31;
      intConvert.VAXToLocalInts( iValue );

      //  run section
    } else if( name.equals( "SRUN" ) ) {
      iErr = getSect.getSect( iFormat[0], 94, iValue, 49 );

      //length   = 94;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "VER2" ) ) {
      iErr = getSect.getSect( iFormat[0], 1, iValue, 49 );

      //length   = 1;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "RUN" ) ) {
      iErr = getSect.getSect( iFormat[0] + 1, 1, iValue, 49 );

      //length   = 1;
      intConvert.VAXToLocalInts( iValue );
    } else if( ( name.equals( "RPB" ) ) || ( name.equals( "IRPB" ) ) ) {
      iErr = getSect.getSect( iFormat[0] + 62, 32, iValue, 49 );

      //length   = 32;
      intConvert.VAXToLocalInts( iValue );

      //  instrument section
    } else if( name.equals( "SINS" ) ) {
      if( iVer[1] == 1 ) {
        iErr = getSect.getSect( 
            iFormat[1], 70 + ( nMon * 2 ) + ( ( 6 + nEff ) * nDet ), iValue, 49 );

        //length   = 70 + ( nMon * 2 ) + ( ( 6 + nEff ) * nDet );
        intConvert.VAXToLocalInts( iValue );
      } else {
        iErr = getSect.getSect( 
            iFormat[1], 70 + ( nMon * 2 ) + ( ( 5 + nEff ) * nDet ), iValue, 49 );

        //length   = 70 + ( nMon * 2 ) + ( ( 5 + nEff ) * nDet );
        intConvert.VAXToLocalInts( iValue );
      }
    } else if( name.equals( "VER3" ) ) {
      iValue[0] = iVer[1];
      //length      = 1;
    } else if( name.equals( "IVPB" ) ) {
      iErr = getSect.getSect( iFormat[1] + 3, 64, iValue, 49 );

      //length   = 64;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "NDET" ) ) {
      iValue[0] = nDet;
      //length      = 1;
    } else if( name.equals( "NMON" ) ) {
      iValue[0] = nMon;
      //length      = 1;
    } else if( name.equals( "NEFF" ) ) {
      iValue[0] = nEff;
      //length      = 1;
    } else if( name.equals( "NUSE" ) ) {
      iValue[0] = nEff;
      //length      = 1;
    } else if( name.equals( "MDET" ) ) {
      iErr = getSect.getSect( iFormat[1] + 70, nMon, iValue, 49 );

      //length   = nMon;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "MONP" ) ) {
      iErr = getSect.getSect( iFormat[1] + 70 + nMon, nMon, iValue, 49 );

      //length   = nMon;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "SPEC" ) ) {
      iFrom   = iFormat[1] + 70 + ( 2 * nMon );
      iErr    = getSect.getSect( iFrom, nDet, iValue, 49 );

      //length   = nDet;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "CODE" ) ) {
      if( iVer[1] != 1 ) {
        iFrom   = iFormat[1] + 70 + ( 2 * nMon ) + ( 3 * nDet );
        iErr    = getSect.getSect( iFrom, nDet, iValue, 49 );

        //length   = nDet;
        intConvert.VAXToLocalInts( iValue );
      } else {
        iErr = 1;
      }
    } else if( name.equals( "TIMR" ) ) {
      if( ver1 == 1 ) {
        iFrom = iFormat[1] + 70 + ( 2 * nMon ) + nDet;
      } else {
        iFrom = iFormat[3] + 65 + ( 3 * nDet );
      }
      iErr = getSect.getSect( iFrom, nDet, iValue, 49 );

      //length   = nDet;
      intConvert.VAXToLocalInts( iValue );

      //  sample environment section
    } else if( name.equals( "SSEN" ) ) {
      if( iVer[2] == 1 ) {
        iErr = getSect.getSect( iFormat[2], 34 + ( nSep * 24 ), iValue, 49 );

        //length   = 34 + ( nSep * 24 );
        intConvert.VAXToLocalInts( iValue );
      } else {
        iErr = getSect.getSect( iFormat[2], 66 + ( nSep * 32 ), iValue, 49 );

        //length   = 66 + ( nSep * 24 );
        intConvert.VAXToLocalInts( iValue );
      }

      if( nSep != 0 ) {
        FErrorAddDummy.fErrorAdd( 
          "getPari", "getPar needs adjusting to take account of SE", " " );
        errCode = 4;
      }
    } else if( name.equals( "VER4" ) ) {
      iErr = getSect.getSect( iFormat[2], 1, iValue, 49 );

      //length   = 1;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "SPB " ) ) {
      if( iVer[2] == 1 ) {
        iErr = getSect.getSect( iFormat[2] + 1, 32, iValue, 49 );

        //length   = 32;
        intConvert.VAXToLocalInts( iValue );
      } else {
        iErr = getSect.getSect( iFormat[2] + 1, 64, iValue, 49 );

        //length   = 64;
        intConvert.VAXToLocalInts( iValue );
      }
    } else if( name.equals( "NSEP" ) ) {
      iValue[1] = nSep;
      //length      = 1;
    } else if( name.substring( 0, 1 ).equals( "SE" ) ) {
      //READ(name.substring(3,4), "(I2.2)") SENUM;
      if( ( iVer[2] == 1 ) || ( nSep < seNum ) ) {
        String msg = "Invalid SE block " + name;
        FErrorAddDummy.fErrorAdd( "getPari", msg, " " );
        errCode = 4;
      } else {
        k      = iFormat[2] + 34 + ( 32 * seNum );
        iErr   = getSect.getSect( k, 32, iValue, 49 );

        //length   = 32;
        intConvert.VAXToLocalInts( iValue );
      }

      //  DAE section
    } else if( name.equals( "SDAE" ) ) {
      if( iVer[3] == 1 ) {
        iErr = getSect.getSect( iFormat[3], 65 + ( 3 * nDet ), iValue, 49 );
        //length   = 65 + ( 3 * nDet );
      } else {
        iErr = getSect.getSect( iFormat[3], 65 + ( 5 * nDet ), iValue, 49 );
        //length   = 65 + ( 5 * nDet );
      }
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "VER5" ) ) {
      iErr = getSect.getSect( iFormat[3], 1, iValue, 49 );

      //length   = 1;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "DAEP" ) ) {
      iErr = getSect.getSect( iFormat[3] + 1, 64, iValue, 49 );

      //length   = 64;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "CRAT" ) ) {
      iErr = getSect.getSect( iFormat[3] + 65, nDet, iValue, 49 );

      //length   = nDet;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "MODN" ) ) {
      iErr = getSect.getSect( iFormat[3] + 65 + nDet, nDet, iValue, 49 );

      //length   = nDet;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "MPOS" ) ) {
      iErr = getSect.getSect( iFormat[3] + 65 + ( 2 * nDet ), nDet, iValue, 49 );

      //length   = nDet;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "UDET" ) ) {
      iErr = getSect.getSect( iFormat[3] + 65 + ( 4 * nDet ), nDet, iValue, 49 );

      //length   = nDet;
      intConvert.VAXToLocalInts( iValue );

      //  TCB section
    } else if( name.equals( "STCB" ) ) {
      if( nTRG != 1 ) {
        FErrorAddDummy.fErrorAdd( 
          "getPari", "getPar needs adjusting to take account of SE", " " );
        errCode = 4;
      } else {
        iErr = getSect.getSect( iFormat[4], 288 + NTC1 + 1, iValue, 49 );

        //length   = 288 + NTC1 + 1;
        intConvert.VAXToLocalInts( iValue );
      }
    } else if( name.equals( "VER6" ) ) {
      iErr = getSect.getSect( iFormat[4], 1, iValue, 49 );

      //length   = 1;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "NTRG" ) ) {
      if( nTRG != 1 ) {
        FErrorAddDummy.fErrorAdd( 
          "getPari", "Multiple time regimes....getPari needs changing", " " );
        errCode = 4;
      } else {
        iValue[0] = nTRG;
        //length      = 1;
      }
    } else if( name.equals( "NFPP" ) ) {
      iErr = getSect.getSect( iFormat[4] + 2, 1, iValue, 49 );

      //length   = 1;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "NPER" ) ) {
      iValue[0] = nPer;
      //length      = 1;
    } else if( name.equals( "PMAP" ) ) {
      iErr = getSect.getSect( iFormat[4] + 4, 256, iValue, 49 );

      //length   = 256;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "NSP1" ) ) {
      iValue[0] = NSP1;
      //length      = 1;
    } else if( name.equals( "NTC1" ) ) {
      iValue[0] = NTC1;
      //length      = 1;
    } else if( name.equals( "TCM1" ) ) {
      iErr = getSect.getSect( iFormat[4] + 262, 5, iValue, 49 );

      //length   = 5;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "PRE1" ) ) {
      iErr = getSect.getSect( iFormat[4] + 287, 1, iValue, 49 );

      //length   = 1;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "TCB1" ) ) {
      iErr = getSect.getSect( iFormat[4] + 288, NTC1 + 1, iValue, 49 );

      //length   = NTC1 + 1;
      intConvert.VAXToLocalInts( iValue );
    } else if( name.equals( "DHDR" ) ) {
      for( i = 0; i < 32; i++ ) {
        iValue[i] = dataHeader[i];
      }

      //length = 32;
    } else if( name.equals( "ULEN" ) ) {
      iValue[0] = uLen;
      //length      = 1;
      // User section
    } else if( name.equals( "VER7" ) ) {
      if( ver1 == 1 ) {
        FErrorAddDummy.fErrorAdd( 
          "getPari", "No USER section, so no VER7 parameter", " " );
      } else {
        iErr = getSect.getSect( iFormat[5], 1, iValue, 49 );

        //length   = 1;
        intConvert.VAXToLocalInts( iValue );
      }

      // data section section
    } else if( name.equals( "VER8" ) ) {
      if( ver1 == 1 ) {
        iErr = getSect.getSect( iFormat[5], 1, iValue, 49 );
      } else {
        iErr = getSect.getSect( iFormat[6], 1, iValue, 49 );
      }

      //length = 1;
      intConvert.VAXToLocalInts( iValue );
      // NOTES section
    } else if( name.equals( "VER9" ) ) {
      if( ver1 == 1 ) {
        // Don't think this version number exists
        //       iValue(1) = 1
        iErr = getSect.getSect( iFormat[6], 1, iValue, 49 );
      } else {
        iErr = getSect.getSect( iFormat[7], 1, iValue, 49 );
      }

      //length = 1;
      intConvert.VAXToLocalInts( iValue );
      // Max Line length in notes section (bytes)
    } else if( name.equals( "NTLL" ) ) {
      if( ver1 == 1 ) {
        noteSect = 7;
      } else {
        noteSect = 8;
      }

      if( iVer[7] < 2 ) {
        iValue[0] = 80;
      } else {
        int[] someInts = { j };

        // Get number of lines into J
        iErr     = getSect.getSect( iFormat[noteSect - 1] + 1, 1, someInts, 49 );
        j        = someInts[0];
        offset   = iFormat[noteSect - 1] + 2;

        // Each line stored as a line length + data
        if( j < 1 ) {
          iValue[0] = 80;
        } else {
          iValue[0] = 0;
        }

        for( i = 0; i < j; i++ ) {
          someInts[0]   = lLen;
          iErr          = getSect.getSect( offset, 1, someInts, 49 );
          lLen          = someInts[0];
          iLlen         = ( ( lLen - 1 ) / 4 ) + 1;
          iValue[0]     = Math.max( iValue[0], iLlen * 4 );
          offset        = offset + iLlen + 1;
        }
      }

      //length = 1;
    } else if( name.equals( "FORM" ) ) {
      iValue[0] = iFormat[9];
      //length      = 1;
      // Number of lines in notes section
    } else if( name.equals( "NTNL" ) ) {
      if( ver1 == 1 ) {
        noteSect = 7;
      } else {
        noteSect = 8;
      }

      if( iVer[7] == 0 ) {
        iValue[0] = ( iFormat[noteSect] - iFormat[noteSect - 1] ) / 20;
      } else {
        iErr = getSect.getSect( iFormat[noteSect - 1] + 1, 1, iValue, 49 );
        intConvert.VAXToLocalInts( iValue );
      }

      if( iValue[0] < 1 ) {
        iValue[0] = 1;
      }

      //length = 1;
      //  non existent requests
    } else {
      errCode = 3;
      //length    = 0;
    }

    if( ( iErr != 0 ) && ( errCode == 0 ) ) {
      String msg = "Error in reading data from file " + runID;
      FErrorAddDummy.fErrorAdd( "getPari", msg, " " );
      errCode = 4;
    }

    //lengthOut = length;
    return errCode;
  }

  /**
   * ERROR CODES  0 = all OK<br>
   * 1 = file runID not currently open<br>
   * 2 = file number is '00000' and so no access available<br>
   * 3 = asked for non-existent parameter<br>
   * 4 = other error<br>
   * Gets float parameter(s) from RAW data file
   */
  public int getParr( String runID, String name, float[] rValue ) {
    //note that lengthIn is the length of rValue[]
    //note also two original equivalence statements:
    //EQUIVALENCE(iWork, work)
    //EQUIVALENCE(temp,iTemp)
    int iErr     = 0;
    int[] iStore = new int[64];
    int iTable   = 0;
    int IPRE1    = 0;
    int i        = 0;
    float temp   = 0;
    float extra  = 0;
    int errCode  = 0;

    //lengthOut    = 0;
    //int length   = lengthIn;
    //  decide whether it's CRPT or just a f
    if( runID.substring( 4, 8 ).equals( "00000" ) ) {
      FErrorAddDummy.fErrorAdd( "getParr", "runID is 00000", " " );
      errCode = 2;

      return errCode;
    }

    //  check name is valid & open file according to runID
    /*if( ( !fileName.equals( "runID" ) ) || ( fileName.equals( " " ) ) ) {
      String msg = "Access requested to " + runID + " when " + fileName +
        " open";
      FErrorAddDummy.fErrorAdd( "getParr", msg, " " );
      errCode = 1;

      return errCode;
    }*/

    //  read variables into rValue
    //  Instrument section
    
    iTable = iFormat[1] + 70 + ( 2 * nMon );

    int[] iArr = new int[rValue.length];
    arrayCopyFloatToInt( rValue, iArr );

    if( name.equals( "LEN2" ) ) {
      iErr = getSect.getSect( iTable + ( 2 * nDet ), nDet, iArr, 49 );
      arrayCopyIntToFloat( iArr, rValue );

      //length   = nDet;
      iErr = fConvert.VAXToIEEEFloat( rValue );
    } else if( name.equals( "OMEG" ) ) {
      if( iVer[1] == 1 ) {
        iErr = getSect.getSect( iTable + ( 3 * nDet ), nDet, iArr, 49 );
        arrayCopyIntToFloat( iArr, rValue );

        //length   = nDet;
        iErr = fConvert.VAXToIEEEFloat( rValue );
      } else {
        String msg = "Cannot access " + name;
        FErrorAddDummy.fErrorAdd( "getParr", msg, " " );
        errCode = 4;
      }
    } else if( name.equals( "DELT" ) ) {
      if( iVer[1] != 1 ) {
        iErr = getSect.getSect( iTable + nDet, nDet, iArr, 49 );
        arrayCopyIntToFloat( iArr, rValue );

        //length   = nDet;
        iErr = fConvert.VAXToIEEEFloat( rValue );
      } else {
        String msg = "Cannot access " + name;
        FErrorAddDummy.fErrorAdd( "getParr", msg, " " );
        errCode = 4;
      }
    } else if( name.equals( "TTHE" ) ) {
      iErr = getSect.getSect( iTable + ( 4 * nDet ), nDet, iArr, 49 );
      arrayCopyIntToFloat( iArr, rValue );

      //length   = nDet;
      iErr = fConvert.VAXToIEEEFloat( rValue );
    } else if( name.equals( "PHI" ) ) {
      if( iVer[1] == 1 ) {
        iErr = getSect.getSect( iTable + ( 5 * nDet ), nDet, iArr, 49 );
        arrayCopyIntToFloat( iArr, rValue );

        //length   = nDet;
        iErr = fConvert.VAXToIEEEFloat( rValue );
      } else {
        //String msg = "getParr: No item " + name + " in RAW file";
        //We do not flag an error here as then 'GET:SPECTRUM' would return an error vie READ_DATA
        //comment out message until i find a RAW file that does have it set!
        //FErrorAddDummy.fErrorAdd('INFORMATION', MESS, ' ')
        errCode = 4;
      }
    } else if( name.equals( "EF01" ) ) {
      iErr = getSect.getSect( iTable + ( 6 * nDet ), nDet, iArr, 49 );
      arrayCopyIntToFloat( iArr, rValue );

      //length   = nDet;
      iErr = fConvert.VAXToIEEEFloat( rValue );
    } else if( name.equals( "EF02" ) ) {
      iErr = getSect.getSect( iTable + ( 7 * nDet ), nDet, iArr, 49 );
      arrayCopyIntToFloat( iArr, rValue );

      //length   = nDet;
      iErr = fConvert.VAXToIEEEFloat( rValue );
    } else if( name.equals( "EF03" ) ) {
      iErr = getSect.getSect( iTable + ( 8 * nDet ), nDet, iArr, 49 );
      arrayCopyIntToFloat( iArr, rValue );

      //length   = nDet;
      iErr = fConvert.VAXToIEEEFloat( rValue );
    } else if( name.equals( "EF04" ) ) {
      iErr = getSect.getSect( iTable + ( 9 * nDet ), nDet, iArr, 49 );
      arrayCopyIntToFloat( iArr, rValue );

      //length   = nDet;
      iErr = fConvert.VAXToIEEEFloat( rValue );
    } else if( name.equals( "EF05" ) ) {
      iErr = getSect.getSect( iTable + ( 10 * nDet ), nDet, iArr, 49 );
      arrayCopyIntToFloat( iArr, rValue );

      //length   = nDet;
      iErr = fConvert.VAXToIEEEFloat( rValue );
    } else if( name.equals( "EF06" ) ) {
      iErr = getSect.getSect( iTable + ( 11 * nDet ), nDet, iArr, 49 );
      arrayCopyIntToFloat( iArr, rValue );

      //length   = nDet;
      iErr = fConvert.VAXToIEEEFloat( rValue );
    } else if( name.equals( "RVPB" ) ) {
      iErr = getSect.getSect( iFormat[1] + 3, 64, iArr, 49 );
      arrayCopyIntToFloat( iArr, rValue );

      //length   = 64;
      iErr = fConvert.VAXToIEEEFloat( rValue );
    } else if( name.substring( 1, 2 ).equals( "UT" ) ) {
      //READ(name(3:4),'(I2)') I
      iErr = getSect.getSect( iTable + ( ( 4 + i ) * nDet ), nDet, iArr, 49 );
      arrayCopyIntToFloat( iArr, rValue );

      //length   = nDet;
      iErr = fConvert.VAXToIEEEFloat( rValue );

      //  Time channel boundaries section
      //     time channel area definition
    } else if( name.equals( "TCP1" ) ) {
      iErr = getSect.getSect( iFormat[4] + 267, 20, iArr, 49 );
      arrayCopyIntToFloat( iArr, rValue );

      //length   = 20;
      iErr = fConvert.VAXToIEEEFloat( rValue );
    } else if( name.equals( "TCB1" ) ) {
      if( ver1 != 1 ) {
        iErr = getSect.getSect( iFormat[3] + 1, 64, iStore, 49 );
        intConvert.VAXToLocalInts( iStore );
        extra = ( float )iStore[23] * 4;
      } else {
        extra = 0.0f;
      }

      //  if tcb's requested in real form then return as microsecs
      iErr    = getSect.getSect( iFormat[4] + 287, 1, iStore, 49 );
      IPRE1   = intConvert.VAXToLocalInt( iStore[0] );
      iErr    = getSect.getSect( iFormat[4] + 288, NTC1 + 1, iArr, 49 );
      arrayCopyIntToFloat( iArr, rValue );

      int[] iArray = new int[rValue.length];

      //VAXToLocalInts wants an int array
      arrayCopyFloatToInt( rValue, iArray );
      intConvert.VAXToLocalInts( iArray );
      arrayCopyIntToFloat( iArray, rValue );

      //  conversion loop - from clock pulses to microsecs
      for( i = 0; i < ( NTC1 + 1 ); i++ ) {
        temp        = ( ( float )( rValue[i] ) / 32.0f * ( float )( IPRE1 ) ) +
          extra;
        rValue[i]   = ( int )temp;
      }

      //length = NTC1 + 1;
    } else if( name.equals( "DAT1" ) ) {
      if( ver1 != 1 ) {
        iErr = getSect.getSect( iFormat[5] + 2, uLen, iArr, 49 );
        arrayCopyIntToFloat( iArr, rValue );

        //length   = uLen;
        iErr = fConvert.VAXToIEEEFloat( rValue );
      } else {
        FErrorAddDummy.fErrorAdd( 
          "getParr", "No user section in this file", " " );
      }

      //  non existent requests
    } else if( name.equals( "RRPB" ) ) {
      iErr = getSect.getSect( iFormat[0] + 62, 32, iArr, 49 );
      
      arrayCopyIntToFloat( iArr, rValue );

      //length   = 32;
      iErr = fConvert.VAXToIEEEFloat( rValue );
    } else {
      errCode = 3;

      //length    = 0;
      return errCode;
    }

    if( ( iErr != 0 ) && ( errCode == 0 ) ) {
      String msg = "Error in reading data from file " + runID;
      FErrorAddDummy.fErrorAdd( "getParr", msg, " " );
      errCode = 4;
    }

    //lengthOut = length;
    return errCode;
  }

  /**
   * This method returns the whole of a run file into the given array.<br>
   * IERROR =<br>
   * 
   * <ul>
   * <li>
   * 0 : ALL OK
   * </li>
   * <li>
   * 1 : FILE OPEN ERROR
   * </li>
   * <li>
   * if error in getDat, returns 10+(error code in getDat)
   * </li>
   * </ul>
   */
  public int getRun( String runID, int[] iArray ) {
    //note that length is the length of iArray[]
    boolean found;
    int start_of_data;
    int iErr      = 0;
    int errCode   = 0;
    int iError    = 0;
    int[] iArray2;

    //  check name is valid & open file according to runID
    if( ( !fileName.equals( runID ) ) || ( fileName.equals( " " ) ) ) {
      found = openFile( runID );

      if( !found ) {
        iError = 1;

        return iError;
      }
    }

    //  want whole file except for log section
    // get this in 2 parts: first the file up to the data section+data version number
    if( ver1 == 1 ) {
      start_of_data = iFormat[5];
    } else {
      start_of_data = iFormat[6];
    }
    iErr = getSect.getSect( 1, start_of_data, iArray, 49 );
    intConvert.VAXToLocalInts( iArray );

    // and now the data...
    //the original code had iArray[start_of_data+1]...I took this to mean that
    //it was the iArray from start_of_data onward...need to check the index for
    //correctness
    iArray2 = new int[iArray.length - start_of_data];

    //this is in Java indexed format
    for( int m = 0; m < iArray2.length; m++ ) {
      iArray2[m] = iArray[start_of_data + m];
    }
    errCode = getDat( runID, 0, ( NSP1 + 1 ) * nPer, iArray2 );

    if( errCode != 0 ) {
      iError = 10 + errCode;

      return iError;
    }

    //now copy it back
    for( int m = 0; m < iArray2.length; m++ ) {
      iArray[start_of_data + m] = iArray2[m];
    }

    // for compressed file, log pointer points to wrong place.
    // Recalculate for data version 2.
    if( iArray[start_of_data] == 2 ) {
      iArray[28] = iFormat[6] + 1 + ( ( NSP1 + 1 ) * ( NTC1 + 1 ) * nPer );
    }

    // we now have uncompressed data in a Version 1 format. Set data version to 1
    iArray[start_of_data] = 1;

    return iError;
  }

  /**
   * Copies the values in iArray to bArray like so:<br>
   * int[n] = xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx byte[n] = xxxx xxxx
   * byte[n + 1] = xxxx xxxx<br>
   * etc.<br>
   * <br>
   * This assumes that the byte array is four times as large as the int array.
   *
   * @param iArray The starting integer array.
   * @param bArray The byte array to store the bytes from iArray in.
   */
  public static void arrayCopyIntToByte( int[] iArray, byte[] bArray ) {
    if( ( bArray.length * 4 ) != iArray.length ) {
      throw new IllegalArgumentException( 
        "Byte array is not four times as large as " + "int array" );
    }

    int j = 0;

    for( int i = 0; i < iArray.length; i++ ) {
      bArray[j]       = ( byte )( iArray[i] >> 3 );
      bArray[j + 1]   = ( byte )( iArray[i] >> 2 );
      bArray[j + 2]   = ( byte )( iArray[i] >> 1 );
      bArray[j + 3]   = ( byte )( iArray[i] );
      j               = j + 4;
    }
  }

  /**
   * Expansion of byte-relative format into 32bit format Each integer is stored
   * relative to the previous value in byte form.The first is relative to
   * zero. This allows for numbers to be within + or - 127 of the previous
   * value. Where a 32bit integer cannot be expressed in this way a special
   * byte code is used (-128) and the full 32bit integer stored elsewhere. The
   * final space used is (NIN-1)/4 +1 + NEXTRA longwords, where NEXTRA is the
   * number of extra longwords used in giving absolute values. Status return<br>
   * =1  no problems!<br>
   * =3  NOUT .lt.NIN/5<br>
   * =2  NIN .le.0 =4  NOUT .gt.NIN =6  number of channels lt NOUT
   */
  public int byteRelExpn( 
    byte[] inData, int nIn, int nFrom, int[] outData, int nOut ) {
    //note the original equivalence statement: EQUIVALENCE(iTemp, bTemp)
    int i;
    int j;
    int iTemp;
    byte[] bTemp = new byte[4];

    // Assume innocent until proven guilty
    int iStatus = 1;

    // First check no slip-ups in the input parameters
    if( nIn <= 0 ) {
      iStatus = 2;

      return statusCheck( nOut, nIn, iStatus );
    }

    if( ( ( nOut + nFrom ) - 1 ) > nIn ) {
      iStatus = 4;

      return statusCheck( nOut, nIn, iStatus );
    }

    // Set initial absolute value to zero and channel counter to zero
    iTemp   = 0;
    j       = 0;

    // Loop over all expected 32bit integers
    for( i = 0; i < ( ( nFrom + nOut ) - 1 ); i++ ) {
      j++;

      if( j > nIn ) {
        // check there are enough bytes
        iStatus = 6;

        return statusCheck( nOut, nIn, iStatus );
      }

      // if number is contained in a byte
      if( inData[j] != -128 ) {
        // add in offset to base
        iTemp = iTemp + inData[j];
      } else {
        // Else skip marker and pick up new absolute value
        if( ( j + 4 ) > nIn ) {
          // check there are enough bytes
          iStatus = 6;

          return statusCheck( nOut, nIn, iStatus );
        }

        // unpack 4 bytes
        bTemp[0]   = inData[j];
        bTemp[1]   = inData[j + 1];
        bTemp[2]   = inData[j + 2];
        bTemp[4]   = inData[j + 3];
        iTemp      = intConvert.VAXToLocalInt( iTemp );
        j          = j + 4;
      }

      // update current value
      if( i >= nFrom ) {
        outData[i - nFrom] = iTemp;
      }
    }

    return statusCheck( nOut, nIn, iStatus );
  }

  /**
   * Opens a data file.  Note that this uses single element integer arrays to
   * simulate reference parameters.  This should eventually be changed. 
   * The following class variables are changed:<br>
   * 
   * 
   * <ul>
   * <li>
   * NTC - number of time channels (minimum value for nTCMax)
   * </li>
   * <li>
   * nDet - the number of detectors (minimum value for nDetMax)
   * </li>
   * <li>
   * nUse - number of user-defined UTn tables
   * </li>
   * </ul>
   * 
   *
   * @param runID Name of data file to open
   *
   * @return error code corresponding to:<br>
   *         <ul><li>0 = all OK, a l'UNIX</li> <li>1 = file not found or error
   *         in opening it</li> </ul>
   */
  public int openDataFile( String runID, RefInt NTC, RefInt nDet, RefInt nUse ) {
    boolean found = false;
    int errCode   = 0;

    if( !fileName.equals( runID ) ) {
      fileName   = " ";
      found      = openFile( runID );

      if( !found ) {
        //this had RUNID(:TRUELEN(RUNID))
        String msg = "File " + runID + " not found.";
        FErrorAddDummy.fErrorAdd( "openDataFile", msg, " " );
        errCode = 1;

        return errCode;
      }
    }
    NTC.innerInt    = NTC1;
    nDet.innerInt   = nDet1;
    nUse.innerInt   = nEff;

    return errCode;
  }

  /**
   * Opens a RAW data file for reading. If a different file is already open it
   * will be closed first.  Note: I am not so sure about the latter statement-
   * CMB
   */
  public boolean openFile( String runID ) {
    //note the original equivalence statement: EQUIVALENCE(cFileTemp, iFileTemp)
    boolean found;
    int[] iTemp      = new int[3];
    int i;
    String cFileTemp;
    int errCode      = 0;
    int iErr         = 0;

    //  Check that the file exists
    i                = trueLength( runID );

      //original: INQUIRE(FILE=RUNID(1:I),EXIST=FOUND)
      found = new java.io.File( runID ).exists(  );
   

    if( !found ) {
      return found;
    }

    //Open new file it
    cFileTemp   = runID;

    //Hack - we have fileName in common block, so temporarily assign it for getsect_orig.f to read
    fileName    = runID;
    errCode     = 0;

    errCode     = getSect.fastGetInit( cFileTemp, 49 );

    fileName    = " ";

    if( errCode != 0 ) {
      found = false;

      return found;
    }

    int[] someInt = new int[1];

    //  Now pick out the vital parameters for future use
    //  version number
    iErr   = getSect.getSect( 21, 1, iTemp, 49 );
    ver1   = intConvert.VAXToLocalInt( iTemp[0] );

    //  Format section
    iErr   = getSect.getSect( 22, 10, iFormat, 49 );
    intConvert.VAXToLocalInts( iFormat );

    //  RUN section
    someInt[0]   = iVer[0];
    iErr         = getSect.getSect( iFormat[0], 1, someInt, 49 );
    iVer[0]      = intConvert.VAXToLocalInt( someInt[0] );

    //  Instrument section
    someInt[0]   = iVer[1];
    iErr         = getSect.getSect( iFormat[1], 1, someInt, 49 );
    iVer[1]      = intConvert.VAXToLocalInt( someInt[0] );

    //  nDet,nMon,nEff
    iErr         = getSect.getSect( iFormat[1] + 67, 3, iTemp, 49 );
    intConvert.VAXToLocalInts( iTemp );
    nDet         = iTemp[0];
    nMon         = iTemp[1];
    nEff         = iTemp[2];

    //  SE section
    someInt[0]   = iVer[2];
    iErr         = getSect.getSect( iFormat[2], 1, someInt, 49 );
    iVer[2]      = intConvert.VAXToLocalInt( someInt[0] );

    //  nSep
    if( iVer[2] == 1 ) {
      iErr = getSect.getSect( iFormat[2] + 33, 1, iTemp, 49 );
    } else {
      iErr = getSect.getSect( iFormat[2] + 65, 1, iTemp, 49 );
    }
    nSep         = intConvert.VAXToLocalInt( iTemp[0] );

    //  DAE section
    someInt[0]   = iVer[3];
    iErr         = getSect.getSect( iFormat[3], 1, someInt, 49 );
    iVer[3]      = intConvert.VAXToLocalInt( someInt[0] );

    //  TCB section
    someInt[0]   = iVer[4];
    iErr         = getSect.getSect( iFormat[4], 1, someInt, 49 );
    iVer[4]      = intConvert.VAXToLocalInt( someInt[0] );

    //  nTRG
    iErr         = getSect.getSect( iFormat[4] + 1, 1, iTemp, 49 );
    nTRG         = intConvert.VAXToLocalInt( iTemp[0] );

    //I commented this out for testing.  It should be fixed.
    if( nTRG != 1 ) {
      //original: WRITE(6,*) 'NTRG Problem', NTRG
      /*System.out.println( "nTRG problem " + nTRG );
      found = false;

      return found;*/
    }

    //  nPer
    iErr   = getSect.getSect( iFormat[4] + 3, 1, iTemp, 49 );
    nPer   = intConvert.VAXToLocalInt( iTemp[0] );

    //  NSP1,NTC1
    iErr   = getSect.getSect( iFormat[4] + 260, 2, iTemp, 49 );
    intConvert.VAXToLocalInts( iTemp );
    NSP1   = iTemp[0];
    NTC1   = iTemp[1];

    //  USER section
    if( ver1 == 1 ) {
      uLen      = 0;
      iVer[5]   = 0;
    } else {
      someInt[0]   = iVer[5];
      iErr         = getSect.getSect( iFormat[5], 1, someInt, 49 );
      iVer[5]      = intConvert.VAXToLocalInt( someInt[0] );
      iErr         = getSect.getSect( iFormat[5] + 1, 1, iTemp, 49 );
      uLen         = intConvert.VAXToLocalInt( iTemp[0] );
    }

    //  DATA and NOTES section
    if( ver1 == 1 ) {
      someInt[0]   = iVer[6];
      iErr         = getSect.getSect( iFormat[5], 1, someInt, 49 );
      iVer[6]      = someInt[0];

      if( iFormat[6] != 0 ) {
        someInt[0]   = iVer[7];
        iErr         = getSect.getSect( iFormat[6], 1, someInt, 49 );
        iVer[7]      = intConvert.VAXToLocalInt( someInt[0] );
      } else {
        iVer[7] = 0;
      }
    } else {
      someInt[0]   = iVer[6];
      iErr         = getSect.getSect( iFormat[6], 1, someInt, 49 );
      iVer[6]      = someInt[0];

      if( iFormat[7] != 0 ) {
        someInt[0]   = iVer[7];
        iErr         = getSect.getSect( iFormat[7], 1, someInt, 49 );
        iVer[7]      = intConvert.VAXToLocalInt( someInt[0] );
      } else {
        iVer[7] = 0;
      }
    }
    iVer[6] = intConvert.VAXToLocalInt( iVer[6] );

    // DATA section header
    for( i = 0; i < dataHeader.length; i++ ) {
      dataHeader[i] = 0;
    }

    if( ( iVer[6] >= 2 ) ) {
      iErr = getSect.getSect( iFormat[6] + 1, 32, dataHeader, 49 );
      intConvert.VAXToLocalInts( dataHeader );
    }

    //  finally store the file name and set flags to true
    fileName   = runID;
    found      = true;

    return found;
  }

  /**
   * This should only be called after a call to openDataFile<br>
   * if (quick == 1)  only get spectrun data and not titles etc<br>
   * INPUT PARAMETERS (UNCHANGED BY THIS ROUTINE) <br>
   * runID<br>
   * iSpec - the identifier of the spectrum to retrieve<br>
   * nDetMax - the size of the work arrays deltaWork, specWork,<br>
   * L2Work and ttheWork<br>
   * nTCMax - maximum size of arrays iDat and TCB<br>
   * RETURNED VALUES <br>
   * L1 - Primary flight path<br>
   * L2 - Secondary flight path (averaged over detectors)<br>
   * tthe - two theta (averaged over detectors)<br>
   * delta -<br>
   * iDat - returned spectra file (of length NTC1+1)<br>
   * TCB - array of NTC1+1 time channel boundaries<br>
   * userTables - contains the UTn tables (nUse of them) averaged over detectors<br>
   * errCode - returned error code:   0 = All OK<br>
   * 1 = file not open<br>
   * 2 = nTCMax, nDetMax or nUse inconsistent<br>
   * 3 = iSpec out of range<br>
   * 4 = other read error<br>
   * 5 = nUse out of range<br>
   */
  public int readData( 
    String runID, int iSpec, float[] deltaWork, int[] specWork, float[] ttheWork,
    float[] L2Work, int nDetMax, float[] TCB, int[] iDat, int nTCMax, RefFloat L1,
    RefFloat L2, RefFloat tthe, RefFloat delta, float phi, RefString runTitle,
    RefFloat duration, RefString combinedTime, RefString userName, RefString instName,
    RefString runNo, float[] userTables, int nUse, int quick ) {
    //note the equivalence: EQUIVALENCE(IVPBWK,RVPBWK)
    //note also that nDetMax = length of deltaWork[], specWork[], ttheWork[],L2Work[]
    //also that nTCMax = length of TCB[]
    //also that nUse = length of userTables[]

    int iErr       = 0;
    int errCode    = 0;
    int i;
    int j;
    int nDetMatch  = 0;
    float rNDet    = 0.0f;
    float[] RVPBWK = new float[64];
    int[] RPB      = new int[32];
    String error1  = "";
    String error2  = "";
    String UTNum   = "";
    String header  = "";

    //String runIdent="";
    String exptTitle = "";
    String startDate = "";
    String startTime = "";

    // *** convErr controls if an error occurs duing fConvert.VAXToIEEEFloat
    int convErr = 0;

    // *** convErr controls if an error occurs duing fConvert.VAXToIEEEFloat
    if( ( runID != fileName ) || ( fileName.equals( " " ) ) ) {
      FErrorAddDummy.fErrorAdd( "readData", "Error in file specification", " " );
      errCode = 1;

      return errCode;
    }

    if( ( NTC1 + 1 ) > nTCMax ) {
      //original: WRITE(ERROR1, '(I8)') NTC1
      error1   = "" + NTC1;

      //original: WRITE(ERROR2, '(I8)') NTCMAX
      error2   = "" + nTCMax;

      String msg = "Too many time channels: NTC1 = " + error1 + ", nTCMax = " +
        error2;
      FErrorAddDummy.fErrorAdd( "readData", msg, " " );
      errCode = 2;

      return errCode;
    }

    if( ( iSpec > ( ( ( NSP1 + 1 ) * nPer ) - 1 ) ) || ( iSpec < 0 ) ) {
      //original: WRITE(MESS,175) ISPEC, ((NSP1+1)*NPER)-1
      //175 FORMAT('Invalid spectrum number = ', I5, '(spectra must
      //be in the range 0 - ', I5, ')')
      String msg = "Invalid spectrum number = " + iSpec +
        " (spectra must be in the range 0 - " + ( ( ( NSP1 + 1 ) * nPer ) - 1 ) +
        ")";
      FErrorAddDummy.fErrorAdd( "readData", msg, " " );
      errCode = 3;

      return errCode;
    }

    if( nDet > nDetMax ) {
      //original: WRITE(ERROR1, '(I8)') NDET
      error1   = "" + nDet;

      //original: WRITE(ERROR2, '(I8)') NTDETMAX
      error2   = "" + nDetMax;

      String msg = "Too many detectors: " + error1 + " > " + error2;
      FErrorAddDummy.fErrorAdd( "readData", msg, " " );
      errCode = 2;

      return errCode;
    }

    if( nUse != nEff ) {
      //original: WRITE(ERROR1, '(I8)') NUSE
      error1   = "" + nUse;

      //original: WRITE(ERROR1, '(I8)') NEFF
      error2   = "" + nEff;

      String msg = "Invalid number of user parameters: " + error1 + " != " +
        error2;
      FErrorAddDummy.fErrorAdd( "readData", msg, " " );
      errCode = 2;

      return errCode;
    }

    if( quick == 0 ) {
      String[] headerArr = new String[1];
      headerArr[0]     = header;
      iErr             = getParc( runID, "HDR", headerArr );
      header           = headerArr[0];

      if( iErr != 0 ) {
        return errorCode999GoTo(  );
      }

      // Run identifier e.g. LAD12345
      //runIdent       = header.substring( 1, 8 );
      runNo.innerString            = header.substring( 4, 8 );

      // User Name
      userName.innerString         = header.substring( 9, 28 );

      //Experiment short title
      exptTitle        = header.substring( 29, 52 );

      // Start date
      startDate        = header.substring( 53, 64 );

      // Start Time
      startTime        = header.substring( 65, 72 );
      combinedTime.innerString     = startDate.substring( 1, trueLength( startDate ) ) +
        " " + startTime.substring( 1, trueLength( startTime ) );
      runTitle.innerString         = exptTitle;

      String[] instNameArr = new String[1];
      instNameArr[0]   = instName.innerString;
      iErr             = getParc( runID, "name", instNameArr );
      instName.innerString         = instNameArr[0];

      if( iErr != 0 ) {
        return errorCode999GoTo(  );
      }
      iErr = getParr( runID, "TCB1", TCB );

      if( iErr != 0 ) {
        return errorCode999GoTo(  );
      }

      // do not need fConvert.VAXToIEEEFloat as stored as integers
    }

    // *** end of >>> if ( quick=0) <<<
    iErr = getParr( runID, "RVPB", RVPBWK );

    if( iErr != 0 ) {
      return errorCode999GoTo(  );
    }
    L1.innerFloat          = RVPBWK[22];

    // spectrum table
    iErr        = getPari( runID, "SPEC", specWork );

    if( iErr != 0 ) {
      return errorCode999GoTo(  );
    }

    // run duration (s)
    iErr = getPari( runID, "RPB", RPB );

    if( iErr != 0 ) {
      return errorCode999GoTo(  );
    }
    duration.innerFloat    = ( float )( RPB[12] );
    iErr        = getParr( runID, "DELT", deltaWork );

    if( iErr != 0 ) {
      return errorCode999GoTo(  );
    }
    iErr = getParr( runID, "LEN2", L2Work );

    if( iErr != 0 ) {
      return errorCode999GoTo(  );
    }
    iErr = getParr( runID, "TTHE", ttheWork );

    if( iErr != 0 ) {
      return errorCode999GoTo(  );
    }

    // average tthe, L2 and delta over detectors used for given spectrum
    tthe.innerFloat        = 0.0f;
    L2.innerFloat          = 0.0f;
    delta.innerFloat       = 0.0f;
    j           = 0;

    for( i = 0; i < nDet; i++ ) {
      if( specWork[i] == iSpec ) {
        tthe.innerFloat    = tthe.innerFloat + ttheWork[i];
        delta.innerFloat   = delta.innerFloat + deltaWork[i];
        L2.innerFloat      = L2.innerFloat + L2Work[i];
        j++;
      }
    }
    nDetMatch   = j;

    // *** phi ***
    iErr        = getParr( runID, "PHI", deltaWork );
    phi         = 0.0f;

    if( iErr == 0 ) {
      for( i = 0; i < nDet; i++ ) {
        if( specWork[i] == iSpec ) {
          phi = phi + deltaWork[i];
        }
      }
    }

    for( i = 0; i < nUse; i++ ) {
      userTables[i] = 0.0f;
    }

    for( i = 0; i < nUse; i++ ) {
      //original: WRITE(UTNUM, '(I2.2)') I
      UTNum   = "" + i;
      iErr    = getParr( runID, "UT" + UTNum, deltaWork );

      if( iErr != 0 ) {
        return errorCode999GoTo(  );
      }

      for( j = 0; j < nDet; j++ ) {
        if( specWork[j] == iSpec ) {
          userTables[i] = userTables[i] + deltaWork[j];
        }
      }
    }

    if( nDetMatch != 0 ) {
      rNDet   = ( float )( nDetMatch );
      tthe.innerFloat    = tthe.innerFloat / rNDet;
      delta.innerFloat   = delta.innerFloat / rNDet;
      L2.innerFloat      = L2.innerFloat / rNDet;
      phi     = phi / rNDet;

      for( i = 0; i < nUse; i++ ) {
        userTables[i] = userTables[i] / rNDet;
      }
    }
    iErr = getDat( runID, iSpec, 1, iDat );

    if( iErr != 0 ) {
      return errorCode999GoTo(  );
    }

    if( convErr != 0 ) {
      FErrorAddDummy.fErrorAdd( 
        "INFORMATION", "Error during conversion VAX format to IEEE",
        "May be unimportant - check data" );
    }

    return errCode;
  }

  /**
   * return string length when trailing blanks are discarded
   */
  public int trueLength( String string ) {
    return string.trim(  ).length(  );
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  private int errorCode999GoTo(  ) {
    int errCode = 4;
    FErrorAddDummy.fErrorAdd( "readData", "Some other error", " " );

    return errCode;
  }

  /**
   * Replacement for the GOTO statement in the original byteRelExpn function.
   *
   * @param nOut DOCUMENT ME!
   * @param nIn DOCUMENT ME!
   * @param iStatus DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  private int statusCheck( int nOut, int nIn, int iStatus ) {
    // expansion OK, but excessive number of bytes given to the routine
    if( nOut < ( nIn / 5 ) ) {
      iStatus = 3;
    }

    return iStatus;
  }
}