/*
 * File:  ISISRawfileRetriever.java
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
 * Revision 1.1  2004/04/30 00:16:09  bouzekc
 * Added to CVS.
 *
 */
package ISIS.retriever;

import DataSetTools.dataset.*;
import DataSetTools.dataset.DataSet;

import DataSetTools.retriever.Retriever;

import DataSetTools.util.*;

import DataSetTools.viewer.*;

import ISIS.Rawfile.*;

import gov.anl.ipns.MathTools.Geometry.*;
import gov.anl.ipns.Util.Sys.*;


/**
 * Retriever for ISIS RAW files.
 */
public class ISISRawfileRetriever extends Retriever {
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
  public ISISRawfileRetriever( String data_source_name ) {
    super( data_source_name );

    int    num_histograms;
    String file_name = StringUtil.setFileSeparator( data_source_name );

    file_name = FilenameUtil.fixCase( file_name );

    if( file_name == null ) {
      System.out.println( 
        "ERROR: file " + file_name + " not found in ISISRawfileRetriever" );
      rawfile = null;

      return;
    }

    try {
      rawfile          = new Rawfile( file_name );

      //not correct in all cases
      num_histograms   = 1;

      //maybe not correct in all cases
      histogram        = new int[3 * num_histograms];

      //not correct in all cases
      num_data_sets    = 1;
      rawfile.Close(  );
    } catch( Exception e ) {
      rawfile = null;
      System.out.println( "Exception in ISISRawfileRetriever constructor" );
      System.out.println( "Exception is " + e );
      e.printStackTrace(  );
    }
  }

  //~ Methods ******************************************************************

  /**
   * Gets a DataSet.  This currently just grabs all the spectra from an ISIS
   * rawfile and creates a data entry for each spectra, i.e. this is not
   * guaranteed to  work correctly.
   *
   * @param data_set_num Unused.
   *
   * @return The DataSet consisting of all spectra.
   */
  public DataSet getDataSet( int data_set_num ) {
    DataSet ds2 = new DataSetFactory( rawfile.RunTitle(  ) ).getDataSet(  );

    setOneTimeDSAttributes( ds2 );

    Data             data;
    DetectorPosition detPos;
    int              totalSpectra = rawfile.numSpectra(  )[0];
    float[]          errors       = new float[1];

    //data block consists of a spectra and an XScale
    //XScale comes from tcb, and element of TCB at time regime
    VariableXScale xscale = new VariableXScale( rawfile.TCBArray(  ) );

    //ID is spectrum number
    for( int i = 0; i < totalSpectra; i++ ) {
      data     = Data.getInstance( 
          xscale, rawfile.Get1DSpectrum( i ), errors, i );
      detPos   = new DetectorPosition(  );
      detPos.setCylindricalCoords( 
        rawfile.FlightPath( i ),
        ( float )( rawfile.DetectorAngle( i ) * ( Math.PI / 180 ) ), 0 );

      //two theta angle
      data.setAttribute( new DetPosAttribute( Attribute.DETECTOR_POS, detPos ) );

      //the detector IDs in subgroup may not be necessary
      data.setAttribute( 
        new IntListAttribute( 
          Attribute.DETECTOR_IDS, rawfile.IdsInSubgroup( i ) ) );
      setOneTimeBlockAttributes( data );
      ds2.addData_entry( data );
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
    ISISRawfileRetriever rr = new ISISRawfileRetriever( args[0] );
    DataSet          ds = rr.getDataSet( 0 );

    new ViewManager( ds, IViewManager.IMAGE );
  }

  /**
   * @return The number of DataSets.
   */
  public int numDataSets(  ) {
    if( rawfile == null ) {
      return 0;
    }

    return num_data_sets;
  }

  /**
   * Sets one time attributes (e.g. end date, end time, etc.) for the given
   * Data block.
   *
   * @param db The Data block  to set attributes for.
   */
  private void setOneTimeBlockAttributes( Data db ) {
    //stuff in powder diffractometer DS and data block
    //initial path (L1 in ISIS-speak)
    db.setAttribute( 
      new FloatAttribute( Attribute.INITIAL_PATH, rawfile.SourceToSample(  ) ) );

    //run number
    db.setAttribute( 
      new IntAttribute( Attribute.RUN_NUM, rawfile.RunNumber(  ) ) );

    //number of pulses on target = good proton charge?
    db.setAttribute( 
      new FloatAttribute( 
        Attribute.NUMBER_OF_PULSES, rawfile.GoodProtonCharge(  ) ) );

    //total count = total proton charge?
    db.setAttribute( 
      new FloatAttribute( Attribute.TOTAL_COUNT, rawfile.TotalProtonCharge(  ) ) );
  }

  /**
   * Sets one time attributes (e.g. end date, end time, etc.) for the given
   * DataSet.
   *
   * @param ds The DataSet  to set attributes for.
   */
  private void setOneTimeDSAttributes( DataSet ds ) {
    //end date
    ds.setAttribute( 
      new StringAttribute( Attribute.END_DATE, rawfile.EndDate(  ) ) );

    //end time
    ds.setAttribute( 
      new StringAttribute( Attribute.END_TIME, rawfile.EndTime(  ) ) );

    //run title
    ds.setAttribute( 
      new StringAttribute( Attribute.RUN_TITLE, rawfile.RunTitle(  ) ) );

    //user name
    ds.setAttribute( 
      new StringAttribute( Attribute.USER, rawfile.UserName(  ) ) );

    //instrument name
    ds.setAttribute( 
      new StringAttribute( Attribute.INST_NAME, rawfile.InstrumentName(  ) ) );

    //filename
    ds.setAttribute( 
      new StringAttribute( Attribute.FILE_NAME, data_source_name ) );

    //run number
    ds.setAttribute( 
      new IntAttribute( Attribute.RUN_NUM, rawfile.RunNumber(  ) ) );

    //number of pulses on target = good proton charge?
    ds.setAttribute( 
      new FloatAttribute( 
        Attribute.NUMBER_OF_PULSES, rawfile.GoodProtonCharge(  ) ) );
    //this stuff below is not used for gppd, so I assume it will not be used for hrpd
    //start date
    //ds.setAttribute( new StringAttribute( Attribute.START_DATE, rawfile.StartDate(  ) ) );
    //start time
    //ds.setAttribute( new StringAttribute( Attribute.START_TIME, rawfile.StartTime(  ) ) );
  }
}
