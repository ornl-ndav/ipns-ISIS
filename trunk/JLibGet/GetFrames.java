/*
 * File:  GetFrames.java
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
 * $Log$
 * Revision 1.4  2004/06/16 21:01:54  kramer
 * Now the source displays the cvs logs for the file.
 *
 */

/*  PROGRAM get_frames
 *
 * Original copyright ??
 * Converted to Java by Chris M. Bouzek, February 2004
 */
package ISIS.JLibGet;

/**
 * This is a driver class for ISISio.
 */
public class GetFrames {
  //~ Instance fields **********************************************************

  private String  runID;
  private int     good_frames;
  private int     total_frames;
  private int[]   rpb1        = new int[32];
  private float[] rpb2        = new float[32];
  private float   total_uamps;
  private float   good_uamps;
  private float   perc_bad;
  private boolean found;
  ISISio          myISISio    = new ISISio(  );

  //~ Methods ******************************************************************

  /**
   * Main method for execution.
   */
  public static void main( String[] args ) {
    new GetFrames(  ).loadRAWFile(  );
  }

  /**
   * Loads an ISIS RAW file using the command line.
   */
  public void loadRAWFile(  ) {
    //get the data file to read
    //System.out.print( "Input data file: " );
    // runID = Command.Script_Class_List_Handler.getString(  );
    runID   = "/home/coldfire/ISISRunFiles/LOQ83396.RAW";

    //READ(5,'(A)') runid
    // Open the RAW file and see if it was able to be opened
    //CHANGED-THIS DID NOT ORIGINALLY RETURN A VALUE
    found   = myISISio.openFile( runID );

    if( !found ) {
      System.out.println( "File for " + runID + " not found!" );

      return;
    }

    String   name   = "USER";
    String[] cValue = new String[500];

    myISISio.getParc( runID, name, cValue );

    for( int i = 0; i < cValue.length; i++ ) {
      if( cValue[i] != null ) {
        System.out.println( cValue[i] );
      }
    }

    // Get the array of integers from the RPB block

    /*myISISio.getPari( runID, "RPB", rpb1 );
       good_frames    = rpb1[10];
       total_frames   = rpb1[11];
       // Now get RPB Block as reals in order to extract the uAmps
       // (for this case the block to get is RRPB)
    
       myISISio.getParr( runID, "RRPB", rpb2 );
       good_uamps    = rpb2[8];
       total_uamps   = rpb2[9];
       perc_bad      = ( ( float )( total_frames ) - ( float )( good_frames ) ) / ( float )( total_frames );
       perc_bad      = perc_bad * 100.0f;
       System.out.println( " Total No. of frames : " + total_frames );
       System.out.println( "         Good frames : " + good_frames );
       System.out.println( " % of bad frames     : " + perc_bad );
       System.out.println(  );
       System.out.println( " Total Beam current  :" + total_uamps );
       System.out.println( "  Good Beam current  :" + good_uamps );*/
  }
}
