import static org.junit.Assert.*;
import java.io.*;
import org.junit.Test;

/** Class automatically generated by YETI **/
public class unitTestFile {

   /** Test case automatically generated by YETI **/
   @Test public void test_1() throws Exception {
      int v140=0;
      yeti.test.YetiTest v199=new yeti.test.YetiTest();
      v199.printInt((int)v140);
      /**BUG FOUND: RUNTIME EXCEPTION**/
      /**YETI EXCEPTION - START 
      java.lang.ArithmeticException: / by zero
      	at yeti.test.YetiTest.printInt(YetiTest.java:85)
      YETI EXCEPTION - END**/ 
      /** original locs: 423 minimal locs: 3**/
   
   }


/** Non-Unique bugs: 73, Unique Bugs: 1, Logs size (locs): 2055**/

}