package hu.elte.recipeanalyzer;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import javax.swing.*;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static hu.elte.recipeanalyzer.ResourceManager.copyResourcesFromJar;

/**
 * Tester for the neutral network that created in the {@link TrainRecipes}.
 */
public class TestRecipes extends JFrame {

    private final WordVectors wordVectors;
    private final TokenizerFactory tokenizerFactory;
    private final MultiLayerNetwork net;

    private JLabel jLabel3;
    private JTextArea recipeTextArea;

    public TestRecipes() throws IOException {

        initComponents();

        copyResourcesFromJar(new File(PrepareWordVector.class.getProtectionDomain().getCodeSource().getLocation().getPath()), "/RecipeData/");

        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        String wordVectorsPath = ResourceManager.RESOURCES_DIRECTORY + "/RecipeData/RecipesWordVector.txt";
        wordVectors = WordVectorSerializer.loadTxtVectors(new File(wordVectorsPath));

        net = ModelSerializer.restoreMultiLayerNetwork(ResourceManager.RESOURCES_DIRECTORY + "/RecipeData/RecipesModel.net");
    }

    private void initComponents() {

        this.setTitle("Predict Recipe Category");
        JLabel jLabel1 = new JLabel();
        JScrollPane jScrollPane1 = new JScrollPane();
        recipeTextArea = new JTextArea();
        JButton jButton1 = new JButton();
        JLabel jLabel2 = new JLabel();
        jLabel3 = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Type Recipe Here");

        recipeTextArea.setColumns(20);
        recipeTextArea.setRows(5);
        jScrollPane1.setViewportView(recipeTextArea);

        jButton1.setText("Check");
        jButton1.addActionListener(evt -> jButton1ActionPerformed());

        jLabel2.setText("Category");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel1)
                                                        .addComponent(jButton1))
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(jButton1)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel2))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    /**
     * The main method of the {@link TestRecipes}. No arguments needed.
     *
     * @param args Unused.
     */
    public static void main(String args[]) throws Exception {

        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }

        TestRecipes test = new TestRecipes();
        test.setVisible(true);
    }

    private void jButton1ActionPerformed() {

        DataSet testRecipe = prepareTestData(recipeTextArea.getText());
        INDArray featureMatrix = testRecipe.getFeatureMatrix();
        INDArray predicted = net.output(featureMatrix, false);
        int arraySize[] = predicted.shape();

        String dataPath = ResourceManager.RESOURCES_DIRECTORY + "/RecipeData/LabelledRecipes";
        File categories = new File(dataPath + File.separator + "categories.txt");

        double max = 0;
        int pos = 0;
        for (int i = 0; i < arraySize[1]; i++) {
            if (max < (double) predicted.getColumn(i).sumNumber()) {
                max = (double) predicted.getColumn(i).sumNumber();
                pos = i;
            }
        }

        try (BufferedReader brCategories = new BufferedReader(new FileReader(categories))) {
            String temp;
            List<String> labels = new ArrayList<>();
            while ((temp = brCategories.readLine()) != null) {
                labels.add(temp);
            }
            brCategories.close();
            jLabel3.setText(labels.get(pos).split(",")[1]);
        } catch (Exception e) {
            System.out.println("File Exception : " + e.getMessage());
        }
    }

    private DataSet prepareTestData(String recipe) {

        List<String> recipes = new ArrayList<>(1);
        int[] category = new int[1];
        recipes.add(recipe);

        List<List<String>> allTokens = new ArrayList<>(recipes.size());
        int maxLength = 0;
        for (String s : recipes) {
            List<String> tokens = tokenizerFactory.create(s).getTokens();
            List<String> tokensFiltered = new ArrayList<>();
            for (String t : tokens) {
                if (wordVectors.hasWord(t)) tokensFiltered.add(t);
            }
            allTokens.add(tokensFiltered);
            maxLength = Math.max(maxLength, tokensFiltered.size());
        }

        INDArray features = Nd4j.create(recipes.size(), wordVectors.lookupTable().layerSize(), maxLength);
        INDArray labels = Nd4j.create(recipes.size(), 5, maxLength);
        INDArray featuresMask = Nd4j.zeros(recipes.size(), maxLength);
        INDArray labelsMask = Nd4j.zeros(recipes.size(), maxLength);

        int[] temp = new int[2];
        for (int i = 0; i < recipes.size(); i++) {
            List<String> tokens = allTokens.get(i);
            temp[0] = i;
            for (int j = 0; j < tokens.size() && j < maxLength; j++) {
                String token = tokens.get(j);
                INDArray vector = wordVectors.getWordVectorMatrix(token);
                features.put(new INDArrayIndex[]{NDArrayIndex.point(i),
                                NDArrayIndex.all(),
                                NDArrayIndex.point(j)},
                        vector);

                temp[1] = j;
                featuresMask.putScalar(temp, 1.0);
            }
            int idx = category[i];
            int lastIdx = Math.min(tokens.size(), maxLength);
            labels.putScalar(new int[]{i, idx, lastIdx - 1}, 1.0);
            labelsMask.putScalar(new int[]{i, lastIdx - 1}, 1.0);
        }

        return new DataSet(features, labels, featuresMask, labelsMask);
    }

    public static String toString(JTextArea recipeTextArea) {
        return "Text area content: " + recipeTextArea.getText();
    }
}
