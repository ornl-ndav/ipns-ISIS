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
 * $Log$
 * Revision 1.7  2004/06/22 16:32:55  kramer
 * Added getter methods (with documentation).  Made the constructors public.
 * Now this class imports 2 classes instead of the entire java.io package.
 * Commented out the old method for getting the time channel boundaries.
 *
 * Revision 1.6  2004/06/16 20:40:51  kramer
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
 * Class to read from the time channel boundaries (TCB) section of an ISIS RAW
 * file.
 *
 * @author J.P. Hammonds Intense Pulsed Neutron Source Argonne National
 *         Laboratory
 */
public class TimeSection {
  //~ Static fields/initializers -----------------------------------------------
  /**
   * The number of entries in the table that gives 
   * the period number for each basic period.
   */
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
  protected int[]       periodMap;
  /**
   * Gives the number of spectra in each time regime.  The ith element 
   * in the array is one more than the number of spectra in the (i+1)th time 
   * regime (an additional spectra is added for the zeroth spectra).  The 
   * length of this array equals the number of time regimes.
   */
  protected int[]       numSpectra;
  /**
   * Gives the number of time channels in each time regime.  The ith element 
   * in the array is one more than the number of time channels in the (i+1)th time 
   * regime (an additional time channel is added for the zeroth time channel).  The 
   * length of this array equals the number of time regimes.
   */
  protected int[]       numTimeChannels;
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
  protected int[][]     timeChannelMode;
  /**
   * Gives the time channel parameters (in microseconds) in each time regime.  
   * timeChannelParameters[i][j][k] gives the parameters for the (i+1)th time regime.  
   * Where 0<j<4 and 0<k<5.  Also, 0<\i<(the number of time regimes).
   */
  protected float[][][] timeChannelParameters;
  /**
   * Prescale value for 32MHz clock (<=15).  The length of this array is equal to 
   * the number of time regimes and the ith element in the array corresponds to 
   * the clock prescale of the (i+1)th time regime.
   */
  protected int[]       clockPrescale;
  /**
   * Gives the time channel boundaries.<br>
   * The first index is used to specify the regime number.<br>
   * The second index is used to specify the time channel number where 
   * values range from 0 to one more than the number of time channels.
   */
  protected int[][]     timeChannelBoundaries;

  //~ Constructors -------------------------------------------------------------

  /**
   * Creates a new TimeSection object.
   */
   public TimeSection(  )
   {
      version = -1;
      numOfRegimes = -1;
      numOfFramesPerPeriod = -1;
      numOfPeriods = -1;
      periodMap = new int[PMAP_SIZE];
      numSpectra = new int[0];
      numTimeChannels = new int[0];
      timeChannelMode = new int[0][0];
      timeChannelParameters = new float[0][0][0];
      clockPrescale = new int[0];
      timeChannelBoundaries = new int[0][0];
   }

  /**
   * Creates a new TimeSection object.
   *
   * @param rawFile The RAW file.
   * @param header The header for the RAW file.
   */
  public TimeSection( RandomAccessFile rawFile, Header header ) {
     this();
    int startAddress = ( header.startAddressTcb - 1 ) * 4;

    try {
      rawFile.seek( startAddress );
      version                = Header.readUnsignedInteger( rawFile, 4 );
      numOfRegimes           = Header.readUnsignedInteger( rawFile, 4 );
      numOfFramesPerPeriod   = Header.readUnsignedInteger( rawFile, 4 );
      numOfPeriods           = Header.readUnsignedInteger( rawFile, 4 );

      for( int ii = 0; ii < PMAP_SIZE; ii++ )
        periodMap[ii] = Header.readUnsignedInteger( rawFile, 4 );

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
      
    } catch( IOException ex ) { ex.printStackTrace(); }

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

  /*
   * Accessor method for the time channel boundary (TCB) array.  This assumes
   * that  the number of time regimes is 1.  If there are no TCBs, this
   * returns null.
   *
   * @return The time channel boundary array (second dimension).
   */
  /*
  public int[] getTimeChannelBoundaries(  ) {
    if( timeChannelBoundaries == null ) {
      return null;
    }

    return timeChannelBoundaries[0];
  }
  */

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

  /**
   * Get the prescale value for a 32 MHz clock for the 
   * given time regime.
   * @param num The regime in question.  Note:  The 
   * first time regime is at num=1 not num=0.  For num 
   * to be valid 1<=<code>num</code><=
   * {@link #getNumOfTimeRegimes() 
   * getNumOfTimeRegimes()}.
   * @return The prescale value for the 32MHz clock for 
   * time regime <code>num</code> or -1 if <code>num
   * </code> is invalid.
   */
  public int getClockPrescaleForRegime(int num)
   {
      if (num>=1 && num<=getNumOfTimeRegimes())
         return clockPrescale[num-1];
      else
         return -1;
   }

   /**
    * Get the number of frames per period.
    * @return The number of frames per 
    * period.
    */
   public int getNumOfFramesPerPeriod()
   {
      return numOfFramesPerPeriod;
   }

   /**
    * Get the number of periods.
    * @return The number of periods.
    */
   public int getNumOfPeriods()
   {
      return numOfPeriods;
   }

   /**
    * Get the number of time regimes.
    * @return The number of time regimes 
    * (normally this is equal to 1).
    */
   public int getNumOfTimeRegimes()
   {
      return numOfRegimes;
   }

   /**
    * Get one more than the number of spectra for the 
    * given time regime (an extra spectra is added for 
    * the zeroth spectra).
    * @param num The regime in question.  Note:  The 
    * first time regime is at num=1 not num=0.  For num 
    * to be valid 1<=<code>num</code><=
    * {@link #getNumOfTimeRegimes() 
    * getNumOfTimeRegimes()}.
    * @return One more than the number of spectra for 
    * time regime <code>num</code> or -1 if <code>num
    * </code> is invalid.
    */
   public int getNumSpectraForRegime(int num)
   {
      if (num>=1 && num<=getNumOfTimeRegimes())
         return numSpectra[num-1];
      else
         return -1;
   }

   /**
    * Get one more than the number of time channels for the 
    * given time regime (an extra time channel is added for 
    * the zeroth time channel).
    * @param num The regime in question.  Note:  The 
    * first time regime is at num=1 not num=0.  For num 
    * to be valid 1<=<code>num</code><=
    * {@link #getNumOfTimeRegimes() 
    * getNumOfTimeRegimes()}.
    * @return One more than the number of time channels for 
    * time regime <code>num</code> or -1 if <code>num
    * </code> is invalid.
    */
   public int getNumTimeChannelsForRegime(int num)
   {
      if (num>=1 && num<=getNumOfTimeRegimes())
         return numTimeChannels[num-1];
      else
         return -1;
   }

   /**
    * Get the map that gives the period number for 
    * each basic period.
    * @return The map that gives the period number 
    * for each basic period.
    */
   public int[] getPeriodMap()
   {
      int[] copy = new int[periodMap.length];
      System.arraycopy(periodMap,0,copy,0,periodMap.length);
      return copy;
   }

   /**
    * Get the time channel modes for the given time regime.
    * @param num The regime in question.  Note:  The 
    * first time regime is at num=1 not num=0.  For num 
    * to be valid 1<=<code>num</code><=
    * {@link #getNumOfTimeRegimes() 
    * getNumOfTimeRegimes()}.
    * @return The time channel modes for the given time 
    * regime or null if <code>num</code> is invalid.
    */
   public int[] getTimeChannelModeForRegime(int num)
   {
      if (num>=1 && num<=getNumOfTimeRegimes())
      {
         int[] copy = new int[timeChannelMode[num-1].length];
         System.arraycopy(timeChannelMode[num-1],0,copy,0,timeChannelMode[num-1].length);
         return copy;
      }
      else
         return null;
   }

   /**
    * Get the time channel parameters for the given time regime.
    * @param num The regime in question.  Note:  The 
    * first time regime is at num=1 not num=0.  For num 
    * to be valid 1<=<code>num</code><=
    * {@link #getNumOfTimeRegimes() 
    * getNumOfTimeRegimes()}.
    * @return The time channel parameters (in microseconds) for 
    * the givne regime or null if <code>num</code> is invalid.
    */
   public float[][] getTimeChannelParametersForRegime(int num)
   {
      if (num>=1 && num<=getNumOfTimeRegimes())
      {
         float[][] copy = new float[timeChannelParameters[num-1].length][];
         float[] subCopy = null;
         for (int i=0; i<timeChannelParameters[num-1].length; i++)
         {
            subCopy = new float[timeChannelParameters[num-1][i].length];
            System.arraycopy(timeChannelParameters[num-1][i],0,subCopy,0,timeChannelParameters[num-1][i].length);
            copy[i] = subCopy;
         }
         return copy;
      }
      else
         return null;
   }

   /**
    * Get the Time Channel Boundary Section version number.
    * @return The Time Channel Boundary Section version number.
    */
   public int getVersion()
   {
      return version;
   }

   /**
    * Get the time channel boundaries for the given time regime.
    * @param num The regime in question.  Note:  The 
    * first time regime is at num=1 not num=0.  For num 
    * to be valid 1<=<code>num</code><=
    * {@link #getNumOfTimeRegimes() 
    * getNumOfTimeRegimes()}.
    * @return The time channel boundaries for the given time 
    * regime or null if <code>num</code> is invalid.
    */
   public int[] getTimeChannelBoundariesForRegime(int num)
   {
      if (num>=1 && num<=getNumOfTimeRegimes())
      {
         int[] copy = new int[timeChannelBoundaries[num-1].length];
         System.arraycopy(timeChannelBoundaries[num-1],0,copy,0,timeChannelBoundaries[num-1].length);
         return copy;
      }
      else
         return null;
   }

}
