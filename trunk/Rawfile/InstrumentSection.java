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
 * Class to get instrument information from an ISIS RAW file.
 *
 * @author J.P. Hammonds Intense Pulsed Neutron Source Argonne National
 *         Laboratory
 */
public class InstrumentSection {
  //~ Instance fields ----------------------------------------------------------

  /** Instrument name. */
  protected String iName = new String(  );

  //CODE in libget.txt spec
  /** 
   * Code to define use of UT values.  The length of this 
   * array is one more than the number of detectors.  
   * The first meaningful value in this array is at index 
   * 1.  The value at index 0 is a garbage value.
   */
  protected int[] codeForUserTableValues = new int[0];

  //TTHE in libget.txt spec
  /**
   * The 2Theta table (scattering angle).  The length of this 
   * array is one more than the number of detectors.  
   * The first meaningful value in this array is at index 
   * 1.  The value at index 0 is a garbage value.
   */
  protected float[] detectorAngle = new float[0];

  //LEN2 in libget.txt spec
  /**
   * The L2 table (m).  The length of this 
   * array is one more than the number of detectors.  
   * The first meaningful value in this array is at index 
   * 1.  The value at index 0 is a garbage value.
   */
  protected float[] flightPath = new float[0];

  //DELT in libget.txt spec
  /**
   * 'HOLD OFF' in microseconds.  The length of this 
   * array is one more than the number of detectors.  
   * The first meaningful value in this array is at index 
   * 1.  The value at index 0 is a garbage value.
   */
  protected float[] holdOff = new float[0];

  //monitor numbers
  /**
   * The detector numbers of the monitors.  The length of this 
   * array is one more than the number of detectors.
   */
  protected int[] monDetNums      = new int[0];
  /**
   * Prescale values for the monitors.  The length of this array 
   * equals the number of monitors.
   */
  protected int[] monPrescale     = new int[0];
  /**
   * The spectrum number table.  The length of this 
   * array is one more than the number of detectors.  
   * The first meaningful value in this array is at index 
   * 1.  The value at index 0 is a garbage value.
   */
  protected int[] spectrumNumbers = new int[0];

  //UT1/UTn in libget.txt spec
  /**
   * User defined tables.  In the rawfile, a table is written as a 
   * array of VAX reals (floats in Java).  In addition, that table 
   * is just one of an array of tables written in the file.  Hence, 
   * userTable is (number of tables)x(number of detectors + 1) sized 
   * 2-dimensional array.
   */
  protected float[][] userTable            = new float[0][0];
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
  InstrumentSection(  ) {}

  /**
   * Creates a new InstrumentSection object.
   *
   * @param rawFile The RAW file to use.
   * @param header The header for the RAW file.  The 
   * header contains information used to locate the Instrument 
   * section in the RAW file.
   */
  InstrumentSection( RandomAccessFile rawFile, Header header ) {
    int startAddress = ( header.startAddressInst - 1 ) * 4;

    try {
      rawFile.seek( startAddress );
      version = Header.readUnsignedInteger( rawFile, 4 );

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
  public static void main( String[] args ) {
    try {
      RandomAccessFile  rawFile = new RandomAccessFile( args[0], "r" );
      Header            header = new Header( rawFile );
      InstrumentSection is     = new InstrumentSection( rawFile, header );

      /*System.out.println( "version: " + is.version );
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
            for( int ii = 0; ii < is.nMon; ii++ ) {
              System.out.print( is.monDetNums[ii] + "  " );
            }
            System.out.println(  );
            System.out.println( "monPrescale: " );
            for( int ii = 0; ii < is.nMon; ii++ ) {
              System.out.print( is.monPrescale[ii] + "  " );
            }
            System.out.println(  );*/
      System.out.println( "spectrumNumbers: " );

      for( int ii = 1; ii <= is.nDet; ii++ ) {
        System.out.println( is.spectrumNumbers[ii] + "  " );
      }

      /*
         System.out.println(  );
         System.out.println( "holdOff: " );
         for( int ii = 1; ii <= is.nDet; ii++ ) {
           System.out.print( is.holdOff[ii] + "  " );
         }
      
         System.out.println(  );
         System.out.println( "flightPath: " );
         for( int ii = 1; ii <= 30; ii++ ) {
           System.out.println( is.flightPath[ii] + "  " );
         }
      
            System.out.println(  );
            System.out.println( "codeforUserTableValues: " );
            for( int ii = 1; ii <= is.nDet; ii++ ) {
              System.out.print( is.codeForUserTableValues[ii] + "  " );
            }
      
         System.out.println(  );
         System.out.println( "detectorAngle: " );
         for( int ii = 1; ii <= 30; ii++ ) {
           System.out.println( is.detectorAngle[ii] + "  " );
         }
         /*
            System.out.println(  );
            System.out.println( "userTable: " );
            for( int jj = 0; jj < is.nUserTables; jj++ ) {
              System.out.println( "---Table " + jj );
              for( int ii = 1; ii <= is.nDet; ii++ ) {
                System.out.print( is.userTable[jj][ii] + "  " );
              }
              System.out.println(  );
            }
            System.out.println(  );*/
    } catch( IOException ex ) {}
  }

  /**
   * Get the detector angle for the specified detector.
   * @param index detector number
   *
   * @return The detector angle for the specified detector.
   */
  public float getDetectorAngle( int index ) {
    return detectorAngle[index];
  }

  /**
   * Get the flight path at the given index.
   * @param index The index for the flight path.
   *
   * @return The flight path at the given index.
   */
  public float getFlightPath( int index ) {
    return flightPath[index];
  }
}
