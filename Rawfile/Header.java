/*
 * File:  Header.java
 *
 * Copyright (C) 2004 J.P. Hammonds
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           J.P. Hammonds <jphammonds@anl.gov>
 *           Dominic Kramer <kramerd@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 * $Log$
 * Revision 1.9  2004/06/22 16:49:03  kramer
 * Made the constructors public.
 *
 * Revision 1.8  2004/06/21 19:11:16  kramer
 *
 * Added getter methods (with documentation).  Now the class imports 2 classes
 * instead of the entire java.io package.  Also, if the class thinks it may
 * read data from the file wrong, it warns the user.
 *
 * Revision 1.7  2004/06/16 20:40:50  kramer
 *
 * Now the source will contain the cvs logs.  Replaced tabs with 3 spaces,
 * created a default contstructor where fields will be initialized (instead
 * of when they are first declared), and when exceptions are caught a stack
 * trace is now printed to standard output.
 *
 */

package ISIS.Rawfile;

import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * This package processes header information from an ISIS Raw data file.
 *
 * @author J.P. Hammonds Intense Pulsed Neutron Source Argonne National
 *         Laboratory
 */
public class Header {
  //~ Instance fields ----------------------------------------------------------

/**
 * The run duration (in {mu}A.Hr).
 */
  protected String runDuration;
  /**
   * Run identifier.  Three characters for the instrument followed by 
   * five characters for the run number (eg LAD12345).
   */
  protected String runID;
  /** Experiment short title. */
  protected String runTitleShort;
  /** Start date. */
  protected String startDate;
  /** Start time. */
  protected String startTime;
  /** The user name. */
  protected String userName;
  /**
   * data format flag (0 or 1)<br>
   * 0 - all time channels for each<br>
   * 1 - the same time channel for every spectrum
   */
  protected int    dataFormatFlag;
  /** Format version number. */
  protected int    formatVersion;
  /** The start address of the DATA ACQUISITION ELECTRONICS section. */
  protected int    startAddressDae;
  /** The start address of the RAW DATA section. */
  protected int    startAddressData;
  /** The start address of the INSTRUMENT section. */
  protected int    startAddressInst;
  /** The start address of the LOG section. */
  protected int    startAddressLog;
  /** The start address of the RUN section. */
  protected int    startAddressRun;
  /** The start address of the SAMPLE ENVIRONMENT section. */
  protected int    startAddressSe;
  /**  The start address of the spare section. */
  protected int    startAddressSpare;
  /**  The start address of the TIME CHANNEL BOUNDARIES section. */
  protected int    startAddressTcb;
  /**  The start address of the USER DEFINED section. */
  protected int    startAddressUser;

  //~ Constructors -------------------------------------------------------------

  /**
   * Creates a new Header object.
   */
   public Header(  )
   {
      runDuration = new String();
      runID = new String();
      runTitleShort = new String();
      startDate = new String();
      startTime = new String();
      userName = new String();
      dataFormatFlag = -1;
      formatVersion = -1;
      startAddressDae = -1;
      startAddressData = -1;
      startAddressInst = -1;
      startAddressLog = -1;
      startAddressRun = -1;
      startAddressSe = -1;
      startAddressSpare = -1;
      startAddressTcb = -1;
      startAddressUser = -1;
   }

  /**
   * Creates a new Header object.
   *
   * @param rawFile The ISIS rawfile to use.
   */
   public Header( RandomAccessFile rawFile ) {
  	this();
    try {
      rawFile.seek( 0 );

      StringBuffer temp = new StringBuffer( 8 );

      for( int ii = 0; ii < 8; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      runID   = temp.toString(  );
      temp    = new StringBuffer( 20 );

      for( int ii = 0; ii < 20; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      userName   = temp.toString(  );
      temp       = new StringBuffer( 24 );

      for( int ii = 0; ii < 24; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      runTitleShort   = temp.toString(  );
      temp            = new StringBuffer( 12 );

      for( int ii = 0; ii < 12; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      startDate   = temp.toString(  );
      temp        = new StringBuffer( 8 );

      for( int ii = 0; ii < 8; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      startTime   = temp.toString(  );
      temp        = new StringBuffer( 8 );

      for( int ii = 0; ii < 8; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      runDuration         = temp.toString(  );
      formatVersion       = readUnsignedInteger( rawFile, 4 );
      if (formatVersion != 2)
         System.out.println("WARNING:  Unrecognized Format version number."
         +"\n          Version found = "+formatVersion
         +"\n          Version numbers corresponding to data that can be processed  = 2"
         +"\n          Data may be incorrectly read and/or interpreted from the file.");
      startAddressRun     = readUnsignedInteger( rawFile, 4 );
      startAddressInst    = readUnsignedInteger( rawFile, 4 );
      startAddressSe      = readUnsignedInteger( rawFile, 4 );
      startAddressDae     = readUnsignedInteger( rawFile, 4 );
      startAddressTcb     = readUnsignedInteger( rawFile, 4 );
      startAddressUser    = readUnsignedInteger( rawFile, 4 );
      startAddressData    = readUnsignedInteger( rawFile, 4 );
      startAddressLog     = readUnsignedInteger( rawFile, 4 );
      startAddressSpare   = readUnsignedInteger( rawFile, 4 );
      dataFormatFlag      = readUnsignedInteger( rawFile, 4 );

      //this looks complete, based on information in libget.txt.
    } catch( IOException ex ) { ex.printStackTrace(); }
  }

  //~ Methods ------------------------------------------------------------------

  /**
   * Tests the constructor {@link #Header( RandomAccessFile rawFile ) Header( RandomAccessFile rawFile )}.  
   * Creates a Header object and prints all of the fields to standard output.
   * @param args args[0] is the only argument used as the filename for 
   * the rawfile to use.
   */
  public static void main( String[] args ) {
    try
    {
	  for (int fileNum=0; fileNum<args.length; fileNum++)
	  {
	     System.out.println("--------------------------------------------------------------------------------");
	     System.out.println("Testing file "+args[fileNum]);
	     System.out.println("--------------------------------------------------------------------------------");
         RandomAccessFile rawFile = new RandomAccessFile( args[fileNum], "r" );
         Header           header = new Header( rawFile );

         rawFile.close(  );
         System.out.println( "RunID                 " + header.runID );
         System.out.println( "UserName              " + header.userName );
         System.out.println( "RunTitle              " + header.runTitleShort );
         System.out.println( "StartDate             " + header.startDate );
         System.out.println( "StartTime             " + header.startTime );
         System.out.println( "RunDuration           " + header.runDuration );
         System.out.println( "formatVersion   " + header.formatVersion );
         System.out.println( "startAddressRun       " + header.startAddressRun );
         System.out.println( "startAddressInst       " + header.startAddressInst );
         System.out.println( "startAddressSe       " + header.startAddressSe );
         System.out.println( "startAddressDae       " + header.startAddressDae );
         System.out.println( "startAddressTcb       " + header.startAddressTcb );
         System.out.println( "startAddressUser       " + header.startAddressUser );
         System.out.println( "startAddressData       " + header.startAddressData );
         System.out.println( "startAddressLog       " + header.startAddressLog );
         System.out.println( "startAddressSpare       " + header.startAddressSpare );
         System.out.println( "dataFormatFlag       " + header.dataFormatFlag );
	  }
    }
    catch( IOException ex )
    {
    	ex.printStackTrace();
    }
  }

  // ---------------------------- ReadVAXReal4 ----------------------
  /**
   * Reads 4 bytes from the RandomAccessFile <code>inFile</code> and 
   * interprets them as a VAX real number.
   */
  protected static double ReadVAXReal4( RandomAccessFile inFile )
    throws IOException {
    int    length   = 4;
    long   hi_mant;
    long   low_mant;
    long   exp;
    long   sign;
    double f_val;
    long   val = ( long )readUnsignedInteger( inFile, length );

    if( val < 0 ) {
      val = val + ( long )Math.pow( 2.0, ( double )32 );
    }

    /* add 128 to put in the implied 1 */
    hi_mant   = ( val & 127 ) + 128;
    val       = val >> 7;

    /* exponent is "excess 128" */
    exp        = ( ( int )( val & 255 ) ) - 128;
    val        = val >> 8;
    sign       = val & 1;
    low_mant   = val >> 1;

    /* This could also be a "reserved" operand of some sort?*/
    if( exp == -128 ) {
      f_val = 0;
    } else {
      f_val = ( ( hi_mant / 256.0 ) + ( low_mant / 16777216.0 ) ) * Math.pow( 2.0,
          ( double )exp );
    }

    if( sign == 1 ) {
      f_val = -f_val;
    }

    return f_val;
  }

  // --------------------------- readUnsignedInteger -------------------
  /**
   * Reads <code>length</code> bytes from the RandomAccessFile <code>inFile</code> 
   * and interprets them as an unsigned integer.  This method is needed because by Java would 
   * possibly read the bytes as representing a negative number.
   */
  protected static int readUnsignedInteger( RandomAccessFile inFile, int length )
    throws IOException {
    byte[] b          = new byte[length];
    int[]  c          = new int[length];
    int    nBytesRead = inFile.read( b, 0, length );

    //	for ( int i = 0; i < length; i++ ){
    //	    System.out.println ( "b[" + i + "] = " + b[i] );
    //	}
    int num = 0;

    for( int i = 0; i < length; ++i ) {
      if( b[i] < 0 ) {
        c[i] = b[i] + 256;
      } else {
        c[i] = b[i];
      }

      num += ( c[i] * ( int )Math.pow( 256.0, ( double )i ) );
    }

    return num;
  }

  // --------------------------- readUnsignedInteger -------------------
  /**
   * Reads <code>length</code> bytes from the RandomAccessFile <code>inFile</code> 
   * and interprets them as an unsigned long.  This method is needed because by Java would 
   * possibly read the bytes as representing a negative number.
   */
  protected static long readUnsignedLong( RandomAccessFile inFile, int length )
    throws IOException {
    byte[] b          = new byte[length];
    int[]  c          = new int[length];
    int    nBytesRead = inFile.read( b, 0, length );
    long   num        = 0;

    for( int i = 0; i < length; ++i ) {
      if( b[i] < 0 ) {
        c[i] = b[i] + 256;
      } else {
        c[i] = b[i];
      }

      num += ( c[i] * ( int )Math.pow( 256.0, ( double )i ) );
    }

    return num;
  }

  /**
   * Reads <code>length</code> characters from the file specified by <code>inFile</code> 
   * (from its file pointer's current location) and creates a String.  If a null character is reached, 
   * it is not added to the String.
   * @param inFile The file to read the data from.
   * @param length The number of bytes to read.
   * @return The String created from the next <code>length</code> bytes read from the file.
   * @throws An IOException is thrown to allow the calling method to decide if it should 
   * continue reading the file or not.
   */
  protected static String readString(RandomAccessFile inFile, int length) throws IOException
  {
     StringBuffer buffer = new StringBuffer(length);
     byte by = 0;
     for (int i=0; i<length; i++)
     {
        by = inFile.readByte();
        if (by != 0)
           buffer.append((char)by);
     }
     return buffer.toString();
  }

   /**
    * Get the data format flag.
    * @return The data format flag (either 0 or 1).
    */
   public int getDataFormatFlag()
   {
      return dataFormatFlag;
   }

   /**
    * Get the format version.
    * @return The format version.
    */
   public int getFormatVersion()
   {
      return formatVersion;
   }

   /**
    * Get the run duration.
    * @return The run duration ({mu}A.Hr).
    */
   public String getRunDuration()
   {
      return runDuration;
   }

   /**
    * Get the run identifier (eg LAD12345).
    * @return The run identifier.  It is composed of 3 characters for the 
    * instrument and 5 characters for the run number.
    */
   public String getRunID()
   {
      return runID;
   }

   /**
    * Get the experiment short title.
    * @return The experiment short title.
    */
   public String getRunTitleShort()
   {
      return runTitleShort;
   }

   /**
    * Get the start address of the Data Acquisition Electronics Section.
    * @return The offset in the file where the Data Acquisition Electronics 
    * section starts.
    */
   public int getStartAddressDAESection()
   {
      return startAddressDae;
   }

   /**
    * Get the start address of the Data Section.
    * @return The offset in the file where the Data section 
    * starts.
    */
   public int getStartAddressDATASection()
   {
      return startAddressData;
   }

   /**
    * Get the start address of the Instrument Section.
    * @return The offset in the file where the 
    * Instrument Section starts.
    */
   public int getStartAddressINSTSection()
   {
      return startAddressInst;
   }

   /**
    * Get the start address of the Log Section.
    * @return The offset in the file where the 
    * Log Section starts.
    */
   public int getStartAddressLOGSection()
   {
      return startAddressLog;
   }

   /**
    * Get the start address of the Run Section.
    * @return The offset in the file where the Run 
    * section starts.
    */
   public int getStartAddressRUNSection()
   {
      return startAddressRun;
   }

   /**
    * Get the start address of the Sample Environment Section.
    * @return The offset in the file where the Sample 
    * Environment Section starts.
    */
   public int getStartAddressSESection()
   {
      return startAddressSe;
   }

   /**
    * Get the start address of the Time Channel Boundary Section.
    * @return The offset in the file where the Time Channel Boundary 
    * Section starts.
    */
   public int getStartAddressTCBSection()
   {
      return startAddressTcb;
   }

   /**
    * Get the start address of the User Section.
    * @return The offset in the file where the User Section starts.
    */
   public int getStartAddressUSERSection()
   {
      return startAddressUser;
   }

   /**
    * Get the start date.
    * @return The start date.
    */
   public String getStartDate()
   {
      return startDate;
   }

   /**
    * Get the start time.
    * @return The start time.
    */
   public String getStartTime()
   {
      return startTime;
   }

   /**
    * Get the user name.
    * @return The user name.
    */
   public String getUserName()
   {
      return userName;
   }

}
