/*
 * File:  FErrorAddDummy.java
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
 *
 * $Log$
 * Revision 1.3  2004/06/16 16:03:49  kramer
 * Added the GNU license header.
 *
 * Revision 1.2  2004/04/30 00:18:16  bouzekc
 * New version.  Old version should not have been in CVS.
 *
 */
 
/*
 *       Module:         FERROR_ADD_DUMMY
 *       Author:         Freddie Akeroyd, ISIS
 *       Purpose:        FORTRAN interface to dummy Genie error handling interface
 *
 * Converted to Java by Chris M. Bouzek February 2004
 */
package ISIS.JLibGet;

/**
 * Class to interface to dummy Genie error handling.
 */
public class FErrorAddDummy {
  //~ Constructors *************************************************************

  /**
   * Do not instantiate this class.
   */
  private FErrorAddDummy(  ) {}

  //~ Methods ******************************************************************

  /**
   * Prints out the error messages from a Genie failure.
   *
   * @param objName The name of the object that caused the failure.
   * @param errorMess The error message.
   * @param solution The solution to the problem.
   */
  public static void fErrorAdd( 
    String objName, String errorMess, String solution ) {
    System.out.println( "Error returned from " + objName + ": " + errorMess );

    if( !solution.equals( " " ) ) {
      System.out.println( "Solution: " + solution );
    }
  }
}
