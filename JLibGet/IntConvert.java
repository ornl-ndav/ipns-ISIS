/*
 * File:  IntConvert.java
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
 * File: IntConvert.java
 * Original Author:         Freddie Akeroyd, ISIS
 *
 *  Converted to Java March 2004 Chris M. Bouzek
 *
 */
package ISIS.JLibGet;

/**
 * Routines to convert from VAX to local integer representations
 */
public class IntConvert {
  //~ Methods ******************************************************************

  /**
   * Converts VAX int to local int.
   *
   * @param i The int to convert.
   *
   * @return The converted int.
   */
  public int VAXToLocalInt( int i ) {
    return swapInt( i );
  }

  /**
   * Converts a VAX int array to a local int array.
   *
   * @param ia The VAX int array to convert.
   */
  public void VAXToLocalInts( int[] ia ) {
    for( int i = 0; i < ia.length; i++ ) {
      ia[i] = swapInt( ia[i] );
    }
  }

  /**
   * Converts a VAX short to a local short.
   *
   * @param s The short to convert.
   *
   * @return The converted short.
   */
  public short VAXToLocalShort( short s ) {
    return swapShort( s );
  }

  /**
   * Converts a VAX short array to a local short array.
   *
   * @param sa The short array to convert.
   */
  public void VAXToLocalShorts( short[] sa ) {
    for( int i = 0; i < sa.length; i++ ) {
      sa[i] = swapShort( sa[i] );
    }
  }

  /**
   * Converts the local int to a VAX int.
   *
   * @param i The local integer.
   *
   * @return The converted VAX integer.
   */
  public int localToVAXInt( int i ) {
    return swapInt( i );
  }

  /**
   * Converts a local short to a VAX short.
   *
   * @param s The short to convert.
   *
   * @return The converted short.
   */
  public short localToVAXShort( short s ) {
    return swapShort( s );
  }

  /**
   * Converts a local array to a VAX array.
   *
   * @param sa
   */
  public void localToVAXShorts( short[] sa ) {
    for( int i = 0; i < sa.length; i++ ) {
      sa[i] = swapShort( sa[i] );
    }
  }

  /**
   * Converts a local int array to a VAX int array.
   *
   * @param ia The int array to convert.
   */
  public void localToVaxInts( int[] ia ) {
    for( int i = 0; i < ia.length; i++ ) {
      ia[i] = swapInt( ia[i] );
    }
  }

  /**
   * Swaps the bytes around in the int, e.g.<br>
   * <br>
   * 0100 1100 0100 1111 0101 0001 0011 1000<br>
   * <br>
   * goes to<br>
   * <br>
   * 0011 1000 0101 0001 0100 1111 0100 1100
   *
   * @param a The int to swap around.
   */
  private int swapInt( int a ) {
    //tested and passed
    return ( int )( ( ( a ) << 24 ) | ( ( ( a ) << 8 ) & 0x00ff0000 ) |
    ( ( ( a ) >> 8 ) & 0x0000ff00 ) | ( ( long )( a ) >> 24 ) );
  }

  /**
   * Swaps the bytes around in the short.
   *
   * @param a The short to swap.
   *
   * @return The byte swapped short.
   */
  private short swapShort( short a ) {
    return ( short )( ( ( a & 0xff ) << 8 ) | ( ( short )( a ) >> 8 ) );
  }
}
