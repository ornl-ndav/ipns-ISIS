/*
 * File:  IRegimeInfo.java
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
 * Revision 1.1  2004/07/12 18:36:56  kramer
 * This interface defines the information a class must provide to allow regime
 * information for particular ISIS instruments to be accurately determined.
 *
 */
package ISIS.Rawfile;

/**
 * This interface defines methods that provide time regime information 
 * that is meant to supplement the information acquired from an ISIS 
 * raw file.  Typically, a class implementing this interface will 
 * provide information for a certain instrument type or group of 
 * instrument types.  Examples of instrument types used in ISIS raw 
 * files are LOQ, SXD, and HRP.
 */
public interface IRegimeInfo
{
   /** Get the smallest <b>valid</b> regime number. */
   public int getMinRegimeNumber();
}
