/*
 * File:  SESection.java 
 *             
 * Copyright (C) 2001, Dominic Kramer
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 * $Log$
 * Revision 1.4  2004/06/16 20:40:50  kramer
 * Now the source will contain the cvs logs.  Replaced tabs with 3 spaces,
 * created a default contstructor where fields will be initialized (instead
 * of when they are first declared), and when exceptions are caught a stack
 * trace is now printed to standard output.
 *
 */
 
package ISIS.Rawfile;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This class represents the Sample Environment (SE) Section of an 
 * ISIS RAW file.
 * @author Dominic Kramer
 */
public class SESection
{
   /** The SE section version number. */
   protected int version;
   //Variables in the SAMPLE PARAMETER BLOCK
   /** Position of sample changer. */
   protected int posSampleChamber;
   /**
    * Sample type.<br>
    * 1 = sample+can<br>
    * 2 = empty can<br>
    * 3 = vanadium<br>
    * 4 = absorber<br>
    * 5 = nothing<br>
    * 6 = sample, no can
    */
   protected int sampleType;
   /**
    * Sample geometry.<br>
    * 1 = cylinder<br>
    * 2 = flat plate<br>
    * 3 = HRPD slab<br>
    */
   protected int sampleGeometry;
   /** Sample thickness normal to sample (in mm). */
   protected float sampleThickness;
   /** Sample height (in mm). */
   protected float sampleHeight;
   /** Sample width (in mm). */
   protected float sampleWidth;
   /** omega sample angle (in degrees). */
   protected float omega;
   /** psi sample angle (in degrees). */
   protected float psi;
   /** phi sample angle (in degrees). */
   protected float phi;
   /**
    * Scat. geom.<br>
    * 1 = trans.<br>
    * 2 = reflect.
    */
   protected float scatGeom;
   /** Sample sCOH (in barns). */
   protected float sample_sCOH;
   /** Sample sINC (in barns). */
   protected float sample_sINC;
   /** Sample sABS (in barns). */
   protected float sample_sABS;
   /** Sample number density (atoms.A-3). */
   protected float sampleNumDensity;
   /** Can wall thickness (in mm). */
   protected float canWallThickness;
   /** Can sCOH (in barns). */
   protected float can_sCOH;
   /** Can sINC (in barns). */
   protected float can_sINC;
   /** Can sABS (in barns). */
   protected float can_sABS;
   /** Can number density (atoms.A-3). */
   protected float canNumDensity;
   /** Sample name or chemical formula. */
   protected String sampleName;
   /** Number of SE parameters. */
   protected int numParams;
   /**
    * An array of the SE parameter blocks in the section.  The length of this 
    * array is equal to the number of SE parameters.
    */
   protected SEParameterBlock[] paramBlockArray;
   /** The offset in the file where this data starts. */
   protected int startAddress;
   
   /** Default constructor. */
   public SESection()
   {
      version = -1;
      posSampleChamber = -1;
      sampleType = -1;
      sampleGeometry = -1;
      sampleThickness = Float.NaN;
      sampleHeight = Float.NaN;
      sampleWidth = Float.NaN;
      omega = Float.NaN;
      psi = Float.NaN;
      phi = Float.NaN;
      scatGeom = Float.NaN;
      sample_sCOH = Float.NaN;
      sample_sINC = Float.NaN;
      sample_sABS = Float.NaN;
      sampleNumDensity = Float.NaN;
      canWallThickness = Float.NaN;
      can_sCOH = Float.NaN;
      can_sINC = Float.NaN;
      can_sABS = Float.NaN;
      canNumDensity = Float.NaN;
      sampleName = new String();
      numParams = -1;
      paramBlockArray = new SEParameterBlock[0];
      startAddress = -1;
   }
   
   /**
   *  Reads the SE section in the file specified and creates an SESection object.  
   *  @param rawFile The RandomAccessFile used to read the RAW file that is to 
   *         be processed.
   *  @param header The header section of the RAW file that is to be processed.  
   *         The header is used to locate the SE section in the RAW file.
   */
   public SESection(RandomAccessFile rawFile, Header header)
   {
      this();
   	  startAddress = ( header.startAddressSe - 1 ) * 4;
   	  
   	  try
   	  {
   	  	rawFile.seek(startAddress);
   	  	version = Header.readUnsignedInteger(rawFile,4);
   	  	
   	  	//now to read the sample parameter block
   	  	posSampleChamber = Header.readUnsignedInteger(rawFile,4);
   	  	sampleType = Header.readUnsignedInteger(rawFile,4);
   	  	sampleGeometry = Header.readUnsignedInteger(rawFile,4);
   	  	
   	  	sampleThickness = (float)Header.ReadVAXReal4(rawFile);
   	  	sampleHeight = (float)Header.ReadVAXReal4(rawFile);
   	  	sampleWidth = (float)Header.ReadVAXReal4(rawFile);
   	  	omega = (float)Header.ReadVAXReal4(rawFile);
   	  	psi = (float)Header.ReadVAXReal4(rawFile);
   	  	phi = (float)Header.ReadVAXReal4(rawFile);
   	  	scatGeom = (float)Header.ReadVAXReal4(rawFile);
   	  	sample_sCOH = (float)Header.ReadVAXReal4(rawFile);
   	  	sample_sINC = (float)Header.ReadVAXReal4(rawFile);
   	  	sample_sABS = (float)Header.ReadVAXReal4(rawFile);
   	  	sampleNumDensity = (float)Header.ReadVAXReal4(rawFile);
   	  	canWallThickness = (float)Header.ReadVAXReal4(rawFile);
   	  	can_sCOH = (float)Header.ReadVAXReal4(rawFile);
   	  	can_sINC = (float)Header.ReadVAXReal4(rawFile);
   	  	can_sABS = (float)Header.ReadVAXReal4(rawFile);
   	  	canNumDensity = (float)Header.ReadVAXReal4(rawFile);
   	  	StringBuffer buffer = new StringBuffer(40);
   	  	   for (int i=0; i<40; i++)
   	  	      buffer.append((char)rawFile.readByte());
   	  	   sampleName = buffer.toString();
   	  	   
   	  	//skip over the spare space
   	  	rawFile.seek(startAddress+(64*4));
   	  	
   	  	numParams = Header.readUnsignedInteger(rawFile,4);
   	  	paramBlockArray = new SEParameterBlock[numParams];
   	  	for (int i=0; i<paramBlockArray.length; i++)
   	  	   paramBlockArray[i] = new SEParameterBlock(rawFile);
   	  	//looks complete based on libget.txt
   	  }
   	  catch (IOException e)
   	  {
   	  	e.printStackTrace();
   	  }
   }
   
   /**
   *  Testbed.
   *  @param args arg[0] is interpreted as the RAW file that is to be read.
   */
   public static void main(String[] args)
   {
   	 try
   	 {
       for (int fileNum=0; fileNum<args.length; fileNum++)
	   {
	     System.out.println("--------------------------------------------------------------------------------");
	     System.out.println("Testing file "+args[fileNum]);
	     System.out.println("--------------------------------------------------------------------------------");
         RandomAccessFile file = new RandomAccessFile(args[fileNum],"r");
         Header header = new Header(file);
         SESection section = new SESection(file,header);
             
         System.out.println("SE section version number="+section.version);
         System.out.println("Position of sample changer="+section.posSampleChamber);
         System.out.println("Sample type="+section.sampleType);
         System.out.println("Sample geometry="+section.sampleGeometry);
         System.out.println("Sample thickness normal to sample="+section.sampleThickness);
         System.out.println("Sample height="+section.sampleHeight);
         System.out.println("Sample width="+section.sampleWidth);
         System.out.println("Omega sample angle="+section.omega);
         System.out.println("Psi sample angle="+section.psi);
         System.out.println("Phi sample angle="+section.phi);
         System.out.println("Scat. Geom.="+section.scatGeom);
         System.out.println("Sample sCOH="+section.sample_sCOH);
         System.out.println("Sample sINC="+section.sample_sINC);
         System.out.println("Sample sABS="+section.sample_sABS);
         System.out.println("Sample number density="+section.sampleNumDensity);
         System.out.println("Can wall thickness="+section.canWallThickness);
         System.out.println("Can sCOH="+section.can_sCOH);
         System.out.println("Can sINC="+section.can_sINC);
         System.out.println("Can sABS="+section.can_sABS);
         System.out.println("Can number density="+section.canNumDensity);
         System.out.println("Sample name or chemical formula="+section.sampleName);
         System.out.println("Number of SE parameters="+section.numParams);
    
         for (int i=0; i<section.paramBlockArray.length; i++)
            System.out.println(section.paramBlockArray[i].toString());
	   }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
   
   /**
   *  Represents an SE parameter block in the SE section of an ISIS RAW file.
   *  @author Dominic Kramer
   */
   private class SEParameterBlock
   {
      /** Name. */
      protected String[] nameArr;
      /** Value. */
      protected int value;
      /** Value exponent. */
      protected int valExponent;
      /** Units of value. */
      protected String[] unitsOfValueArr;
      /** Low trip. */
      protected int lowTrip;
      /** High trip. */
      protected int highTrip;
      /** Current value. */
      protected int currentVal;
      /** Status (in bounds ?). */
      protected int status;
      /** Controlled parameter (true/false). */
      protected int controlledParam;
      /** Run control parameter (true/false). */
      protected int runControlParam;
      /** Log parameter changes (true/false). */
      protected int logParamChanges;
      /** Stability value (units per sec). */
      protected float stabilityVal;
      /** Monitor repeat period. */
      protected float monRepeatPeriod;
      /** CAMAC location N. */
      protected int camacLocationN;
      /** CAMAC location A. */
      protected int camacLocationA;
      /** CAMAC offset (added to value). */
      protected int camacOffset;
      /** CAMAC register group (1 or 2). */
      protected int camacRegisterGroup;
      /** Pre process routine number. */
      protected int routineNumber;
      /** CAMAC values.  This array has 12 elements. */
      protected int[] camacValuesArr;
      
      /** Default constructor. */
      public SEParameterBlock()
      {
         nameArr = new String[2];
         value = -1;
         valExponent = -1;
         unitsOfValueArr = new String[2];
         lowTrip = -1;
         highTrip = -1;
         currentVal = -1;
         status = -1;
         controlledParam = -1;
         runControlParam = -1;
         logParamChanges = -1;
         stabilityVal = Float.NaN;
         monRepeatPeriod = Float.NaN;
         camacLocationN = -1;
         camacLocationA = -1;
         camacOffset = -1;
         camacRegisterGroup = -1;
         routineNumber = -1;
         camacValuesArr = new int[12];
      }
      
      /**
      *  Creates an SEParameterBlock object by reading the file given.  This 
      *  constructor assumes that the file pointer of the RandomAccessFile is 
      *  at the start the parameter block that is to be read.
      *  @param header The header section of the RAW file that is to be read.
      *  @param rawFile The RAW file that is to be read.
      */
      public SEParameterBlock(RandomAccessFile rawFile) throws IOException
      {
         this();
         nameArr = new String[2];
         for (int i=0; i<2; i++)
         {
            StringBuffer buffer = new StringBuffer(4);
            for (int j=0; j<4; j++)
               buffer.append((char)rawFile.readByte());
               
            nameArr[i] = buffer.toString();
         }
         
         value = Header.readUnsignedInteger(rawFile,4);
         valExponent = Header.readUnsignedInteger(rawFile,4);
         
         unitsOfValueArr = new String[2];
         for (int i=0; i<2; i++)
         {
            StringBuffer buffer = new StringBuffer(4);
            for (int j=0; j<4; j++)
               buffer.append((char)rawFile.readByte());
               
            unitsOfValueArr[i] = buffer.toString();
         }
         
         lowTrip = Header.readUnsignedInteger(rawFile,4);
         highTrip = Header.readUnsignedInteger(rawFile,4);
         currentVal = Header.readUnsignedInteger(rawFile,4);
         status = Header.readUnsignedInteger(rawFile,4);
         controlledParam = Header.readUnsignedInteger(rawFile,4);
         runControlParam = Header.readUnsignedInteger(rawFile,4);
         logParamChanges = Header.readUnsignedInteger(rawFile,4);
         
         stabilityVal = (float)Header.ReadVAXReal4(rawFile);
         monRepeatPeriod = (float)Header.ReadVAXReal4(rawFile);
         
         camacLocationN = Header.readUnsignedInteger(rawFile,4);
         camacLocationA = Header.readUnsignedInteger(rawFile,4);
         camacOffset = Header.readUnsignedInteger(rawFile,4);
         camacRegisterGroup = Header.readUnsignedInteger(rawFile,4);
         
         routineNumber = Header.readUnsignedInteger(rawFile,4);
         for (int i=0; i<12; i++)
            camacValuesArr[i] = Header.readUnsignedInteger(rawFile,4);
      }
      
      /**
      *  Get a String representation of this SE parameter block.
      */
      public String toString()
      {
         StringBuffer buffer = new StringBuffer("New SE Parameter Block");
	 buffer.append("\n");
         for (int i=0; i<nameArr.length; i++)
	 {
	    buffer.append("   name "+i+"=");
	    buffer.append(nameArr[i]);
	    buffer.append("\n");
	 }
         
	 buffer.append("   value=");
	 buffer.append(value);
	 buffer.append("\n");
	 
         buffer.append("   value exponent=");
	 buffer.append(valExponent);
	 buffer.append("\n");
	 
	 for (int i=0; i<unitsOfValueArr.length; i++)
	 {
	    buffer.append("   Units of value "+i+"=");
            buffer.append(unitsOfValueArr);
	    buffer.append("\n");
	 }
   
         buffer.append("   low trip=");
	 buffer.append(lowTrip);
	 buffer.append("\n");
      
         buffer.append("   high trip=");
	 buffer.append(highTrip);
	 buffer.append("\n");
	 
         buffer.append("   current value=");
	 buffer.append(currentVal);
	 buffer.append("\n");
	 
	 buffer.append("   status=");
         buffer.append(status);
	 buffer.append("\n");
	 
	 buffer.append("   controlled parameter=");
         buffer.append(controlledParam);
	 buffer.append("\n");
	 
	 buffer.append("   run control parameter=");
         buffer.append(runControlParam);
	 buffer.append("\n");
	 
	 buffer.append("   log control parameter=");
         buffer.append(logParamChanges);
	 buffer.append("\n");
	 
	 buffer.append("   stability value=");
         buffer.append(stabilityVal);
	 buffer.append("\n");
	 
	 buffer.append("   monitor repeat period=");
         buffer.append(monRepeatPeriod);
	 buffer.append("\n");
	 
	 buffer.append("   CAMAC location N=");
         buffer.append(camacLocationN);
	 buffer.append("\n");
	 
	 buffer.append("   CAMAC location A=");
         buffer.append(camacLocationA);
	 buffer.append("\n");
	 
	 buffer.append("   CAMAC offset=");
         buffer.append(camacOffset);
	 buffer.append("\n");
	 
	 buffer.append("   CAMAC regoster group=");
         buffer.append(camacRegisterGroup);
	 buffer.append("\n");
	 
	 buffer.append("   Pre process routine number=");
         buffer.append(routineNumber);
	 buffer.append("\n");
	 
	 for (int i=0; i<camacValuesArr.length; i++)
	 {
	    buffer.append("   CAMAC value "+i+"=");
            buffer.append(camacValuesArr[i]);
	    buffer.append("\n");
	 }
	 
	 return buffer.toString();
      }
   }
}
