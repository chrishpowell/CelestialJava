/*
 */
package eu.discoveri.codesnippets.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.neo4j.ogm.annotation.Properties;


/**
 *
 * @author Chris Powell, Discoveri OU
 * @param <Void>
 * @email info@astrology.ninja
 */
public class Token <Void> extends GraphEntity implements Predicate<Void>
{
    private final String    token;
    private String          stem;
    private String          lemma;
    private String          pos ="0X0X";
    private List<Double>    probabilities = new ArrayList<>();
    
    
    /**
     * Constructor.
     * 
     * @param token 
     */
    public Token(String token)
    {
        super("tiktok",token);
        this.token = token;
    }

    /**
     * Test for token being Noun or Adjective.
     * 
     * @param v
     * @return 
     */
    @Override
    public boolean test( Void v )
    {
        return  this.getPOS().equals(PennPOSCode.NN.name()) ||
                this.getPOS().equals(PennPOSCode.NNS.name()) ||
                this.getPOS().equals(PennPOSCode.NNP.name()) ||
                this.getPOS().equals(PennPOSCode.NNPS.name()) ||
                this.getPOS().equals(PennPOSCode.JJ.name()) ||
                this.getPOS().equals(PennPOSCode.JJR.name()) ||
                this.getPOS().equals(PennPOSCode.JJS.name());
    }
    
    /**
     * Lemma for this token.
     * @param lemma 
     */
    public void setLemma(String lemma) { this.lemma = lemma; }
    
    /**
     * Stem for this token.
     * @param stem 
     */
    public void setStem(String stem) { this.stem = stem; }

    /**
     * POS tag for this token.
     * @param pos 
     */
    public void setPos(String pos) { this.pos = pos; }

    /**
     * Set list of probabilities.
     * @param tprobs
     */
    public void setProbabilities(double[] tprobs)
    {
        this.probabilities = DoubleStream.of(tprobs).boxed().collect(Collectors.toList());
    }

    /**
     * Get the token.
     * @return 
     */
    public String getToken() { return token; }

    /**
     * Get the stem.
     * @return 
     */
    public String getStem() { return stem; }

    /**
     * Get the probabilities.
     * @return 
     */
    public List<Double> getProbability() { return probabilities; }
    
    /**
     * Get the POS type.
     * 
     * @return 
     */
    public String getPOS() { return pos; }
    
    /**
     * Token plus stem.
     * @return 
     */
    @Override
    public String toString()
    {
        return token+": ("+lemma+") ["+pos+"]";
    }

    /**
     * Hashcode.
     * @return 
     */
    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.token);
        return hash;
    }

    /**
     * Equals.
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj) { return true; }
        if(obj == null) { return false; }
        if(getClass() != obj.getClass()) { return false; }
        
        final Token tok = (Token)obj;
        return tok.token.equals(token);
    }
}
