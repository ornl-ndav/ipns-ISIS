/*
 * File: SXD_Grids.java
 *
 * Copyright (C) 2004, Dennis Mikkelson
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
 *  Revision 1.2  2004/06/29 16:20:42  dennis
 *  Now implements IInstrument_Grid_Info.
 *  Made all data members private static.
 *
 *  Revision 1.1  2004/06/24 20:14:29  dennis
 *  This class contains an array of UniformGrid objects representing the
 *  detectors for the SXD at ISIS.
 *
 */

package ISIS.Rawfile;

import gov.anl.ipns.MathTools.Geometry.*;
import DataSetTools.dataset.*;

public class SXD_Grids  implements IInstrument_Grid_Info
{
  static String units = "meter";

  //  The following lines constructing vectors and grids were generated 
  //  by the Write_SXD_Grid_Info class.
   
  private static Vector3D center1 = 
                  new Vector3D( -0.17850482f, 0.13697158f, 3.7252903E-9f );
  private static Vector3D x_vec1 = new Vector3D( 0.0f, 0.0f, 1.0f );
  private static Vector3D y_vec1 = 
                  new Vector3D( -0.6087615f, -0.7933533f, 0.0f );

  private static final UniformGrid  SXD_Det1 =
         new UniformGrid( 1, units, center1, x_vec1, y_vec1, 
                          0.19199952f, 0.19199656f, 0.0f, 64, 64 );

  private static Vector3D center2 = 
                  new Vector3D( 7.9348683E-7f, 0.22500008f, 3.7252903E-9f );
  private static Vector3D x_vec2 = new Vector3D( 0.0f, 0.0f, 1.0f );
  private static Vector3D y_vec2 = new Vector3D( -1.0f, 3.54793E-6f, 0.0f );

  private static final UniformGrid  SXD_Det2 =
         new UniformGrid( 2, units, center2, x_vec2, y_vec2, 
                          0.19199952f, 0.19199814f, 0.0f, 64, 64 );

  private static Vector3D center3 = 
                  new Vector3D( 0.17766427f, 0.13805903f, 3.7252903E-9f );
  private static Vector3D x_vec3 = new Vector3D( 0.0f, 0.0f, 1.0f );
  private static Vector3D y_vec3 = 
                  new Vector3D( -0.6135963f, 0.78961986f, 0.0f );

  private static final UniformGrid  SXD_Det3 =
         new UniformGrid( 3, units, center3, x_vec3, y_vec3, 
                          0.19199952f, 0.19199975f, 0.0f, 64, 64 );

  private static Vector3D center4 = 
                  new Vector3D( 0.17709652f, -0.13878655f, 3.7252903E-9f );
  private static Vector3D x_vec4 = new Vector3D( 0.0f, 0.0f, 1.0f );
  private static Vector3D y_vec4 = new Vector3D( 0.6168298f, 0.7870965f, 0.0f );

  private static final UniformGrid  SXD_Det4 =
         new UniformGrid( 4, units, center4, x_vec4, y_vec4, 
                          0.19199952f, 0.19199975f, 0.0f, 64, 64 );
  
  private static Vector3D center5 = 
                  new Vector3D( 7.9348683E-7f, -0.22500008f, 3.7252903E-9f );
  private static Vector3D x_vec5 = new Vector3D( 0.0f, 0.0f, 1.0f );
  private static Vector3D y_vec5 = new Vector3D( 1.0f, 3.54793E-6f, 0.0f );

  private static final UniformGrid  SXD_Det5 =
         new UniformGrid( 5, units, center5, x_vec5, y_vec5, 
                          0.19199952f, 0.19199814f, 0.0f, 64, 64 );

  private static Vector3D center6 = 
                  new Vector3D( -0.17850482f, -0.13697158f, 3.7252903E-9f );
  private static Vector3D x_vec6 = new Vector3D( 0.0f, 0.0f, 1.0f );
  private static Vector3D y_vec6 = 
                  new Vector3D( 0.6087615f, -0.7933533f, 0.0f );

  private static final UniformGrid  SXD_Det6 =
         new UniformGrid( 6, units, center6, x_vec6, y_vec6, 
                          0.19199952f, 0.19199656f, 0.0f, 64, 64 );

  private static Vector3D center7 = 
                  new Vector3D( 8.381903E-7f, 0.19091913f, -0.19091907f );
  private static Vector3D x_vec7 = 
                  new Vector3D( 5.9131605E-7f, 0.7071071f, 0.7071064f );
  private static Vector3D y_vec7 = new Vector3D( -1.0f, 2.562375E-6f, 0.0f );

  private static final UniformGrid  SXD_Det7 =
         new UniformGrid( 7, units, center7, x_vec7, y_vec7, 
                          0.19199997f, 0.19199955f, 0.0f, 64, 64 );

  private static Vector3D center8 = 
                  new Vector3D( 0.19489503f, -3.3527613E-8f, -0.18685828f );
  private static Vector3D x_vec8 = 
                  new Vector3D( 0.6920673f, 3.5478973E-7f, 0.72183293f );
  private static Vector3D y_vec8 = new Vector3D( 0.0f, 1.0f, 0.0f );

  private static final UniformGrid  SXD_Det8 =
         new UniformGrid( 8, units, center8, x_vec8, y_vec8, 
                          0.1919999f, 0.19200028f, 0.0f, 64, 64 );

  private static Vector3D center9 = 
                  new Vector3D( 3.7625432E-7f, -0.19091904f, -0.19091907f );
  private static Vector3D x_vec9 = 
                  new Vector3D( 5.479525E-6f, -0.70710754f, 0.707106f );
  private static Vector3D y_vec9 = new Vector3D( 1.0f, 2.562375E-6f, 0.0f );

  private static final UniformGrid  SXD_Det9 =
         new UniformGrid( 9, units, center9, x_vec9, y_vec9, 
                          0.19200009f, 0.19199955f, 0.0f, 64, 64 );

  private static Vector3D center10 = 
                  new Vector3D( -0.19091937f, 5.364418E-7f, -0.19091907f );
  private static Vector3D x_vec10 = 
                  new Vector3D( -0.70710754f, -5.6766303E-6f, 0.707106f );
  private static Vector3D y_vec10 = new Vector3D( 0.0f, -1.0f, 0.0f );

  private static final UniformGrid  SXD_Det10 =
         new UniformGrid( 10, units, center10, x_vec10, y_vec10, 
                          0.19200009f, 0.1919989f, 0.0f, 64, 64 );

  private static Vector3D center11 = new Vector3D( 0.0f, 0.0f, -0.27800003f );
  private static Vector3D x_vec11 = new Vector3D( -1.0f, 0.0f, 0.0f );
  private static Vector3D y_vec11 = new Vector3D( 0.0f, -1.0f, 0.0f );

  private static final UniformGrid  SXD_Det11 =
         new UniformGrid( 11, units, center11, x_vec11, y_vec11, 
                          0.19199978f, 0.19199978f, 0.0f, 64, 64 );

  /** 
   *  Array containing the 11 UniformGrid objects describing the 11 detectors 
   *  on the SXD at ISIS.
   */
  private static final UniformGrid grid_list[] =
         { SXD_Det1, SXD_Det2, SXD_Det3, SXD_Det4,   SXD_Det5, SXD_Det6,
           SXD_Det7, SXD_Det8, SXD_Det9, SXD_Det10,  SXD_Det11         };

  private static final int first_spec_id[] =
         {        1,     4101,     8201,     12301,     16401,    20501,
              24601,    28701,    32801,     36901,     41001          };


  /* --------------------------- numGrids ------------------------------- */
  /**
   *  Get the number of detectors (i.e. data grids) for the SXD
   *
   *  @return the number of data grids that can be obtained
   */
  public int numGrids()
  {
    return grid_list.length;
  }

  
  /* -------------------------- getGridAtIndex --------------------------- */
  /**
   *  Get the data grid at the specified index in the list of grids for
   *  this instrument.
   *
   *  @param  index   The index of the grid in the list
   * 
   *  @return A copy of the data grid at the specified index, or null if
   *          the index is invalid.
   */
  public IDataGrid getGridAtIndex( int index )
  {
    if ( index >= 0 && index < grid_list.length )
      return new UniformGrid( grid_list[ index ], false );
    else
      return null;
  }


  /* ------------------------- getFirstSpectrumID ------------------------- */
  /**
   *  Get the ID of the first spectrum for the data grid at the specified
   *  index in the list of data grids for this instrument.  We assume that
   *  a contiguous block of spectrum IDs are associated with each detector,
   *  in row major order.
   *
   *  @param index  The index of the grid in the list
   *
   *  @return The ID of of the first spectrum for the grid
   */
  public int getFirstSpectrumID( int index )
  {
    if ( index >= 0 && index < grid_list.length )
      return first_spec_id[ index ];
    else
      return -1;
  }

}
