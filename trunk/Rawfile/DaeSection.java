/*
 * File:  DaeSection.java
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
 * Revision 1.13  2005/06/10 14:11:48  dennis
 * Fixed problem with several javadoc comments.
 *
 * Revision 1.12  2004/07/12 19:10:53  kramer
 *
 * Added a field of type IRegimeInfo which is used to get the mimimum regime
 * number.  Added a method to get a copy of the time regime table.  Modified the
 * getMinimumRegimeNumber() method to use the IRegimeInfo object to get the
 * minimum regime number.  Removed the methods getMaximumRegimeNumber() and
 * isAValidRegimeNumber().  The TimeSection class contains these methods.
 *
 * Revision 1.11  2004/07/07 18:54:11  kramer
 *
 * Added methods to get the minimum and maximum regime numbers as recorded in
 * the rawfile.  Also added a method to determine if a given integer is a valid
 * regime number.
 *
 * Revision 1.10  2004/06/24 21:33:00  kramer
 *
 * Changed all of the fields' visiblity from protected to private.  Fields
 * are now accessed from other classes in this package through getter methods
 * instead of using <object>.<field name>.  Also, this class should now be
 * immutable.
 *
 * Revision 1.9  2004/06/22 16:49:02  kramer
 *
 * Made the constructors public.
 *
 * Revision 1.8  2004/06/18 16:36:03  kramer
 *
 * Fixed the Javadoc statements.
 *
 * Revision 1.7  2004/06/18 15:59:15  kramer
 *
 * Added getter methods (with documentation) for all fields.  Improved Javadoc
 * comments.  Now the class imports 2 classes instead of the entire java.io
 * package.  It also warns the user if it thinks it is reading an ISIS file it
 * can't understand.
 *
 * Revision 1.6  2004/06/16 20:40:49  kramer
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
 * This class processes DAE (Data Acquistion Electronics) information from an ISIS RAW file.
 *
 * @author J.P. Hammonds Intense Pulsed Neutron Source Argonne National
 *         Laboratory
 */
public class DaeSection {
  //~ Instance fields ----------------------------------------------------------
  
  /**
   * The crate number for each detector.  The length of 
   * this array equals 1 more than the number of 
   * detectors.  Indexing starts at 1.  Index 0 contains 
   * garbage values.  The number of detectors should
   * match the value in InstrumentSection.nDet.
   */
  private int[] crateNum;

  /**
   * Position in module for each detector.  The length of 
   * this array equals 1 more than the number of 
   * detectors.  Indexing starts at 1.  Index 0 contains 
   * garbage values.  The number of detectors should
   * match the value in InstrumentSection.nDet.
   */
  private int[] inputNum;

  /**
   * The module number for each detector.  The length of 
   * this array equals 1 more than the number of 
   * detectors.  Indexing starts at 1.  Index 0 contains 
   * garbage values.  The number of detectors should
   * match the value in InstrumentSection.nDet.
   */
  private int[] moduleNum;

  /**
   * Time regime number table.  The length of 
   * this array equals 1 more than the number of 
   * detectors.  Indexing starts at 1.  Index 0 contains 
   * garbage values.  The number of detectors should
   * match the value in InstrumentSection.nDet.
   */
  private int[] timeRegimeTable;

  /**
   * 'User detector number' for each detector.  The length of 
   * this array equals 1 more than the number of 
   * detectors.  Indexing starts at 1.  Index 0 contains 
   * garbage values.  The number of detectors should
   * match the value in InstrumentSection.nDet.
   */
  private int[] userDetectorNumber;
  /** Crate for monitor 1. */
  private int   crateMon1;
  /** Crate for monitor 2. */
  private int   crateMon2;
  /** Detector for monitor 1. */
  private int   detectorMon1;
  /** Detector for monitor 2. */
  private int   detectorMon2;
  /** external neutron gate (t1) (in microseconds). */
  private int   extNeutGateT1;
  /** external nuetron gate (t2) (in microseconds). */
  private int   extNeutGateT2;
  /**External veto 0 (0 dis, 1 en). */
  private int   externalVeto1;
  /**External veto 1 (0 dis, 1 en). */
  private int   externalVeto2;
  /**External veto 2 (0 dis, 1 en). */
  private int   externalVeto3;
  /** Frame synch delay (4 microsecond steps). */
  private int   frameSyncDelay;
  /** Frame synch origin (0:none/1:ext/2:int). */
  private int   frameSyncOrigin;
  /** Good ext. neut tot (high 32 bits). */
  private int   goodExtNeutTotalHigh32;
  /** Good ext. neut tot (low 32 bits). */
  private int   goodExtNeutTotalLow32;
  /** Good PPP total (high 32 bits). */
  private int   goodPppTotalHigh32;
  /** Good PPP total (low 32 bits). */
  private int   goodPppTotalLow32;
  /** Length of bulk store memory (bytes). */
  private int   lengthOfBulkStore;
  /** Mask for monitor 1. */
  private int   maskMon1;
  /** Mask for monitor 2. */
  private int   maskMon2;
  /** Module for monitor 1. */
  private int   moduleMon1;
  /** Module for monitor 2. */
  private int   moduleMon2;
  /** PPP minimum value. */
  private int   pppMinValue;
  /** Raw ext. neut tot (high 32 bits). */
  private int   rawExtNeutTotalHigh32;
  /** Raw ext. neut tot (low 32 bits). */
  private int   rawExtNeutTotalLow32;
  /** Raw PPP total (high 32 bits). */
  private int   rawPppTotalHigh32;
  /** Row PPP total (low 32 bits). */
  private int   rawPppTotalLow32;
  /** Secondary Master Pulse (0:en, 1:dis). */
  private int   secondaryMasterPulse;
  /** Total GOOD EVENTS (high 32 bits). */
  private int   totalGoodEventsHigh32;
  /** Total GOOD EVENTS (low 32 bits). */
  private int   totalGoodEventsLow32;
  /** DAE section version number. */
  private int   version;
  /** Word length in bulk store memory. */
  private int   wordLength;
  
  /**
   * Used to determine regime information.
   */
  private IRegimeInfo regimeInfoInformant;

  //~ Constructors -------------------------------------------------------------

  /**
   * Creates a new DaeSection object.
   */
  public DaeSection(  )
  {
      crateNum = new int[0];
      inputNum = new int[0];
      moduleNum = new int[0];
      timeRegimeTable = new int[0];
      userDetectorNumber = new int[0];
      crateMon1 = -1;
      crateMon2 = -1;
      detectorMon1 = -1;
      detectorMon2 = -1;
      extNeutGateT1 = -1;
      extNeutGateT2 = -1;
      externalVeto1 = -1;
      externalVeto2 = -1;
      externalVeto3 = -1;
      frameSyncDelay = -1;
      frameSyncOrigin = -1;
      goodExtNeutTotalHigh32 = -1;
      goodExtNeutTotalLow32 = -1;
      goodPppTotalHigh32 = -1;
      goodPppTotalLow32 = -1;
      lengthOfBulkStore = -1;
      maskMon1 = -1;
      maskMon2 = -1;
      moduleMon1 = -1;
      moduleMon2 = -1;
      pppMinValue = -1;
      rawExtNeutTotalHigh32 = -1;
      rawExtNeutTotalLow32 = -1;
      rawPppTotalHigh32 = -1;
      rawPppTotalLow32 = -1;
      secondaryMasterPulse = -1;
      totalGoodEventsHigh32 = -1;
      totalGoodEventsLow32 = -1;
      version = -1;
      wordLength = -1;
      
      regimeInfoInformant = new UnknownRegimeInfoInformant(this);
  }

  /**
   * Creates a new DaeSection object.
   *
   * @param rawFile The RAW file.
   * @param header The header for the RAW file.
   * @param nDet The number of detectors.
   */
   public DaeSection( RandomAccessFile rawFile, Header header, int nDet ) {
  	this();
    int startAddress = ( header.getStartAddressDAESection() - 1 ) * 4;

    try {
      rawFile.seek( startAddress );
      version                  = Header.readUnsignedInteger( rawFile, 4 );
      if (version != 2)
         System.out.println("WARNING:  Unrecognized Data Acquisition Electronics version number."
         +"\n          Version found = "+version
         +"\n          Version numbers corresponding to data that can be processed  = 2"
         +"\n          Data may be incorrectly read and/or interpreted from the file.");
      wordLength               = Header.readUnsignedInteger( rawFile, 4 );
      lengthOfBulkStore        = Header.readUnsignedInteger( rawFile, 4 );
      pppMinValue              = Header.readUnsignedInteger( rawFile, 4 );
      goodPppTotalHigh32       = Header.readUnsignedInteger( rawFile, 4 );
      goodPppTotalLow32        = Header.readUnsignedInteger( rawFile, 4 );
      rawPppTotalHigh32        = Header.readUnsignedInteger( rawFile, 4 );
      rawPppTotalLow32         = Header.readUnsignedInteger( rawFile, 4 );
      goodExtNeutTotalHigh32   = Header.readUnsignedInteger( rawFile, 4 );
      goodExtNeutTotalLow32    = Header.readUnsignedInteger( rawFile, 4 );
      rawExtNeutTotalHigh32    = Header.readUnsignedInteger( rawFile, 4 );
      rawExtNeutTotalLow32     = Header.readUnsignedInteger( rawFile, 4 );
      extNeutGateT1            = Header.readUnsignedInteger( rawFile, 4 );
      extNeutGateT2            = Header.readUnsignedInteger( rawFile, 4 );
      detectorMon1             = Header.readUnsignedInteger( rawFile, 4 );
      moduleMon1               = Header.readUnsignedInteger( rawFile, 4 );
      crateMon1                = Header.readUnsignedInteger( rawFile, 4 );
      maskMon1                 = Header.readUnsignedInteger( rawFile, 4 );
      detectorMon2             = Header.readUnsignedInteger( rawFile, 4 );
      moduleMon2               = Header.readUnsignedInteger( rawFile, 4 );
      crateMon2                = Header.readUnsignedInteger( rawFile, 4 );
      maskMon2                 = Header.readUnsignedInteger( rawFile, 4 );
      totalGoodEventsHigh32    = Header.readUnsignedInteger( rawFile, 4 );
      totalGoodEventsLow32     = Header.readUnsignedInteger( rawFile, 4 );
      frameSyncDelay           = Header.readUnsignedInteger( rawFile, 4 );
      frameSyncOrigin          = Header.readUnsignedInteger( rawFile, 4 );
      secondaryMasterPulse     = Header.readUnsignedInteger( rawFile, 4 );
      externalVeto1            = Header.readUnsignedInteger( rawFile, 4 );
      externalVeto2            = Header.readUnsignedInteger( rawFile, 4 );
      externalVeto3            = Header.readUnsignedInteger( rawFile, 4 );

      //skip over the spare space
      rawFile.seek( startAddress + ( 64 * 4 ) );

      //keep reading
      crateNum             = new int[nDet + 1];
      moduleNum            = new int[nDet + 1];
      inputNum             = new int[nDet + 1];
      timeRegimeTable      = new int[nDet + 1];
      userDetectorNumber   = new int[nDet + 1];

      for( int ii = 1; ii <= nDet; ii++ ) {
        crateNum[ii] = Header.readUnsignedInteger( rawFile, 4 );
      }

      for( int ii = 1; ii <= nDet; ii++ ) {
        moduleNum[ii] = Header.readUnsignedInteger( rawFile, 4 );
      }

      for( int ii = 1; ii <= nDet; ii++ ) {
        inputNum[ii] = Header.readUnsignedInteger( rawFile, 4 );
      }

      for( int ii = 1; ii <= nDet; ii++ ) {
        timeRegimeTable[ii] = Header.readUnsignedInteger( rawFile, 4 );
      }

      for( int ii = 1; ii <= nDet; ii++ ) {
        userDetectorNumber[ii] = Header.readUnsignedInteger( rawFile, 4 );
      }
    } catch( IOException ex ) { ex.printStackTrace(); }

    //looks complete based on libget.txt
    String type = header.getInstrumentType();
    if (type.trim().equalsIgnoreCase("SXD"))
       regimeInfoInformant = new SXDRegimeInfoInformant();
    else if (type.trim().equalsIgnoreCase("LOQ"))
       regimeInfoInformant = new LOQRegimeInfoInformant();
    else if (type.trim().equalsIgnoreCase("HRP"))
       regimeInfoInformant = new HRPRegimeInfoInformant();
    else
    {
       System.out.println("<DaeSection.java>  Warning:  Unknown instrument type:  "+type);
       System.out.println("  Using an UnknownRegimeInfoInformant to determine time regime information.");
       regimeInfoInformant = new UnknownRegimeInfoInformant(this);
    }
  }

  //~ Methods ------------------------------------------------------------------

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
          RandomAccessFile  rawFile = new RandomAccessFile( args[fileNum], "r" );
          Header            header = new Header( rawFile );
          InstrumentSection is     = new InstrumentSection( rawFile, header );
          DaeSection        ds     = new DaeSection( rawFile, header, is.getNumberOfDetectors() );
          
          System.out.println( "versionNumber:        " + ds.version );
          System.out.println( "wordLength:           " + ds.wordLength );
          System.out.println( "lengthOfBulkStore:    " + ds.lengthOfBulkStore );
          System.out.println( "pppMinValue:          " + ds.pppMinValue );
          System.out.println( "goodPppTotalHigh32:   " + ds.goodPppTotalHigh32 );
          System.out.println( "goodPppTotalLow32:   " + ds.goodPppTotalLow32 );
          System.out.println( "rawPppTotalHigh32:   " + ds.rawPppTotalHigh32 );
          System.out.println( "rawPppTotalLow32:   " + ds.rawPppTotalLow32 );
          System.out.println( "goodExtNeutTotalHigh32:   " +
            ds.goodExtNeutTotalHigh32 );
          System.out.println( "goodExtNeutTotalLow32:   " +
            ds.goodExtNeutTotalLow32 );
          System.out.println( "rawExtNeutTotalHigh32:   " +
            ds.rawExtNeutTotalHigh32 );
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
          System.out.println( "totalGoodEventsHigh32:  " +
            ds.totalGoodEventsHigh32 );
          System.out.println( "totalGoodEventsLow32:   " + ds.totalGoodEventsLow32 );
          System.out.println( "frameSyncDelay:         " + ds.frameSyncDelay );
          System.out.println( "frameSyncOrigin:         " + ds.frameSyncOrigin );
          System.out.println( "secondaryMasterPulse:    " +
            ds.secondaryMasterPulse );
          System.out.println( "externalVeto1:           " + ds.externalVeto1 );
          System.out.println( "externalVeto2:           " + ds.externalVeto2 );
          System.out.println( "externalVeto3:           " + ds.externalVeto3 );
          System.out.println( "minimum valid\n" +
                              "  regime number:         " + ds.getMinimumRegimeNumber() );
          System.out.println( 
            "Detector   Crate   Module  Input   timeRegime  userDetectorNum" );

          for( int ii = 1; ii <= is.getNumberOfDetectors(); ii++ )
          {
            System.out.println( "  " + ii + "          " + ds.crateNum[ii] +
              "       " + ds.moduleNum[ii] + "      " + ds.inputNum[ii] +
              "          " + ds.timeRegimeTable[ii] + "           " +
              ds.userDetectorNumber[ii] );
          }
		}
    }
    catch( IOException ex )
    {
    	ex.printStackTrace();
    }
  }
  
   /**
    * Get the crate number for monitor 1.
    * @return The crate number for monitor 1 (4 bits are used to hold the data).
    */
   public int getCrateNumForMonitor1()
   {
      return crateMon1;
   }

   /**
    * Get the crate number for monitor 2.
    * @return The crate number for monitor 2 (4 bits are used to hold the data).
    */
   public int getCrateNumForMonitor2()
   {
      return crateMon2;
   }

   /**
    * Get the crate number at detector <code>detectorNum</code>.  
    * Note:  The first detector is at <code>detectorNum</code>=1 not 0.
    * @param detectorNum The number of the detector you are 
    * refering you.  Note:  for <code>detectorNum</code> to be 
    * valid, 1 <= <code>detectorNum</code> <= 
    * {@link InstrumentSection#getNumberOfDetectors() 
    * InstrumentSection.getNumberOfDetectors()}.
    * @return The crate number for the specified detector or -1 if 
    * <code>detectorNum</code> is invalid.
    */
   public int getCrateNumForDetector(int detectorNum)
   {
      if (detectorNum>=1 && detectorNum<crateNum.length)
         return crateNum[detectorNum];
      else
         return -1;
   }

   /**
    * Get the detector number for monitor 1.
    * @return The detector number for monitor 1 (12 bits are used to store this data).
    */
   public int getDetectorNumForMonitor1()
   {
      return detectorMon1;
   }

   /**
    * Get the detector number for monitor 2.
    * @return The detector number for monitor 2 (12 bits are used to store this data).
    */
   public int getDetectorNumForMonitor2()
   {
      return detectorMon2;
   }

   /**
    * Get the first external veto.
    * @return The first external veto (aka veto 0).  An external veto 
    * is a pulse put into the system to tell it to immediately start or 
    * stop collecting data.  Possible values:<br>
    * 0 = disable<br>
    * 1 = enable<br>
    */
   public int getFirstExternalVeto()
   {
      return externalVeto1;
   }

   /**
    * Get the second external veto.
    * @return The second external veto (aka veto 1).  An external veto 
    * is a pulse put into the system to tell it to immediately start or 
    * stop collecting data.  Possible values:<br>
    * 0 = disable<br>
    * 1 = enable<br>
    */
   public int getSecondExternalVeto()
   {
      return externalVeto2;
   }

   /**
    * Get the third external veto.
    * @return The third external veto (aka veto 2).  An external veto 
    * is a pulse put into the system to tell it to immediately start or 
    * stop collecting data.  Possible values:<br>
    * 0 = disable<br>
    * 1 = enable<br>
    */
   public int getThirdExternalVeto()
   {
      return externalVeto3;
   }

   /**
    * Get the external neutron gate (t1).
    * @return The external neutron gate (t1).  
    * The value is in microseconds.
    */
   public int getExtNeutronGateT1()
   {
      return extNeutGateT1;
   }

   /**
    * Get the external neutron gate (t2).
    * @return The external neutron gate (t2).  
    * The value is in microseconds.
    */
   public int getExtNeutronGateT2()
   {
      return extNeutGateT2;
   }

   /**
    * Get the frame synch delay.
    * @return The frame synch delay (in 4 microsecond steps).
    */
   public int getFrameSyncDelay()
   {
      return frameSyncDelay;
   }

   /**
    * Get the frame synch origin.
    * @return The frame synch origin (0:none, 1:external, 2:internal).
    */
   public int getFrameSyncOrigin()
   {
      return frameSyncOrigin;
   }

   /**
    * Get the good external neutron total (high 32 bits).
    * @return The good external neutron total (high 32 bits).
    */
   public int getGoodExtNeutronTotalHigh32()
   {
      return goodExtNeutTotalHigh32;
   }

   /**
    * Get the good external neutron total (low 32 bits).
    * @return The good external neutron total (low 32 bits).
    */
   public int getGoodExtNeutronTotalLow32()
   {
      return goodExtNeutTotalLow32;
   }

   /**
    * Get the good PPP total (high 32 bits).
    * @return The good PPP total (high 32 bits).
    */
   public int getGoodPPPTotalHigh32()
   {
      return goodPppTotalHigh32;
   }

   /**
    * Get the good PPP total (low 32 bits).
    * @return The good PPP total (low 32 bits).
    */
   public int getGoodPPPTotalLow32()
   {
      return goodPppTotalLow32;
   }

   /**
    * Get the position in the module for detector <code>detectorNum</code>.  
    * Note:  The first detector is at <code>detectorNum</code>=1 not 0.
    * @param detectorNum The number of the detector you are 
    * refering you.  Note:  for <code>detectorNum</code> to be 
    * valid, 1 <= <code>detectorNum</code> <= 
    * {@link InstrumentSection#getNumberOfDetectors() 
    * InstrumentSection.getNumberOfDetectors()}.
    * @return The position in the module for the specified detector or -1 if 
    * <code>detectorNum</code> is invalid.
    */
   public int getInputNumForDetector(int detectorNum)
   {
      if (detectorNum>=1 && detectorNum<inputNum.length)
         return inputNum[detectorNum];
      else
         return -1;
   }

   /**
    * Get the length of bulk store memory.
    * @return The length of bulk store memory (in bytes).
    */
   public int getLengthOfBulkStore()
   {
      return lengthOfBulkStore;
   }

   /**
    * Get the mask for monitor 1.
    * @return The mask for monitor 1.  
    * As written in libget.txt (the file describing the 
    * layout of ISIS RAW files), the data is written 
    * in the form c4:m4:d12.  It is hypothesized that 
    * this means that the first 4 bits hold the crate 
    * data, the next 4 bits hold the module data, and 
    * the next 12 bits hold the detector data.
    */
   public int getMaskForMonitor1()
   {
      return maskMon1;
   }

   /**
    * Get the mask for monitor 2.
    * @return The mask for monitor 2.  
    * As written in libget.txt (the file describing the 
    * layout of ISIS RAW files), the data is written 
    * in the form c4:m4:d12.  It is hypothesized that 
    * this means that the first 4 bits hold the crate 
    * data, the next 4 bits hold the module data, and 
    * the next 12 bits hold the detector data.
    */
   public int getMaskForMonitor2()
   {
      return maskMon2;
   }

   /**
    * Get the module for monitor 1.
    * @return The module for monitor 1 
    * (4 bits are used to store this information).
    */
   public int getModuleForMonitor1()
   {
      return moduleMon1;
   }

   /**
    * Get the module for monitor 2.
    * @return The module for monitor 2.  
    * (4 bits are used to store this information).
    */
   public int getModuleForMonitor2()
   {
      return moduleMon2;
   }

   /**
    * Get the module number at detector <code>detectorNum</code>.  
    * Note:  The first detector is at <code>detectorNum</code>=1 not 0.
    * @param detectorNum The number of the detector you are 
    * refering you.  Note:  for <code>detectorNum</code> to be 
    * valid, 1 <= <code>detectorNum</code> <= 
    * {@link InstrumentSection#getNumberOfDetectors() 
    * InstrumentSection.getNumberOfDetectors()}.
    * @return The module number for the specified detector or -1 if 
    * <code>detectorNum</code> is invalid.
    */
   public int getModuleNumForDetector(int detectorNum)
   {
      if (detectorNum>=1 && detectorNum<moduleNum.length)
         return moduleNum[detectorNum];
      else
         return -1;
   }

   /**
    * Get the PPP minimum value.
    * @return The PPP minimum value.
    */
   public int getPPPMinValue()
   {
      return pppMinValue;
   }

   /**
    * Get the raw external neutron total (high 32 bits).
    * @return Get the raw external neutron total (high 32 bits).
    */
   public int getRawExtNeutronTotalHigh32()
   {
      return rawExtNeutTotalHigh32;
   }

   /**
    * Get the raw external neutron total (low 32 bits).
    * @return The raw external neutron total (low 32 bits).
    */
   public int getRawExtNeutronTotalLow32()
   {
      return rawExtNeutTotalLow32;
   }

   /**
    * Get the raw PPP total (high 32 bits).
    * @return The raw PPP total (high 32 bits).
    */
   public int getRawPPPTotalHigh32()
   {
      return rawPppTotalHigh32;
   }

   /**
    * Get the raw PPP total (low 32 bits).
    * @return The raw PPP total (low 32 bits).
    */
   public int getRawPPPTotalLow32()
   {
      return rawPppTotalLow32;
   }

   /**
    * Get the secondary master pulse.
    * @return The secondary master pulse.  
    * Possible values:<br>
    * 0 = enable<br>
    * 1 = disenable<br>
    */
   public int getSecondaryMasterPulse()
   {
      return secondaryMasterPulse;
   }

   /**
    * Get the time regime number for detector <code>detectorNum</code>.  
    * Note:  The first detector is at <code>detectorNum</code>=1 not 0.
    * @param detectorNum The number of the detector you are 
    * refering you.  Note:  for <code>detectorNum</code> to be 
    * valid, 1 <= <code>detectorNum</code> <= 
    * {@link InstrumentSection#getNumberOfDetectors() 
    * InstrumentSection.getNumberOfDetectors()}.
    * @return The time regime number for the specified detector or -1 if 
    * <code>detectorNum</code> is invalid.
    */
   public int getTimeRegimeForDetector(int detectorNum)
   {
      if (detectorNum>=1 && detectorNum<timeRegimeTable.length)
         return timeRegimeTable[detectorNum];
      else
         return -1;
   }
   
   /**
    * Get a copy of the time regime table.
    * @return A copy of the time regime table.
    */
   public int[] getTimeRegimeTable()
   {
      int[] newArr = new int[timeRegimeTable.length-1];
      System.arraycopy(timeRegimeTable,1,newArr,0,newArr.length);
      return newArr;
   }

   /**
    * Get the total good events (high 32 bits).
    * @return The total good events (high 32 bits).
    */
   public int getTotalGoodEventsHigh32()
   {
      return totalGoodEventsHigh32;
   }

   /**
    * Get the total good events (low 32 bits).
    * @return The total good events (low 32 bits).
    */
   public int getTotalGoodEventsLow32()
   {
      return totalGoodEventsLow32;
   }

   /**
    * Get the user detector number for detector <code>detectorNum</code>.  
    * Note:  The first detector is at <code>detectorNum</code>=1 not 0.
    * @param detectorNum The number of the detector you are 
    * refering you.  Note:  for <code>detectorNum</code> to be 
    * valid, 1 <= <code>detectorNum</code> <= 
    * {@link InstrumentSection#getNumberOfDetectors() 
    * InstrumentSection.getNumberOfDetectors()}.
    * @return The user detector number for the specified detector or -1 if 
    * <code>detectorNum</code> is invalid.
    */
   public int getUserDetectorNumForDetector(int detectorNum)
   {
      if (detectorNum>=1 && detectorNum<userDetectorNumber.length)
         return userDetectorNumber[detectorNum];
      else
         return -1;
   }

   /**
    * Get the DAE section version number.
    * @return The DAE section version number.
    */
   public int getVersion()
   {
      return version;
   }

   /**
    * Get the word length in bulk store memory.
    * @return The word length in bulk store memory.
    */
   public int getWordLength()
   {
      return wordLength;
   }
   
   /**
    * Returns the minimum valid regime number.
    */
   public int getMinimumRegimeNumber()
   {
      return regimeInfoInformant.getMinRegimeNumber();
   }
}
