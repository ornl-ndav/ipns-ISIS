/*
 * File:  TimeSection.java
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
 * Class to read from the time channel boundaries (TCB) section of an ISIS RAW
 * file.
 *
 * @author J.P. Hammonds Intense Pulsed Neutron Source Argonne National
 *         Laboratory
 */
public class TimeSection {
  //~ Static fields/initializers -----------------------------------------------

  protected static final int PMAP_SIZE = 256;

  //~ Instance fields ----------------------------------------------------------
  
  /** TCB section version number. */
  protected int         version;
  /** Number of time regimes (normally = 1). */
  protected int         numOfRegimes;
  /** Number of frames per period. */
  protected int         numOfFramesPerPeriod;
  /** Number of periods. */
  protected int         numOfPeriods;
  /** Period number for each basic period. */
  protected int[]       periodMap             = new int[PMAP_SIZE];
  /**
   * Gives the number of spectra in each time regime.  The ith element 
   * in the array is one more than the number of spectra in the (i+1)th time 
   * regime (an additional spectra is added for the zeroth spectra).  The 
   * length of this array equals the number of time regimes.
   */
  protected int[]       numSpectra            = new int[0];
  /**
   * Gives the number of time channels in each time regime.  The ith element 
   * in the array is one more than the number of time channels in the (i+1)th time 
   * regime (an additional time channel is added for the zeroth time channel).  The 
   * length of this array equals the number of time regimes.
   */
  protected int[]       numTimeChannels       = new int[0];
  /**
   * Gives the time channel mode for each time regime.  The ith element 
   * in the array is the time channel mode for the (i+1)th time regime.<br>
   * TIME CHANNEL MODES:<br>
   *    0       Boundaries set by a table held in file TCB.DAT<br>
   *<br>
   *    1       TCB1(n) = (TCP1(1) + (n-1)*TCP1(2)-DAEP(24)*4)*32/PRE1<br>
   *                    (ie. Dt = c )<br>
   *                    (ie. less frm.synch delay)<br>
   *<br>
   *    2       temp(1)  = TCP1(1)<br>
   *             TCB1(1)  = (TCP1(1)-DAEP(24)*4)*32/PRE1 (ie. Dt = c.t<br>
   *             temp(n+1)= temp(n)*(1 + TCP1(2))<br>
   *             TCB1(n+1)= (temp(n+1) - DAEP(24)*4)*32/PRE1
   *   
   *           <br>
   *              where:<br>
   *                 TCB1 is the time channel boundary for the first time regime.<br>
   *                 TCP1 is the time channel parameters for the first time regime.<br>
   *                 DAEP refers to the DAE (Data Acquistion electronics) block.<br>
   *                 PRE1 is the prescale value for 32 MHz clock for the first time regime.<br>
   *              NOTE:  Parentheses are used here to reference elements in an array 
   *                           (as apposed to brackets used in Java).  That is becuase this 
   *                           documentation is interpreted from how ISIS files were accessed.  
   *                           Initially, Fortran was used to access data from an ISIS file.
   */
  protected int[][]     timeChannelMode       = new int[0][0];
  /**
   * Gives the time channel parameters (in microseconds) in each time regime.  
   * timeChannelParameters[i][j][k] gives the parameters for the (i+1)th time regime.  
   * Where 0<j<4 and 0<k<5.  Also, 0<\i<(the number of time regimes).
   */
  protected float[][][] timeChannelParameters = new float[0][0][0];
  /**
   * Prescale value for 32MHz clock (<=15).  The length of this array is equal to 
   * the number of time regimes and the ith element in the array corresponds to 
   * the clock prescale of the (i+1)th time regime.
   */
  protected int[]       clockPrescale         = new int[0];
  /**
   * Gives the time channel boundaries.<br>
   * The first index is used to specify the regime number.<br>
   * The second index is used to specify the time channel number where 
   * values range from 0 to one more than the number of time channels.
   */
  protected int[][]     timeChannelBoundaries = new int[0][0];

  //~ Constructors -------------------------------------------------------------

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
      version                = Header.readUnsignedInteger( rawFile, 4 );
      numOfRegimes           = Header.readUnsignedInteger( rawFile, 4 );
      numOfFramesPerPeriod   = Header.readUnsignedInteger( rawFile, 4 );
      numOfPeriods           = Header.readUnsignedInteger( rawFile, 4 );

      for( int ii = 0; ii < PMAP_SIZE; ii++ ) {
        periodMap[ii] = Header.readUnsignedInteger( rawFile, 4 );
      }

      numSpectra              = new int[numOfRegimes];
      numTimeChannels         = new int[numOfRegimes];
      timeChannelMode         = new int[numOfRegimes][5];
      timeChannelParameters   = new float[numOfRegimes][4][5];
      clockPrescale           = new int[numOfRegimes];
      timeChannelBoundaries   = new int[numOfRegimes][];

      for( int ii = 0; ii < numOfRegimes; ii++ )
      {
        numSpectra[ii]        = Header.readUnsignedInteger( rawFile, 4 );
        numTimeChannels[ii]   = Header.readUnsignedInteger( rawFile, 4 );

        for( int jj = 0; jj < 5; jj++ )
          timeChannelMode[ii][jj] = Header.readUnsignedInteger( rawFile, 4 );

        for( int jj = 0; jj < 4; jj++ )
        {
          for( int kk = 0; kk < 5; kk++ )
            timeChannelParameters[ii][jj][kk] = ( float )Header.ReadVAXReal4( rawFile );
        }

        clockPrescale[ii]           = Header.readUnsignedInteger( rawFile, 4 );
        timeChannelBoundaries[ii]   = new int[numTimeChannels[ii] + 1];
      }

      for( int ii = 0; ii < numOfRegimes; ii++ )
      {
        for( int jj = 0; jj < ( numTimeChannels[ii] + 1 ); jj++ )
          timeChannelBoundaries[ii][jj] = Header.readUnsignedInteger( rawFile, 4 );
      }
      
    } catch( IOException ex ) {}

    //looks complete based on libget.txt

    /*
    System.out.println("Printing data for timeChannelParameters (a 3D array)");
    for (int k=0; k<5;k++)
    {
    	System.out.println("k="+k);
    	System.out.println();
    	for (int j=0; j<4; j++)
    	{
    		System.out.println("j="+j);
    		for (int i=0; i<numOfRegimes; i++)
		    {
		    	System.out.print(timeChannelParameters[i][j][k]+">>\n");
		    }
    	}
    }
    */
  }

  //~ Methods ------------------------------------------------------------------

  /**
   * Accessor method for the time channel boundary (TCB) array.  This assumes
   * that  the number of time regimes is 1.  If there are no TCBs, this
   * returns null.
   *
   * @return The time channel boundary array (second dimension).
   */
  public int[] getTimeChannelBoundaries(  ) {
    if( timeChannelBoundaries == null ) {
      return null;
    }

    return timeChannelBoundaries[0];
  }

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
	      RandomAccessFile rawFile = new RandomAccessFile( args[fileNum], "r" );
	      Header           header = new Header( rawFile );
	      TimeSection      ts     = new TimeSection( rawFile, header );

   	      System.out.println( "version: " + ts.version );
          System.out.println( "numOfRegimes:  " + ts.numOfRegimes );
          System.out.println( "numOfFramesPerPeriod:  " + ts.numOfFramesPerPeriod );
          System.out.println( "numOfPeriods: " + ts.numOfPeriods );
          System.out.println( "periodMap: " );
          for( int ii = 0; ii < 256; ii++ )
            System.out.print( ts.periodMap[ii] + "  " );
          System.out.println(  );
          for( int ii = 0; ii < ts.numOfRegimes; ii++ )
          {
             System.out.println( "-Regime " + ii );
             System.out.println( "---numSpectra:       " + ts.numSpectra[ii] );
             System.out.println( "---numTimeChannels:  " + ts.numTimeChannels[ii] );
             System.out.println( "---timeChannelMode:  " );
             for( int jj = 0; jj < 5; jj++ )
               System.out.print( ts.timeChannelMode[ii][jj] + "   " );
             System.out.println(  );
             System.out.println( "---timeChannelParameters:" );
             for( int jj = 0; jj < 4; jj++ )
             {
                System.out.print("jj=" + jj + "-- " );
                for( int kk = 0; kk < 5; kk++ )
                   System.out.print( ts.timeChannelParameters[ii][jj][kk] + "  " );
                System.out.println(  );
              }
              System.out.println( "---clockPrescale:    " + ts.clockPrescale[ii] );
              System.out.println( "---timeChannelBoundaries:" );
              for( int jj = 0; jj < ( ts.numTimeChannels[ii] + 1 ); jj++ )
                System.out.print( ts.timeChannelBoundaries[ii][jj] + "   " );
          }
          System.out.println();
      }
    }
    catch( IOException ex )
    {
    	ex.printStackTrace();
    }
  }
}
