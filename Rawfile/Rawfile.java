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

/*
 * $Log$
 * Revision 1.1  2004/04/30 00:02:09  bouzekc
 * Initial revision
 *
 */
public class Rawfile {
  //~ Instance fields **********************************************************

  protected String            rawfileName = new String(  );
  protected RandomAccessFile  rawfile;
  protected Header            header    = new Header(  );
  protected RunSection        runSect   = new RunSection(  );
  protected InstrumentSection instSect  = new InstrumentSection(  );
  protected DaeSection        daeSect   = new DaeSection(  );
  protected TimeSection       timeSect  = new TimeSection(  );
  protected DataSection       dataSect  = new DataSection(  );
  protected boolean           leaveOpen = false;
  protected String            filename;

  //~ Constructors *************************************************************

  /**
   * Default Constructor with empty rawfile
   */
  public Rawfile(  ) {}

  /**
   * Creates a new Rawfile object.
   *
   * @param infileName The name of the RAW file.
   */
  public Rawfile( String infileName ) {
    try {
      filename   = infileName;
      rawfile    = new RandomAccessFile( filename, "rw" );
      header     = new Header( rawfile );
      runSect    = new RunSection( rawfile, header );
      instSect   = new InstrumentSection( rawfile, header );
      daeSect    = new DaeSection( rawfile, header, instSect.nDet );
      timeSect   = new TimeSection( rawfile, header );
      dataSect   = new DataSection( rawfile, header, timeSect );
    } catch( IOException ex ) {}
  }

  //~ Methods ******************************************************************

  /**
   * @return The actual run duration as stored in the run section
   */
  public int ActualRunDuration(  ) {
    return runSect.actualRunDuration;
  }

  /**
   * @return A clone of the integer array holding the number of spectra.
   */
  public int[] numSpectra(  ) {
    return ( int[] )timeSect.numSpectra.clone(  );
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
   * Retrieves the spectrum of a 1D detector.  This method is not complete nor
   * completely usable yet.
   *
   * @param subgroup Subgroup ID to be retrieved. (currently unused)
   *
   * @return The retrieved spectrum.
   */
  public float[] Get1DSpectrum( int spect ) {
    return dataSect.get1DSpectrum( rawfile, spect, timeSect );

    /*float[] spectra = new float[dataSect.rawData.length];
       for( int k = 0; k < spectra.length; k++ ) {
         spectra[k] = dataSect.rawData[k];
       }
       return spectra;*/
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
   * Testbed
   *
   * @param args unused.
   */
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
  /**
   * @return The instrument section for this rawfile.
   */
  public InstrumentSection getInstSect() {
    return instSect;
  }

  /**
   * @param section
   */
  public void setInstSect(InstrumentSection section) {
    instSect = section;
  }

}
