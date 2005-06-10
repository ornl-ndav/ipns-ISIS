/*
 * File:  LOQRegimeInfoInformant.java
 *
 * Copyright (C) 2004 Dominic Kramer
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
 *           Dominic Kramer <kramerd@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 * $Log$
 * Revision 1.1  2004/07/12 18:42:47  kramer
 * This class is responsible for determining correct time regime information
 * for ISIS LOQ instruments.
 *
 */
package ISIS.Rawfile;

/**
 * Used to acquire time regime information for an ISIS LOQ instrument.
 */
public class LOQRegimeInfoInformant implements IRegimeInfo
{
   /**
    * Get the minimum regime number used when recording 
    * spectra for LOQ instruments.
    */
   public int getMinRegimeNumber()
   {
      //from looking at printouts of LOQ data this looks 
      //like the correct valid regime number.
      return 1;
   }
}
