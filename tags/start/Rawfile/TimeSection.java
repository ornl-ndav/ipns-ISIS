package ISIS.Rawfile;

import java.io.*;


/**
 * Class to read from the time channel boundaries (TCB) section of an ISIS RAW
 * file.
 *
 * @author J.P. Hammonds Intense Pulsed Neutron Source Argonne National
 *         Laboratory
 */
public class TimeSection {
  //~ Static fields/initializers ***********************************************

  protected static final int PMAP_SIZE = 256;

  //~ Instance fields **********************************************************

  protected int         version;
  protected int         numOfRegimes;
  protected int         numOfFramesPerPeriod;
  protected int         numOfPeriods;
  protected int[]       periodMap             = new int[PMAP_SIZE];
  protected int[]       numSpectra            = new int[0];
  protected int[]       numTimeChannels       = new int[0];
  protected int[][]     timeChannelMode       = new int[0][0];
  protected float[][][] timeChannelParameters = new float[0][0][0];
  protected int[]       clockPrescale         = new int[0];
  protected int[][]     timeChannelBoundaries = new int[0][0];

  //~ Constructors *************************************************************

  /**
   * Creates a new TimeSection object.
   */
  TimeSection(  ) {}

  /**
   * Creates a new TimeSection object.
   *
   * @param rawFile The RAW file.
   * @param header The header for the RAW file.
   */
  TimeSection( RandomAccessFile rawFile, Header header ) {
    int startAddress = ( header.startAddressTcb - 1 ) * 4;

    try {
      rawFile.seek( startAddress );
      version                 = header.readUnsignedInteger( rawFile, 4 );
      numOfRegimes            = header.readUnsignedInteger( rawFile, 4 );
      numOfFramesPerPeriod    = header.readUnsignedInteger( rawFile, 4 );
      numOfPeriods            = header.readUnsignedInteger( rawFile, 4 );

      for( int ii = 0; ii < PMAP_SIZE; ii++ ) {
        periodMap[ii] = header.readUnsignedInteger( rawFile, 4 );
      }

      numSpectra              = new int[numOfRegimes];
      numTimeChannels         = new int[numOfRegimes];
      timeChannelMode         = new int[numOfRegimes][5];
      timeChannelParameters   = new float[numOfRegimes][4][5];
      clockPrescale           = new int[numOfRegimes];
      timeChannelBoundaries   = new int[numOfRegimes][];

      for( int ii = 0; ii < numOfRegimes; ii++ ) {
        numSpectra[ii]              = header.readUnsignedInteger( rawFile, 4 );
        numTimeChannels[ii]         = header.readUnsignedInteger( rawFile, 4 );
        System.out.println(" ii, num channels = " + ii + ", " + numTimeChannels[ii] );
        for( int jj = 0; jj < 5; jj++ ) {
          timeChannelMode[ii][jj] = header.readUnsignedInteger( rawFile, 4 );
        }

        for( int jj = 0; jj < 4; jj++ ) {
          for( int kk = 0; kk < 5; kk++ ) {
            timeChannelParameters[ii][jj][kk] = ( float )header.ReadVAXReal4( 
                rawFile );
          }
        }

        clockPrescale[ii]           = header.readUnsignedInteger( rawFile, 4 );
        timeChannelBoundaries[ii]   = new int[numTimeChannels[ii] + 1];
      }

      for( int ii = 0; ii < numOfRegimes; ii++ ) {
        for( int jj = 0; jj < ( numTimeChannels[ii] + 1 ); jj++ ) {
          timeChannelBoundaries[ii][jj] = header.readUnsignedInteger( 
              rawFile, 4 );
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
      RandomAccessFile rawFile = new RandomAccessFile( args[0], "r" );
      Header           header = new Header( rawFile );
      TimeSection      ts     = new TimeSection( rawFile, header );

      /*System.out.println( "version: " + ts.version );
      System.out.println( "numOfRegimes:  " + ts.numOfRegimes );
      System.out.println( "numOfFramesPerPeriod:  " + ts.numOfFramesPerPeriod );
      System.out.println( "numOfPeriods: " + ts.numOfPeriods );
      System.out.println( "periodMap: " );

      for( int ii = 0; ii < 256; ii++ ) {
        System.out.print( ts.periodMap[ii] + "  " );
      }

      System.out.println(  );

      for( int ii = 0; ii < ts.numOfRegimes; ii++ ) {
        System.out.println( "-Regime " + ii );
        System.out.println( "---numSpectra:       " + ts.numSpectra[ii] );
        System.out.println( "---numTimeChannels:  " + ts.numTimeChannels[ii] );
        System.out.println( "---timeChannelMode:  " );

        for( int jj = 0; jj < 5; jj++ ) {
          System.out.print( ts.timeChannelMode[ii][jj] + "   " );
        }

        System.out.println(  );
        System.out.println( "---timeChannelParameters:" );

        for( int jj = 0; jj < 4; jj++ ) {
          System.out.print( jj + "-- " );

          for( int kk = 0; kk < 5; kk++ ) {
            System.out.print( ts.timeChannelParameters[ii][jj][kk] + "  " );
          }

          System.out.println(  );
        }

        System.out.println( "---clockPrescale:    " + ts.clockPrescale[ii] );
        System.out.println( "---timeChannelBoundaries:" );

        for( int jj = 0; jj < ( ts.numTimeChannels[ii] + 1 ); jj++ ) {
          System.out.print( ts.timeChannelBoundaries[ii][jj] + "   " );
        }
      }*/
    } catch( IOException ex ) {}
  }
}
