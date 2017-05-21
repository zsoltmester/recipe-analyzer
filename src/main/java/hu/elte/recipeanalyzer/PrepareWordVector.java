/*
  Code based on DL4J examples
  ===========================
  This program generates a word-vector from news items stored in resources folder.
  News File is located at \dl4j-examples\src\main\resources\NewsData\RawNewsToGenerateWordVector.txt
  Word vector file : \dl4j-examples\src\main\resources\NewsData\NewsWordVector.txt
  Note :
  1) This code is modification of original example named Word2VecRawTextExample.java
  2) Word vector generated in this program is used in Training RNN to categorise news headlines.
  <p>
  <b></b>KIT Solutions Pvt. Ltd. (www.kitsol.com)</b>
 */

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

import java.io.File;

import static hu.elte.recipeanalyzer.ResourceManager.RESOURCES_DIRECTORY;
import static hu.elte.recipeanalyzer.ResourceManager.copyResourcesFromJar;

public class PrepareWordVector {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrepareWordVector.class);

    public static void main(String[] args) throws Exception {

        copyResourcesFromJar(new File(PrepareWordVector.class.getProtectionDomain().getCodeSource().getLocation().getPath()), "/RecipeData/");

        File resource = new File(RESOURCES_DIRECTORY + "/RecipeData/RawRecipesToGenerateWordVector.txt");

        // Gets Path to Text file
        String filePath = resource.getPath();

        LOGGER.debug("Load & Vectorize Sentences....");
        // Strip white space before and after for each line
        SentenceIterator iter = new BasicLineIterator(filePath);
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();

        //CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
        //So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
        //Additionally it forces lower case for all tokens.
        t.setTokenPreProcessor(new CommonPreprocessor());

        LOGGER.debug("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(2)
                .iterations(5)
                .layerSize(100)
                .seed(42)
                .windowSize(20)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        LOGGER.debug("Fitting Word2Vec model....");
        vec.fit();

        LOGGER.debug("Writing word vectors to text file....");
        WordVectorSerializer.writeWordVectors(vec, RESOURCES_DIRECTORY + "/RecipesWordVector.txt");
    }
}
