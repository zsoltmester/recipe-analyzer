package hu.elte.recipeanalyzer;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.swing.*;

/**
 * Tests for the {@link TestRecipes}.
 */
public class TestRecipesJUnitTest {

    private JTextArea recipeTextArea;

    @Before
    public void before() {
        recipeTextArea = Mockito.mock(JTextArea.class);
    }

    @Test
    public void toStringTest() throws Exception {
        Mockito.when(recipeTextArea.getText()).thenReturn("my test");

        String textAreaContents = TestRecipes.toString(recipeTextArea);

        TestCase.assertEquals("Text area content: my test", textAreaContents);
    }
}
