package Utils;

import edu.sussex.nlp.jws.JWS;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

import java.util.*;

public class Global {
    public static String exampleNS="http://example.com/";
    public static Map <String,String> IriToLabelMapping = new HashMap<String, String>();
    public static String pathWritableFiles="file:///home/noor/Downloads/ProjectTest/";

    public static List<String> labelIRIs = Arrays.asList(DCTerms.title.getURI(), RDFS.label.getURI());
    public static List<String> commentIRIs = Arrays.asList(DCTerms.description.getURI(), SKOS.definition.getURI(),
            RDFS.comment.getURI());

    public static String wordnetPath = "/home/noor/Downloads/WordNet";
    public static String wordnetDictPath = wordnetPath+"/3.0/dict/";

    public static Dictionary dict;
    public static JWS ws;

}
