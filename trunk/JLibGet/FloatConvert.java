/*
 * File: FloatConvert
 * Copyright: Freddie Akeroyd, ISIS
 *
 */
package ISIS.JLibGet;

/**
 * Routines to convert from vax to ieee floating point, based on the XDR
 * routines of SUN RPC  Converted to Java by Chris M. Bouzek, February 2004.
 * Note: The JVM is big-endian and uses IEEE floating point format.
 */
public class FloatConvert {
  //~ Static fields/initializers ***********************************************

  //byte, short or int?  these were #define statements
  private static final int VAX_SNG_BIAS  = 0x81;
  private static final int IEEE_SNG_BIAS = 0x7f;

  //~ Instance fields **********************************************************

  //Max VAX
  //Max IEEE
  private SingleLimits mmax = new SingleLimits( 
      0x7f, 0xff, 0x0, 0xffff, 0x0, 0xff, 0x0 );

  //Min VAX
  //Min IEEE 
  private SingleLimits mmin = new SingleLimits( 
      0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 );

  //~ Methods ******************************************************************

  /**
   * Convert a local IEEE single float to little endian VAX F FLOAT format.
   * This keeps the floating point in an array.
   *
   * @param fp The floating point to convert.
   */
  public int IEEEToVaxFloat( float[] fp ) {
    IEEESingle is = new IEEESingle( ( int )fp[0], ( int )fp[1], ( int )fp[2] );
    VAXSingle  vs = new VAXSingle(  );

    switch( is.getExp(  ) ) {
      case 0:

        if( is.getMantissa(  ) == mmin.getIEEE(  ).getMantissa(  ) ) {
          vs = mmin.getVAX(  );
        } else {
          int tmp = is.getMantissa(  ) >> 20;

          if( tmp >= 4 ) {
            vs.setExp( 2 );
          } else if( tmp >= 2 ) {
            vs.setExp( 1 );
          } else {
            vs = mmin.getVAX(  );

            break;
          }

          tmp = is.getMantissa(  ) - ( 1 << ( 20 + vs.getExp(  ) ) );
          tmp <<= ( 3 - vs.getExp(  ) );
          vs.setMantissa2( tmp );
          vs.setMantissa1( ( tmp >> 16 ) );
        }

        break;

      case 0xfe:
      case 0xff:
        vs = mmax.getVAX(  );

        break;

      default:
        vs.setExp( is.getExp(  ) - IEEE_SNG_BIAS + VAX_SNG_BIAS );
        vs.setMantissa2( is.getMantissa(  ) );
        vs.setMantissa1( is.getMantissa(  ) >> 16 );
    }

    vs.setSign( is.getSign(  ) );
    fp[0]   = vs.getMantissa2(  );
    fp[1]   = vs.getSign(  );
    fp[2]   = vs.getExp(  );
    fp[3]   = vs.getMantissa1(  );
    flipBytes( fp );  // Make little endian

    return 0;
  }

  /**
   * Convert VAX F FLOAT into a local IEEE single float.  This uses an array to
   * store the floating point.
   *
   * @param fp The array to convert.
   */
  public int VAXToIEEEFloat( float[] fp ) {
    IEEESingle is = new IEEESingle(  );

    flipBytes( fp );

    VAXSingle vs = new VAXSingle( 
        ( int )fp[0], ( int )fp[1], ( int )fp[2], ( int )fp[3] );

    switch( vs.getExp(  ) ) {
      case 0:

        // all vax float with zero exponent map to zero
        is = mmin.getIEEE(  );

        break;

      case 2:
      case 1:

        // These will map to subnormals 
        is.setExp( 0 );
        is.setMantissa( ( vs.getMantissa1(  ) << 16 ) | vs.getMantissa2(  ) );

        // lose some precision 
        is.setMantissa( is.getMantissa(  ) >> ( 3 - vs.getExp(  ) ) );
        is.setMantissa( is.getMantissa(  ) + ( 1 << ( 20 + vs.getExp(  ) ) ) );

        break;

      case 0xff:  // mmax.s.exp

        if( 
          ( vs.getMantissa2(  ) == mmax.getVAX(  ).getMantissa2(  ) ) &&
            ( vs.getMantissa1(  ) == mmax.getVAX(  ).getMantissa1(  ) ) ) {
          // map largest vax float to ieee infinity 
          is = mmax.getIEEE(  );

          break;
        }

      // else, fall thru
      default:
        is.setExp( vs.getExp(  ) - VAX_SNG_BIAS + IEEE_SNG_BIAS );
        is.setMantissa( ( vs.getMantissa1(  ) << 16 ) | vs.getMantissa2(  ) );
    }

    is.setSign( vs.getSign(  ) );

    //*fp = *((float*)&is);
    fp[0]   = is.getSign(  );
    fp[1]   = is.getExp(  );
    fp[2]   = is.getMantissa(  );

    return 0;
  }

  /**
   * VAX is little endian, so we need to flip.
   *
   * @param p The floating point to flip.
   */
  private int flipBytes( float[] p ) {
    //this originally took a char[] (char* actually), and this has not been taken 
    //care of correctly yet
    float c_tmp;
    int   n = p.length;

    for( int i = 0; i < ( n / 2 ); i++ ) {
      c_tmp          = p[i];
      p[i]           = p[n - i - 1];
      p[n - i - 1]   = c_tmp;
    }

    return 0;
  }

  //~ Inner Classes ************************************************************

  /**
   * What IEEE single precision floating point looks like on the JVM
   */
  private class IEEESingle {
    //~ Instance fields ********************************************************

    private int sign;  //1 bit
    private int exp;  //8 bits
    private int mantissa;  //23 bits

    //~ Constructors ***********************************************************

    /**
     * Creates a new IEEESingle object.
     */
    public IEEESingle(  ) {}

    /**
     * Creates a new IEEESingle object.
     *
     * @param sign The sign of the floating point.
     * @param exp The exponent.
     * @param mantissa The mantissa.
     */
    public IEEESingle( int sign, int exp, int mantissa ) {
      setSign( sign );
      setExp( exp );
      setMantissa( mantissa );
    }

    //~ Methods ****************************************************************

    /**
     * Sets the exponent.  This must be no more than 256 (8 bit unsigned int).
     *
     * @param exp The exponent.
     */
    public void setExp( int exp ) {
      /*if( exp > 256 ) {
         throw new IllegalArgumentException(
           "IEEE exponent can be no more than 256." );
         }*/
      this.exp = exp;
    }

    /**
     * @return The exponent
     */
    public int getExp(  ) {
      return exp;
    }

    /**
     * Sets the mantissa.  This can be no more than 8,388,608 (23 bit unsigned
     * int).
     *
     * @param i The mantissa to set.
     */
    public void setMantissa( int i ) {
      /*if( mantissa > 8388608 ) {
         throw new IllegalArgumentException(
           "IEEE mantissa can be no more than 8,388,608." );
         }*/
      mantissa = i;
    }

    /**
     * @return The mantissa.
     */
    public int getMantissa(  ) {
      return mantissa;
    }

    /**
     * Sets the sign.  This can be no greater than 1 (1 bit unsigned int).
     *
     * @param sign The new sign.
     */
    public void setSign( int sign ) {
      /*if( sign > 1 ) {
         throw new IllegalArgumentException( "IEEE sign can be no more than 1." );
         }*/
      this.sign = sign;
    }

    /**
     * @return The sign.
     */
    public int getSign(  ) {
      return sign;
    }
  }

  /**
   * Limits on single precision floating point.
   */
  private class SingleLimits {
    //~ Instance fields ********************************************************

    private VAXSingle  s;
    private IEEESingle ieee;

    //~ Constructors ***********************************************************

    /**
     * Creates a new SingleLimits object.
     */
    public SingleLimits(  ) {}

    /**
     * Creates a new SingleLimits object.
     *
     * @param VAXMantissa2 The second mantissa for the VAX single floating
     *        point.
     * @param VAXsign The sign for the VAX single floating point.
     * @param VAXexp The exponent for the VAX single floating point.
     * @param VAXmantissa1 The first mantissa for the VAX single floating
     *        point.
     * @param IEEEsign The mantissa for the IEEE single floating point.
     * @param IEEEexp The exponent for the IEEE single floating point.
     * @param IEEEmantissa The mantissa for the IEEE single floating point.
     */
    public SingleLimits( 
      int VAXMantissa2, int VAXsign, int VAXexp, int VAXmantissa1, int IEEEsign,
      int IEEEexp, int IEEEmantissa ) {
      s      = new VAXSingle( VAXMantissa2, VAXsign, VAXexp, VAXmantissa1 );
      ieee   = new IEEESingle( IEEEsign, IEEEexp, IEEEmantissa );
    }

    //~ Methods ****************************************************************

    /**
     * Sets the IEEE single.
     *
     * @param single The new IEEESingle.
     */
    public void setIEEE( IEEESingle single ) {
      ieee = single;
    }

    /**
     * @return The IEEESingle.
     */
    public IEEESingle getIEEE(  ) {
      return ieee;
    }

    /**
     * Sets the VAX Single.
     *
     * @param single The new VAXSingle.
     */
    public void setVAX( VAXSingle single ) {
      s = single;
    }

    /**
     * @return The VAX Single.
     */
    public VAXSingle getVAX(  ) {
      return s;
    }
  }

  /**
   * Vax single precision floating point
   */
  private class VAXSingle {
    //~ Instance fields ********************************************************

    private int mantissa2;  //16
    private int sign;  //1
    private int exp;  //8
    private int mantissa1;  //7

    //~ Constructors ***********************************************************

    /**
     * Creates a new VAXSingle object.
     */
    public VAXSingle(  ) {}

    /**
     * Creates a new VAXSingle object.
     *
     * @param mantissa2 The second VAX mantissa
     * @param sign The sign.
     * @param exp The VAX exponent.
     * @param mantissa1 The first VAX mantissa.
     */
    public VAXSingle( int mantissa2, int sign, int exp, int mantissa1 ) {
      setSign( sign );
      setExp( exp );
      setMantissa1( mantissa1 );
      setMantissa2( mantissa2 );
    }

    //~ Methods ****************************************************************

    /**
     * Sets the exponent.  This can be no larger than 256 (8 bit unsigned int).
     *
     * @param i The new exponent.
     */
    public void setExp( int i ) {
      /*if( i > 256 ) {
         throw new IllegalArgumentException(
           "VAX exponent can be no more than 256." );
         }*/
      exp = i;
    }

    /**
     * @return The VAX exponent.
     */
    public int getExp(  ) {
      return exp;
    }

    /**
     * Sets the first VAX mantissa.  This can be no larger than 128 (7 bit
     * unsigned int).
     *
     * @param i The new first mantissa.
     */
    public void setMantissa1( int i ) {
      /*if( i > 128 ) {
         throw new IllegalArgumentException(
           "VAX mantissa1 can be no more than 128." );
         }*/
      mantissa1 = i;
    }

    /**
     * @return The first VAX mantissa.
     */
    public int getMantissa1(  ) {
      return mantissa1;
    }

    /**
     * Set the second VAX mantissa.  This can be no larger than 65536 (16 bit
     * unsigned int).
     *
     * @param i The new second VAX mantissa.
     */
    public void setMantissa2( int i ) {
      /*if( i > 65536 ) {
         throw new IllegalArgumentException(
           "VAX mantissa2 can be no more than 65536." );
         }*/
      mantissa2 = i;
    }

    /**
     * @return The second VAX mantissa.
     */
    public int getMantissa2(  ) {
      return mantissa2;
    }

    /**
     * Sets the VAX sign.  This must be no more than 1 (1 bit unsigned int).
     *
     * @param i The new sign.
     */
    public void setSign( int i ) {
      /*if( i > 1 ) {
         throw new IllegalArgumentException( "VAX sign can be no more than 1." );
         }*/
      sign = i;
    }

    /**
     * @return The VAX sign.
     */
    public int getSign(  ) {
      return sign;
    }
  }
}
