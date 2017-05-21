package hu.elte.recipeanalyzer;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static hu.elte.recipeanalyzer.ResourceManager.copyResourcesFromJar;

/**
 * Trains a neutral network to label a recipe with a category from a predefined set. You should have a
 * RecipesWordVector.txt as a jar resource. YOu can use the {@link PrepareWordVector} to generate it.
 */
public class TrainRecipes {

    /**
     * The logger instance for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainRecipes.class);

    /**
     * The batch size.
     */
    private static final int BATCH_SIZE = 50;

    /**
     * The number of epochs (turns) to run the training.
     */
    private static final int NUMBER_OF_EPOCHS = 100;

    /**
     * As number of words.
     */
    private static final int RECIPE_MAX_LENGTH = 300;

    /**
     * The main method of the {@link TrainRecipes}. No arguments needed.
     *
     * @param args Unused.
     */
    public static void main(String[] args) throws Exception {

        // prepare the resources
        copyResourcesFromJar(ResourceManager.THIS_JAR, "/RecipeData/");
        String recipesPath = ResourceManager.RESOURCES_DIRECTORY + "/RecipeData/LabelledRecipes";

        // load the word vectors
        WordVectors wordVectors = loadWordVectors();

        // create a tokenizer
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        // create the training iterator
        RecipesIterator trainingIterator = new RecipesIterator.Builder()
                .dataDirectory(recipesPath)
                .wordVectors(wordVectors)
                .batchSize(BATCH_SIZE)
                .truncateLength(RECIPE_MAX_LENGTH)
                .tokenizerFactory(tokenizerFactory)
                .train(true)
                .build();

        // create the testing iterator
        RecipesIterator testingIterator = new RecipesIterator.Builder()
                .dataDirectory(recipesPath)
                .wordVectors(wordVectors)
                .batchSize(BATCH_SIZE)
                .tokenizerFactory(tokenizerFactory)
                .truncateLength(RECIPE_MAX_LENGTH)
                .train(false)
                .build();

        // create the neutral network
        MultiLayerNetwork neutralNetwork = createNeutralNetwork(wordVectors, trainingIterator.getLabels().size());

        // train the neutral network
        LOGGER.info("Training the neutral network...");
        for (int i = 0; i < NUMBER_OF_EPOCHS; i++) {
            neutralNetwork.fit(trainingIterator);
            trainingIterator.reset();
            LOGGER.debug("Epoch " + i + " complete. Starting evaluation:");

            // evaluate the results after this iteration
            Evaluation evaluation = new Evaluation();
            while (testingIterator.hasNext()) {
                DataSet t = testingIterator.next();
                INDArray features = t.getFeatureMatrix();
                INDArray lables = t.getLabels();
                INDArray outMask = t.getLabelsMaskArray();
                INDArray predicted = neutralNetwork.output(features, false);
                evaluation.evalTimeSeries(lables, predicted, outMask);
            }
            testingIterator.reset();
            LOGGER.debug(evaluation.stats());
        }

        // save the model
        LOGGER.info("Saving the model...");
        ModelSerializer.writeModel(neutralNetwork, ResourceManager.RESOURCES_DIRECTORY + "/RecipeData/RecipesModel.net", true);
    }

    /**
     * Loads the word vectors from the {@code ResourceManager.RESOURCES_DIRECTORY + "/RecipeData/RecipesWordVector.txt"}.
     *
     * @return The loaded word vectors.
     */
    private static WordVectors loadWordVectors() throws IOException {

        File wordVectorsFile = new File(ResourceManager.RESOURCES_DIRECTORY + "/RecipeData/RecipesWordVector.txt");
        return WordVectorSerializer.loadTxtVectors(wordVectorsFile);
    }

    /**
     * Creates the neutral network based on the given params. You can tune the parameters of the neutral network in this method.
     *
     * @param wordVectors            The word vectors.
     * @param numberOfTrainingLabels The number of the training labels.
     * @return The created neutral network.
     */
    private static MultiLayerNetwork createNeutralNetwork(WordVectors wordVectors, int numberOfTrainingLabels) {

        int inputNeurons = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).length;

        MultiLayerConfiguration neutralNetworkConfiguration = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .iterations(1)
                .updater(Updater.RMSPROP)
                .regularization(true)
                .l2(1e-5)
                .weightInit(WeightInit.XAVIER)
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                .gradientNormalizationThreshold(1.0)
                .learningRate(0.0018)
                .list()
                .layer(0, new GravesLSTM.Builder()
                        .nIn(inputNeurons)
                        .nOut(200)
                        .activation("softsign")
                        .build())
                .layer(1, new RnnOutputLayer.Builder()
                        .activation("softmax")
                        .lossFunction(LossFunctions.LossFunction.MCXENT)
                        .nIn(200)
                        .nOut(numberOfTrainingLabels)
                        .build())
                .pretrain(false)
                .backprop(true)
                .build();

        MultiLayerNetwork neutralNetwork = new MultiLayerNetwork(neutralNetworkConfiguration);
        neutralNetwork.init();

        neutralNetwork.setListeners(new ScoreIterationListener(1));

        return neutralNetwork;
    }

}
