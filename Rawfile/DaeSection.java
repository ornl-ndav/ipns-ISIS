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
   * detectors.
   * 
   * @see InstrumentSection#nDet
   */
  protected int[] crateNum               = new int[0];
  /**
   * Position in module for each detector.  The length of 
   * this array equals 1 more than the number of 
   * detectors.
   * 
   * @see InstrumentSection#nDet
   */
  protected int[] inputNum               = new int[0];
  /**
   * The module number for each detector.  The length of 
   * this array equals 1 more than the number of 
   * detectors.
   * 
   * @see InstrumentSection#nDet
   */
  protected int[] moduleNum              = new int[0];
  /**
   * Time regime number table.  The length of 
   * this array equals 1 more than the number of 
   * detectors.
   * 
   * @see InstrumentSection#nDet
   */
  protected int[] timeRegimeTable        = new int[0];
  /**
   * 'User detector number' for each detector.  The length of 
   * this array equals 1 more than the number of 
   * detectors.
   * 
   * @see InstrumentSection#nDet
   */
  protected int[] userDetectorNumber     = new int[0];
  /** Crate for monitor 1. */
  protected int   crateMon1;
  /** Crate for monitor 2. */
  protected int   crateMon2;
  /** Detector for monitor 1. */
  protected int   detectorMon1;
  /** Detector for monitor 2. */
  protected int   detectorMon2;
  /** ext. neutron gate (t1) (in microseconds). */
  protected int   extNeutGateT1;
  /** ext. nuetron gate (t2) (in microseconds). */
  protected int   extNeutGateT2;
  /**External veto 0 (0 dis, 1 en). */
  protected int   externalVeto1;
  /**External veto 1 (0 dis, 1 en). */
  protected int   externalVeto2;
  /**External veto 2 (0 dis, 1 en). */
  protected int   externalVeto3;
  /** Frame synch delay (4 microsecond steps). */
  protected int   frameSyncDelay;
  /** Frame synch origin (0:none/1:ext/2:int). */
  protected int   frameSyncOrigin;
  /** Good ext. neut tot (high 32 bits). */
  protected int   goodExtNeutTotalHigh32;
  /** Good ext. neut tot (low 32 bits). */
  protected int   goodExtNeutTotalLow32;
  /** Good PPP total (high 32 bits). */
  protected int   goodPppTotalHigh32;
  /** Good PPP total (low 32 bits). */
  protected int   goodPppTotalLow32;
  /** Length of bulk store memory (bytes). */
  protected int   lengthOfBulkStore;
  /** Mask for monitor 1. */
  protected int   maskMon1;
  /** Mask for monitor 2. */
  protected int   maskMon2;
  /** Module for monitor 1. */
  protected int   moduleMon1;
  /** Module for monitor 2. */
  protected int   moduleMon2;
  /** PPP minimum value. */
  protected int   pppMinValue;
  /** Raw ext. neut tot (high 32 bits). */
  protected int   rawExtNeutTotalHigh32;
  /** Raw ext. neut tot (low 32 bits). */
  protected int   rawExtNeutTotalLow32;
  /** Raw PPP total (high 32 bits). */
  protected int   rawPppTotalHigh32;
  /** Row PPP total (low 32 bits). */
  protected int   rawPppTotalLow32;
  /** Secondary Master Pulse (0:en, 1:dis). */
  protected int   secondaryMasterPulse;
  /** Total GOOD EVENTS (high 32 bits). */
  protected int   totalGoodEventsHigh32;
  /** Total GOOD EVENTS (low 32 bits). */
  protected int   totalGoodEventsLow32;
  /** DAE section version number. */
  protected int   version;
  /** Word length in bulk store memory. */
  protected int   wordLength;

  //~ Constructors -------------------------------------------------------------

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
      version                  = Header.readUnsignedInteger( rawFile, 4 );
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
    } catch( IOException ex ) {}

    //looks complete based on libget.txt
  }

  //~ Methods ------------------------------------------------------------------

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
      System.out.println( 
        "Detector   Crate   Module  Input   timeRegime  userDetectorNum" );

      for( int ii = 1; ii <= is.nDet; ii++ ) {
        System.out.println( "  " + ii + "          " + ds.crateNum[ii] +
          "       " + ds.moduleNum[ii] + "      " + ds.inputNum[ii] +
          "          " + ds.timeRegimeTable[ii] + "           " +
          ds.userDetectorNumber[ii] );
      }
    } catch( IOException ex ) {}
  }
}
