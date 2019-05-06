package Utils;

import Word.Keyword;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.*;

public class Utils {

    /**
     * Given a dictionary and a word, find all the parts of speech the
     * word can be.
     */
    public static Collection getPartsOfSpeech(Dictionary dict, String word) {
        ArrayList<POS> parts = new ArrayList<POS>();
        WordnetStemmer stemmer = new WordnetStemmer(dict);
        // Check every part of speech.
        for (POS pos : POS.values()) {
            // Check every stem, because WordNet doesn't have every surface
            // form in its database.
            for (String stem : stemmer.findStems(word, pos)) {
                IIndexWord iw = dict.getIndexWord(stem, pos);
                if (iw != null) {
                    parts.add(pos);
                }
            }
        }
        return parts;
    }

    public static Set <Keyword> getKeywordsFromURL(String input){
        Set<Keyword> labelKeywords = new HashSet<>();
        try{
            URL u = new URL(input); // this would check for the protocol
            u.toURI();
            input = input.substring(input.lastIndexOf("/")+1,input.length());
            if (input.contains("#")){
                input = input.substring(input.lastIndexOf("#")+1,input.length());
            }
            input = String.join(" ",input.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"));

            labelKeywords = Utils.extractKeywords(input, true);
            if (labelKeywords.size() == 0){
                for (String rawWord:input.split(" ")){
                    labelKeywords.add(new Keyword(rawWord));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return labelKeywords;
    }


    public static Set<Keyword> extractKeywords (String input, boolean label) throws IOException {
        Set <Keyword> keywords = new HashSet<Keyword>();

        TokenStream tokenStream = null;
        try {

            /*Text cleaning and replacement*/
            // hack to keep dashed words (e.g. "non-specific" rather than "non" and "specific")
            input = input.replaceAll("-+", "-0");
            // replace any punctuation char but apostrophes and dashes by a space
            input = input.replaceAll("[\\p{Punct}&&[^'-]]+", " ");
            // replace most common english contractions
            input = input.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");

            AttributeFactory factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;

            //Initialize Tokenizer
            StandardTokenizer standardTokenizer =  new StandardTokenizer(factory);
            standardTokenizer.setReader(new StringReader(input));

            // to lowercase
            tokenStream = new LowerCaseFilter(standardTokenizer);

            // remove dots from acronyms (and "'s" but already done manually above)
            tokenStream = new ClassicFilter(tokenStream);

            // convert any char to ASCII
            tokenStream = new ASCIIFoldingFilter(tokenStream);


            /*additional stopwords*/
            String[] alphabet = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u",
                    "v","w","x","y","z"};
            String [] digits = {"0","1","2","3","4","5","6","7","8","9"};


            String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across",
                    "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};
            CharArraySet englishStopWords = new CharArraySet(EnglishAnalyzer.getDefaultStopSet(),false);
            String[] domainStopWords = {"ontology","concept","data","owl","domain","http"};
            for (String stopword:stopwords){
                englishStopWords.add(stopword);
            }
            for (String stopword:alphabet){
                englishStopWords.add(stopword);
            }
            for (String stopword:alphabet){
                englishStopWords.add(digits);
            }
            for (String stopword:domainStopWords){
                englishStopWords.add(stopword);
            }
            tokenStream = new StopFilter(tokenStream,englishStopWords);
            tokenStream.reset();

            //Loop over tokens
            Set <String> rawKeywords = new HashSet<>();
            while (tokenStream.incrementToken()){

                //get current keywords
                String keyword =tokenStream.getAttribute(CharTermAttribute.class).toString();

                //remove keywords that contain numbers
                if (keyword.matches(".*\\d.*")){
                    continue;
                }


                //get part of speech of keyword
                Collection parts = getPartsOfSpeech(Global.dict, keyword);
                POS pos = null;
                if (label){
                    if (parts.iterator().hasNext()){
                        pos = (POS) parts.iterator().next();
                    } else {
                        //the label is meaningless probably
                        continue;
                    }

                } else {
                    //keywords are from a comment
                    if (parts!=null & parts.size()>0 && !parts.contains(POS.VERB)){
                        /*if ("dogontinteroperationdomoticgithub".contains(keyword)){
                            continue;
                        }
                        System.out.println(keyword+" "+parts);*/

                        pos = (POS)parts.iterator().next();
                    } else {continue;}
                }


                //System.out.println(keyword);
                rawKeywords.add(stemWord(keyword,pos));
            }/**End Of loop over keywords**/

            rawKeywords.forEach(currentKeyword->{
                if (currentKeyword.length()>2){
                    keywords.add(new Keyword(currentKeyword));
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                tokenStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return keywords;
    }
    public static String stemWord(String oKeyword, POS pos){
        try{if (!Global.dict.isOpen()){ Global.dict.open();}}catch (Exception e){e.printStackTrace();}
        String fKeyword = "";
        //use PorterStemmer and stem
        //check if stem word exist in wordnet
        //if yes, return keyword
        //else use original keyword
        //WARNING: is there a need to perform disambiguation ?


        WordnetStemmer wstemmer = new WordnetStemmer(Global.dict);
        PorterStemmer pstemmer = new PorterStemmer();

        pstemmer.setCurrent(oKeyword);
        pstemmer.stem();
        String tempStem = pstemmer.getCurrent();

        List<String> stems = wstemmer.findStems(tempStem, pos);
        if (stems.size()>0){
            String keyword0 = stems.get(0);
            IIndexWord word = Global.dict.getIndexWord(keyword0, pos);

            if (word !=null){
                fKeyword = word.getLemma();
                if (fKeyword != null) {
                    return fKeyword;
                }
            }
        }

        stems = wstemmer.findStems(oKeyword, pos);
        String keyword0 = stems.get(0);
        IIndexWord word = Global.dict.getIndexWord(keyword0, pos);
        fKeyword = word.getLemma();
        if (fKeyword!=null){
            return fKeyword;
        }

        return fKeyword;
    }
    public static Collection <Keyword> addUniqueKeywords(Collection<Keyword> sourceKeywords, Collection<Keyword> FinalKeywords){
        for (Keyword keyword:sourceKeywords){
            if (!FinalKeywords.toString().contains(keyword.toString())){
                FinalKeywords.add(keyword);
            }
        }
        return FinalKeywords;
    }
}
