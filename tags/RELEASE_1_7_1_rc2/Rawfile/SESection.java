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
 * Revision 1.6  2004/06/24 21:41:41  kramer
 * Changed all of the fields' visiblity from protected to private.  Fields
 * are now accessed from other classes in this package through getter methods
 * instead of using <object>.<field name>.  Also, this class should now be
 * immutable.
 *
 * Revision 1.5  2004/06/22 14:13:49  kramer
 *
 * Added getter methods (with documentation).  Now this class imports 2 classes
 * instead of the entire java.io package.  Also, if it thinks it may not
 * correctly read the file it warns the user.
 *
 * Revision 1.4  2004/06/16 20:40:50  kramer
 *
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
   private int version;
   //Variables in the SAMPLE PARAMETER BLOCK
   /** Position of sample changer. */
   private int posSampleChanger;
   /**
    * Sample type.<br>
    * 1 = sample+can<br>
    * 2 = empty can<br>
    * 3 = vanadium<br>
    * 4 = absorber<br>
    * 5 = nothing<br>
    * 6 = sample, no can
    */
   private int sampleType;
   /**
    * Sample geometry.<br>
    * 1 = cylinder<br>
    * 2 = flat plate<br>
    * 3 = HRPD slab<br>
    */
   private int sampleGeometry;
   /** Sample thickness normal to sample (in mm). */
   private float sampleThickness;
   /** Sample height (in mm). */
   private float sampleHeight;
   /** Sample width (in mm). */
   private float sampleWidth;
   /** omega sample angle (in degrees). */
   private float omega;
   /** psi sample angle (in degrees). */
   private float psi;
   /** phi sample angle (in degrees). */
   private float phi;
   /**
    * Scat. geom.<br>
    * 1 = trans.<br>
    * 2 = reflect.
    */
   private float scatGeom;
   /** Sample sCOH (in barns). */
   private float sample_sCOH;
   /** Sample sINC (in barns). */
   private float sample_sINC;
   /** Sample sABS (in barns). */
   private float sample_sABS;
   /** Sample number density (atoms.A-3). */
   private float sampleNumDensity;
   /** Can wall thickness (in mm). */
   private float canWallThickness;
   /** Can sCOH (in barns). */
   private float can_sCOH;
   /** Can sINC (in barns). */
   private float can_sINC;
   /** Can sABS (in barns). */
   private float can_sABS;
   /** Can number density (atoms.A-3). */
   private float canNumDensity;
   /** Sample name or chemical formula. */
   private String sampleName;
   /** Number of SE parameters. */
   private int numParams;
   /**
    * An array of the SE parameter blocks in the section.  The length of this 
    * array is equal to the number of SE parameters.
    */
   private SEParameterBlock[] paramBlockArray;
   /** The offset in the file where this data starts. */
   private int startAddress;
   
   /** Default constructor. */
   public SESection()
   {
      version = -1;
      posSampleChanger = -1;
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
   	  startAddress = ( header.getStartAddressSESection() - 1 ) * 4;
   	  
   	  try
   	  {
   	  	rawFile.seek(startAddress);
   	  	version = Header.readUnsignedInteger(rawFile,4);
         if (version != 2)
            System.out.println("WARNING:  Unrecognized Sample Environment Section version number."
            +"\n          Version found = "+version
            +"\n          Version numbers corresponding to data that can be processed  = 2"
            +"\n          Data may be incorrectly read and/or interpreted from the file.");
         
   	  	//now to read the sample parameter block
   	  	posSampleChanger = Header.readUnsignedInteger(rawFile,4);
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
        sampleName = Header.readString(rawFile,40);
   	  	   
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
         System.out.println("Position of sample changer="+section.posSampleChanger);
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
   private static class SEParameterBlock
   {
      /** Name. */
      private String[] nameArr;
      /** Value. */
      private int value;
      /** Value exponent. */
      private int valExponent;
      /** Units of value. */
      private String[] unitsOfValueArr;
      /** Low trip. */
      private int lowTrip;
      /** High trip. */
      private int highTrip;
      /** Current value. */
      private int currentVal;
      /** Status (in bounds ?). */
      private int status;
      /** Controlled parameter (true/false). */
      private int controlledParam;
      /** Run control parameter (true/false). */
      private int runControlParam;
      /** Log parameter changes (true/false). */
      private int logParamChanges;
      /** Stability value (units per sec). */
      private float stabilityVal;
      /** Monitor repeat period. */
      private float monRepeatPeriod;
      /** CAMAC location N. */
      private int camacLocationN;
      /** CAMAC location A. */
      private int camacLocationA;
      /** CAMAC offset (added to value). */
      private int camacOffset;
      /** CAMAC register group (1 or 2). */
      private int camacRegisterGroup;
      /** Pre process routine number. */
      private int routineNumber;
      /** CAMAC values.  This array has 12 elements. */
      private int[] camacValuesArr;
      
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
      *  @param rawFile The RAW file that is to be read.
      */
      public SEParameterBlock(RandomAccessFile rawFile) throws IOException
      {
         this();
         nameArr = new String[2];
         for (int i=0; i<2; i++)
            nameArr[i] = Header.readString(rawFile,4);
         
         value = Header.readUnsignedInteger(rawFile,4);
         valExponent = Header.readUnsignedInteger(rawFile,4);
         
         unitsOfValueArr = new String[2];
         for (int i=0; i<2; i++)
            unitsOfValueArr[i] = Header.readString(rawFile,4);
         
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
   
   /**
    * Get the can sABS.
    * @return The can sABS (in barns).
    */
   public float getCan_sABS()
   {
      return can_sABS;
   }

   /**
    * Get the can sCOH.
    * @return The can sCOH (in barns).
    */
   public float getCan_sCOH()
   {
      return can_sCOH;
   }

   /**
    * Get the can sINC (in barns).
    * @return The can sINC (in barns).
    */
   public float getCan_sINC()
   {
      return can_sINC;
   }

   /**
    * Get the can number density.
    * @return The can number density (atoms.A-3).
    */
   public float getCanNumDensity()
   {
      return canNumDensity;
   }

   /**
    * Get the can wall thickness.
    * @return The can wall thickness.
    */
   public float getCanWallThickness()
   {
      return canWallThickness;
   }

   /**
    * Get the number of Sample Environment parameters.
    * @return The number of Sample Environment parameters.
    */
   public int getNumParams()
   {
      return numParams;
   }

   /**
    * Get the omega sample angle.
    * @return The omega sample angle (in degrees).
    */
   public float getOmega()
   {
      return omega;
   }

   /**
    * Get the phi sample angle.
    * @return The phi sample angle (in degrees).
    */
   public float getPhi()
   {
      return phi;
   }

   /**
    * Get the position of the sample changer.
    * @return The position of the sample changer.
    */
   public int getPosSampleChanger()
   {
      return posSampleChanger;
   }

   /**
    * Get the psi sample angle.
    * @return The psi sample angle (in degrees).
    */
   public float getPsi()
   {
      return psi;
   }

   /**
    * Get the sample sABS.
    * @return The sample sABS (in barns).
    */
   public float getSample_sABS()
   {
      return sample_sABS;
   }

   /**
    * Get the sample sCOH.
    * @return The sample sCOH (in barns).
    */
   public float getSample_sCOH()
   {
      return sample_sCOH;
   }

   /**
    * Get the sample sINC.
    * @return The sample sINC (in barns).
    */
   public float getSample_sINC()
   {
      return sample_sINC;
   }

   /**
    * Get the sample geometry.
    * @return The sample geometry.  Possible values 
    * are:<br>
    * 1 = cylinder<br>
    * 2 = flat plate<br>
    * 3 = HRPD slab
    */
   public int getSampleGeometry()
   {
      return sampleGeometry;
   }

   /**
    * Get the sample height.
    * @return The sample height (in mm).
    */
   public float getSampleHeight()
   {
      return sampleHeight;
   }

   /**
    * Get the sample name or chemical formula.
    * @return The sample name or chemical formula.
    */
   public String getSampleName()
   {
      return sampleName;
   }

   /**
    * Get the sample number density.
    * @return The sample number density (atoms.A-3).
    */
   public float getSampleNumDensity()
   {
      return sampleNumDensity;
   }

   /**
    * Get the sample thickness normal to the sample.
    * @return The sample thickness normal to the 
    * sample (in mm).
    */
   public float getSampleThickness()
   {
      return sampleThickness;
   }

   /**
    * Get the sample type.
    * @return The sample type.  Possible values are:<br>
    * 1 = sample+can<br>
    * 2 = empty can<br>
    * 3 = vanadium<br>
    * 4 = absorber<br>
    * 5 = nothing
    */
   public int getSampleType()
   {
      return sampleType;
   }

   /**
    * Get the sample width.
    * @return The sample width (in mm).
    */
   public float getSampleWidth()
   {
      return sampleWidth;
   }

   /**
    * Get the scattering geometry.
    * @return The scattering geometry.  Possible values:<br>
    * 1 = trans.<br>
    * 2 = reflect.<br>
    */
   public float getScatteringGeom()
   {
      return scatGeom;
   }

   /**
    * Get the Sample Environment Section version number.
    * @return The Sample Environment Section version number.
    */
   public int getVersion()
   {
      return version;
   }

   /**
    * Get SE Parameter Block <code>num</code>.
    * @param num The number of the parameter block you want to get.  
    * Note:  The first parameter block is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() getNumParams()}.
    * @return The correct parameter block or <code>null</code> if <code>num
    * </code> is invalid.
    */
   private SEParameterBlock getSEParameterBlock(int num)
   {
      if (num>=1 && num<=paramBlockArray.length)
         return paramBlockArray[num-1];
      else
         return null;
   }

   /**
    * Get the CAMAC location A for parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() getNumParams()}.
    * @return  The CAMAC location A for parameter <code>num</code> or 
    * -1 if <code>num</code> is invalid.
    */
   public int getCAMACLocationAForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.camacLocationA;
      else
         return -1;
   }

   /**
    * Get the CAMAC location N for parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() getNumParams()}.
    * @return  The CAMAC location N for parameter <code>num</code> or -1 if 
    * <code>num</code> is invalid.
    */
   public int getCamacLocationNForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.camacLocationN;
      else
         return -1;
   }
   
   /**
    * Get the CAMAC offset (added to value) for 
    * parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() 
    * getNumParams()}.
    * @return The CAMAC offset (added to value) for 
    * parameter <code>num</code> or -1 if <code>num</code> is 
    * invalid.
    */
   public int getCAMACOffsetForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.camacOffset;
      else
         return -1;
   }

   /**
    * Get the CAMAC register group for parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() getNumParams()}.
     * @return The CAMAC register group for parameter <code>num</code>.  Possible 
    * legitimate values are 1 or 2.  If <code>num</code> is invalid -1 is returned.
    */
   public int getCAMACRegisterGroupForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.camacRegisterGroup;
      else
         return -1;
   }

   /**
    * Get the CAMAC values for parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() getNumParams()}.
    * @return The CAMAC values for parameter <code>num</code> or 
    * <code>null</code> if <code>num</code> is invalid.
    */
   public int[] getCAMACValuesForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
      {
         int[] copy = new int[block.camacValuesArr.length];
         System.arraycopy(block.camacValuesArr,0,copy,0,block.camacValuesArr.length);
         return copy;
      } 
      else
         return null;
   }

   /**
    * Get controlled parameter at parameter <code>num</code>.
    * @param num The number of the parameter block want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() 
    * getNumParams()}.
    * @return Get controlled parameter (corellates to true or false) for 
    * parameter <code>num</code> or -1 if <code>num</code> is 
    * invalid.
    */
   public int getControlledParamForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.controlledParam;
      else
         return -1;
   }

   /**
    * Get the current value for parameter <code>num</code>.
    * @param num The number of the parameter block want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() 
    * getNumParams()}.
    * @return The current value for parameter <code>num</code> 
    * or -1 if <code>num</code> is invalid.
    */
   public int getCurrentValForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.currentVal;
      else
         return -1;
   }

   /**
    * Get the high trip for parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() 
    * getNumParams()}.
    * @return The high trip for parameter <code>num</code> or 
    * -1 if <code>num</code> is invalid.
    */
   public int getHighTripForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.highTrip;
      else
         return -1;
   }

   /**
    * Get log parameter changes for parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() 
    * getNumParams()}.
     * @return Log parameter changes for parameter <code>num</code>.  Note:  
     * this value corellates to true or false.  If <code>num</code> is invalid 
     * -1 is returned.
    */
   public int getLogParamChangesForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.logParamChanges;
      else
         return -1;
   }

   /**
    * Get the low trip for parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() 
    * getNumParams()}.
    * @return The low trip for parameter <code>num</code> or -1 
    * is <code>num</code> is invalid.
    */
   public int getLowTripForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.lowTrip;
      else
         return -1;
   }

   /**
    * Get the monitor repeat period for parameter <code>num</code>.
    * Get the high trip for parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() 
    * getNumParams()}.
    * @return The monitor repeat period for parameter <code>num</code> 
    * or -1 if <code>num</code> is invalid.
    */
   public float getMonitorRepeatPeriodForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.monRepeatPeriod;
      else
         return Float.NaN;
   }

   /**
    * Get the names for parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() 
    * getNumParams()}.
    * @return The names for parameter <code>num</code> or 
    * <code>nulll</code> if <code>num</code> is invalid.
    */
   public String[] getNamesForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
      {
         String[] copy = new String[block.nameArr.length];
         System.arraycopy(block.nameArr,0,copy,0,block.nameArr.length);
         return copy;
      }
      else
         return null;
   }

   /**
    * Get the pre process routine number for parameter <code>
    * num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() 
    * getNumParams()}.
    * @return The pre process routine number for parameter 
    * <code>num</code> or -1 if <code>num</code> is invalid.
    */
   public int getRoutineNumberForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.routineNumber;
      else
         return -1;
   }

   /**
    * Get run control parameter for parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() 
    * getNumParams()}.
    * @return Run control parameter for parameter <code>num
    *</code>.  Note:  This value corresponds to true or false.  If 
    *<code>num</code> is invalid -1 is returned.
    */
   public int getRunControlParamForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.runControlParam;
      else
         return -1;
   }

   /**
    * Get the stability value for parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() 
    * getNumParams()}.
    * @return The stability value for parameter <code>num</code> 
    * (in units per sec) or Float.NaN if <code>num</code> is invalid.
    */
   public float getStabilityValForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.stabilityVal;
      else
         return Float.NaN;
   }

   /**
    * Get the status for parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() 
    * getNumParams()}.
    * @return The status for parameter <code>num</code> (aka is it 
    * in bounds) or -1 if <code>num</code> is invalid.
    */
   public int getStatusForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.status;
      else
         return -1;
   }

   /**
    * Get the units of value for parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() 
    * getNumParams()}.
    * @return Get the units of value for parameter <code>num</code> 
    * or <code>null</code> if <code>num</code> is invalid.
    */
   public String[] getUnitsOfValueForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
      {
         String[] copy = new String[block.unitsOfValueArr.length];
         System.arraycopy(block.unitsOfValueArr,0,copy,0,block.unitsOfValueArr.length);
         return copy;
      }
      else
         return null;
   }

   /**
    * Get value exponent for parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() 
    * getNumParams()}.
    * @return Get value exponent for parameter <code>num</code> 
    * or -1 if <code>num</code> is invalid.
    */
   public int getValueExponentForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.valExponent;
      else
         return -1;
   }

   /**
    * Get the value for parameter <code>num</code>.
    * @param num The number of the parameter you want to get.  
    * Note:  The first parameter is at num=1 not num=0.  For num to 
    * be valid 1<=<code>num</code><={@link #getNumParams() 
    * getNumParams()}.
    * @return The value for parameter <code>num</code> or -1 if 
    * <code>num</code> is invalid.
    */
   public int getValueForParameter(int num)
   {
      SEParameterBlock block = getSEParameterBlock(num);     
      if (block != null)
         return block.value;
      else
         return -1;
   }
}
