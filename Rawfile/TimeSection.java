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
 * Revision 1.9  2004/07/12 19:15:54  kramer
 * Added a DaeSection field which this class uses to verify time regime
 * information.  Added the methods getMinimumRegimeNumber(),
 * getMaximumRegimeNumber(), isAValidRegimeNumber(), and getArrayIndexForRegime().
 * These methods are now used to verify if a specified regime number is valid
 * and are used to determine the correct information for the regime number.
 *
 * Revision 1.8  2004/06/24 21:35:32  kramer
 *
 * Changed all of the fields' visiblity from protected to private.  Fields
 * are now accessed from other classes in this package through getter methods
 * instead of using <object>.<field name>.  Also, this class should now be
 * immutable.  I also modified the main method to print the time channel
 * boundaries in clock pulses and microseconds.
 *
 * Revision 1.7  2004/06/22 16:32:55  kramer
 *
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
  private int         version;
  /** Number of time regimes (normally = 1). */
  private int         numOfRegimes;
  /** Number of frames per period. */
  private int         numOfFramesPerPeriod;
  /** Number of periods. */
  private int         numOfPeriods;
  /** Period number for each basic period. */
  private int[]       periodMap;
  /**
   * Gives the number of spectra in each time regime.  The ith element 
   * in the array is one more than the number of spectra in the (i+1)th time 
   * regime (an additional spectra is added for the zeroth spectra).  The 
   * length of this array equals the number of time regimes.
   */
  private int[]       numSpectra;
  /**
   * Gives the number of time channels in each time regime.  The ith element 
   * in the array is one more than the number of time channels in the (i+1)th time 
   * regime (an additional time channel is added for the zeroth time channel).  The 
   * length of this array equals the number of time regimes.
   */
  private int[]       numTimeChannels;
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
  private int[][]     timeChannelMode;
  /**
   * Gives the time channel parameters (in microseconds) in each time regime.  
   * timeChannelParameters[i][j][k] gives the parameters for the (i+1)th time regime.  
   * Where 0<j<4 and 0<k<5.  Also, 0<\i<(the number of time regimes).
   */
  private float[][][] timeChannelParameters;
  /**
   * Prescale value for 32MHz clock (<=15).  The length of this array is equal to 
   * the number of time regimes and the ith element in the array corresponds to 
   * the clock prescale of the (i+1)th time regime.
   */
  private int[]       clockPrescale;
  /**
   * Gives the time channel boundaries (in clock pulses).<br>
   * The first index is used to specify the regime number.<br>
   * The second index is used to specify the time channel number where 
   * values range from 0 to one more than the number of time channels.
   */
  private int[][]     timeChannelBoundaries;
  
  /**
   * Represents the DAE (Data Acquisition Electronics) object used to get 
   * time regime information.
   */
  private DaeSection daeSection;  

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
      
      daeSection = new DaeSection();
   }

  /**
   * Creates a new TimeSection object.
   *
   * @param rawFile The RAW file.
   * @param header The header for the RAW file.
   */
  public TimeSection( RandomAccessFile rawFile, Header header, DaeSection DAESection ) {
     this();
    daeSection = DAESection;
    int startAddress = ( header.getStartAddressTCBSection() - 1 ) * 4;

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
	      Header            header = new Header( rawFile );
          InstrumentSection iSect  = new InstrumentSection( rawFile, header);
          DaeSection        dSect  = new DaeSection( rawFile, header, iSect.getNumberOfDetectors() );
	      TimeSection       ts     = new TimeSection( rawFile, header, dSect );

   	      System.out.println( "version: " + ts.version );
          System.out.println( "numOfRegimes:  " + ts.numOfRegimes );
          System.out.println( "numOfFramesPerPeriod:  " + ts.numOfFramesPerPeriod );
          System.out.println( "numOfPeriods: " + ts.numOfPeriods );
          System.out.println( "Min Regime Number:  " + ts.getMinimumRegimeNumber());
          System.out.println( "Max Regime Number:  " + ts.getMaximumRegimeNumber());
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
              System.out.println( "---timeChannelBoundaries (in clock pulses):" );
              for( int jj = 0; jj < ( ts.numTimeChannels[ii] + 1 ); jj++ )
                System.out.print( ""+(jj+1)+"->"+ts.timeChannelBoundaries[ii][jj] + "   " );
              System.out.println();
              System.out.println("--timeChannelBoundaries (computed as time):  ");
              for( int jj = 0; jj < ( ts.numTimeChannels[ii] + 1 ); jj++ )
                System.out.print( ""+(jj+1)+"->"+(ts.timeChannelBoundaries[ii][jj]*ts.clockPrescale[ii]/32.0+ts.timeChannelParameters[ii][0][0]) + "   " );
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
      if (isAValidRegimeNumber(num))
         return clockPrescale[getArrayIndexForRegime(num)];
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
      if (isAValidRegimeNumber(num))
      {
         int index = getArrayIndexForRegime(num);
         System.out.println("Requesting the number of spectra for regime at index "+index);
         return numSpectra[index];
      }
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
      if (isAValidRegimeNumber(num))
         return numTimeChannels[getArrayIndexForRegime(num)];
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
      if (isAValidRegimeNumber(num))
      {
         int index = getArrayIndexForRegime(num);
         int[] copy = new int[timeChannelMode[index].length];
         System.arraycopy(timeChannelMode[index],0,copy,0,timeChannelMode[index].length);
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
      if (isAValidRegimeNumber(num))
      {
         int index = getArrayIndexForRegime(num);
         float[][] copy = new float[timeChannelParameters[index].length][];
         float[] subCopy = null;
         for (int i=0; i<timeChannelParameters[index].length; i++)
         {
            subCopy = new float[timeChannelParameters[index][i].length];
            System.arraycopy(timeChannelParameters[index][i],0,subCopy,0,timeChannelParameters[index][i].length);
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
    * The values in the array are in clock pulses.
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
      if (isAValidRegimeNumber(num))
      {
         int index = getArrayIndexForRegime(num);
         int[] copy = new int[timeChannelBoundaries[index].length];
         System.arraycopy(timeChannelBoundaries[index],0,copy,0,timeChannelBoundaries[index].length);
         return copy;
      }
      else
         return null;
   }
   
   /**
    * Get the smallest regime number.
    * @return The smallest regime number or 
    * -1 if it cannot be determined.
    */
   public int getMinimumRegimeNumber()
   {
      return daeSection.getMinimumRegimeNumber();
   }
   
   /**
    * Get the largest regime number.
    * @return The largest regime number or 
    * -1 if it cannot be determined.
    */
   public int getMaximumRegimeNumber()
   {
      int min = getMinimumRegimeNumber();
      if (min==-1) //then the min could not be determined
         return -1;
      else
      {
         return (min+numOfRegimes-1);
      }
   }
   
   /**
    * Determine if the given integer is a 
    * valid regime number.
    * @return True if the integer is a valid 
    * regime number and false otherwise.  This 
    * method looks at the minimum and maximum 
    * regime number as written in the rawfile to 
    * determine if the specified integer is valid.
    */
   public boolean isAValidRegimeNumber(int num)
   {
      int min = getMinimumRegimeNumber();
      int max = getMaximumRegimeNumber();
      if (min==-1 && max==-1) //then the min and max 
         return false;        //could not be determined
      else
         return (num>=min && num<=max);
   }
   
   //-------------------------=[ Private Methods ]=---------------------------------------------------
   /**
    * This class uses arrays to hold information about 
    * time regimes.  The 0th element in one of these arrays, 
    * for instance, contains the number of spectra in the 
    * first time regime.  This method uses <code>num</code>, 
    * the actual time regime number as seen in the ISIS 
    * rawfile, and determines the index in the arrays that 
    * holds the data for this regime.  This method assumes 
    * that index 0 contains the first meaningful data in 
    * the array.  It also assumes that invoking 
    * <code>DaeSection.isAValidRegimeNumber(num)</code> would 
    * return true.  Because, this method is private and is 
    * only used by the methods in this class, these 
    * assumptions are not unreasonable.
    * @param num The regime number.
    * @return The index in an array (starting with data at 
    * index 0) that would hold the data corresponding to 
    * time regime <code>num</code>.
    */
   private int getArrayIndexForRegime(int num)
   {
      return (num-getMinimumRegimeNumber());
   }
}
