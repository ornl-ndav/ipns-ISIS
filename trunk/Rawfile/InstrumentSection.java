/*
 * File:  InstrumentSection.java
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
 * Revision 1.8  2004/06/18 18:29:18  kramer
 *
 * Added getter methods (with documentation) for all of the fields.  Now the
 * class imports two classes instead of the entire java.io package.  It also
 * warns the user if it thinks it cannot accurately read data from the file.
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
 * Class to get instrument information from an ISIS RAW file.
 *
 * @author J.P. Hammonds Intense Pulsed Neutron Source Argonne National
 *         Laboratory
 */
public class InstrumentSection {
  //~ Instance fields ----------------------------------------------------------

  /** Instrument name. */
  protected String iName;

  //CODE in libget.txt spec
  /** 
   * Code to define use of UT values.  The length of this 
   * array is one more than the number of detectors.  
   * The first meaningful value in this array is at index 
   * 1.  The value at index 0 is a garbage value.
   */
  protected int[] codeForUserTableValues;

  //TTHE in libget.txt spec
  /**
   * The 2Theta table (scattering angle).  The length of this 
   * array is one more than the number of detectors.  
   * The first meaningful value in this array is at index 
   * 1.  The value at index 0 is a garbage value.
   */
  protected float[] detectorAngle;

  //LEN2 in libget.txt spec
  /**
   * The L2 table (m).  The length of this 
   * array is one more than the number of detectors.  
   * The first meaningful value in this array is at index 
   * 1.  The value at index 0 is a garbage value.
   */
  protected float[] flightPath;

  //DELT in libget.txt spec
  /**
   * 'HOLD OFF' in microseconds.  The length of this 
   * array is one more than the number of detectors.  
   * The first meaningful value in this array is at index 
   * 1.  The value at index 0 is a garbage value.
   */
  protected float[] holdOff;

  //monitor numbers
  /**
   * The detector numbers of the monitors.  The length of this 
   * array is one more than the number of detectors.
   */
  protected int[] monDetNums;
  /**
   * Prescale values for the monitors.  The length of this array 
   * equals the number of monitors.
   */
  protected int[] monPrescale;
  /**
   * The spectrum number table.  The length of this 
   * array is one more than the number of detectors.  
   * The first meaningful value in this array is at index 
   * 1.  The value at index 0 is a garbage value.
   */
  protected int[] spectrumNumbers;

  //UT1/UTn in libget.txt spec
  /**
   * User defined tables.  In the rawfile, a table is written as a 
   * array of VAX reals (floats in Java).  In addition, that table 
   * is just one of an array of tables written in the file.  Hence, 
   * userTable is (number of tables)x(number of detectors + 1) sized 
   * 2-dimensional array.
   */
  protected float[][] userTable;
  /** L1. */
  protected float     L1;
  /** Angle of incidence. */
  protected float     angleOfIncidence;
  /** Beam apperture horizontal (in mm). */
  protected float     beamapertureHoriz;
  /** Beam apperture vertical (in mm). */
  protected float     beamapertureVert;
  /** Frequency chopper 1 (in Hz). */
  protected float     chopFreq1;
  /** Frequency chopper 2 (in Hz). */
  protected float     chopFreq2;
  /** Frequency chopper 3 (in Hz). */
  protected float     chopFreq3;
  /** FOE angle. */
  protected float     foeAngle;
  /** LOQ X center. */
  protected float     loqXCenter;
  /** LOQ Y center. */
  protected float     loqYCenter;
  /** Radius beam stop. */
  protected float     radiusBeamStop;
  /** Rotor energy. */
  protected float     rotorEnergy;
  /** Rotor frequency. */
  protected float     rotorFrequency;
  /** Rotor phase. */
  protected float     rotorPhase;
  /** Source to detector distance. */
  protected float     sourceToDetectorDist;
  /** Apperture c1. */
  protected int       apertureC1;
  /** Apperture c2. */
  protected int       apertureC2;
  /** Apperture c3. */
  protected int       apertureC3;
  /** Beam stop. */
  protected int       beamStop;
  /** Delay c1 (in microseconds). */
  protected int       delayC1;
  /** Delay c2 (in microseconds). */
  protected int       delayC2;
  /** Delay c3 (in microseconds). */
  protected int       delayC3;
  /** Detector tank vacuum (1=vacuum). */
  protected int       detectorTankVacuum;
  /** Main shutter (open = 1). */
  protected int       mainShutter;
  /** Max error on delay c1 (in microseconds). */
  protected int       maxErrorDelayC1;
  /** Max error on delay c2 (in microseconds). */
  protected int       maxErrorDelayC2;
  /** Max error on delay c3 (in microseconds). */
  protected int       maxErrorDelayC3;
  /** Moderator type number. */
  protected int       moderatorTypeNum;
  /** Number of detectors. */
  protected int       nDet;

  //number of detectors
  /** Number of monitors. */
  protected int nMon;
  /**
   * Number of UTn tables.  The length of this 
   * array equals the number of monitors.
   */
  protected int nUserTables;
  /** Rotor slit package. */
  protected int rotorSlitPackage;
  /** Scattering position (eg 1 or 2 HRPD). */
  protected int scatteringPosition;
  /** Slow chopper. */
  protected int slowChopper;
  /** Status c1 (run, stopped, stop open). */
  protected int statusC1;
  /** Status c2 (run, stopped, stop open). */
  protected int statusC2;
  /** Status c3 (run, stopped, stop open). */
  protected int statusC3;
  /** Thermal shutter (open = 1). */
  protected int thermalShutter;
  /** INSTRUMENT section version number. */
  protected int version;

  //~ Constructors -------------------------------------------------------------

  /**
   * Creates a new InstrumentSection object.
   */
  public InstrumentSection(  )
  {
     iName = new String();
     codeForUserTableValues = new int[0];
     detectorAngle = new float[0];
     flightPath = new float[0];
     holdOff = new float[0];
     monDetNums = new int[0];
     monPrescale = new int[0];
     spectrumNumbers = new int[0];
     userTable = new float[0][0];
     L1 = Float.NaN;
     angleOfIncidence = Float.NaN;
     beamapertureHoriz = Float.NaN;
     beamapertureVert = Float.NaN;
     chopFreq1 = Float.NaN;
     chopFreq2 = Float.NaN;
     chopFreq3 = Float.NaN;
     foeAngle = Float.NaN;
     loqXCenter = Float.NaN;
     loqYCenter = Float.NaN;
     radiusBeamStop = Float.NaN;
     rotorEnergy = Float.NaN;
     rotorFrequency = Float.NaN;
     rotorPhase = Float.NaN;
     sourceToDetectorDist = Float.NaN;
     apertureC1 = -1;
     apertureC2 = -1;
     apertureC3 = -1;
     beamStop = -1;
     delayC1 = -1;
     delayC2 = -1;
     delayC3 = -1;
     detectorTankVacuum = -1;
     mainShutter = -1;
     maxErrorDelayC1 = -1;
     maxErrorDelayC2 = -1;
     maxErrorDelayC3 = -1;
     moderatorTypeNum = -1;
     nDet = -1;
     nMon = -1;
     nUserTables = -1;
     rotorSlitPackage = -1;
     scatteringPosition = -1;
     slowChopper = -1;
     statusC1 = -1;
     statusC2 = -1;
     statusC3 = -1;
     thermalShutter = -1;
     version = -1;
  }

  /**
   * Creates a new InstrumentSection object.
   *
   * @param rawFile The RAW file to use.
   * @param header The header for the RAW file.  The 
   * header contains information used to locate the Instrument 
   * section in the RAW file.
   */
   public InstrumentSection( RandomAccessFile rawFile, Header header ) {
    this();
    int startAddress = ( header.startAddressInst - 1 ) * 4;

    try {
      rawFile.seek( startAddress );
      version = Header.readUnsignedInteger( rawFile, 4 );
      if (version != 2)
         System.out.println("WARNING:  Unrecognized Instrument Section version number."
         +"\n          Version found = "+version
         +"\n          Version numbers corresponding to data that can be processed  = 2"
         +"\n          Data may be incorrectly read and/or interpreted from the file.");

      StringBuffer temp;

      temp = new StringBuffer( 8 );

      for( int ii = 0; ii < 8; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      iName                  = temp.toString(  );
      chopFreq1              = ( float )Header.ReadVAXReal4( rawFile );
      chopFreq2              = ( float )Header.ReadVAXReal4( rawFile );
      chopFreq3              = ( float )Header.ReadVAXReal4( rawFile );
      delayC1                = Header.readUnsignedInteger( rawFile, 4 );
      delayC2                = Header.readUnsignedInteger( rawFile, 4 );
      delayC3                = Header.readUnsignedInteger( rawFile, 4 );
      maxErrorDelayC1        = Header.readUnsignedInteger( rawFile, 4 );
      maxErrorDelayC2        = Header.readUnsignedInteger( rawFile, 4 );
      maxErrorDelayC3        = Header.readUnsignedInteger( rawFile, 4 );
      apertureC1             = Header.readUnsignedInteger( rawFile, 4 );
      apertureC2             = Header.readUnsignedInteger( rawFile, 4 );
      apertureC3             = Header.readUnsignedInteger( rawFile, 4 );
      statusC1               = Header.readUnsignedInteger( rawFile, 4 );
      statusC2               = Header.readUnsignedInteger( rawFile, 4 );
      statusC3               = Header.readUnsignedInteger( rawFile, 4 );
      mainShutter            = Header.readUnsignedInteger( rawFile, 4 );
      thermalShutter         = Header.readUnsignedInteger( rawFile, 4 );
      beamapertureHoriz      = ( float )Header.ReadVAXReal4( rawFile );
      beamapertureVert       = ( float )Header.ReadVAXReal4( rawFile );
      scatteringPosition     = Header.readUnsignedInteger( rawFile, 4 );
      moderatorTypeNum       = Header.readUnsignedInteger( rawFile, 4 );
      detectorTankVacuum     = Header.readUnsignedInteger( rawFile, 4 );
      L1                     = ( float )Header.ReadVAXReal4( rawFile );
      rotorFrequency         = ( float )Header.ReadVAXReal4( rawFile );
      rotorEnergy            = ( float )Header.ReadVAXReal4( rawFile );
      rotorPhase             = ( float )Header.ReadVAXReal4( rawFile );
      rotorSlitPackage       = Header.readUnsignedInteger( rawFile, 4 );
      slowChopper            = Header.readUnsignedInteger( rawFile, 4 );
      loqXCenter             = ( float )Header.ReadVAXReal4( rawFile );
      loqYCenter             = ( float )Header.ReadVAXReal4( rawFile );
      beamStop               = Header.readUnsignedInteger( rawFile, 4 );
      radiusBeamStop         = ( float )Header.ReadVAXReal4( rawFile );
      sourceToDetectorDist   = ( float )Header.ReadVAXReal4( rawFile );
      foeAngle               = ( float )Header.ReadVAXReal4( rawFile );
      angleOfIncidence       = ( float )Header.ReadVAXReal4( rawFile );

      //skip around the spare section
      rawFile.seek( startAddress + ( 67 * 4 ) );

      //keep reading
      nDet          = Header.readUnsignedInteger( rawFile, 4 );
      nMon          = Header.readUnsignedInteger( rawFile, 4 );
      nUserTables   = Header.readUnsignedInteger( rawFile, 4 );
      monDetNums    = new int[nMon];

      for( int ii = 0; ii < nMon; ii++ ) {
        monDetNums[ii] = Header.readUnsignedInteger( rawFile, 4 );
      }

      monPrescale = new int[nMon];

      for( int ii = 0; ii < nMon; ii++ ) {
        monPrescale[ii] = Header.readUnsignedInteger( rawFile, 4 );
      }

      spectrumNumbers = new int[nDet + 1];

      for( int ii = 1; ii <= nDet; ii++ ) {
        spectrumNumbers[ii] = Header.readUnsignedInteger( rawFile, 4 );
      }

      holdOff = new float[nDet + 1];

      for( int ii = 1; ii <= nDet; ii++ ) {
        holdOff[ii] = ( float )Header.ReadVAXReal4( rawFile );
      }

      flightPath = new float[nDet + 1];

      for( int ii = 1; ii <= nDet; ii++ ) {
        flightPath[ii] = ( float )Header.ReadVAXReal4( rawFile );
      }

      codeForUserTableValues = new int[nDet + 1];

      for( int ii = 1; ii <= nDet; ii++ ) {
        codeForUserTableValues[ii] = Header.readUnsignedInteger( rawFile, 4 );
      }

      detectorAngle = new float[nDet + 1];

      for( int ii = 1; ii <= nDet; ii++ ) {
        detectorAngle[ii] = ( float )Header.ReadVAXReal4( rawFile );
      }

      userTable = new float[nUserTables][nDet + 1];

      for( int jj = 0; jj < nUserTables; jj++ ) {
        for( int ii = 1; ii <= nDet; ii++ ) {
          userTable[jj][ii] = ( float )Header.ReadVAXReal4( rawFile );
        }
      }
    } catch( IOException ex ) {}

    //looks complete based on libget.txt    
  }

  //~ Methods ------------------------------------------------------------------

  /**
   * Testbed.
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
          RandomAccessFile  rawFile = new RandomAccessFile( args[fileNum], "r" );
          Header            header = new Header( rawFile );
          InstrumentSection is     = new InstrumentSection( rawFile, header );

         System.out.println( "version: " + is.version );
         System.out.println( "iName: " + is.iName );
         System.out.println( "chopFreq1: " + is.chopFreq1 );
         System.out.println( "chopFreq2: " + is.chopFreq2 );
         System.out.println( "chopFreq3: " + is.chopFreq3 );
         System.out.println( "delayC1: " + is.delayC1 );
         System.out.println( "delayC2: " + is.delayC2 );
         System.out.println( "delayC3: " + is.delayC3 );
         System.out.println( "maxErrorDelayC1: " + is.maxErrorDelayC1 );
         System.out.println( "maxErrorDelayC2: " + is.maxErrorDelayC2 );
         System.out.println( "maxErrorDelayC3: " + is.maxErrorDelayC3 );
         System.out.println( "apertureC1: " + is.apertureC1 );
         System.out.println( "apertureC2: " + is.apertureC2 );
         System.out.println( "apertureC3: " + is.apertureC3 );
         System.out.println( "statusC1: " + is.statusC1 );
         System.out.println( "statusC2: " + is.statusC2 );
         System.out.println( "statusC3: " + is.statusC3 );
         System.out.println( "mainShutter: " + is.mainShutter );
         System.out.println( "thermalShutter: " + is.thermalShutter );
         System.out.println( "beamapertureHoriz: " + is.beamapertureHoriz );
         System.out.println( "beamapertureVert: " + is.beamapertureVert );
         System.out.println( "scatteringPosition: " + is.scatteringPosition );
         System.out.println( "moderatorTypeNum: " + is.moderatorTypeNum );
         System.out.println( "detectorTankVacuum: " + is.detectorTankVacuum );
         System.out.println( "L1: " + is.L1 );
         System.out.println( "rotorFrequency: " + is.rotorFrequency );
         System.out.println( "rotorEnergy: " + is.rotorEnergy );
         System.out.println( "rotorPhase: " + is.rotorPhase );
         System.out.println( "rotorSlitPackage: " + is.rotorSlitPackage );
         System.out.println( "slowChopper: " + is.slowChopper );
         System.out.println( "loqXCenter: " + is.loqXCenter );
         System.out.println( "loqYCenter: " + is.loqYCenter );
         System.out.println( "beamStop: " + is.beamStop );
         System.out.println( "radiusBeamStop: " + is.radiusBeamStop );
         System.out.println( "sourceToDetectorDist: " + is.sourceToDetectorDist );
         System.out.println( "foeAngle: " + is.foeAngle );
         System.out.println( "angleOfIncidence: " + is.angleOfIncidence );
         System.out.println( "nDet: " + is.nDet );
         System.out.println( "nMon: " + is.nMon );
         System.out.println( "nUserTables: " + is.nUserTables );
         System.out.println( "monDetNums: " );
         for( int ii = 0; ii < is.nMon; ii++ )
           System.out.print( is.monDetNums[ii] + "  " );
         System.out.println(  );
         System.out.println( "monPrescale: " );
         for( int ii = 0; ii < is.nMon; ii++ )
           System.out.print( is.monPrescale[ii] + "  " );
         System.out.println(  );
         System.out.println( "spectrumNumbers: " );

         for( int ii = 1; ii <= is.nDet; ii++ )
           System.out.println( is.spectrumNumbers[ii] + "  " );
      
         System.out.println(  );
         System.out.println( "holdOff: " );
         for( int ii = 1; ii <= is.nDet; ii++ ) {
           System.out.print( is.holdOff[ii] + "  " );
         }
      
         System.out.println(  );
         System.out.println( "flightPath: " );
         for( int ii = 1; ii <= is.nDet; ii++ ) {
           System.out.println( is.flightPath[ii] + "  " );
         }
      
            System.out.println(  );
            System.out.println( "codeforUserTableValues: " );
            for( int ii = 1; ii <= is.nDet; ii++ ) {
              System.out.print( is.codeForUserTableValues[ii] + "  " );
            }
      
         System.out.println(  );
         System.out.println( "detectorAngle: " );
         for( int ii = 1; ii <= is.nDet; ii++ ) {
           System.out.println( is.detectorAngle[ii] + "  " );
         }
         
            System.out.println(  );
            System.out.println( "userTable: " );
            for( int jj = 0; jj < is.nUserTables; jj++ ) {
              System.out.println( "---Table " + jj );
              for( int ii = 1; ii <= is.nDet; ii++ ) {
                System.out.print( is.userTable[jj][ii] + "  " );
              }
              System.out.println(  );
            }
            System.out.println(  );
	    }
    }
    catch( IOException ex )
    {
    	ex.printStackTrace();
    }
  }

  /**
   * Get the detector angle for detector <code>detectorNum</code>.  Note:  
   * the first detector is at <code>detectorNum</code>=1 not 0.
   * @param detectorNum The number of the detector you are referring to.  
   * Note:  For <code>detectorNum</code> to be valid, 1 <= 
   * <code>detectorNum</code> <= {@link #getNumberOfDetectors() 
   * getNumberOfDetectors()}.
   * @return The detector angle for the detector or Float.NaN if 
   * <code>detectorNum</code> is invallid.
   */
  public float getDetectorAngleForDetector( int detectorNum )
  {
     if (detectorNum>=1 && detectorNum<=getNumberOfDetectors())
         return detectorAngle[detectorNum];
     else
         return Float.NaN;
  }

  /**
   * Get the fight path for detector <code>detectorNum</code>.  Note:  
   * the first detector is at <code>detectorNum</code>=1 not 0.
   * @param detectorNum The number of the detector you are referring to.  
   * Note:  For <code>detectorNum</code> to be valid, 1 <= 
   * <code>detectorNum</code> <= {@link #getNumberOfDetectors() 
   * getNumberOfDetectors()}.
   * @return The flight path for the detector or Float.NaN if 
   * <code>detectorNum</code> is invallid.
   */
  public float getFlightPathForDetector( int detectorNum )
  {
     if (detectorNum>=1 && detectorNum<=getNumberOfDetectors())
         return flightPath[detectorNum];
     else
         return Float.NaN;
  }
  
   /**
    * Get the angle of incidence.
    * @return The angle of incidence.
    */
   public float getAngleOfIncidence()
   {
      return angleOfIncidence;
   }

   /**
    * Get apperture c1.
    * @return Apperture c1.
    */
   public int getApertureC1()
   {
     return apertureC1;
   }

   /**
    * Get apperture c2.
    * @return Apperture c2.
    */
   public int getApertureC2()
   {
      return apertureC2;
   }

   /**
    * Get apperture c3.
    * @return Apperture c3.
    */
   public int getApertureC3()
   {
      return apertureC3;
   }

   /**
    * Get the beam apperture horizontal.
    * @return The beam apperture horizontal (in mm).
    */
   public float getBeamAppertureHoriz()
   {
      return beamapertureHoriz;
   }

   /**
    * Get the beam apperture vertical.
    * @return The beam apperture vertical (in mm).
    */
   public float getBeamAppertureVert()
   {
      return beamapertureVert;
   }

   /**
    * Get the beam stop.
    * @return The beam stop.
    */
   public int getBeamStop()
   {
      return beamStop;
   }

   /**
    * Get the frequency of chopper 1.
    * @return The frequency of chopper 1 (in Hz).
    */
   public float getChopperFreq1()
   {
      return chopFreq1;
   }

   /**
    * Get the frequency of chopper 2.
    * @return The frequency of chopper 2 (in Hz).
    */
   public float getChopperFreq2()
   {
      return chopFreq2;
   }

   /**
    * Get the frequency of chopper 3.
    * @return The frequency of chopper 3 (in Hz).
    */
   public float getChopperFreq3()
   {
      return chopFreq3;
   }

  /**
   * Get the code for the user table for detector <code>detectorNum</code>.  Note:  
   * the first detector is at <code>detectorNum</code>=1 not 0.
   * @param detectorNum The number of the detector you are referring to.  
   * Note:  For <code>detectorNum</code> to be valid, 1 <= 
   * <code>detectorNum</code> <= {@link #getNumberOfDetectors() 
   * getNumberOfDetectors()}.
   * @return The code for the user table for the detector or -1 if 
   * <code>detectorNum</code> is invallid.
   */
   public int getCodeForUserTableValuesForDetector(int detectorNum)
   {
      if (detectorNum>=1 && detectorNum<=getNumberOfDetectors())
         return codeForUserTableValues[detectorNum];
      else
         return -1;
   }

   /**
    * Get the delay c1.
    * @return The delay c1 (in microseconds).
    */
   public int getDelayC1()
   {
      return delayC1;
   }

   /**
    * Get the delay c2.
    * @return The delay c2 (in microseconds).
    */
   public int getDelayC2()
   {
      return delayC2;
   }

   /**
    * Get the delay c3.
    * @return The delay c3 (in microseconds).
    */
   public int getDelayC3()
   {
      return delayC3;
   }

   /**
    * Get the detector tank vacuum.
    * @return The detector tank vaccum.<br>
    * 1=vacuum
    */
   public int getDetectorTankVacuum()
   {
      return detectorTankVacuum;
   }

   /**
    * Get the FOE angle.
    * @return The FOE angle.
    */
   public float getFOEAngle()
   {
      return foeAngle;
   }

  /**
   * Get the hold off for detector <code>detectorNum</code>.  Note:  
   * the first detector is at <code>detectorNum</code>=1 not 0.
   * @param detectorNum The number of the detector you are referring to.  
   * Note:  For <code>detectorNum</code> to be valid, 1 <= 
   * <code>detectorNum</code> <= {@link #getNumberOfDetectors() 
   * getNumberOfDetectors()}.
   * @return The hold off for the detector or Float.NaN if 
   * <code>detectorNum</code> is invallid.
   */
   public float getHoldOffForDetector(int detectorNum)
   {
      if (detectorNum>=1 && detectorNum<=getNumberOfDetectors())
         return holdOff[detectorNum];
      else
         return Float.NaN;
   }

   /**
    * Get the instrument's name.
    * @return The instrument's name.
    */
   public String getInstrumentName()
   {
      return iName;
   }

   /**
    * Get L1.
    * @return L1.
    */
   public float getL1()
   {
      return L1;
   }

   /**
    * Get LOQ X center.
    * @return LOQ X center.
    */
   public float getLOQXCenter()
   {
      return loqXCenter;
   }

   /**
    * Get LOQ Y center.
    * @return LOQ Y center.
    */
   public float getLoqYCenter()
   {
      return loqYCenter;
   }

   /**
    * Get the main shutter value.
    * @return The main shutter value.<br>
    * 1=open
    */
   public int getMainShutter()
   {
      return mainShutter;
   }

   /**
    * Get the max error on delay c1.
    * @return The max error on delay c1 (in microseconds).
    */
   public int getMaxErrorDelayC1()
   {
      return maxErrorDelayC1;
   }

   /**
    * Get the max error on delay c2.
    * @return The max error on delay c2 (in microseconds).
    */
   public int getMaxErrorDelayC2()
   {
      return maxErrorDelayC2;
   }

   /**
    * Get the max error on delay c3.
    * @return The max error on delay c3 (in microseconds).
    */
   public int getMaxErrorDelayC3()
   {
      return maxErrorDelayC3;
   }

   /**
    * Get the moderator type number.
    * @return The moderator type number.
    */
   public int getModeratorTypeNum()
   {
      return moderatorTypeNum;
   }

  /**
   * Get the detector number for monitor <code>monitorNum</code>.  Note:  
   * the first monitor is at <code>monitorNum</code>=1 not 0.
   * @param monitorNum The number of the monitor you are referring to.  
   * Note:  For <code>monitorNum</code> to be valid, 1 <= 
   * <code>monitorNum</code> <= {@link #getNumberOfMonitors() 
   * getNumberOfMonitors()}.
   * @return The detector number for the monitor or -1 if 
   * <code>monitorNum</code> is invallid.
   */
   public int getMonDetNumForMonitor(int monitorNum)
   {
      if (monitorNum>=1 && monitorNum<=getNumberOfMonitors())
         return monDetNums[monitorNum-1];
      else
         return -1;
   }

  /**
   * Get the prescale value for monitor <code>monitorNum</code>.  Note:  
   * the first monitor is at <code>monitorNum</code>=1 not 0.
   * @param monitorNum The number of the monitor you are referring to.  
   * Note:  For <code>monitorNum</code> to be valid, 1 <= 
   * <code>monitorNum</code> <= {@link #getNumberOfMonitors() 
   * getNumberOfMonitors()}.
   * @return The prescale value for the monitor or -1 if 
   * <code>monitorNum</code> is invallid.
   */
   public int getMonPrescaleForMonitor(int monitorNum)
   {
      if (monitorNum>=1 && monitorNum<=getNumberOfMonitors())
         return monPrescale[monitorNum-1];
      else
         return -1;
   }

   /**
    * Get the number of detectors.
    * @return The number of detectors.
    */
   public int getNumberOfDetectors()
   {
      return nDet;
   }

   /**
    * Get the number of monitors.
    * @return The number of monitors.
    */
   public int getNumberOfMonitors()
   {
      return nMon;
   }

   /**
    * Get the number of user defined tables.
    * @return The number of user defined tables.
    */
   public int getNumberOfUserTables()
   {
      return nUserTables;
   }

   /**
    * Get the radius beam stop.
    * @return The radius beam stop.
    */
   public float getRadiusBeamStop()
   {
      return radiusBeamStop;
   }

   /**
    * Get the rotor energy.
    * @return The rotor energy.
    */
   public float getRotorEnergy()
   {
      return rotorEnergy;
   }

   /**
    * Get the rotor frequency.
    * @return The rotor frequency.
    */
   public float getRotorFrequency()
   {
      return rotorFrequency;
   }

   /**
    * Get the rotor phase.
    * @return The rotor phase.
    */
   public float getRotorPhase()
   {
      return rotorPhase;
   }

   /**
    * Get the rotor slit package.
    * @return The rotor slit package.
    */
   public int getRotorSlitPackage()
   {
      return rotorSlitPackage;
   }

   /**
    * Get the scattering position.
    * @return The scattering position (eg 1 or 2 HRPD).
    */
   public int getScatteringPosition()
   {
      return scatteringPosition;
   }

   /**
    * Get the slow chopper.
    * @return The slow chopper.
    */
   public int getSlowChopper()
   {
      return slowChopper;
   }

   /**
    * Get the distance from the source to the detector.
    * @return The distance from the source to the detector.
    */
   public float getSourceToDetectorDist()
   {
      return sourceToDetectorDist;
   }

  /**
   * Get the spectrum number for detector <code>detectorNum</code>.  Note:  
   * the first detector is at <code>detectorNum</code>=1 not 0.
   * @param detectorNum The number of the detector you are referring to.  
   * Note:  For <code>detectorNum</code> to be valid, 1 <= 
   * <code>detectorNum</code> <= {@link #getNumberOfDetectors() 
   * getNumberOfDetectors()}.
   * @return The spectrum number for the detector or -1 if 
   * <code>detectorNum</code> is invallid.
   */
  public int getSpectrumNumberForDetector(int detectorNum)
  {
      if (detectorNum>=1 && detectorNum<=getNumberOfDetectors())
         return spectrumNumbers[detectorNum];
      else
         return -1;
  }

   /**
    * Get status c1.
    * @return Status c1<br>
    * Possible values:<br>
    * run<br>
    * stopped<br>
    * stop<br>
    * open
    */
   public int getStatusC1()
   {
      return statusC1;
   }

   /**
    * Get status c2.
    * @return Status c2<br>
    * Possible values:<br>
    * run<br>
    * stopped<br>
    * stop<br>
    * open
    */
   public int getStatusC2()
   {
      return statusC2;
   }

   /**
    * Get status c3.
    * @return Status c3<br>
    * Possible values:<br>
    * run<br>
    * stopped<br>
    * stop<br>
    * open
    */   public int getStatusC3()
   {
      return statusC3;
   }

   /**
    * Get the thermal shutter value.
    * @return The thermal shutter value (open=1).
    */
   public int getThermalShutter()
   {
      return thermalShutter;
   }

  /**
   * Get the user table for detector <code>detectorNum</code>.  Note:  
   * the first detector is at <code>detectorNum</code>=1 not 0.
   * @param detectorNum The number of the detector you are referring to.  
   * Note:  For <code>detectorNum</code> to be valid, 1 <= 
   * <code>detectorNum</code> <= {@link #getNumberOfDetectors() 
   * getNumberOfDetectors()}.
   * @return The user table for the detector or null if 
   * <code>detectorNum</code> is invallid.
   */
   public float[] getUserTableForDetector(int detectorNum)
   {
      float[] resultArr = new float[getNumberOfUserTables()];
      if (detectorNum>=1 && detectorNum<=getNumberOfDetectors())
      {
         for (int i=0; i<resultArr.length; i++)
            resultArr[i] = userTable[i][detectorNum];
            
         return resultArr;
      }
      else
         return null;
   }

   /**
    * Get the Instrument Section version number.
    * @return The Instrument Section version number.
    */
   public int getVersion()
   {
      return version;
   }
}
