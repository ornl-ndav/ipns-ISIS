/*
 * File:  Rawfile.java
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
 * Revision 1.9  2004/06/22 16:38:34  kramer
 * Added a method to get the run ID.  Now the main method also reads Header
 * information from a file.
 *
 * Revision 1.8  2004/06/16 20:38:19  kramer
 *
 * Replaced tabs with 3 spaces and created a default constructor where fields
 * are now initialized (instead of when they were first declared).
 *
 * Revision 1.7  2004/06/16 16:43:01  kramer
 *
 * Added J.P. Hammonds <jphammonds@anl.gov> to the list of contacts.
 *
 * Revision 1.6  2004/06/16 16:13:08  kramer
 *
 * Fixed the GNU license header.
 *
 * Revision 1.5  2004/06/16 15:20:46  kramer
 *
 * Added:
 *        The GNU license header
 *        an SESection field
 * The main method was also improved.
 *
 * Revision 1.4  2004/06/15 20:08:28  kramer
 *
 * Fixed indents (the code was indented too far into the page).
 *
 * Revision 1.2  2004/04/30 00:16:51  bouzekc
 * Newer version.  Old version should not have been in CVS.
 *
 */
 
package ISIS.Rawfile;

import java.io.*;


/**
 * This class is designed to provide an interface to the ISIS raw files.  This
 * class reads information by instatiating classes for different section of
 * the  raw file header information.  Public methods to retrieve information
 * are all provided by this class
 *
 * @author J.P. Hammonds Intense Pulsed Neutron Source Argonne National
 *         Laboratory
 */

public class Rawfile {
  //~ Instance fields ----------------------------------------------------------

  protected String            rawfileName;
  protected RandomAccessFile  rawfile;
  protected Header            header;
  protected RunSection        runSect;
  protected InstrumentSection instSect;
  protected SESection seSect;
  protected DaeSection        daeSect;
  protected TimeSection       timeSect;
  protected DataSection       dataSect;
  protected boolean           leaveOpen;
  protected String            filename;

  //~ Constructors -------------------------------------------------------------

  /**
   * Default Constructor with empty rawfile.<br>
   * <b>Note:  rawfile is not instantiated.  Instead it is null </b>
   */
   public Rawfile(  )
   {
      rawfileName = new String();
      rawfile = null;
      header = new Header();
      runSect = new RunSection();
      instSect = new InstrumentSection();
      seSect = new SESection();
      daeSect = new DaeSection();
      timeSect = new TimeSection();
      dataSect = new DataSection();
      leaveOpen = false;
      filename = new String();
   }

  /**
   * Creates a new Rawfile object.
   *
   * @param infileName The name of the RAW file.
   */
  public Rawfile( String infileName ) {
  	this();
    try {
      filename   = infileName;
      rawfile    = new RandomAccessFile( filename, "rw" );
      header     = new Header( rawfile );
      runSect    = new RunSection( rawfile, header );
      instSect   = new InstrumentSection( rawfile, header );
      seSect = new SESection(rawfile,header);
      daeSect    = new DaeSection( rawfile, header, instSect.nDet );
      timeSect   = new TimeSection( rawfile, header );
      dataSect   = new DataSection( rawfile, header, timeSect );
    } catch( IOException ex ) { ex.printStackTrace(); }
  }

  //~ Methods ------------------------------------------------------------------

  /**
   * @return The actual run duration as stored in the run section
   */
  public int ActualRunDuration(  ) {
    return runSect.actualRunDuration;
  }

  /*public int[] getTCB( int start, int end ) {
     return timeSect.timeChannelBoundaries[0][0]*/

  /**
   * Closes files opened with LeaveOpen.
   */
  public void Close(  ) {
    if( leaveOpen == false ) {
      return;
    }

    try {
      rawfile.close(  );
    } catch( IOException e ) {
      System.out.println( "Problem Closing File: " + rawfileName );
      e.printStackTrace(  );
    }

    leaveOpen = false;
  }

  /**
   * @return The angle for a given detector ID
   */
  public float DetectorAngle( int detID ) {
    return instSect.detectorAngle[detID];
  }

  /**
   * @return The end date in the run section
   */
  public String EndDate(  ) {
    return runSect.finishDate;
  }

  /**
   * @return The end time in the run section
   */
  public String EndTime(  ) {
    return runSect.finishTime;
  }

  /**
   * @return The flight path length for a given detector ID
   */
  public float FlightPath( int detID ) {
    return instSect.flightPath[detID];
  }

  /**
   * Retrieves the spectrum of a 1D detector.  This method is not complete yet,
   * as the underlying code in DataSection is not yet complete (04/16/2004).
   *
   * @param subgroup Subgroup ID to be retrieved.
   *
   * @return The retrieved spectrum.
   */
  public float[] Get1DSpectrum( int spect ) {
    return dataSect.get1DSpectrum( rawfile, spect, timeSect );
  }

  /**
   * @return The good proton charge for this run from the runSection
   */
  public float GoodProtonCharge(  ) {
    return runSect.goodProtonCharge;
  }

  /**
   * @return A list of detector IDs which are in the specified spectrum
   *         (subgroup)
   */
  public int[] IdsInSubgroup( int sg ) {
    int[] sgList = new int[0];

    //these instSec were originally instDesc.  I changed them so it would 
    //compile
    for( int ii = 1; ii < instSect.spectrumNumbers.length; ii++ ) {
      if( sg == instSect.spectrumNumbers[ii] ) {
        int[] tempList = new int[sgList.length + 1];

        System.arraycopy( sgList, 0, tempList, 0, sgList.length );
        tempList[sgList.length]   = ii;
        sgList                    = tempList;
      }
    }

    return sgList;
  }

  /**
   * @return The instrument name.
   */
  public String InstrumentName(  ) {
    return instSect.iName;
  }

  /**
   * Trigger to leave the file open for subsequent reads.  This is not required
   * but will speed up file access if a large number of spectra  are
   * retrieved.  Care must be used when using this routine.  It is not  used
   * to initialize the object or retrieving header information.  It  leaves
   * the file open to speed up getting spectra data.  After getting  data or
   * between long pauses Close Method should be used.
   */
  public void LeaveOpen(  ) {
    if( leaveOpen == true ) {
      return;
    }

    try {
      rawfile = new RandomAccessFile( rawfileName, "r" );
    } catch( IOException e ) {
      System.out.println( "Problem Opening File: " + rawfileName );
      e.printStackTrace(  );
    }

    leaveOpen = true;
  }

  /**
   * @return The maximum group of detector data (
   */
  public int MaxSubgroupID(  ) {
    int maxVal = 0;

    //these instSec were originally instDesc.  I changed them so it would 
    //compile
    for( int ii = 1; ii < instSect.spectrumNumbers.length; ii++ ) {
      maxVal = Math.max( instSect.spectrumNumbers[ii], maxVal );
    }

    return maxVal;
  }

  /**
   * @return An array containing the detector numbers beam monitors
   */
  public int[] MonitorDetNums(  ) {
    return instSect.monDetNums;
  }

  /**
   * @return The monitor 1 sum as stored in the run section
   */
  public int MonitorSum1(  ) {
    return runSect.monitorSum1;
  }

  /**
   * @return The monitor 2 sum as stored in the run section
   */
  public int MonitorSum2(  ) {
    return runSect.monitorSum2;
  }

  /**
   * @return The monitor 3 sum as stored in the run section
   */
  public int MonitorSum3(  ) {
    return runSect.monitorSum3;
  }

  /**
   * @return The number of detectors for this instrument
   */
  public int NumDet(  ) {
    return instSect.nDet;
  }

  /**
   * @return The number of monitors specified for this instrument
   */
  public int NumMon(  ) {
    return instSect.nMon;
  }

  /**
   * @return The number of user tables for this instrument
   */
  public int NumUserTables(  ) {
    return instSect.nUserTables;
  }

  /**
   * @return The RAL Proposal Number stored in the run section
   */
  public int RALProposalNum(  ) {
    return runSect.ralProposalNum;
  }

  /**
   * @return The required run duration
   */
  public int RequiredRunDuration(  ) {
    return runSect.requiredRunDuration;
  }

  /**
   * @return The run number in the run section
   */
  public int RunNumber(  ) {
    return runSect.runNumber;
  }

  /**
   * @return The full title stored in the run section
   */
  public String RunTitle(  ) {
    return runSect.runTitle;
  }

  /**
   * @return The incident flight path L1 from the instrument section
   */
  public float SourceToSample(  ) {
    return instSect.L1;
  }

  /**
   * @return The start date in the header
   */
  public String StartDate(  ) {
    return header.startDate;
  }

  /**
   * @return The start time in the header
   */
  public String StartTime(  ) {
    return header.startTime;
  }

  /**
   * @return A list that maps detectors by ID to a spectrum (subgroup) that
   *         contains data for that detector.  This list has nDet + 1
   *         elements. This is a 0 indexed array.
   */
  public int[] SubgroupIDList(  ) {
    //these instSec were originally instDesc.  I changed them so it would 
    //compile
    return instSect.spectrumNumbers;
  }

  /**
   * Accessor method for the time channel boundary array.  This assumes one
   * time regime.
   *
   * @return The TCB array.  If it does not exist, this returns null.
   */
  public float[] TCBArray(  ) {
    if( timeSect.timeChannelBoundaries == null ) {
      return null;
    }

    float[] TCBFloatArray = new float[timeSect.timeChannelBoundaries[0].length];

    for( int i = 0; i < TCBFloatArray.length; i++ ) {
      TCBFloatArray[i] = ( timeSect.timeChannelBoundaries[0][i] * 0.0125f ) +
        ( timeSect.timeChannelParameters[0][0][0] * 0.1f );
    }

    return TCBFloatArray;
  }

  /**
   * @return The total proton charge for this run from the runSection
   */
  public float TotalProtonCharge(  ) {
    return runSect.totalProtonCharge;
  }

  /**
   * @return The user name stored int the run section
   */
  public String UserName(  ) {
    return runSect.userName;
  }
  
  /**
   * Get the run ID.
   * @return The run ID.
      *
   */
  public String getRunID()
  {
     return header.runID;
  }

  /**
   * Testbed
   *
   * @param args unused.
   */
  public static void main(String[] args)
  {
  	for (int fileNum=0; fileNum<args.length; fileNum++)
  	{
      Rawfile rawfile = new Rawfile(args[fileNum]);
      String[] fileArr = new String[1];
         fileArr[0] = args[fileNum];
      System.out.println("Processing ISIS RAW file:  "+rawfile.filename);
      System.out.println("    RAW File name = "+rawfile.rawfileName);
      
      System.out.println("*******************************************************");
      System.out.println("HEADER Section");
      System.out.println("*******************************************************");
      Header.main(fileArr);

      System.out.println("*******************************************************");
      System.out.println("RUN Section");
      System.out.println("*******************************************************");
      RunSection.main(fileArr);

      System.out.println("*******************************************************");
      System.out.println("INSTRUMENT Section");
      System.out.println("*******************************************************");
      InstrumentSection.main(fileArr);

      System.out.println("*******************************************************");
      System.out.println("SE Section");
      System.out.println("*******************************************************");
      SESection.main(fileArr);

      System.out.println("*******************************************************");
      System.out.println("DAE Section");
      System.out.println("*******************************************************");
      DaeSection.main(fileArr);
      
      System.out.println("*******************************************************");
      System.out.println("TCB Section");
      System.out.println("*******************************************************");
      TimeSection.main(fileArr);
     
      System.out.println("*******************************************************");
      System.out.println("DATA Section");
      System.out.println("*******************************************************");
      DataSection.main(fileArr);
      
      System.out.println("##########################################");
  	}
  }
  
  /*
  public static void main( String[] args ) {
    Rawfile file = new Rawfile( args[0] );

    System.out.print( "Actual Run Duration " );
    System.out.println( file.ActualRunDuration(  ) );
    System.out.print( "End Date " );
    System.out.println( file.EndDate(  ) );
    System.out.print( "End Time " );
    System.out.println( file.EndTime(  ) );
    System.out.print( "Good Proton Charge " );
    System.out.println( file.GoodProtonCharge(  ) );
    System.out.print( "Max Subgroup ID " );
    System.out.println( file.MaxSubgroupID(  ) );
    System.out.println( "Monitor Detector Numbers " );

    int[] monitorDetNums = file.MonitorDetNums(  );

    for( int m = 0; m < monitorDetNums.length; m++ ) {
      System.out.println( monitorDetNums[m] );
    }

    System.out.print( "Monitor Sum 1 " );
    System.out.println( file.MonitorSum1(  ) );
    System.out.print( "Monitor Sum 2 " );
    System.out.println( file.MonitorSum2(  ) );
    System.out.print( "Monitor Sum 3 " );
    System.out.println( file.MonitorSum3(  ) );
    System.out.print( "Number of Detectors " );
    System.out.println( file.NumDet(  ) );
    System.out.print( "Number of Monitors " );
    System.out.println( file.NumMon(  ) );
    System.out.print( "Number of User Tables " );
    System.out.println( file.NumUserTables(  ) );
    System.out.print( "RAL Proposal Number " );
    System.out.println( file.RALProposalNum(  ) );
    System.out.print( "Required Run Duration " );
    System.out.println( file.RequiredRunDuration(  ) );
    System.out.print( "Run Number " );
    System.out.println( file.RunNumber(  ) );
    System.out.print( "Run Title " );
    System.out.println( file.RunTitle(  ) );
    System.out.print( "Source To Sample " );
    System.out.println( file.SourceToSample(  ) );
    System.out.print( "Start Date " );
    System.out.println( file.StartDate(  ) );

    int[] subgroupIDList = file.SubgroupIDList(  );

    System.out.println( "Subgroup ID List" );

    for( int k = 0; k < subgroupIDList.length; k++ ) {
      System.out.println( subgroupIDList[k] );
    }

    System.out.println( file.TotalProtonCharge(  ) );
    System.out.println( file.UserName(  ) );
    file.Close(  );
  }
*/

  /**
   * @return A clone of the integer array holding the number of spectra.
   */
  public int[] numSpectra(  ) {
    int[] tempSpect = new int[timeSect.numSpectra.length];

    System.arraycopy( timeSect.numSpectra, 0, tempSpect, 0, tempSpect.length );

    return tempSpect;
  }
}
