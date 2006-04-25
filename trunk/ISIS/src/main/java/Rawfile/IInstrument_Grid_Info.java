/*
 * File: IInstrument_Grid_Info.java
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
 *  Revision 1.1  2004/06/29 16:19:24  dennis
 *  Initial version of interface for classes that provide information
 *  about detector data grids.  This will allow us to get detector
 *  position information for different ISIS instruments "polymorphically"
 *  by instantiating an appropriate class for an instrument in an
 *  ISIS data retriever.
 *
 *
 */

package ISIS.Rawfile;

import DataSetTools.dataset.*;

/**
 *  This interface describes methods needed to obtain information about
 *  the detectors and the spectra associated with the detectors for a
 *  "raw" file from ISIS, which does not contain detector position information.
 */

public  interface  IInstrument_Grid_Info
{

  /* --------------------------- numGrids ------------------------------- */
  /**
   *  Get the number of detectors (i.e. data grids) for the instrument 
   *
   *  @return the number of data grids that can be obtained
   */
  public int numGrids();


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
  public IDataGrid getGridAtIndex( int index );


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
  public int getFirstSpectrumID( int index );

}
