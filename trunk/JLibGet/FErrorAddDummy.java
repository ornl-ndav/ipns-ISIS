/*
 *       Module:         FERROR_ADD_DUMMY
 *       Author:         Freddie Akeroyd, ISIS
 *       Purpose:        FORTRAN interface to dummy Genie error handling interface
 *
 * Converted to Java by Chris M. Bouzek February 2004
 *
 * $Log$
 * Revision 1.2  2004/04/30 00:18:16  bouzekc
 * New version.  Old version should not have been in CVS.
 *
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
