/*
 * File:  InstrumentSectionHashtable.java
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
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 * $Log$
 * Revision 1.2  2005/01/20 21:48:34  dennis
 * Fixed dead @link in javadoc comment.
 *
 * Revision 1.1  2004/06/24 21:49:22  kramer
 *
 * This class is a hashtable used to find the detector number associated with
 * a spectrum number more efficiently than with a linear search.
 *
 */
package ISIS.Rawfile;

/**
 * This class keeps a relationship between detector numbers and spectrum 
 * numbers.  This makes finding a detector number for its spectrum number easy.
 * Note:  You can find a spectrum number given a detector number using 
 * {@link InstrumentSection#getSpectrumNumberForDetector(int detectorNum) 
 * getSpectrumNumberForDetector(int detectorNum)}.
 * @author Dominic Kramer
 */
public class InstrumentSectionHashtable
{
   /**
    * The array holding the detector numbers.  This 
    * is used to find the detector number corresponding 
    * to a given spectrum number.
    */
   private int[] dataArray;
      
   /** Creates an empty hashtable. */
   public InstrumentSectionHashtable() { dataArray = new int[0]; }
      
   /**
    * Creates a hashtable where the spectrum number and detector id 
    * relationship is established.
    * @param spectrumNumbers The array of spectrum numbers.  Element '0' in 
    * this array is assumed to hold a garbage value, which complies with the  
    * array holding the spectrum numbers in InstrumentSection.java.  Thus, the  
    * element with the first meaningful information is at element '1'.
    * @param numOfSpectra The number of spectra that exist.
    */
   public InstrumentSectionHashtable(int[] spectrumNumbers, int numOfSpectra)
   {
      dataArray = new int[numOfSpectra+1];
      //initialize the array
      for (int i=0; i<dataArray.length; i++)
         dataArray[i] = -1;
      for (int i=1; i<spectrumNumbers.length; i++)
         setDetectorNum(spectrumNumbers[i], i);
   }
   
   /** 
    * Get the key that is used to determine where the data about the 
    * detector corresponding to spectrum <code>spect</code> is 
    * located.
    */
   public int getKeyForSpectrumNum(int spect) { return spect; }
   
   /**
    * Sets the value for the data given spectrum.
    * @param spect The spectrum in question.
    * @param det The detector corresponding to that spectrum
    */
   public void setDetectorNum(int spect, int det)
   {
      dataArray[getKeyForSpectrumNum(spect)] = det;
   }
   
   /**
    * Get the detector number corresponding to the 
    * given spectrum.
    * @param spect The spectrum in question.
    * @return The spectrum's corresponding 
    * detector number.
    */
   public int getDetectorNum(int spect)
   {
      return dataArray[getKeyForSpectrumNum(spect)];
   }
   
   /**
    * Test the hashtable by creating an array of random 2 
    * digit natural numbers.  An InstrumentSectionHashtable 
    * is made and its methods are used to print the detector 
    * numbers given the spectrum numbers to standard output.
    */
   public static void main(String args[])
   {
      final int DET_NUM = 9;
      int[] spectArr = new int[DET_NUM];
      int max = 0;
      int val = 0;
      boolean tryAgain = true;
      System.out.println("Creating the original array");
      for (int detNum=1; detNum<spectArr.length; detNum++)
      {
         tryAgain = true;
         while (tryAgain)
         {
            val = (int)Math.round(Math.random()*10);
            max = Math.max(val,max);
            int i=0;
            boolean found = false;
            tryAgain = false;
            while (i<detNum && !found)
            {
              if (spectArr[i] == val)
              {
                 found = true;
                 tryAgain = true;
              }
              i++;
            }
         }
         
         spectArr[detNum] = val;
         System.out.println("Detector Number = "+detNum+
                            ":  Spectrum Number = "+val);
      }
      
      System.out.println("Now to retrieve the data from the hashtable");
      InstrumentSectionHashtable table = 
                                new InstrumentSectionHashtable(spectArr,max);
      System.out.println("largest spectrum number = "+max);
      for (int i=1; i<=max; i++)
         System.out.println("Spectrum Number="+i+
                            ":  Detector Number = "+table.getDetectorNum(i));
   }
}
