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
 * Revision 1.11  2004/07/01 22:12:32  kramer
 * Added methods to get the min and max detector IDs corresponding to monitors.
 * Also, fixed MinSubgroupID() to return a non-zero result.  Fixed,
 * MinSubgroupID() and MaxSubgroupID() to use 1 as the first histogram (not 0).
 *
 * Revision 1.10  2004/06/24 21:57:17  kramer
 *
 * Changed all of the fields' visiblity from protected to private.  Fields
 * are now accessed from other classes in this package through getter methods
 * instead of using <object>.<field name>.  Also, this class should now be
 * immutable.  The methods in this class now more closely match those in the
 * class IPNS.Runfile.Runfile.  The old methods are still in the code but
 * have been commented out.
 *
 * Revision 1.9  2004/06/22 16:38:34  kramer
 *
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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import IPNS.Runfile.InstrumentType;

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

  private String            rawfileName;
  private RandomAccessFile  rawfile;
  private Header            header;
  private RunSection        runSect;
  private InstrumentSection instSect;
  private SESection seSect;
  private DaeSection        daeSect;
  private TimeSection       timeSect;
  private DataSection       dataSect;
  private boolean           leaveOpen;
  private String            filename;
  
  private int minMonitorID;
  private int maxMonitorID;

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
      
      minMonitorID = -1;
      maxMonitorID = -1;
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
      daeSect    = new DaeSection( rawfile, header, instSect.getNumberOfDetectors() );
      timeSect   = new TimeSection( rawfile, header );
      dataSect   = new DataSection( rawfile, header, timeSect );
    } catch( IOException ex ) { ex.printStackTrace(); }
  }

  //~ Methods ------------------------------------------------------------------
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
   * Get the detector IDs which are in the spectrum (aka subgroup) <code>sg</code>.
   * @param sg The spectrum in question.
   * @return A list of detector IDs which are in the specified spectrum
   *         (subgroup)
   */
  public int[] IdsInSubgroup( int sg ) {
    /*
    int[] sgList = new int[0];

    //these instSec were originally instDesc.  I changed them so it would 
    //compile
    for( int ii = 1; ii < instSect.spectrumNumbers.length; ii++ )
    {
      if( sg == instSect.spectrumNumbers[ii] )
      {
        int[] tempList = new int[sgList.length + 1];

        System.arraycopy( sgList, 0, tempList, 0, sgList.length );
        tempList[sgList.length]   = ii;
        sgList                    = tempList;
      }
    }
    */
    
    Vector found = new Vector(instSect.getNumberOfDetectors());
    for (int i=1; i<=instSect.getNumberOfDetectors(); i++)
    {
       if (instSect.getSpectrumNumberForDetector(i)==sg)
         found.add(new Integer(i));
    }
    
    int[] answer = new int[found.size()];
    for (int i=0; i<found.size(); i++)
      answer[i] = ((Integer)found.elementAt(i)).intValue();

    return answer;
  }
  
  /**
   * Get the smallest detector ID corresponding 
   * to a monitor.
   * @return The smallest detector ID 
   * corresponding to a monitor or -1 if 
   * there aren't any monitors.
   */
  public int MinMonitorID()
  {
     if (minMonitorID != -1)
        return minMonitorID;
     else
     {
        int numMon = instSect.getNumberOfMonitors();
        if (numMon >= 1)
        {
           int min = instSect.getMonDetNumForMonitor(1);
           for (int i=2; i<=numMon; i++)
              min = Math.min(min,instSect.getMonDetNumForMonitor(i)); 
           minMonitorID = min;
           return min;
        }
        else
           return -1;
     }
  }
  
  /**
   * Get the largest detector ID corresponding 
   * to a monitor.
   * @return The largest detector ID 
   * corresponding to a monitor or -1 if 
   * there aren't any monitors.
   */
  public int MaxMonitorID()
  {
     if (maxMonitorID != -1)
        return maxMonitorID;
     else
     {
        int numMon = instSect.getNumberOfMonitors();
        if (numMon >= 1)
        {
           int max = instSect.getMonDetNumForMonitor(1);
           for (int i=2; i<=numMon; i++)
              max = Math.max(max,instSect.getMonDetNumForMonitor(i));
           maxMonitorID = max;
           return max;
        }
        else
           return -1;
     }
  }
  
  /**
   * Get the maximum subgroup ID (aka the pectrum number as it is 
   * recorded in the ISIS RAW file).
   * @param hist The histogram of spectra to search through.  Note:  
   * The first histogram is at hist=1.
   * @return The maximum group of detector data or -1 if the value of 
   * <code>hist</code> is invalid.
   */
  public int MaxSubgroupID(int hist)
  {
     if (hist == 1)
     {
       int maxVal = 0;

       //these instSec were originally instDesc.  I changed them so it would 
      //compile
       for( int ii = 1; ii <= instSect.getNumberOfDetectors(); ii++ )
       {
          if (!IsSubgroupBeamMonitor(ii))
            maxVal = Math.max( instSect.getSpectrumNumberForDetector(ii), maxVal );
       }
       return maxVal;
     }
     else
       return -1;
  }

  /**
   * Get the minimum subgroup ID (aka the pectrum number as it is 
   * recorded in the ISIS RAW file).
   * @param hist The histogram of spectra to search through.  Note:  
   * The first histogram is at hist=1.
   * @return Possible values:<br>
   * The minimum group of detector data or<br>
   * -1 if the value of <code>hist</code> is invalid or<br>
   * -2 if the histogram does not contain any detectors
   */
  public int MinSubgroupID(int hist)
  {
     if (hist == 1)
     {
       //these instSec were originally instDesc.  I changed them so it would 
      //compile
       int numDet = instSect.getNumberOfDetectors();
       if (numDet >= 1)
       {
          int minVal = instSect.getSpectrumNumberForDetector(1);
          for( int ii = 1; ii <= numDet; ii++ )
          {
             if (!IsSubgroupBeamMonitor(ii))
                minVal = Math.min( instSect.getSpectrumNumberForDetector(ii), minVal );
          }
          return minVal;
       }
       else
          return -2;
     }
     else
       return -1;
  }
  
  /**
   * Get the Time Channel Boundary array (where the time is in 
   * microseconds) for the given time regime.
   * @param num The time regime to get the TCB array for.  Note:  
   * The first time regime corresponds to num=1 not num=0.
   * @return The TCB array.  If it does not exist, this returns null.
   */
  public float[] TimeChannelBoundariesForRegime(int num)
  {
    int[] timeChanBound = timeSect.getTimeChannelBoundariesForRegime(num);
    if (timeChanBound == null)
      return null;
    else
    {
       int prescale = timeSect.getClockPrescaleForRegime(num);
       float error = timeSect.getTimeChannelParametersForRegime(num)[0][0];
       float[] tcbArray = new float[timeChanBound.length];
       //here 4 is subtracted because data collected from ISIS RAW files seems 
       //to be hinting that the first and last values in timeChanBound are always 
       //too high by 4 units.  Therefore, it is hypothesized that all of the values are 
       //too high by 4 units.  This makes the calculated values match 
       //getTimeChannelParametersForRegime(num)[0][0] and 
       //getTimeChannelParametersForRegime(num)[0][1] which appear to be the 
       //initial and final times (in microseconds) respectively
       for (int i=0; i<timeChanBound.length; i++)
         tcbArray[i] = (timeChanBound[i]*prescale/32.0f+error-4);
       
       return tcbArray;
    }
  }
  
  /**
   * Get the time channel boundaries (where the time is in 
   * microseconds) for the given given detector.
   * @param id The detector in question.  Note:  The first 
   * detector is id=1 not id=0.
   * @return The time channel boundaries for the given 
   * detector or null if <code>id</code> is invalid.
   */
  public float[] TimeChannelBoundaries(int id)
  {
     return TimeChannelBoundariesForRegime(daeSect.getTimeRegimeForDetector(id));
  }

  /**
   * Retrieves the spectrum of a 1D detector.  This method is not complete yet,
   * as the underlying code inX DataSection is not yet complete (04/16/2004).
   * @param spect The number of the spectrum that is to be recieved.
   * @return The retrieved spectrum.
   */
  public float[] Get1DSpectrum( int spect )
  {
    return dataSect.get1DSpectrum( rawfile, spect, timeSect );
  }
  
  /**
   * Is the detector numbered <code>detNum</code> a monitor?
   * @param detNum The number of the detector in question.
   * @return True if the detector is a monitor and false otherwise.
   */
  public boolean IsSubgroupBeamMonitor(int detNum)
  {
     return instSect.isAMonitor(detNum);
  }
  
  /**
   * Get the phi angle.
   * @return The phi angle (in degrees).
   */
  public float Phi()
  {
     return seSect.getPhi();
  }
  
  /**
   * Get the chi angle.
   * @return The chi angle (in degrees).
   */
  public float Chi()
  {
     //IPNS calls the angle Chi
     //ISIS calls it Psi
     return seSect.getPsi();
  }
  
  /**
   * Get the omega angle.
   * @return The omega angle (in degrees).
   */
  public float Omega()
  {
     return seSect.getOmega();
  }
   
   /**
    * Get the distance from the source to the sample.
    * @return The distance from the source to the sample.
    */
   public float SourceToSample()
   {
      return instSect.getL1();
   }
   
   /**
    * The number of histograms.
    * @return The number of histograms.
    */
   public short NomOfHistograms()
   {
      //it looks like all of the data is in one histogram
      return 1;
   }
   
   /**
    * Get the instrument type.
    * @return An integer code specifying the 
    * instrument's type.  The integer value 
    * returned corresponds to one of the 
    * public static final int fields in the class 
    * IPNS.Runfile.InstrumentType
    */
   public int InstrumentType()
   {
      String type = header.getInstrumentType();
      if (type.equalsIgnoreCase("HRP"))
         return InstrumentType.TOF_DIFFRACTOMETER;
      else if (type.equalsIgnoreCase("SXD"))
         return InstrumentType.TOF_SCD;
      else if (type.equalsIgnoreCase("LOQ"))
         return InstrumentType.TOF_SAD;
      else
         return InstrumentType.UNKNOWN;
   }
   
   /**
    * Get the run number.
    * @return The run number or -1 if the 
    * run number could not be properly 
    * determined.
    */
   public int RunNumber()
   {
      String numStr = header.getRunNumber();
      int result = -1;
      try { result = Integer.valueOf(numStr).intValue(); }
      catch (NumberFormatException e)
      {
         result = -1;
         e.printStackTrace();
      }
      return result;
   }
   
   /**
    * Get the run title.
    * @return The run title.
    */
   public String RunTitle()
   {
      return runSect.getRunTitle();
   }
   
   /**
    * Get the end date.
    * @return The end date in the 
    * format dd-mmm-yyyy_
    */
   public String EndDate()
   {
      return runSect.getFinishDate();
   }
   
   /**
    * Get the finish time.
    * @return The finish time in the 
    * format hh-mm-ss
    */
   public String EndTime()
   {
      return runSect.getFinishTime();
   }
   
   /**
    * Get the user's name.
    * @return The user's name.
    */
   public String UserName()
   {
      return runSect.getUserName();
   }
   
   /***
    * Get the raw flight path (in meters) for the monitor 
    * specified by the detector ID <code>detNum</code>.
    * @param detID The number of the detector you are 
    * referring to.  Note:  For <code>detectorNum</code> 
    * to be valid, 1 <= <code>detectorNum</code> <= 
    * {@link InstrumentSection#getNumberOfDetectors() 
    * getNumberOfDetectors()}.  Also, ther first detector 
    * is at detID=1 not detID=0.  
    * @return The flight path for the monitor specified.  
    * The flight path is the same as the L2 distance (the 
    * distance from the sample to the detector in meters).  
    * If detNum is invalid or does not specify a monitor 
    * Float.NaN is returned.
    */
   public double MonitorRawFlightPath(int detID)
   {
      if (IsSubgroupBeamMonitor(detID))
         return instSect.getFlightPathForDetector(detID);
      else
         return Float.NaN;
   }
   
   /**
    * Get the time field type for the detector with 
    * the detector ID <code>detID</code>.
    * @param detID The number of the detector you are 
    * refering you.  Note:  for <code>detectorNum</code> to be 
    * valid, 1 <= <code>detectorNum</code> <= 
    * {@link InstrumentSection#getNumberOfDetectors() 
    * InstrumentSection.getNumberOfDetectors()}.
    * @return The time field type (ie the time regime) 
    * for the detector specified or -1 if <code>detID</code> 
    * is invallid.
    */
   public int TimeFieldType(int detID)
   {
      return daeSect.getTimeRegimeForDetector(detID);
   }

  /**
   * Get the run ID.
   * @return The run ID.
   */
  public String getRunID()
  {
     return header.getRunID();
  }

  /**
   * Get one more than the number of spectra for the 
   * given time regime (an extra spectra is added for 
   * the zeroth spectra).
   * @param num The regime in question.  Note:  The 
   * first time regime is at num=1 not num=0.  For num 
   * to be valid 1<=<code>num</code><=
   * {@link InstrumentSection#getNumOfTimeRegimes() 
   * getNumOfTimeRegimes()}.
   * @return One more than the number of spectra for 
   * time regime <code>num</code> or -1 if <code>num
   * </code> is invalid.
   */
  public int getNumSpectraForRegime(int num)
  {
     return timeSect.getNumSpectraForRegime(num);
  }
  
  /**
   * Get the instrument's name.
   * @return The instrument name.
   */
  public String InstrumentName(  )
  {
    return instSect.getInstrumentName();
  }
  
  /**
   * The good proton charge.
   * @return The good proton charge for this run.
   */
  public float GoodProtonCharge(  )
  {
    return runSect.getGoodProtonCharge();
  }

  /**
   * The angle for the detector specified by 
   * <code>detID</code>.
   * @param detID The number of the detector you are referring to.  
   * Note:  For <code>detectorNum</code> to be valid, 1 <= 
   * <code>detectorNum</code> <= 
   * {@link InstrumentSection#getNumberOfDetectors() 
   * getNumberOfDetectors()}.
   * @return The angle for a given detector ID
   */
  public float DetectorAngle( int detID )
  {
    return instSect.getDetectorAngleForDetector(detID);
  }

  /**
   * The flight path length for a given detector ID.
   * @param detID The number of the detector you are referring to.  
   * Note:  For <code>detectorNum</code> to be valid, 1 <= 
   * <code>detectorNum</code> <= 
   * {@link InstrumentSection#getNumberOfDetectors() 
   * getNumberOfDetectors()}.
   * @return The flight path length for a given detector ID
   */
  public float FlightPath( int detID )
  {
     return instSect.getFlightPathForDetector(detID);
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
  
  //these are all of the old methods from this file
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
   * @return The actual run duration as stored in the run section
   /
  public int ActualRunDuration(  ) {
    return runSect.actualRunDuration;
  }

  /*public int[] getTCB( int start, int end ) {
     return timeSect.timeChannelBoundaries[0][0]*/

  /**
   * @return The end date in the run section
   /
  public String EndDate(  ) {
    return runSect.finishDate;
  }

  /**
   * @return The end time in the run section
   /
  public String EndTime(  ) {
    return runSect.finishTime;
  }

  /**
   * @return An array containing the detector numbers beam monitors
   
  public int[] MonitorDetNums(  ) {
    return instSect.monDetNums;
  }

  /**
   * @return The monitor 1 sum as stored in the run section
   /
  public int MonitorSum1(  ) {
    return runSect.monitorSum1;
  }

  /**
   * @return The monitor 2 sum as stored in the run section
   /
  public int MonitorSum2(  ) {
    return runSect.monitorSum2;
  }

  /**
   * @return The monitor 3 sum as stored in the run section
   /
  public int MonitorSum3(  ) {
    return runSect.monitorSum3;
  }

  /**
   * @return The number of detectors for this instrument
   /
  public int NumDet(  ) {
    return instSect.nDet;
  }

  /**
   * @return The number of monitors specified for this instrument
   /
  public int NumMon(  ) {
    return instSect.nMon;
  }

  /**
   * @return The number of user tables for this instrument
   /
  public int NumUserTables(  ) {
    return instSect.nUserTables;
  }

  /**
   * @return The RAL Proposal Number stored in the run section
   /
  public int RALProposalNum(  ) {
    return runSect.ralProposalNum;
  }

  /**
   * @return The required run duration
   /
  public int RequiredRunDuration(  ) {
    return runSect.requiredRunDuration;
  }

  /**
   * @return The run number in the run section
   /
  public int RunNumber(  ) {
    return runSect.runNumber;
  }

  /**
   * @return The full title stored in the run section
   /
  public String RunTitle(  ) {
    return runSect.runTitle;
  }

  /**
   * @return The incident flight path L1 from the instrument section
   /
  public float SourceToSample(  ) {
    return instSect.L1;
  }

  /**
   * @return The start date in the header
   /
  public String StartDate(  ) {
    return header.startDate;
  }

  /**
   * @return The start time in the header
   /
  public String StartTime(  ) {
    return header.startTime;
  }

  /**
   * @return A list that maps detectors by ID to a spectrum (subgroup) that
   *         contains data for that detector.  This list has nDet + 1
   *         elements. This is a 0 indexed array.
   /
  public int[] SubgroupIDList(  ) {
    //these instSec were originally instDesc.  I changed them so it would 
    //compile
    return instSect.spectrumNumbers;
  }

  /**
   * @return The total proton charge for this run from the runSection
   /
  public float TotalProtonCharge(  ) {
    return runSect.totalProtonCharge;
  }

  /**
   * @return The user name stored int the run section
   /
  public String UserName(  ) {
    return runSect.userName;
  }
*/
}
