package ISIS.Rawfile;

import java.io.*;


/**
 * This class processes DAE information from an ISIS RAW file.
 *
 * @author J.P. Hammonds Intense Pulsed Neutron Source Argonne National
 *         Laboratory
 */
public class DaeSection {
  //~ Instance fields **********************************************************

  protected int[] crateNum               = new int[0];
  protected int[] inputNum               = new int[0];
  protected int[] moduleNum              = new int[0];
  protected int[] timeRegimeTable        = new int[0];
  protected int[] userDetectorNumber     = new int[0];
  protected int   crateMon1;
  protected int   crateMon2;
  protected int   detectorMon1;
  protected int   detectorMon2;
  protected int   extNeutGateT1;
  protected int   extNeutGateT2;
  protected int   externalVeto1;
  protected int   externalVeto2;
  protected int   externalVeto3;
  protected int   frameSyncDelay;
  protected int   frameSyncOrigin;
  protected int   goodExtNeutTotalHigh32;
  protected int   goodExtNeutTotalLow32;
  protected int   goodPppTotalHigh32;
  protected int   goodPppTotalLow32;
  protected int   lengthOfBulkStore;
  protected int   maskMon1;
  protected int   maskMon2;
  protected int   moduleMon1;
  protected int   moduleMon2;
  protected int   pppMinValue;
  protected int   rawExtNeutTotalHigh32;
  protected int   rawExtNeutTotalLow32;
  protected int   rawPppTotalHigh32;
  protected int   rawPppTotalLow32;
  protected int   secondaryMasterPulse;
  protected int   totalGoodEventsHigh32;
  protected int   totalGoodEventsLow32;
  protected int   version;
  protected int   wordLength;

  //~ Constructors *************************************************************

  /**
   * Creates a new DaeSection object.
   */
  DaeSection(  ) {}

  /**
   * Creates a new DaeSection object.
   *
   * @param rawFile The RAW file.
   * @param header The header for the RAW file.
   * @param nDet The number of detectors.
   */
  DaeSection( RandomAccessFile rawFile, Header header, int nDet ) {
    int startAddress = ( header.startAddressDae - 1 ) * 4;

    try {
      rawFile.seek( startAddress );
      version                  = header.readUnsignedInteger( rawFile, 4 );
      wordLength               = header.readUnsignedInteger( rawFile, 4 );
      lengthOfBulkStore        = header.readUnsignedInteger( rawFile, 4 );
      pppMinValue              = header.readUnsignedInteger( rawFile, 4 );
      goodPppTotalHigh32       = header.readUnsignedInteger( rawFile, 4 );
      goodPppTotalLow32        = header.readUnsignedInteger( rawFile, 4 );
      rawPppTotalHigh32        = header.readUnsignedInteger( rawFile, 4 );
      rawPppTotalLow32         = header.readUnsignedInteger( rawFile, 4 );
      goodExtNeutTotalHigh32   = header.readUnsignedInteger( rawFile, 4 );
      goodExtNeutTotalLow32    = header.readUnsignedInteger( rawFile, 4 );
      rawExtNeutTotalHigh32    = header.readUnsignedInteger( rawFile, 4 );
      rawExtNeutTotalLow32     = header.readUnsignedInteger( rawFile, 4 );
      extNeutGateT1            = header.readUnsignedInteger( rawFile, 4 );
      extNeutGateT2            = header.readUnsignedInteger( rawFile, 4 );
      detectorMon1             = header.readUnsignedInteger( rawFile, 4 );
      moduleMon1               = header.readUnsignedInteger( rawFile, 4 );
      crateMon1                = header.readUnsignedInteger( rawFile, 4 );
      maskMon1                 = header.readUnsignedInteger( rawFile, 4 );
      detectorMon2             = header.readUnsignedInteger( rawFile, 4 );
      moduleMon2               = header.readUnsignedInteger( rawFile, 4 );
      crateMon2                = header.readUnsignedInteger( rawFile, 4 );
      maskMon2                 = header.readUnsignedInteger( rawFile, 4 );
      totalGoodEventsHigh32    = header.readUnsignedInteger( rawFile, 4 );
      totalGoodEventsLow32     = header.readUnsignedInteger( rawFile, 4 );
      frameSyncDelay           = header.readUnsignedInteger( rawFile, 4 );
      frameSyncOrigin          = header.readUnsignedInteger( rawFile, 4 );
      secondaryMasterPulse     = header.readUnsignedInteger( rawFile, 4 );
      externalVeto1            = header.readUnsignedInteger( rawFile, 4 );
      externalVeto2            = header.readUnsignedInteger( rawFile, 4 );
      externalVeto3            = header.readUnsignedInteger( rawFile, 4 );

      //skip over the spare space
      rawFile.seek( startAddress + ( 64 * 4 ) );

      //keep reading
      crateNum             = new int[nDet + 1];
      moduleNum            = new int[nDet + 1];
      inputNum             = new int[nDet + 1];
      timeRegimeTable      = new int[nDet + 1];
      userDetectorNumber   = new int[nDet + 1];

      for( int ii = 1; ii <= nDet; ii++ ) {
        crateNum[ii] = header.readUnsignedInteger( rawFile, 4 );
      }

      for( int ii = 1; ii <= nDet; ii++ ) {
        moduleNum[ii] = header.readUnsignedInteger( rawFile, 4 );
      }

      for( int ii = 1; ii <= nDet; ii++ ) {
        inputNum[ii] = header.readUnsignedInteger( rawFile, 4 );
      }

      for( int ii = 1; ii <= nDet; ii++ ) {
        timeRegimeTable[ii] = header.readUnsignedInteger( rawFile, 4 );
      }

      for( int ii = 1; ii <= nDet; ii++ ) {
        userDetectorNumber[ii] = header.readUnsignedInteger( rawFile, 4 );
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
      DaeSection        ds     = new DaeSection( rawFile, header, is.nDet );

      System.out.println( "versionNumber:        " + ds.version );
      System.out.println( "wordLength:           " + ds.wordLength );
      System.out.println( "lengthOfBulkStore:    " + ds.lengthOfBulkStore );
      System.out.println( "pppMinValue:          " + ds.pppMinValue );
      System.out.println( "goodPppTotalHigh32:   " + ds.goodPppTotalHigh32 );
      System.out.println( "goodPppTotalLow32:   " + ds.goodPppTotalLow32 );
      System.out.println( "rawPppTotalHigh32:   " + ds.rawPppTotalHigh32 );
      System.out.println( "rawPppTotalLow32:   " + ds.rawPppTotalLow32 );
      System.out.println( 
        "goodExtNeutTotalHigh32:   " + ds.goodExtNeutTotalHigh32 );
      System.out.println( 
        "goodExtNeutTotalLow32:   " + ds.goodExtNeutTotalLow32 );
      System.out.println( 
        "rawExtNeutTotalHigh32:   " + ds.rawExtNeutTotalHigh32 );
      System.out.println( "rawExtNeutTotalLow32:   " + ds.rawExtNeutTotalLow32 );
      System.out.println( "extNeutGateT1:          " + ds.extNeutGateT1 );
      System.out.println( "extNeutGateT2:          " + ds.extNeutGateT2 );
      System.out.println( "detectorMon1:           " + ds.detectorMon1 );
      System.out.println( "moduleMon1:             " + ds.moduleMon1 );
      System.out.println( "crateMon1:              " + ds.crateMon1 );
      System.out.println( "maskMon1:               " + ds.maskMon1 );
      System.out.println( "detectorMon2:           " + ds.detectorMon2 );
      System.out.println( "moduleMon2:             " + ds.moduleMon2 );
      System.out.println( "crateMon2:              " + ds.crateMon2 );
      System.out.println( "maskMon2:               " + ds.maskMon2 );
      System.out.println( 
        "totalGoodEventsHigh32:  " + ds.totalGoodEventsHigh32 );
      System.out.println( "totalGoodEventsLow32:   " + ds.totalGoodEventsLow32 );
      System.out.println( "frameSyncDelay:         " + ds.frameSyncDelay );
      System.out.println( "frameSyncOrigin:         " + ds.frameSyncOrigin );
      System.out.println( 
        "secondaryMasterPulse:    " + ds.secondaryMasterPulse );
      System.out.println( "externalVeto1:           " + ds.externalVeto1 );
      System.out.println( "externalVeto2:           " + ds.externalVeto2 );
      System.out.println( "externalVeto3:           " + ds.externalVeto3 );
      System.out.println( 
        "Detector   Crate   Module  Input   timeRegime  userDetectorNum" );

      for( int ii = 1; ii <= is.nDet; ii++ ) {
        System.out.println( 
          "  " + ii + "          " + ds.crateNum[ii] + "       " +
          ds.moduleNum[ii] + "      " + ds.inputNum[ii] + "          " +
          ds.timeRegimeTable[ii] + "           " + ds.userDetectorNumber[ii] );
      }
    } catch( IOException ex ) {}
  }
}
