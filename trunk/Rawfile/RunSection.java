package ISIS.Rawfile;

import java.io.*;


/**
 * Class to retrieve run section information from a RAW file.
 *
 * @author J.P. Hammonds Intense Pulsed Neutron Source Argonne National
 *         Laboratory
 */
public class RunSection {
  //~ Instance fields **********************************************************

  protected String finishDate           = new String( "" );
  protected String finishTime           = new String( "" );
  protected String runTitle             = new String( "" );
  protected String userInstitution;
  protected String userName;
  protected String userPhone1;
  protected String userPhone2;
  protected String userPhone3;
  protected float  goodProtonCharge;
  protected float  totalProtonCharge;
  protected int    actualRunDuration;
  protected int    actualRunDurationSec;
  protected int    dumpInterval;
  protected int    monitorSum1;
  protected int    monitorSum2;
  protected int    monitorSum3;
  protected int    numberOfGoodFrames;
  protected int    ralProposalNum;
  protected int    requiredRunDuration;
  protected int    runNumber;
  protected int    scalerForRPB1;
  protected int    scalerForRPB4;
  protected int    testInterval2;
  protected int    testInterval5;
  protected int    totalNumberOfFrames;
  protected int    twobyk;
  protected int    version;

  //~ Constructors *************************************************************

  /**
   * Creates a new RunSection object.
   */
  RunSection(  ) {}

  /**
   * Creates a new RunSection object.
   *
   * @param rawFile DOCUMENT ME!
   * @param header DOCUMENT ME!
   */
  RunSection( RandomAccessFile rawFile, Header header ) {
    int startAddress = ( header.startAddressRun - 1 ) * 4;

    try {
      rawFile.seek( startAddress );
      version           = header.readUnsignedInteger( rawFile, 4 );
      runNumber         = header.readUnsignedInteger( rawFile, 4 );

      StringBuffer temp;

      temp = new StringBuffer( 80 );

      for( int ii = 0; ii < 80; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      runTitle          = temp.toString(  );
      temp              = new StringBuffer( 20 );

      for( int ii = 0; ii < 20; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      userName          = temp.toString(  );
      temp              = new StringBuffer( 20 );

      for( int ii = 0; ii < 20; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      userPhone1        = temp.toString(  );
      temp              = new StringBuffer( 20 );

      for( int ii = 0; ii < 20; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      userPhone2        = temp.toString(  );
      temp              = new StringBuffer( 20 );

      for( int ii = 0; ii < 20; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      userPhone3        = temp.toString(  );
      temp              = new StringBuffer( 20 );

      for( int ii = 0; ii < 20; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      userInstitution   = temp.toString(  );
      temp              = new StringBuffer( 60 );

      //spare section
      for( int ii = 0; ii < 60; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      actualRunDuration      = header.readUnsignedInteger( rawFile, 4 );
      scalerForRPB1          = header.readUnsignedInteger( rawFile, 4 );
      testInterval2          = header.readUnsignedInteger( rawFile, 4 );
      dumpInterval           = header.readUnsignedInteger( rawFile, 4 );
      scalerForRPB4          = header.readUnsignedInteger( rawFile, 4 );
      testInterval5          = header.readUnsignedInteger( rawFile, 4 );

      // 2**k (SNS frequency(Hz)=50/2**k)
      twobyk                 = header.readUnsignedInteger( rawFile, 4 );
      goodProtonCharge       = ( float )header.ReadVAXReal4( rawFile );
      totalProtonCharge      = ( float )header.ReadVAXReal4( rawFile );
      numberOfGoodFrames     = header.readUnsignedInteger( rawFile, 4 );
      totalNumberOfFrames    = header.readUnsignedInteger( rawFile, 4 );
      requiredRunDuration    = header.readUnsignedInteger( rawFile, 4 );
      actualRunDurationSec   = header.readUnsignedInteger( rawFile, 4 );
      monitorSum1            = header.readUnsignedInteger( rawFile, 4 );
      monitorSum2            = header.readUnsignedInteger( rawFile, 4 );
      monitorSum3            = header.readUnsignedInteger( rawFile, 4 );
      temp                   = new StringBuffer( 12 );

      for( int ii = 0; ii < 12; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      finishDate       = temp.toString(  );
      temp             = new StringBuffer( 8 );

      for( int ii = 0; ii < 8; ii++ ) {
        temp.append( ( char )rawFile.readByte(  ) );
      }

      finishTime       = temp.toString(  );
      ralProposalNum   = header.readUnsignedInteger( rawFile, 4 );

      //complete except for missing spare section (RPB(-32)) ?
    } catch( IOException ex ) {}
  }

  //~ Methods ******************************************************************

  /**
   * Testbed
   */
  public static void main( String[] args ) {
    try {
      RandomAccessFile rawFile = new RandomAccessFile( args[0], "r" );
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
    } catch( IOException ex ) {}
  }
}
