/*
 * File:  GetSect.java
 *
 * Copyright (C) 2004 Chris M. Bouzek
 * adapted from FORTRAN routines 
 * originally written by Freddie Akeroyd, ISIS
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

/*
 * File:  getsect.c - A fast interface to the old GenieII get routines.
 * Author:  Freddie Akeroyd, ISIS computer group
 *
 * Converted to Java March 2004 Chris M. Bouzek
 */
package ISIS.JLibGet;

import java.io.*;


/**
 * Basically, we just read the whole file into "file_buffer" and then catch
 * calls to the old FORTRAN GETSECT routine
 */
public class GetSect {
  //~ Static fields/initializers ***********************************************

  //"magic" number-it is the largest positive value a 32 bit integer can hold, and we'll
  //need it for the buffer size checking  
  private static final int MAX_BUFFER_SIZE = 2147483647;

  //~ Instance fields **********************************************************

  private boolean          readWholeFile     = true;
  private String           file_name;  // Name of the file to buffer (passed from fortran)
  private byte[]           file_buffer       = new byte[50];
  private long             file_size         = 0;  // How big "file_name" is  
  private long             buffer_iunit      = 0;  // Fortran unit associated with "file_buffer" 
  private RandomAccessFile file_fd           = null;
  private int              FOPEN_BUFFER_SIZE = 0;

  //~ Methods ******************************************************************

  /**
   * Gets a segment from the data file.
   *
   * @param istart The index to start at.
   * @param ilong The length of the segment.
   * @param ivalue The array to store the segment in.
   * @param iunit Used for DAE and CRPT_ACCESS-currently unnecessary
   *
   * @return Error code information.
   */
  public int getSect( int istart, int ilong, int[] ivalue, int iunit ) {
    return getsectFile( istart, ilong, ivalue );
  }

  /**
   * 0 on success, 1 on failure
   */
  public int fastGetInit( String name, int iUnit ) {
    if( ( name == null ) || ( name.length(  ) <= 0 ) ) {
      FErrorAddDummy.fErrorAdd( "FASTGET_INIT", "null file name given", "" );

      return 1;
    }

    String error_message;
    int    first_call = 1;

    if( first_call == 1 ) {
      first_call          = 0;

      //I just picked this number (2^16) because I felt like it
      FOPEN_BUFFER_SIZE   = 65536;
    }

    file_name      = name;
    buffer_iunit   = iUnit;

    RandomAccessFile fd;

    try {
      fd = new RandomAccessFile( file_name, "r" );
    } catch( IOException ioe ) {
      error_message = "Cannot open " + file_name;
      FErrorAddDummy.fErrorAdd( 
        "FASTGET_INIT", error_message, "Check file exists and is readable" );

      return 1;
    }

    try {
      file_size = fd.length(  );
    } catch( IOException ioe6 ) {
      error_message = "Error retrieving length of " + file_name;
      FErrorAddDummy.fErrorAdd( 
        "FASTGET_INIT", error_message, "This shouldn't happen!" );

      return 1;
    }

    if( file_size > MAX_BUFFER_SIZE ) {
      readWholeFile = false;
      enlargeBuffer( FOPEN_BUFFER_SIZE );
    } else {
      readWholeFile = true;

      if( file_size > file_buffer.length ) {
        enlargeBuffer( ( int )file_size );
      }
    }

    // Close any previously opened file.
    if( file_fd != null ) {
      try {
        file_fd.close(  );
        file_fd = null;
      } catch( IOException ioe3 ) {
        //drop it on the floor
      }
    }

    // Now fill up our buffer
    file_fd = fd;

    if( readWholeFile ) {
      try {
        file_fd.readFully( file_buffer );
      } catch( IOException ioe5 ) {
        error_message = "Cannot read " + file_size + " bytes from " +
          file_name;
        FErrorAddDummy.fErrorAdd( 
          "FASTGET_INIT", error_message,
          "Check file is not corrupted or has been deleted" );

        return 1;
      }
    }

    return 0;
  }

  /**
   * Enlarges the internal file_buffer to the newSize.
   *
   * @param newSize The new size (in bytes) of the file buffer.
   */
  private void enlargeBuffer( int newSize ) {
    byte[] temp = new byte[newSize];

    for( int i = 0; i < file_buffer.length; i++ ) {
      temp[i] = file_buffer[i];
    }

    file_buffer = temp;
  }

  /**
   * Gets a segment from the data file.
   *
   * @param istart The index to start at.
   * @param ilong The length of the segment.
   * @param ivalue The array to store the segment in.
   *
   * @return Error code information.
   */
  private int getsectFile( int istart, int ilong, int[] ivalue ) {
    int       ierr          = 0;
    String    error_message = null;
    final int sz            = 4;  //sizeof (int);

    try {
      if( readWholeFile ) {
        //i.e. we have already loaded the whole file into file_buffer, so we can easily 
        //just read off the buffer.  This will always get hit for files 2GB in size or
        //less
        int tempInt;

        for( int i = 0; i < ivalue.length; i++ ) {
          //concatenate the bits up to 32 bit integer size
          tempInt = file_buffer[i];
          tempInt <<= 24;
          ivalue[i]   = tempInt;
          tempInt     = file_buffer[i + 1];
          tempInt <<= 16;
          ivalue[i] |= tempInt;
          tempInt = file_buffer[i + 2];
          tempInt <<= 8;
          ivalue[i] |= tempInt;
          ivalue[i] |= file_buffer[i + 3];
        }
      } else {
        //this is untested.  When we run into a file more than 2GB in size, it
        //will need to be tested.
        try {
          file_fd.seek( ( istart - 1 ) * sz );
        } catch( IOException ioe ) {
          error_message = "Error seeking file size" + file_size + " bytes at " +
            ( ( istart - 1 ) * sz );
          FErrorAddDummy.fErrorAdd( 
            "GETSECT", error_message, "This shouldn't happen!" );
          ierr = -1;

          return ierr;
        }

        try {
          //if( fread( ivalue, sz, ilong, file_fd ) != ( size_t )( ilong ) ) {
          for( int i = 0; i < ilong; i++ ) {
            ivalue[i] = file_fd.readInt(  );
          }
        } catch( IOException ioe2 ) {
          error_message = "Error reading file size " + file_size +
            " bytes for " + ilong + "words from " + ( istart - 1 );
          FErrorAddDummy.fErrorAdd( 
            "GETSECT", error_message, "This shouldn't happen!" );
          ierr = -1;

          return ierr;
        }
      }

      return ierr;
    } catch( ArrayIndexOutOfBoundsException e ) {
      try {
        error_message = "Attempt to read invalid part of file buffer: istart = " +
          istart + " words, ilong = " + ilong + " words, file size = " +
          file_fd.length(  ) + " bytes";
        FErrorAddDummy.fErrorAdd( 
          "GETSECT", error_message, "This shouldn't happen!" );
        ierr = -1;

        return ierr;
      } catch( IOException ioe ) {
        return -1;
      }
    }
  }
}
