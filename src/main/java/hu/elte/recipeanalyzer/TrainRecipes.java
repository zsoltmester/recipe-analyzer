/**-
 * Code based on DL4J examples
 * ===========================
 * This program trains a RNN to predict category of a news headlines. It uses word vector generated from PrepareWordVector.java.
 * - Labeled Recipes are stored in \dl4j-examples\src\main\resources\RecipeData\LabelledRecipes folder in train and test folders.
 * - categories.txt file in \dl4j-examples\src\main\resources\NewsData\LabelledNews folder contains category code and description.
 * - This categories are used along with actual recipes for training.
 * - recipes word vector is contained  in \dl4j-examples\src\main\resources\NewsData\RecipesWordVector.txt file.
 * - Trained model is stored in \dl4j-examples\src\main\resources\NewsData\NewsModel.net file
 * - Recipe Data contains only 5 categories currently.
 * - Data set structure is as given below
 * - categories.txt - this file contains various categories in category id,category description format. Sample categories are as below
 * 0,appetizer
 * 1,dessert
 * 2,main
 * 3,pasta
 * 4,soup
 * - For each category id above, there is a file containing actual recipes
 * - You can add any new category by adding one line in categories.txt and respective recipes file in folder mentioned above.
 * - Below are training results with the news data given with this example.
 * ==========================Scores========================================
 * Accuracy:        0.9343
 * Precision:       0.9249
 * Recall:          0.9327
 * F1 Score:        0.9288
 * ========================================================================
 * <p>
 * Note :
 * - This code is a modification of original example named Word2VecSentimentRNN.java
 * - Results may vary with the data you use to train this network
 * <p>
 * <b>KIT Solutions Pvt. Ltd. (www.kitsol.com)</b>
 */

package hu.elte.recipeanalyzer;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;

public class TrainRecipes {
    public static String userDirectory = "";
    public static String DATA_PATH = "";
    public static String WORD_VECTORS_PATH = "";
    public static WordVectors wordVectors;
    private static TokenizerFactory tokenizerFactory;

    public static void main(String[] args) throws Exception {
        userDirectory = new ClassPathResource("RecipeData").getFile().getAbsolutePath() + File.separator;
        DATA_PATH = userDirectory + "LabelledRecipes";
        WORD_VECTORS_PATH = userDirectory + "RecipesWordVector.txt";

        int batchSize = 50;     //Number of examples in each minibatch, default 50
        int nEpochs = 100;        //Number of epochs (full passes of training data) to train on, default 1000
        int truncateReviewsToLength = 300;  //Truncate reviews with length (# words) greater than this

        //DataSetIterators for training and testing respectively
        //Using AsyncDataSetIterator to do data loading in a separate thread; this may improve performance vs. waiting for data to load
        wordVectors = WordVectorSerializer.loadTxtVectors(new File(WORD_VECTORS_PATH));

        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        RecipesIterator iTrain = new RecipesIterator.Builder()
            .dataDirectory(DATA_PATH)
            .wordVectors(wordVectors)
            .batchSize(batchSize)
            .truncateLength(truncateReviewsToLength)
            .tokenizerFactory(tokenizerFactory)
            .train(true)
            .build();

        RecipesIterator iTest = new RecipesIterator.Builder()
            .dataDirectory(DATA_PATH)
            .wordVectors(wordVectors)
            .batchSize(batchSize)
            .tokenizerFactory(tokenizerFactory)
            .truncateLength(truncateReviewsToLength)
            .train(false)
            .build();

        //DataSetIterator train = new AsyncDataSetIterator(iTrain,1);
        //DataSetIterator test = new AsyncDataSetIterator(iTest,1);

        int inputNeurons = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).length; // 100 in our case
        int outputs = iTrain.getLabels().size();

        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
        //Set up network configuration
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(1)
            .updater(Updater.RMSPROP)
            .regularization(true).l2(1e-5)
            .weightInit(WeightInit.XAVIER)
            .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(1.0)
            .learningRate(0.0018)
            .list()
            .layer(0, new GravesLSTM.Builder().nIn(inputNeurons).nOut(200)
                .activation("softsign").build())
            .layer(1, new RnnOutputLayer.Builder().activation("softmax")
                .lossFunction(LossFunctions.LossFunction.MCXENT).nIn(200).nOut(outputs).build())
            .pretrain(false).backprop(true).build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));

        System.out.println("Starting training");
        for (int i = 0; i < nEpochs; i++) {
            net.fit(iTrain);
            iTrain.reset();
            System.out.println("Epoch " + i + " complete. Starting evaluation:");

            //Run evaluation. This is on 25k reviews, so can take some time
            Evaluation evaluation = new Evaluation();
            while (iTest.hasNext()) {
                DataSet t = iTest.next();
                INDArray features = t.getFeatureMatrix();
                INDArray lables = t.getLabels();
                //System.out.println("labels : " + lables);
                INDArray inMask = t.getFeaturesMaskArray();
                INDArray outMask = t.getLabelsMaskArray();
                INDArray predicted = net.output(features, false);

                //System.out.println("predicted : " + predicted);
                evaluation.evalTimeSeries(lables, predicted, outMask);
            }
            iTest.reset();

            System.out.println(evaluation.stats());
        }

        ModelSerializer.writeModel(net, userDirectory + "RecipesModel.net", true);
        System.out.println("----- Example complete -----");
    }

}
