/*
 * File: Write_SXD_Grid_Info.java
 *
 * Copyright (C) 2003, Dennis Mikkelson
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
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.2  2004/06/29 16:29:32  dennis
 *  Now makes the vectors and grids private data members.
 *
 *  Revision 1.1  2004/06/24 20:13:02  dennis
 *  This class reads an sxdII.config file, then calculates and prints detector
 *  center, size and orientation information needed to make a UniformGrid
 *  object.  The information can be printed as a list of values, or in the
 *  form of java code to construct the UniformGrid objects.
 *
 */

package ISIS.Rawfile;

import gov.anl.ipns.MathTools.Geometry.*;

import gov.anl.ipns.Util.File.*;

  /**
   *  Write the information defining a data grid for a 64x64 SXD detector
   *  to System.out.  The position of each pixel is read from an SXD
   *  configuration file.  The configuration file name is the first
   *  argument on the command line.
   *
   *  NOTE: The pixels on the grid will correspond to spectra from the
   *        "raw" file. We assume that the spectra are stored in row major
   *        order and assign local coordinates to the data grid so that the
   *        local x axis is in the direction of the rows (increasing
   *        column number) and the local y axis is in the direction of 
   *        the columns (increasing row number).  
   */
public class Write_SXD_Grid_Info 
{
                      // The size and number of detectors is needed to read the
                      // configuration file, since the configuration file just
                      // lists the id, longitude, latitude and distance for 
                      // each pixel, without any "meta" data.
  static int N_ROWS = 64;
  static int N_COLS = 64;

  static int N_DET = 11;
  static int PIX_PER_DET = N_ROWS * N_COLS;
  static int N_PIX = N_DET * PIX_PER_DET;
  static Vector3D points[] = new Vector3D[N_PIX];

 
  /**
   *  Calculate detector centers, width, etc. from the pixel positions read
   *  from the SXD config file.  Either write the data to the console, or 
   *  write java code to create appropriate data grids to the console. 
   */ 
  private static void ShowMakeDet( int det, Vector3D points[] )
  {
    boolean WRITE_CODE = true;

    int r1c1 = det * PIX_PER_DET;      // first pixel 
    int r1cN = r1c1 + (N_COLS -1);     // end of first row
    int rNcN = r1c1 + PIX_PER_DET - 1; // last pixel
    int rNc1 = rNcN - (N_COLS -1);     // start of last row

                                  // center is average of first and last pixel
    Vector3D center = new Vector3D( points[r1c1] );
    center.add( points[rNcN] );
    center.multiply( 0.5f );
                                  // local x-directon in direction of row 1
    Vector3D x_vec = new Vector3D( points[r1cN] );
    x_vec.subtract( points[r1c1] );
    float width = N_COLS * x_vec.length() / (N_COLS - 1);
    x_vec.normalize();
                                  // local y-direction in direction of col 1
    Vector3D y_vec = new Vector3D( points[rNc1] );
    y_vec.subtract( points[r1c1] );
    float height = N_ROWS * y_vec.length() / (N_ROWS - 1);
    y_vec.normalize();

    float depth = 0;

    if ( !WRITE_CODE )
    {
      System.out.println("Detector : " + det + " ---------------------- " );
      System.out.println("  Center : " + center );
      System.out.println("  x_vec  : " + x_vec  );
      System.out.println("  y_vec  : " + y_vec  );
      System.out.println("  width  : " + width  );
      System.out.println("  height : " + height  );
    }
    else
    {
      System.out.println();
      ShowMakeVector( det, "center", center ); 
      ShowMakeVector( det, "x_vec",  x_vec  ); 
      ShowMakeVector( det, "y_vec",  y_vec  ); 

      System.out.println();
      System.out.println("  private static final UniformGrid  SXD_Det" 
                               +(det+1)+" =");
      System.out.print  ("         new UniformGrid( " );
      System.out.print  ( (det+1) + ", " );
      System.out.print  ("units"  + ", " );
      System.out.print  ("center" + (det+1) + ", " );
      System.out.print  ("x_vec"  + (det+1) + ", " );
      System.out.println("y_vec"  + (det+1) + ", " );
      System.out.print  ("                          ");
      System.out.print  ( width  + "f, " );
      System.out.print  ( height + "f, " );
      System.out.print  ( depth  + "f, " );
      System.out.print  ( N_ROWS + ", " );
      System.out.println( N_COLS + " );" );
    }
  }


  /**
   *  Write the code to construct an individual vector for the center or local
   *  coordinate basis vectors.
   */
  private static void ShowMakeVector( int det, String name, Vector3D vec )
  {
    System.out.print  (" private static Vector3D " + name + (det+1) + 
                       " = new Vector3D( ");
    System.out.print  ( vec.get()[0] + "f, " );
    System.out.print  ( vec.get()[1] + "f, " );
    System.out.println( vec.get()[2] + "f );" );
  } 


  /**
   *  Load a sxdII.config file and print the information for each detector
   *  either as a list of raw information, or as java code to construct ISAW
   *  data grids.  Since the sxdII.config file does not have any descriptive
   *  information, this program will need to be updated if the number (11) 
   *  or size (64x64) of the detectors is changed.
   *
   *  @param args The first command line parameter specifies the fully qualified
   *              sxdII.config file.  If not present, a default file (that
   *              probably does not exist on your system) will be tried.
   */
  public static void main( String args[] )
  {
    String filename = "/home/dennis/ISIS_SXD/sxdII.config";
    if ( args.length > 0 )
      filename = args[0];

    float coords[] = new float[3];

    float r, theta, phi;
    DetectorPosition pos = new DetectorPosition();
                                              // load the pixel locations into
    try                                       // a list of Vector3D points
    {
      TextFileReader tfr = 
                     new TextFileReader("/home/dennis/ISIS_SXD/sxdII.config");
      int i = 0;
      for ( int det = 0; det < N_DET; det++ )
        for ( int pix = 0; pix < PIX_PER_DET; pix++ )
      {
         tfr.read_float();
         theta = tfr.read_float(); 
         phi   = 90 - tfr.read_float();
         r     = tfr.read_float() / 1000;

         theta *= (float)(Math.PI/180);
         phi *= (float)(Math.PI/180);

         pos.setSphericalCoords( r, theta, phi );
         coords = pos.getCartesianCoords();
         points[i] = new Vector3D( coords );
  
         i++;
      }
    }
    catch ( Exception e )
    {
       System.out.println("EXCEPTION " + e );
       e.printStackTrace();
    }

    for ( int det = 0; det < N_DET; det++ )
      ShowMakeDet( det, points );
  }

}
