/*
 * File:  RunSection.java
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
 * Revision 1.6  2004/06/16 20:40:50  kramer
 * Now the source will contain the cvs logs.  Replaced tabs with 3 spaces,
 * created a default contstructor where fields will be initialized (instead
 * of when they are first declared), and when exceptions are caught a stack
 * trace is now printed to standard output.
 *
 */

package ISIS.Rawfile;

import java.io.*;


/**
 * Class to retrieve run section information from a RAW file.
 *
 * @author J.P. Hammonds Intense Pulsed Neutron Source Argonne National
 *         Laboratory
 */
public class RunSection {
  //~ Instance fields ----------------------------------------------------------
  
  /** Finish date (dd-mmm-yyyy_). */
  protected String finishDate;
  /** Finish time (hh-mm-ss). */
  protected String finishTime;
  /** Run title. */
  protected String runTitle;
  /** User institution. */
  protected String userInstitution;
  /** User name. */
  protected String userName;
  /** User telephone number 1 (day). */
  protected String userPhone1;
  /** User telephone number 2 (day). */
  protected String userPhone2;
  /** User telephone number (night). */
  protected String userPhone3;
  /** Good proton charge (uA.hr). */
  protected float  goodProtonCharge;
  /** Total proton charge (uA.hr). */
  protected float  totalProtonCharge;
  /** Actual run duration. */
  protected int    actualRunDuration;
  /** Actual run duration (seconds). */
  protected int    actualRunDurationSec;
  /** Dump interval. */
  protected int    dumpInterval;
  /** Monitor sum 1. */
  protected int    monitorSum1;
  /** Monitor sum 2. */
  protected int    monitorSum2;
  /** Monitor sum 3. */
  protected int    monitorSum3;
  /** Number of 'good' frames. */
  protected int    numberOfGoodFrames;
  /** RAL proposal number. */
  protected int    ralProposalNum;
  /**
   * Required run duration.  The units are the same as 
   * those for the actual run duration.
   */
  protected int    requiredRunDuration;
  /** Run number (starting from 1). */
  protected int    runNumber;
  /** Scaler for the actual run duration. */
  protected int    scalerForRPB1;
  /** Scaler for the dump interval. */
  protected int    scalerForRPB4;
  /***
   * The test interval of the scaler for the actual run duration.  
   * The units are in seconds.
   */
  protected int    testInterval2;
  /**
   * The test interval of the scaler for the dump interval.  
   * The units are in seconds.
   */
  protected int    testInterval5;
  /** The total number of frames. */
  protected int    totalNumberOfFrames;
  /** 2**k (SNS frequency(Hz)=50/2**k). */
  protected int    twobyk;
  /** RUN section version number. */
  protected int    version;

  //~ Constructors -------------------------------------------------------------

  /**
   * Creates a new RunSection object.
   */
   public RunSection(  )
   {
      finishDate = new String();
      finishTime = new String();
      runTitle = new String();
      userInstitution = new String();
      userName = new String();
      userPhone1 = new String();
      userPhone2 = new String();
      userPhone3 = new String();
      goodProtonCharge = Float.NaN;
      totalProtonCharge = Float.NaN;
      actualRunDuration = -1;
      actualRunDurationSec = -1;
      dumpInterval = -1;
      monitorSum1 = -1;
      monitorSum2 = -1;
      monitorSum3 = -1;
      numberOfGoodFrames = -1;
      ralProposalNum = -1;
      requiredRunDuration = -1;
      runNumber = -1;
      scalerForRPB1 = -1;
      scalerForRPB4 = -1;
      testInterval2 = -1;
      testInterval5 = -1;
      totalNumberOfFrames = -1;
      twobyk = -1;
      version = -1;
   }

  /**
   * Creates a new RunSection object.
   *
   * @param rawFile RandomAccessFile used to read the rawfile.
   * @param header The Header used to access the RUN section.  
   * The Header contains the information used to locate the RUN 
   * section in the rawfile.
   */
  RunSection( RandomAccessFile rawFile, Header header ) {
  	this();
    int startAddress = ( header.startAddressRun - 1 ) * 4;

    try {
      rawFile.seek( startAddress );
      version     = Header.readUnsignedInteger( rawFile, 4 );
      runNumber   = Header.readUnsignedInteger( rawFile, 4 );

      StringBuffer temp;

      temp = new StringBuffer( 80 );

      for( int ii = 0; ii < 80; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      runTitle   = temp.toString(  );
      temp       = new StringBuffer( 20 );

      for( int ii = 0; ii < 20; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      userName   = temp.toString(  );
      temp       = new StringBuffer( 20 );

      for( int ii = 0; ii < 20; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      userPhone1   = temp.toString(  );
      temp         = new StringBuffer( 20 );

      for( int ii = 0; ii < 20; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      userPhone2   = temp.toString(  );
      temp         = new StringBuffer( 20 );

      for( int ii = 0; ii < 20; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      userPhone3   = temp.toString(  );
      temp         = new StringBuffer( 20 );

      for( int ii = 0; ii < 20; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      userInstitution   = temp.toString(  );
      temp              = new StringBuffer( 60 );

      //spare section
      for( int ii = 0; ii < 60; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      actualRunDuration   = Header.readUnsignedInteger( rawFile, 4 );
      scalerForRPB1       = Header.readUnsignedInteger( rawFile, 4 );
      testInterval2       = Header.readUnsignedInteger( rawFile, 4 );
      dumpInterval        = Header.readUnsignedInteger( rawFile, 4 );
      scalerForRPB4       = Header.readUnsignedInteger( rawFile, 4 );
      testInterval5       = Header.readUnsignedInteger( rawFile, 4 );

      // 2**k (SNS frequency(Hz)=50/2**k)
      twobyk                 = Header.readUnsignedInteger( rawFile, 4 );
      goodProtonCharge       = ( float )Header.ReadVAXReal4( rawFile );
      totalProtonCharge      = ( float )Header.ReadVAXReal4( rawFile );
      numberOfGoodFrames     = Header.readUnsignedInteger( rawFile, 4 );
      totalNumberOfFrames    = Header.readUnsignedInteger( rawFile, 4 );
      requiredRunDuration    = Header.readUnsignedInteger( rawFile, 4 );
      actualRunDurationSec   = Header.readUnsignedInteger( rawFile, 4 );
      monitorSum1            = Header.readUnsignedInteger( rawFile, 4 );
      monitorSum2            = Header.readUnsignedInteger( rawFile, 4 );
      monitorSum3            = Header.readUnsignedInteger( rawFile, 4 );
      temp                   = new StringBuffer( 12 );

      for( int ii = 0; ii < 12; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      finishDate   = temp.toString(  );
      temp         = new StringBuffer( 8 );

      for( int ii = 0; ii < 8; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      finishTime       = temp.toString(  );
      ralProposalNum   = Header.readUnsignedInteger( rawFile, 4 );

      //complete except for missing spare section (RPB(-32)) ?
    } catch( IOException ex ) { ex.printStackTrace(); }
  }

  //~ Methods ------------------------------------------------------------------

  /**
   * Tests the constructor {@link #RunSection( RandomAccessFile rawFile, Header header ) 
   * RunSection( RandomAccessFile rawFile, Header header )}.
   * @param args args[0] specifies the filename for the rawfile to use.
   */
  public static void main( String[] args )
  {
    try
    {
		for (int fileNum=0; fileNum<args.length; fileNum++)
		{
		   System.out.println("--------------------------------------------------------------------------------");
		   System.out.println("Testing file "+args[fileNum]);
		   System.out.println("--------------------------------------------------------------------------------");
           RandomAccessFile rawFile = new RandomAccessFile( args[fileNum], "r" );
           Header           header = new Header( rawFile );
           RunSection       rs     = new RunSection( rawFile, header );

           System.out.println( "versionNumber:        " + rs.version );
           System.out.println( "runNumber:            " + rs.runNumber );
           System.out.println( "runTitle:             " + rs.runTitle );
           System.out.println( "userName:             " + rs.userName );
           System.out.println( "userPhone1:           " + rs.userPhone1 );
           System.out.println( "userPhone2:           " + rs.userPhone2 );
           System.out.println( "userPhone3:           " + rs.userPhone3 );
           System.out.println( "userInstitution:      " + rs.userInstitution );
           System.out.println( "actualRunDuration:    " + rs.actualRunDuration );
           System.out.println( "scalerForRPB1:        " + rs.scalerForRPB1 );
           System.out.println( "testInterval2:        " + rs.testInterval2 );
           System.out.println( "dumpInterval:         " + rs.dumpInterval );
           System.out.println( "scalerForRPB4:        " + rs.scalerForRPB4 );
           System.out.println( "testInterval5:        " + rs.testInterval5 );
           System.out.println( "twobyk:               " + rs.twobyk );
           System.out.println( "goodProtonCharge:     " + rs.goodProtonCharge );
           System.out.println( "totalProtonCharge:    " + rs.totalProtonCharge );
           System.out.println( "numberOfGoodFrames:   " + rs.numberOfGoodFrames );
           System.out.println( "totalNumberOfFrames:  " + rs.totalNumberOfFrames );
           System.out.println( "requiredRunDuration:  " + rs.requiredRunDuration );
           System.out.println( "actualRunDurationSec: " + rs.actualRunDurationSec );
           System.out.println( "monitorSum1:          " + rs.monitorSum1 );
           System.out.println( "monitorSum2:          " + rs.monitorSum2 );
           System.out.println( "monitorSum3:          " + rs.monitorSum3 );
           System.out.println( "finishDate:           " + rs.finishDate );
           System.out.println( "finishTime:           " + rs.finishTime );
           System.out.println( "ralProposalNumber:    " + rs.ralProposalNum );
		}
    }
    catch( IOException ex )
    {
    	ex.printStackTrace();
    }
  }
}
