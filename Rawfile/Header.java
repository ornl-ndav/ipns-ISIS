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
 */

package ISIS.Rawfile;

import java.io.*;


/**
 * This package processes header information from an ISIS Raw data file.
 *
 * @author J.P. Hammonds Intense Pulsed Neutron Source Argonne National
 *         Laboratory
 */
public class Header {
  //~ Instance fields ----------------------------------------------------------

  protected String runDuration       = new String( "" );
  /**
   * Run identifier.  Three characters for the instrument followed by 
   * five characters for the run number (eg LAD12345).
   */
  protected String runID             = new String( "" );
  /** Experiment short title. */
  protected String runTitleShort     = new String( "" );
  /** Start date. */
  protected String startDate         = new String( "" );
  /** Start time. */
  protected String startTime         = new String( "" );
  /** The user name. */
  protected String userName          = new String( "" );
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
  /**  THe start address of the USER DEFINED section. */
  protected int    startAddressUser;

  //~ Constructors -------------------------------------------------------------

  /**
   * Creates a new Header object.
   */
  Header(  ) {}

  /**
   * Creates a new Header object.
   *
   * @param rawFile The ISIS rawfile to use.
   */
  Header( RandomAccessFile rawFile ) {
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
    } catch( IOException ex ) {}
  }

  //~ Methods ------------------------------------------------------------------

  /**
   * Tests the constructor {@link #Header( RandomAccessFile rawFile ) Header( RandomAccessFile rawFile )}.  
   * Creates a Header object and prints all of the fields to standard output.
   * @param args args[0] is the only argument used as the filename for 
   * the rawfile to use.
   */
  public static void main( String[] args ) {
    try {
      RandomAccessFile rawFile = new RandomAccessFile( args[0], "r" );
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
      System.out.println( "startAddressSpare       " +
        header.startAddressSpare );
      System.out.println( "dataFormatFlag       " + header.dataFormatFlag );
    } catch( IOException ex ) {}
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
}