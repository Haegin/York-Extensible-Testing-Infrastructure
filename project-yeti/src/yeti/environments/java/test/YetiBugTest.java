package yeti.environments.java.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import yeti.environments.java.YetiBug;

public class YetiBugTest {
    
    private YetiBug bug = new YetiBug(this.getClass().getName(), "yeti.environments.java.test.setUp_136");

    @Before
    public void setUp() throws Exception {
        int[] numbs = { 1, 2, 3};
        try {
            int a = numbs[3]; // If this isn't on line 18 then the line number test below needs changing.
        } catch(Exception ex) {
            bug.setThrowable(ex);
        }
    }
    
    @Test
    public void testMethodName() {
        assertEquals("setUp", bug.getUsefulMethodName());
    }

    @Test
    public void testLineNumbers() {
        bug.setLineNumberFromException();
        assertEquals(18, (int) bug.getLineNumber());
    }

}
