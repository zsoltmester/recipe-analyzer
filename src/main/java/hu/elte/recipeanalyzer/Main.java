package hu.elte.recipeanalyzer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Hello Recipe Analyzer");
        System.out.println("---");
        System.out.println("PrepareWordVector test run");
        PrepareWordVector.main(null);
        System.out.println("---");
    }

    private static void printCategories() {
        try (InputStream inputStream = Main.class.getResourceAsStream("/RecipeData/LabelledRecipes/categories.txt");) {
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String fileContent = scanner.next();
            System.out.println(fileContent);
        } catch (IOException e) {
            System.out.println("Error while reading the categories file.");
        }
    }
}
