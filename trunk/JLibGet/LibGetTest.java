/*
 * File:  LibGetTest.java
 *
 * Copyright (C) 2004 Chris M. Bouzek
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
 *           Chris Bouzek <coldfusion78@yahoo.com>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA and by
 * the National Science Foundation under grant number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 * 
 * $Log$
 * Revision 1.3  2004/06/16 16:03:51  kramer
 * Added the GNU license header.
 *
 * Revision 1.2  2004/04/30 00:18:18  bouzekc
 * New version.  Old version should not have been in CVS.
 * 
 */

/*
 * Original copyright ??
 * Converted to Java by Chris M. Bouzek, February 2004
 */
package ISIS.JLibGet;

/**
 * Class to test the Jlibget library.
 */
public class LibGetTest {
  //~ Static fields/initializers ***********************************************

  private static final int MAXTCB = 20000;

  //~ Instance fields **********************************************************

  private int     maxTCB;
  private int     errCode;
  private int     NTCB;
  private int     iSpec;
  private boolean found;
  private int[]   counts = new int[MAXTCB];
  private float[] TCB    = new float[MAXTCB];
  private String  runID;

  //~ Methods ******************************************************************

  /**
   * Main method for execution.
   */
  public static void main( String[] args ) {
    new LibGetTest(  ).runTest(  );
  }

  /**
   * Runs a test of the libget code.
   */
  public void runTest(  ) {
    System.out.print( "Input data file: " );

    //READ(5,9000) RUNID
    //9000 FORMAT(A)
    //      RUNID=/usr/local/genie/examples/data/hrp08639.raw
    //CHANGED-THIS DID NOT ORIGINALLY RETURN A VALUE
    // found = ISISio.openFile(runID);
    if( !found ) {
      System.out.println( "File for " + runID + " not found" );

      return;
    }

    //CHANGED-THIS DID NOT ORIGINALLY RETURN A VALUE OR THROW AN EXCEPTION
    try {
      //TCB =ISISio.getParr(runID,"TCB1", maxTCB, NTCB, errCode);
    } catch( Exception e ) {
      System.out.println( "Error in io.getParr: " );
      e.printStackTrace(  );

      return;
    }

    System.out.println( "Read " + NTCB + " time channels." );

    //read spectrum 1
    iSpec = 1;

    //    CHANGED-THIS DID NOT ORIGINALLY RETURN A VALUE OR THROW AN EXCEPTION
    try {
      //counts = ISISio.getDat(runID,iSpec,1,MAXTCB);
    } catch( Exception e ) {
      System.out.println( "Error in io.getDat: " );
      e.printStackTrace(  );

      return;
    }

    System.out.println( "Read spectrum " + iSpec );

    for( int i = 1; i <= Math.min( 10, NTCB ); i++ ) {
      //WRITE(6,9010) TCB(I), COUNTS(I)
      System.out.println( "TOF = " + TCB[i] + ", Counts = " + counts[i] );
    }

    //9010 FORMAT('TOF = ', G12.3, ', Counts = ', I7)
  }
}
