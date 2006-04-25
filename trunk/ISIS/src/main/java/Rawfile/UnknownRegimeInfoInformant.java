/*
 * File:  UnknownRegimeInfoInformant.java
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
 * Revision 1.1  2004/07/12 18:48:44  kramer
 * This class is responsible for determining correct time regime information
 * from an ISIS raw file if the instrument type specified in the file is not
 * supported in its own class that implements IRegimeInfo.java.
 *
 */
package ISIS.Rawfile;

/**
 * Used to acquire time regime information for an unknown ISIS instrument.  
 */
public class UnknownRegimeInfoInformant implements IRegimeInfo
{
   private DaeSection daeSection;
   private int minRegimeNumber;
   
   public UnknownRegimeInfoInformant(DaeSection ds)
   {
      daeSection = ds;
      minRegimeNumber = -1;
   }
   
   /**
    * Returns the minimum regime number stored in the file.
    * @return The smallest regime number or 
    * -1 if it cannot be determined.
    */
   public int getMinRegimeNumber()
   {
      if (minRegimeNumber != -1)
         return minRegimeNumber;
      else
      {
         if (daeSection.getTimeRegimeTable().length>=1)
         {
            int min = daeSection.getTimeRegimeTable()[0];
            for (int i=1; i<daeSection.getTimeRegimeTable().length; i++)
               min = Math.min(min,daeSection.getTimeRegimeTable()[i]);
            minRegimeNumber = min;
            return min;
         }
         else
            return -1;
      }
   }

}
