/*
 * File:  DataSection.java
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
 */

package ISIS.Rawfile;

import java.io.*;


/**
 * Class to retrieve data from an ISIS rawfile data section.
 *
 * @author J.P. Hammonds Intense Pulsed Neutron Source Argonne National
 *         Laboratory
 */
public class DataSection {
  //~ Instance fields ----------------------------------------------------------

  /**
   * Data version number (either 1 or 2).
   */
  protected int version;

  //version 1
  // For version 2 compression
  //0 = no compression, 1 = byte relative compression 
  /**
   * Compression type.  Possible values:<br>
   * 0 = no compression
   * 1 =byte relative compression.
   */
  protected int   compressionType;
  /** Reserved. */
  protected int   reserved;
  /** Offset to Spectrum Description Array. */
  protected int   offsetToSpectrumDescArray;
  /** Equivalent version 1 filesize (blocks). */
  protected int   equivV1FileSize;
  /** Compression Ration for data section. */
  protected float compRatioDataSect;
  /** Compression Ratio for whole file. */
  protected float compRatioWholeFile;
  /**
   * The total of all spectra in all time regimes 
   * in all periods.
   */
  protected int   nspec;
  /**
   * The Spectrum Descriptor Array.  The elements in this array are:<br>
   * Number of words in compressed spectrum 1<br>
   * Offset to compressed spectrum 1<br>
   * Number of words in compressed spectrum 2<br>
   * Offset to compressed spectrum 2<br>
   * Number of words in compressed spectrum 3<br>
   * Offset to compressed spectrum 3<br>
   * . . . . <br>
   * The length of this array is 2*nspec+1.
   */
  protected int[] spectrumDescArray = new int[0];
  /** The offset in the file where the data for this section starts. */
  protected int   startAddress      = 0;
  /**
   * The data format flag from the Header section.
   */
  protected int   dataFormat        = -1;

  //~ Constructors -------------------------------------------------------------

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
      version = Header.readUnsignedInteger( rawFile, 4 );

      if( version == 1 ) {
        //dealt with in Get1DSpectrum
        dataFormat = header.dataFormatFlag;
      } else if( version == 2 ) {
        compressionType             = Header.readUnsignedInteger( rawFile, 4 );
        reserved                    = Header.readUnsignedInteger( rawFile, 4 );
        offsetToSpectrumDescArray   = Header.readUnsignedInteger( rawFile, 4 );
        equivV1FileSize             = Header.readUnsignedInteger( rawFile, 4 );
        compRatioDataSect           = ( float )Header.ReadVAXReal4( rawFile );
        compRatioWholeFile          = ( float )Header.ReadVAXReal4( rawFile );
        nspec                       = 0;

        for( int ii = 0; ii < ts.numOfRegimes; ii++ ) {
          nspec += ts.numSpectra[ii];
        }

        spectrumDescArray = new int[( 2 * nspec ) + 1];
        rawFile.seek( startAddress + ( offsetToSpectrumDescArray * 4 ) );

        for( int ii = 0; ii < nspec; ii++ ) {
          spectrumDescArray[( 2 * ii )]   = Header.readUnsignedInteger( rawFile,
              4 );
          spectrumDescArray[( 2 * ii ) + 1] = Header.readUnsignedInteger( rawFile,
              4 );
        }
      }
    } catch( IOException ex ) {}
  }

  //~ Methods ------------------------------------------------------------------

  /*
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


/*
  //this was the old method for getting a specified spectrum
  public float[] get1DSpectrum( RandomAccessFile rawFile, int spect,
    TimeSection ts ) {
    //note: at some point, this should deal directly with float[] rather than converting
    //from int[] to float[]
    int     size;
    int[]   rawData;
    float[] data;
    
    System.out.println("version="+version);
	System.out.println("data format flag="+dataFormat);
	System.out.println("compression type="+compressionType);
    
    try {
      if( version == 1 ) {
        //read raw data for the time regimes: (ntc1+1)*(nsp1+1) channels
        //If data format flag=0 then data is arranged (ntc1+1,nsp1+1); 
        //i.e. (nsp1 spectra each containing ntc1 data points)
        //If data format flag=1, then data is arranged (nsp1+1,ntc1+1);
        //i.e (for each time channel all spectra stored together)
        if( dataFormat == 0 ) {
          rawFile.seek( startAddress +
            ( spect * ( ts.numTimeChannels[0] + 1 ) * 4 ) );
          size      = ts.numTimeChannels[0] + 1;
          rawData   = new int[size];
          data      = new float[rawData.length];

          byte[] num = new byte[4];

          for( int mm = 0; mm < size; mm++ ) {
            rawFile.read( num );
            rawData[mm] = convertLSBIntToMSBInt( num );
          }

          for( int jj = 0; jj < size; jj++ ) {
            data[jj] = rawData[jj];
          }

          return data;
        } else if( dataFormat == 1 ) {
          return null;
        } else {
          return null;
        }
      } else if( version == 2 ) {
        //get the compressed data
        if( compressionType == 1 ) {
          //byte relative compression
          //need total bytes for all spectra-read and uncompress
          int    numWords  = spectrumDescArray[2 * spect];
          byte[] compBytes = new byte[( numWords ) * 4];

          size      = ts.numTimeChannels[0] + 1;
          rawData   = new int[size];

          //read the bytes from the file
          rawFile.seek( startAddress +
            ( spectrumDescArray[( 2 * spect ) + 1] * 4 ) );
          rawFile.read( compBytes );
          byteRelExpn( compBytes, rawData );
          data = new float[rawData.length];

          for( int k = 0; k < data.length; k++ ) {
            data[k] = rawData[k];
          }

          return data;
        } else if( compressionType == 0 ) {
          return null;
        } else {
          return null;
        }
      } else {
        return null;
      }
    } catch( IOException ioe ) {
      return null;
    }
  }
*/

  /**
   * Returns spectrum from a rawfile.  Note that the time channels here are not
   * exactly specified correctly for all rawfiles.
   *
   * @param rawFile The rawfile to use.
   * @param spect The spectrum number.
   * @param ts The time section to use.
   *
   * @return The spectrum in a float array.
   */
  public float[] get1DSpectrum(RandomAccessFile rawFile, int spect, TimeSection ts)
  {
  	float[] result = null;
	try
	{
		if (version == 1)
		  result = getDataForDataVersion1(rawFile,spect,ts);
		else if (version == 2)
		  result = getDataForDataVersion2(rawFile,spect,ts);
	}
	catch(IOException e)
	{
		System.out.print(e);
		System.out.println(" has ocurred in get1DSpectrum(RandomAccessFile rawFile, int spect, TimeSection ts)");
	}
	
	return result;
  }
  
  private float[] getDataForDataVersion1(RandomAccessFile rawFile, int spect, TimeSection ts) throws IOException
  {
	if (dataFormat == 0)
	  return getDataForDataFormatFlag0(rawFile,spect,ts);
	else if (dataFormat == 1)
	  return getDataForDataFormatFlag1(rawFile,spect,ts);
	else
	  return null;
  }
    
	private float[] getDataForDataFormatFlag0(RandomAccessFile rawFile, int spect, TimeSection ts) throws IOException
	{
		//note: at some point, this should deal directly with float[] rather than converting
		//from int[] to float[]
		int     size;
		int[]   rawData;
		float[] data;
    	
		rawFile.seek( startAddress +
		  ( spect * ( ts.numTimeChannels[0] + 1 ) * 4 ) );
		size      = ts.numTimeChannels[0] + 1;
		rawData   = new int[size];
		data      = new float[rawData.length];

		byte[] num = new byte[4];

		for( int mm = 0; mm < size; mm++ )
		{
		  rawFile.read( num );
		  rawData[mm] = convertLSBIntToMSBInt( num );
		}

		for( int jj = 0; jj < size; jj++ )
		{
		  data[jj] = rawData[jj];
		}

		return data;
	}
    
    /**
     * Get the spectrum specified from the ISIS RAW file assuming that the data was 
     * written to the file with the data version number = 1 and the data format flag =1.
     * THIS METHOD NEEDS TO BE TESTED.  CURRENTLY, IT IS NOT 
     * GAURANTEED TO WORK.
     * @param rawFile Reader for the ISIS RAW file that is to be read.
     * @param spect The spectrum that is to be read.
     * @param ts The TimeSection of the ISIS RAW file that is to be read.
     * @return The spectrum.
     * @throws IOException
     */
	private float[] getDataForDataFormatFlag1(RandomAccessFile rawFile, int spect, TimeSection ts) throws IOException
	{
	    boolean sectionFound = false;
	    int actualSpectraNumber = spect+1;
	    int regimeNumber = 0;
	    int total = 0;
	    int previousTotal = total;
	    int spectraNumberInRegime = 0;
	    while (!sectionFound && regimeNumber<ts.numSpectra.length)
	    {
	    	previousTotal = total;
	    	total += ts.numSpectra[regimeNumber]+1;
	    	if (total>=actualSpectraNumber)
	    	{
	    		sectionFound = true;
	    		spectraNumberInRegime = actualSpectraNumber-previousTotal-1;
	    	}
	    	else
	    		regimeNumber++;
	    }
	    if (sectionFound)	    	
		{
			//the spectra that is to be found is "spectraNumberInRegime" in regime "regimeNumber"
			int offset = 0;
			for (int i=0; i<regimeNumber; i++)
			   offset += (ts.numSpectra[i]+1)*(ts.numTimeChannels[i]+1)*4;
			
			//skip ahead to the regime
			rawFile.seek(startAddress+offset);
			int skipAhead = (ts.numSpectra[regimeNumber]+1-spectraNumberInRegime-1)*4;
			float[] data = new float[ts.numTimeChannels[regimeNumber]+1];
			for (int i=0; i<data.length; i++)
			{
			   rawFile.seek(rawFile.getFilePointer()+spectraNumberInRegime*4);
			   data[i] = Header.readUnsignedInteger(rawFile,4);
			   rawFile.seek(rawFile.getFilePointer()+skipAhead);
			}
			return data;
		}
		else
		   return null;
	}
  
  private float[] getDataForDataVersion2(RandomAccessFile rawFile, int spect, TimeSection ts) throws IOException
  {
	if (compressionType == 0)
	  return getDataForCompressionType0(rawFile,spect,ts);
	else if (compressionType == 1)
	  return getDataForCompressionType1(rawFile,spect,ts);
	else
	  return null;
  }
    
   private float[] getDataForCompressionType0(RandomAccessFile rawFile, int spect, TimeSection ts) throws IOException
   {
   	   return null;
   }
    
	private float[] getDataForCompressionType1(RandomAccessFile rawFile, int spect, TimeSection ts) throws IOException
	{
		//note: at some point, this should deal directly with float[] rather than converting
		//from int[] to float[]
		int     size;
		int[]   rawData;
		float[] data;

		//byte relative compression
		//need total bytes for all spectra-read and uncompress
		int    numWords  = spectrumDescArray[2 * spect];
		byte[] compBytes = new byte[( numWords ) * 4];

		size      = ts.numTimeChannels[0] + 1;
		rawData   = new int[size];

		//read the bytes from the file
		rawFile.seek( startAddress +
		  ( spectrumDescArray[( 2 * spect ) + 1] * 4 ) );
		rawFile.read( compBytes );
		byteRelExpn( compBytes, rawData );
		data = new float[rawData.length];

		for( int k = 0; k < data.length; k++ )
		{
		  data[k] = rawData[k];
		}

		return data;
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
          TimeSection       ts     = new TimeSection( rawFile, header );
          InstrumentSection is     = new InstrumentSection( rawFile, header );
          DataSection       ds     = new DataSection( rawFile, header, ts );

         System.out.println( "versionNumber:        " + ds.version );
         if( ds.version == 2 )
         {
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
           for( int ii = 0; ii < ds.nspec; ii++ )
           {
             System.out.println(
               ii + "       " + ds.spectrumDescArray[( 2 * ii ) + 1] + "       " +
               ds.spectrumDescArray[( 2 * ii ) + 2] );
           }
         }
	  }
    } 
    catch( IOException ex )
    {
    	ex.printStackTrace();
    }
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
        iTemp      = convertLSBIntToMSBInt( bTemp );
        j          = j + 4;
      }

      // update current value
      outData[i] = iTemp;
      j++;
    }
  }

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
   * Converts a byte array representing a little endian integer  (byte[0] =
   * least significant byte) to a 32 bit big endian integer. This is taken
   * from Rawfile.java's readUnsignedInteger.
   *
   * @param b The byte array holding the 4 bytes representing the  integer
   *
   * @return The converted 32 bit integer.
   *
   * @throws IllegalArgumentException If the byte array is not of size 4.
   */
  private int convertLSBIntToMSBInt( byte[] b ) throws IllegalArgumentException {
    if( b.length != 4 ) {
      throw new IllegalArgumentException( 
        "Byte array must have length 4: byteArrayToInt" );
    }

    int[] c   = new int[b.length];
    int   num = 0;

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
}
