package hu.elte.recipeanalyzer;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static hu.elte.recipeanalyzer.ResourceManager.RESOURCES_DIRECTORY;
import static hu.elte.recipeanalyzer.ResourceManager.copyResourcesFromJar;

/**
 * Prepares the recipes word vector from the raw word vector. Use the {@link #main(String[])} to run this.
 */
public class PrepareWordVector {

    /**
     * The logger instance for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PrepareWordVector.class);

    /**
     * The main method of the {@link PrepareWordVector}. No arguments needed.
     *
     * @param args Unused.
     */
    public static void main(String[] args) throws Exception {

        // prepare the resources
        copyResourcesFromJar(ResourceManager.THIS_JAR, "/RecipeData");
        String recipesFilePath = RESOURCES_DIRECTORY + "/RecipeData/RawRecipesToGenerateWordVector.txt";

        // create a line iterator
        SentenceIterator lineIterator = new BasicLineIterator(recipesFilePath);

        // create a tokenizer
        TokenizerFactory tokenizer = new DefaultTokenizerFactory();
        tokenizer.setTokenPreProcessor(new CommonPreprocessor());

        // create, fit and save the word vector
        LOGGER.info("Building and fitting the word vector...");
        Word2Vec wordVector = buildWordVector(lineIterator, tokenizer);
        wordVector.fit();
        WordVectorSerializer.writeWordVectors(wordVector, RESOURCES_DIRECTORY + "/RecipesWordVector.txt");
    }

    /**
     * Builds the word vector based on the given parameters. You can tune the parameters of the word vector in this method.
     *
     * @param lineIterator The line iterator. It cannot be null.
     * @param tokenizer    The tokenizer. It cannot be null.
     * @return The built word vector.
     */
    private static Word2Vec buildWordVector(SentenceIterator lineIterator, TokenizerFactory tokenizer) {

        return new Word2Vec.Builder()
                .minWordFrequency(2)
                .iterations(5)
                .layerSize(100)
                .seed(42)
                .windowSize(20)
                .iterate(lineIterator)
                .tokenizerFactory(tokenizer)
                .build();
    }

}
