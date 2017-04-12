package hu.elte.recipeanalyzer;

import org.datavec.api.util.ClassPathResource;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Hello Recipe Analyzer");
        System.out.println("---");
        System.out.println("PrepareWordVector test run");

        //System.out.println(new ClassPathResource(".").getFile().getAbsolutePath());
        PrepareWordVector.main(null);
        System.out.println("---");
    }
}
