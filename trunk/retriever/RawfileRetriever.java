/*
 * File:  RawfileRetriever.java
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
 * Revision 1.1  2004/04/30 00:02:09  bouzekc
 * Initial revision
 *
 */
package ISIS.retriever;

import DataSetTools.dataset.DataSet;

import DataSetTools.retriever.Retriever;

import DataSetTools.util.*;

import ISIS.Rawfile.*;

import DataSetTools.dataset.*;

import gov.anl.ipns.Util.Sys.*;
import DataSetTools.viewer.*;


/**
 * Retriever for ISIS RAW files.
 */
public class RawfileRetriever extends Retriever {
  //~ Instance fields **********************************************************

  private Rawfile rawfile;
  private int[]   histogram;
  private int     num_data_sets;

  //~ Constructors *************************************************************

  /**
   * Construct a Rawfile retriever for a specific file.
   *
   * @param data_source_name The fully qualified file name for the Rawfile.
   */
  public RawfileRetriever( String data_source_name ) {
    super( data_source_name );

    int     first_id;
    int     last_id;
    int     num_histograms;
    boolean has_monitors;
    boolean has_detectors;
    boolean has_pulse_height;
    String  file_name = StringUtil.setFileSeparator( data_source_name );

    file_name = FilenameUtil.fixCase( file_name );

    if( file_name == null ) {
      System.out.println( 
        "ERROR: file " + file_name + " not found in RawfileRetriever" );
      rawfile = null;

      return;
    }

    try {
      rawfile = new Rawfile( file_name );
      //rawfile.LeaveOpen(  );

      /*instrument_type  = rawfile.InstrumentType();
         if ( instrument_type == InstrumentType.UNKNOWN )
           instrument_type = InstrumentType.getIPNSInstrumentType( file_name );*/

      num_histograms   = 1;  //rawfile.NumOfHistograms();

      //data_set_type    = new int[ 3 * num_histograms ];
      histogram        = new int[3 * num_histograms];
      num_data_sets    = 1;

      /*for( int hist = 1; hist <= num_histograms; hist++ ) {
        if( rawfile.IsHistogramGrouped( hist ) ) {
          first_id           = rawfile.MinSubgroupID( hist );
          last_id            = rawfile.MaxSubgroupID( hist );
          has_monitors       = false;
          has_detectors      = false;
          has_pulse_height   = false;

          for( int group_id = first_id; group_id < last_id; group_id++ ) {
            if( rawfile.IsSubgroupBeamMonitor( group_id ) ) {
              has_monitors = true;
            } else if( rawfile.IsPulseHeight( group_id ) ) {
              has_pulse_height = true;
            } else {
              has_detectors = true;
            }
          }

          if( has_monitors ) {
            data_set_type[num_data_sets]   = MONITOR_DATA_SET;
            histogram[num_data_sets]       = hist;
            num_data_sets++;

            if( instrument_type == InstrumentType.TOF_DG_SPECTROMETER ) {
              calculated_E_in = CalculateEIn(  );
            }
          }

          if( has_pulse_height ) {
            data_set_type[num_data_sets]   = PULSE_HEIGHT_DATA_SET;
            histogram[num_data_sets]       = hist;
            num_data_sets++;
          }

          if( has_detectors ) {
            data_set_type[num_data_sets]   = HISTOGRAM_DATA_SET;
            histogram[num_data_sets]       = hist;
            num_data_sets++;
          }
        }
      }*/

      rawfile.Close(  );
    } catch( Exception e ) {
      rawfile = null;
      System.out.println( "Exception in RawfileRetriever constructor" );
      System.out.println( "Exception is " + e );
      e.printStackTrace(  );
    }
  }

  //~ Methods ******************************************************************

  /**
   * Gets a DataSet.  This currently just grabs all the spectra from an ISIS 
   * rawfile and creates a data entry for each spectra.  Note that this method is
   * TOTALLY kludged together.  I just wanted to see if I could load something 
   * into ISAW.
   *
   * @param data_set_num Unused.
   *
   * @return The DataSet consisting of all spectra.
   */
  public DataSet getDataSet( int data_set_num ) {
    DataSet ds = new DataSet( rawfile.RunTitle(  ), "" );
    DataSet ds2 = new DataSetFactory( rawfile.RunTitle(  ) ).getDataSet(  );
    
    //two theta angle
    ds2.setAttribute( ( Attribute )Attribute.Build( Attribute.DELTA_2THETA, "" + rawfile.getInstSect(  ).getDetectorAngle( 0 ) ) );
    
    int totalSpectra = rawfile.numSpectra(  )[0];
    //System.out.println( "Number of spectra " + rawfile.numSpectra(  )[0] );
    float[] errors = new float[1];
    
    //data block consists of a spectra and an XScale
    //XScale comes from tcb, and element of TCB at time regime
    //add 1 to tcb number since this is histogram
    //try to reuse XScale
    UniformXScale xscale = new UniformXScale( 0, 50, rawfile.Get1DSpectrum( 0 ).length + 1 );
    
    //ID is spectrum number
    //int i = 129;
    for( int i = 0; i < totalSpectra; i++ ) {
      ds2.addData_entry( Data.getInstance( xscale, rawfile.Get1DSpectrum( i ), errors, i ) );
    }
    return ds2;
  }

  /**
   * This returns the type of the DataSet.  Currently it just returns
   * Retriever.HISTOGRAM_DATA_SET.
   */
  public int getType( int data_set_num ) {
    return Retriever.HISTOGRAM_DATA_SET;
  }

  /**
   * Testbed.
   */
  public static void main( String[] args ) {
    RawfileRetriever rr = new RawfileRetriever( args[0] );
    DataSet ds = rr.getDataSet( 0 );
    
    new ViewManager( ds, IViewManager.IMAGE );
  }

  /**
   * @return The number of DataSets. 
   */
  public int numDataSets(  ) {
    if ( rawfile == null )
      return 0;
 
    return num_data_sets;
  }
}
