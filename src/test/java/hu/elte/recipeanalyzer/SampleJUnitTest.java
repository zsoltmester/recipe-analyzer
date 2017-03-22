package hu.elte.recipeanalyzer;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SampleJUnitTest {

    private Main main;

    @Before
    public void before() {
        main = Mockito.mock(Main.class);
    }

    @Test
    public void mySampleTest() throws Exception {
        Mockito.when(main.toString()).thenReturn("ASD");
        TestCase.assertEquals("ASD", main.toString());
    }
}