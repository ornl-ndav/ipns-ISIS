package ISIS.Rawfile;

import java.io.*;


/**
 * Class to get instrument information from an ISIS RAW file.
 *
 * @author J.P. Hammonds Intense Pulsed Neutron Source Argonne National
 *         Laboratory
 */
public class InstrumentSection {
  //~ Instance fields **********************************************************

  //instrument name
  protected String iName = new String(  );

  //CODE in libget.txt spec
  protected int[] codeForUserTableValues = new int[0];

  //TTHE in libget.txt spec
  protected float[] detectorAngle = new float[0];

  //LEN2 in libget.txt spec
  protected float[] flightPath = new float[0];

  //DELT in libget.txt spec
  protected float[] holdOff = new float[0];

  //monitor numbers
  protected int[] monDetNums      = new int[0];
  protected int[] monPrescale     = new int[0];
  protected int[] spectrumNumbers = new int[0];

  //UT1/UTn in libget.txt spec
  protected float[][] userTable            = new float[0][0];
  protected float     L1;
  protected float     angleOfIncidence;
  protected float     beamapertureHoriz;
  protected float     beamapertureVert;
  protected float     chopFreq1;
  protected float     chopFreq2;
  protected float     chopFreq3;
  protected float     foeAngle;
  protected float     loqXCenter;
  protected float     loqYCenter;
  protected float     radiusBeamStop;
  protected float     rotorEnergy;
  protected float     rotorFrequency;
  protected float     rotorPhase;
  protected float     sourceToDetectorDist;
  protected int       apertureC1;
  protected int       apertureC2;
  protected int       apertureC3;
  protected int       beamStop;
  protected int       delayC1;
  protected int       delayC2;
  protected int       delayC3;
  protected int       detectorTankVacuum;
  protected int       mainShutter;
  protected int       maxErrorDelayC1;
  protected int       maxErrorDelayC2;
  protected int       maxErrorDelayC3;
  protected int       moderatorTypeNum;
  protected int       nDet;

  //number of detectors
  protected int nMon;
  protected int nUserTables;
  protected int rotorSlitPackage;
  protected int scatteringPosition;
  protected int slowChopper;
  protected int statusC1;
  protected int statusC2;
  protected int statusC3;
  protected int thermalShutter;
  protected int version;

  //~ Constructors *************************************************************

  /**
   * Creates a new InstrumentSection object.
   */
  InstrumentSection(  ) {}

  /**
   * Creates a new InstrumentSection object.
   *
   * @param rawFile The RAW file to use.
   * @param header The header for the RAW file.
   */
  InstrumentSection( RandomAccessFile rawFile, Header header ) {
    int startAddress = ( header.startAddressInst - 1 ) * 4;

    try {
      rawFile.seek( startAddress );
      version = header.readUnsignedInteger( rawFile, 4 );

      StringBuffer temp;

      temp = new StringBuffer( 8 );

      for( int ii = 0; ii < 8; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      iName                  = temp.toString(  );
      chopFreq1              = ( float )header.ReadVAXReal4( rawFile );
      chopFreq2              = ( float )header.ReadVAXReal4( rawFile );
      chopFreq3              = ( float )header.ReadVAXReal4( rawFile );
      delayC1                = header.readUnsignedInteger( rawFile, 4 );
      delayC2                = header.readUnsignedInteger( rawFile, 4 );
      delayC3                = header.readUnsignedInteger( rawFile, 4 );
      maxErrorDelayC1        = header.readUnsignedInteger( rawFile, 4 );
      maxErrorDelayC2        = header.readUnsignedInteger( rawFile, 4 );
      maxErrorDelayC3        = header.readUnsignedInteger( rawFile, 4 );
      apertureC1             = header.readUnsignedInteger( rawFile, 4 );
      apertureC2             = header.readUnsignedInteger( rawFile, 4 );
      apertureC3             = header.readUnsignedInteger( rawFile, 4 );
      statusC1               = header.readUnsignedInteger( rawFile, 4 );
      statusC2               = header.readUnsignedInteger( rawFile, 4 );
      statusC3               = header.readUnsignedInteger( rawFile, 4 );
      mainShutter            = header.readUnsignedInteger( rawFile, 4 );
      thermalShutter         = header.readUnsignedInteger( rawFile, 4 );
      beamapertureHoriz      = ( float )header.ReadVAXReal4( rawFile );
      beamapertureVert       = ( float )header.ReadVAXReal4( rawFile );
      scatteringPosition     = header.readUnsignedInteger( rawFile, 4 );
      moderatorTypeNum       = header.readUnsignedInteger( rawFile, 4 );
      detectorTankVacuum     = header.readUnsignedInteger( rawFile, 4 );
      L1                     = ( float )header.ReadVAXReal4( rawFile );
      rotorFrequency         = ( float )header.ReadVAXReal4( rawFile );
      rotorEnergy            = ( float )header.ReadVAXReal4( rawFile );
      rotorPhase             = ( float )header.ReadVAXReal4( rawFile );
      rotorSlitPackage       = header.readUnsignedInteger( rawFile, 4 );
      slowChopper            = header.readUnsignedInteger( rawFile, 4 );
      loqXCenter             = ( float )header.ReadVAXReal4( rawFile );
      loqYCenter             = ( float )header.ReadVAXReal4( rawFile );
      beamStop               = header.readUnsignedInteger( rawFile, 4 );
      radiusBeamStop         = ( float )header.ReadVAXReal4( rawFile );
      sourceToDetectorDist   = ( float )header.ReadVAXReal4( rawFile );
      foeAngle               = ( float )header.ReadVAXReal4( rawFile );
      angleOfIncidence       = ( float )header.ReadVAXReal4( rawFile );

      //skip around the spare section
      rawFile.seek( startAddress + ( 67 * 4 ) );

      //keep reading
      nDet                     = header.readUnsignedInteger( rawFile, 4 );
      nMon                     = header.readUnsignedInteger( rawFile, 4 );
      nUserTables              = header.readUnsignedInteger( rawFile, 4 );
      monDetNums               = new int[nMon];

      for( int ii = 0; ii < nMon; ii++ ) {
        monDetNums[ii] = header.readUnsignedInteger( rawFile, 4 );
      }

      monPrescale = new int[nMon];

      for( int ii = 0; ii < nMon; ii++ ) {
        monPrescale[ii] = header.readUnsignedInteger( rawFile, 4 );
      }

      spectrumNumbers = new int[nDet + 1];

      for( int ii = 1; ii <= nDet; ii++ ) {
        spectrumNumbers[ii] = header.readUnsignedInteger( rawFile, 4 );
      }

      holdOff = new float[nDet + 1];

      for( int ii = 1; ii <= nDet; ii++ ) {
        holdOff[ii] = ( float )header.ReadVAXReal4( rawFile );
      }

      flightPath = new float[nDet + 1];

      for( int ii = 1; ii <= nDet; ii++ ) {
        flightPath[ii] = ( float )header.ReadVAXReal4( rawFile );
      }

      codeForUserTableValues = new int[nDet + 1];

      for( int ii = 1; ii <= nDet; ii++ ) {
        codeForUserTableValues[ii] = header.readUnsignedInteger( rawFile, 4 );
      }

      detectorAngle = new float[nDet + 1];

      for( int ii = 1; ii <= nDet; ii++ ) {
        detectorAngle[ii] = ( float )header.ReadVAXReal4( rawFile );
      }

      userTable = new float[nUserTables][nDet + 1];

      for( int jj = 0; jj < nUserTables; jj++ ) {
        for( int ii = 1; ii <= nDet; ii++ ) {
          userTable[jj][ii] = ( float )header.ReadVAXReal4( rawFile );
        }
      }
    } catch( IOException ex ) {}

    //looks complete based on libget.txt
  }

  //~ Methods ******************************************************************

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
      System.out.println( "detectorTankVacuum: " + is.detectorTankVacuum );*/
      System.out.println( "L1: " + is.L1 );/*
      System.out.println( "rotorFrequency: " + is.rotorFrequency );
      System.out.println( "rotorEnergy: " + is.rotorEnergy );
      System.out.println( "rotorPhase: " + is.rotorPhase );
      System.out.println( "rotorSlitPackage: " + is.rotorSlitPackage );
      System.out.println( "slowChopper: " + is.slowChopper );*/
      System.out.println( "loqXCenter: " + is.loqXCenter );
      System.out.println( "loqYCenter: " + is.loqYCenter );/*
      System.out.println( "beamStop: " + is.beamStop );
      System.out.println( "radiusBeamStop: " + is.radiusBeamStop );*/
      System.out.println( "sourceToDetectorDist: " + is.sourceToDetectorDist );
      System.out.println( "foeAngle: " + is.foeAngle );
      System.out.println( "angleOfIncidence: " + is.angleOfIncidence );
      System.out.println( "nDet: " + is.nDet );
      System.out.println( "nMon: " + is.nMon );/*
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

      System.out.println(  );
      System.out.println( "spectrumNumbers: " );

      for( int ii = 1; ii <= is.nDet; ii++ ) {
        System.out.println( is.spectrumNumbers[ii] + "  " );
      }

      System.out.println(  );
      System.out.println( "holdOff: " );

      for( int ii = 1; ii <= is.nDet; ii++ ) {
        System.out.print( is.holdOff[ii] + "  " );
      }
*/
      System.out.println(  );
      System.out.println( "flightPath: " );

      for( int ii = 1; ii <= 30; ii++ ) {
        System.out.println( is.flightPath[ii] + "  " );
      }
/*
      System.out.println(  );
      System.out.println( "codeforUserTableValues: " );

      for( int ii = 1; ii <= is.nDet; ii++ ) {
        System.out.print( is.codeForUserTableValues[ii] + "  " );
      }
*/
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
   * @param detector number
   * 
   * @return The detector angle for the specified detector.
   */
  public float getDetectorAngle( int index ) {
    return detectorAngle[index];
  }

  /**
   * @param fs
   */
  public void setDetectorAngle(float[] fs) {
    detectorAngle = fs;
  }

}
