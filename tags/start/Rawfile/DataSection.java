package ISIS.Rawfile;

import java.io.*;


/**
 * Class to retrieve data from an ISIS rawfile data section.
 *
 * @author J.P. Hammonds Intense Pulsed Neutron Source Argonne National
 *         Laboratory
 */
public class DataSection {
  //~ Instance fields **********************************************************

  protected int version;

  //version 1

  // For version 2 compression
  //0 = no compression, 1 = byte relative compression 
  protected int   compressionType;
  protected int   reserved;
  protected int   offsetToSpectrumDescArray;
  protected int   equivV1FileSize;
  protected float compRatioDataSect;
  protected float compRatioWholeFile;
  protected int   nspec;
  protected int[] spectrumDescArray = new int[0];
  protected int startAddress = 0;
  protected int realDataAddress = 0;

  //~ Constructors *************************************************************

  /**
   * Creates a new DataSection object.
   */
  DataSection(  ) {}

  /**
   * Creates a new DataSection object.
   *
   * @param rawFile The RAW file.
   * @param header The header for the RAW file.
   * @param ts The time section for the RAW file.
   */
  DataSection( RandomAccessFile rawFile, Header header, TimeSection ts ) {
    startAddress = ( header.startAddressData - 1 ) * 4;

    try {
      rawFile.seek( startAddress );
      version = header.readUnsignedInteger( rawFile, 4 );

      if( version == 1 ) {
        int dataFormat = header.dataFormatFlag;

        //read raw data for the time regimes: (ntc1+1)*(nsp1+1) channels
        //If data format flag=0 then data is arranged (ntc1+1,nsp1+1); 
        //i.e. (nsp1 spectra each containing ntc1 data points)
        //If data format flag=1, then data is arranged (nsp1+1,ntc1+1);
        //i.e (for each time channel all spectra stored together)
        if( dataFormat == 0 ) {}
      } else if( version == 2 ) {
        compressionType             = header.readUnsignedInteger( rawFile, 4 );
        reserved                    = header.readUnsignedInteger( rawFile, 4 );
        offsetToSpectrumDescArray   = header.readUnsignedInteger( rawFile, 4 );
        equivV1FileSize             = header.readUnsignedInteger( rawFile, 4 );
        compRatioDataSect           = ( float )header.ReadVAXReal4( rawFile );
        compRatioWholeFile          = ( float )header.ReadVAXReal4( rawFile );
        nspec                       = 0;

        for( int ii = 0; ii < ts.numOfRegimes; ii++ ) {
          nspec += ts.numSpectra[ii];
        }

        spectrumDescArray = new int[( 2 * nspec ) + 1];
        rawFile.seek( startAddress + ( offsetToSpectrumDescArray * 4 ) );

        for( int ii = 0; ii < nspec; ii++ ) {
          spectrumDescArray[( 2 * ii )]       = header.readUnsignedInteger( 
              rawFile, 4 );
          spectrumDescArray[( 2 * ii ) + 1]   = header.readUnsignedInteger( 
              rawFile, 4 );
        }
        
        realDataAddress = (int)rawFile.getFilePointer();
      }
    } catch( IOException ex ) {}
  }

  //~ Methods ******************************************************************

  /**
   * Takes a four element byte array and converts it to a 32 bit integer, using
   * bTemp[0] as the first byte of the integer in big-endian style.
   *
   * @param bTemp The byte array.
   *
   * @return The integer.
   */
//  private static int MSBByteArrayToInt32( byte[] bTemp ) {
//    if( bTemp.length != 4 ) {
//      throw new IllegalArgumentException( 
//        "Byte array must have length 4: byteArrayToInt" );
//    }
//
//    return ( ( ( bTemp[0] << 24 ) & 0xff000000 ) |
//    ( ( bTemp[1] << 16 ) & 0x00ff0000 ) | ( ( bTemp[2] << 8 ) & 0x0000ff00 ) |
//    ( bTemp[3] & 0x000000ff ) );
//  }

  /**
   * Converts a byte array representing a little endian integer 
   * (byte[0] = least significant byte) to a 32 bit big endian integer.
   * This is taken from Rawfile.java's readUnsignedInteger.
   * 
   * @param b The byte array holding the 4 bytes representing the 
   * integer
   * @return The converted 32 bit integer.
   * @throws IllegalArgumentException If the byte array is not of size 4.
   */  
  private int convertLSBIntToMSBInt( byte[] b )
    throws IllegalArgumentException {
      
    if( b.length != 4 ) {
       throw new IllegalArgumentException( 
        "Byte array must have length 4: byteArrayToInt" );
    }
    int[]  c          = new int[b.length];

    int num = 0;

    for( int i = 0; i < b.length; ++i ) {
      if( b[i] < 0 ) {
        c[i] = b[i] + 256;
      } else {
        c[i] = b[i];
      }

      num += ( c[i] * ( int )Math.pow( 256.0, ( double )i ) );
    }

    return num;
  }


  /**
   * Converts a VAX integer to local (big-endian) integer.
   *
   * @param VAXInt The VAX integer to convert.
   *
   * @return The converted integer.
   */
//  private static int convertVAXToLocal( int VAXInt ) {
//    //there are at least three ways to do this
//    //#1 is the only extensively tested way
//    //#1
//    return swapBytes( VAXInt );
//    //#2-Original DataSection way
//
//    /*byte[] b = new byte[4];
//    int32ToByteArray( VAXInt, b );
//    int[] c   = new int[4];
//       int   num = 0;
//       for( int i = 0; i < 4; ++i ) {
//         if( b[i] < 0 ) {
//           c[i] = b[i] + 256;
//         } else {
//           c[i] = b[i];
//         }
//         num += ( c[i] * ( int )Math.pow( 256.0, ( double )i ) );
//       }
//       return num;*/
//
//    //#3 C code way from libget
//
//    /*int a = VAXInt;
//       return ( ( a << 24 ) |
//       ( ( a << 8 ) & 0x00ff0000 ) |
//       ( ( a >> 8 ) & 0x0000ff00 ) |
//       ( a >> 24 ) );*/
//  }

  /**
   * Returns spectrum from a rawfile.  Note that the time channels here are 
   * not exactly specified correctly for all rawfiles.
   *
   * @param rawFile The rawfile to use.
   * @param spect The spectrum number.
   * @param ts The time section to use.
   *
   * @return The spectrum in a float array.
   */
  public float[] get1DSpectrum( 
    RandomAccessFile rawFile, int spect, TimeSection ts ) {
    //get the compressed data
    try {
      if( compressionType == 1 ) {
        //byte relative compression
        //need total bytes for all spectra-read and uncompress
        int    numWords  = spectrumDescArray[2 * spect];
         
        byte[] compBytes = new byte[500];
 
        //read the bytes from the file
        int myStartAddress = 761656;  //= realDataAddress + 1000; is this standard?
        rawFile.seek( myStartAddress + ( spect * 104 ) );
        
        rawFile.read( compBytes );

        //size of int array
        //this (below) came from io.f-I am not sure what they were doing, 
        //but it seems that it would leave data behind
        //( ( nspec - 1 ) / 4 ) + 1;
        int   size    = ts.numTimeChannels[0] + 1;
        int[] rawData = new int[size];

        byteRelExpn( compBytes, rawData );

        float[] data = new float[rawData.length];

        for( int k = 0; k < data.length; k++ ) {
          data[k] = rawData[k];
        }

        return data;
      }

      return null;
    } catch( IOException ioe ) {
      return null;
    }
  }

  /**
   * Takes a 32 bit integer and places its bytes into a 4 element byte array as
   * follows: <br>
   * bArr[0]   = ( byte )( ( someInt ) & 0x000000ff );<br>
   * bArr[1]   = ( byte )( ( someInt >> 8 ) & 0x000000ff );<br>
   * bArr[2]   = ( byte )( ( someInt >> 16 ) & 0x000000ff );<br>
   * bArr[3]   = ( byte )( ( someInt >> 24) & 0x000000ff );<br>
   *
   * @param someInt The integer.
   * @param bArr The byte array to use for storage.
   */
//  private static void LSBInt32ToByteArray( int someInt, byte[] bArr ) {
//    if( bArr.length != 4 ) {
//      throw new IllegalArgumentException( 
//        "Byte array must have length 4: byteArrayToInt" );
//    }
//
//    bArr[0]   = ( byte )( ( someInt ) & 0x000000ff );
//    bArr[1]   = ( byte )( ( someInt >> 8 ) & 0x000000ff );
//    bArr[2]   = ( byte )( ( someInt >> 16 ) & 0x000000ff );
//    bArr[3]   = ( byte )( ( someInt >> 24 ) & 0x000000ff );
//  }

  /**
   * Testbed
   */
  public static void main( String[] args ) {
    try {
      RandomAccessFile  rawFile = new RandomAccessFile( args[0], "r" );
      Header            header = new Header( rawFile );
      TimeSection       ts     = new TimeSection( rawFile, header );
      InstrumentSection is     = new InstrumentSection( rawFile, header );
      DataSection       ds     = new DataSection( rawFile, header, ts );
      /*System.out.println( "versionNumber:        " + ds.version );
         if( ds.version == 2 ) {
           System.out.println(
             "compressionType:            " + ds.compressionType );
           System.out.println(
             "offsetToSpectrumDescArray:  " + ds.offsetToSpectrumDescArray );
           System.out.println(
             "equivV1FileSize:            " + ds.equivV1FileSize );
           System.out.println(
             "compRatioDataSect:       " + ds.compRatioDataSect );
           System.out.println(
             "compRatioWholeFile:      " + ds.compRatioWholeFile );
           System.out.println( "nspec:                      " + ds.nspec );
           System.out.println( "nspec   numWords    offset" );
           for( int ii = 0; ii < ds.nspec; ii++ ) {
             System.out.println(
               ii + "       " + ds.spectrumDescArray[( 2 * ii ) + 1] + "       " +
               ds.spectrumDescArray[( 2 * ii ) + 2] );
           }
         }*/
    } catch( IOException ex ) {}
  }

  /**
   * Swaps the bytes in someInt.  Useful for converting between big-endian and
   * little-endian.
   *
   * @param someInt The integer to swap bytes for.
   *
   * @return The byte swapped integer.
   */
//  private static int swapBytes( int someInt ) {
//    byte[] b = new byte[4];
//
//    LSBInt32ToByteArray( someInt, b );
//
//    //test to see if we really handled the int correctly-this should come out zero
//
//    /*System.out.println(
//       someInt -
//       ( ( ( b[0] << 24 ) & 0xff000000 ) + ( ( b[1] << 16 ) & 0x00ff0000 ) +
//       ( ( b[2] << 8 ) & 0x0000ff00 ) + ( b[3] & 0x000000ff ) ) );*/
//    byte[] bTemp = new byte[4];
//
//    bTemp[0]   = b[3];
//    bTemp[1]   = b[2];
//    bTemp[2]   = b[1];
//    bTemp[3]   = b[0];
//
//    return MSBByteArrayToInt32( bTemp );
//  }

  /**
   * Expansion of byte-relative format into 32bit format Each integer is stored
   * relative to the previous value in byte form.The first is relative to
   * zero. This allows for numbers to be within + or - 127 of the previous
   * value. Where a 32bit integer cannot be expressed in this way a special
   * byte code is used (-128) and the full 32bit integer stored elsewhere. The
   * final space used is (NIN-1)/4 +1 + NEXTRA longwords, where NEXTRA is the
   * number of extra longwords used in giving absolute values. Status return<br>
   * =1  no problems!<br>
   * =3  NOUT .lt.NIN/5<br>
   * =2  NIN .le.0 =4  NOUT .gt.NIN =6  number of channels lt NOUT
   */
  private void byteRelExpn( byte[] inData, int[] outData ) {
    int    j;
    int    iTemp;
    byte[] bTemp = new byte[4];

    // Set initial absolute value to zero and channel counter to zero
    iTemp   = 0;
    j       = 0;
    

    // Loop over all expected 32bit integers
    for( int i = 0; i < outData.length; i++ ) {
      //System.out.println( "j " + j + " indata[j] " + inData[j] );
      // if number is contained in a byte
      if( inData[j] != -128 ) {
        // add in offset to base
        iTemp = iTemp + inData[j];
      } else {
        // Else skip marker and pick up new absolute value
        // unpack next 4 bytes
        bTemp[0]   = inData[j + 1];
        bTemp[1]   = inData[j + 2];
        bTemp[2]   = inData[j + 3];
        bTemp[3]   = inData[j + 4];

        //seem to produce same result
        //iTemp      = byteArrayToInt32( bTemp );
        //iTemp = NetComm.ByteConvert.toInt( bTemp, 0 );
        iTemp      = convertLSBIntToMSBInt( bTemp );
        j          = j + 4;
      }

      // update current value
      outData[i] = iTemp;
      j++;
    }
  }

}
