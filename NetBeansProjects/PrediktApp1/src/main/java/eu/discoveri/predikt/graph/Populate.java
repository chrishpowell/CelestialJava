/*
 */
package eu.discoveri.predikt.graph;

import eu.discoveri.predikt.sentences.LangCode;
import eu.discoveri.predikt.sentences.Language;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
//import java.util.stream.Collectors;

import eu.discoveri.predikt.config.Constants;
import eu.discoveri.predikt.sentences.Token;
import eu.discoveri.predikt.lemmatizer.SimpleLemmatizer;

import eu.discoveri.predikt.exceptions.ListLengthsDifferException;
import eu.discoveri.predikt.exceptions.POSTagsListIsEmptyException;
import eu.discoveri.predikt.exceptions.SentenceIsEmptyException;
import eu.discoveri.predikt.exceptions.TokensListIsEmptyException;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Sequence;


/**
 * Get text to populate Graph nodes.
 * Note: Dictionaries are structured as follows:
 *  token\tab\POStag\tab\lemma
 * 
 * @author Chris Powell, Discoveri OU
 * @email info@astrology.ninja
 */
public class Populate
{
    // OpenNLP functions
    private final SentenceDetectorME    sdme;
    private final TokenizerME           tme;
    private final POSTaggerME           pme;
    private final SimpleTokenizer       st;
    private final SimpleLemmatizer      sl;
    
    
    /**
     * Constructor.Instantiate all NLP Max.Entropy (ME) processes.
     * 
     * This is a heavy constructor, call once only. Note: Model files are found
     * in class Language EnumMap.
     * @param langCode Which language is being processed.
     * @throws FileNotFoundException
     */
    public Populate( LangCode langCode )
            throws FileNotFoundException, IOException
    {
        // Sentence model, model file
        SentenceModel sm = new SentenceModel(new FileInputStream(Constants.RESMODELS+Language.getLangModels().get(langCode).get(Constants.SENTMODEL)));
        // Feed model to detector
        sdme = new SentenceDetectorME(sm);
        
        // Tokenizer model, model file
        TokenizerModel tm = new TokenizerModel(new FileInputStream(Constants.RESMODELS+Language.getLangModels().get(langCode).get(Constants.TOKENMODEL)));
        // Feed model to Max. Entropy tokenizer
        tme = new TokenizerME(tm);
        
        // Simple Tokenizer
        st = SimpleTokenizer.INSTANCE;
        
        // POS Tagger model, model file
        POSModel pm = new POSModel(new FileInputStream(Constants.RESMODELS+Language.getLangModels().get(langCode).get(Constants.SEQMODEL)));
        // Feed model to Max. Entropy tokenizer
        pme = new POSTaggerME(pm);
        
        // Discoveri version of dictionary lemmatizer
        sl = new SimpleLemmatizer();
        //SimpleLemmatizer.simpleLemmatizer(new FileInputStream(Constants.RESMODELS+Language.getLangModels().get(langCode).get(Constants.LEMMATIZE)));
    }

    /**
     * Extract list of sentences from doc.  Get Sentence probability at same time.
     * 
     * @param doc
     * @return 
     */
    public List<SentenceNode> extractSentences( String doc )
    {
        List<SentenceNode> sents = new ArrayList<>();
        
        // Extract sentences (creating a unique name)
        int idx = 0;
        for( String sent: sdme.sentDetect(doc) )
        {
            // Create a roughly unique name
            String uname = "S"+idx+"T"+System.currentTimeMillis();
            
            // Add sentence text to new Sentence
            SentenceNode s = new SentenceNode(uname,sent);
            
            // Add sentence probability
            s.addSentenceProbs( sdme.getSentenceProbabilities() );
            
            // Add to overall list of sentences
            sents.add(s);
            
            // 'Increment' sentence
            ++idx;
        }
        
        return sents;
    }
    
    /**
     * Extract tokens from sentence.
     * 
     * @param s sentence
     * @return list of Tokens
     * @throws SentenceIsEmptyException 
     */
    public List<Token> extractTokens( SentenceNode s )
            throws SentenceIsEmptyException
    {
        return s.tokenizeThisSentence(tme);
    }
    
    /**
     * Get POS tags of a sentence.
     * 
     * @param s
     * @return
     * @throws TokensListIsEmptyException 
     */
    public List<String> posTags( SentenceNode s )
            throws TokensListIsEmptyException
    {
        return s.posTagThisSentence(pme);
    }
    
    /**
     * Tokens (and tags) of the sentence.
     * 
     * @param s
     * @return
     * @throws SentenceIsEmptyException
     * @throws TokensListIsEmptyException 
     */
    public List<Token> tokensAndTags( SentenceNode s )
            throws SentenceIsEmptyException, TokensListIsEmptyException
    {
        return s.tokPosThisSentence(tme, pme);
    }
    
    /**
     * Sequences of the sentence.
     * 
     * @param s
     * @return 
     * @throws eu.discoveri.predikt.exceptions.TokensListIsEmptyException 
     */
    public List<Sequence> sequenceTokens( SentenceNode s )
            throws TokensListIsEmptyException
    {
        return s.addSequences2Sentence(pme);
    }
    
    /**
     * Get the lemmas of tokens of the sentence (via SimpleLemmatizer).
     * 
     * @param s
     * @param match2NN
     * @return
     * @throws TokensListIsEmptyException 
     * @throws POSTagsListIsEmptyException 
     * @throws ListLengthsDifferException 
     */
    public Map<Token,String> lemmasOfSentence( SentenceNode s, boolean match2NN )
            throws TokensListIsEmptyException, POSTagsListIsEmptyException, ListLengthsDifferException
    {   
        return s.lemmatizeThisSentence(sl, match2NN);
    }

    
    public SentenceDetectorME getSdme() { return sdme; }

    public TokenizerME getTme() { return tme; }

    public POSTaggerME getPme() { return pme; }

    public SimpleLemmatizer getSl() { return sl; }
    
    public SimpleTokenizer getSt() { return st; }
}
