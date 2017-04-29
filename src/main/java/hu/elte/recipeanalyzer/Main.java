package hu.elte.recipeanalyzer;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Main {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        LOGGER.info("Hello Recipe Analyzer");
    }

    private static void printCategories() {
        try (InputStream inputStream = Main.class.getResourceAsStream("/RecipeData/LabelledRecipes/categories.txt");) {
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String fileContent = scanner.next();
            LOGGER.info(fileContent);
        } catch (IOException e) {
            LOGGER.error("Error while reading the categories file.");
        }
    }
}
