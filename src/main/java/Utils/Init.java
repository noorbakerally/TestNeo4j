package Utils;

import edu.mit.jwi.Dictionary;
import edu.sussex.nlp.jws.JWS;

import java.io.File;
import java.io.IOException;

public class Init {
    public static void init() throws IOException {

        System.out.println();
        System.setProperty("wordnet.database.dir",Global.wordnetDictPath);
        Global.ws = new JWS(Global.wordnetPath,"3.0");
        System.out.println(Global.wordnetDictPath);
        Global.dict = new Dictionary(new File(Global.wordnetDictPath));
        try{if (!Global.dict.isOpen()){ Global.dict.open();}}catch (Exception e){e.printStackTrace();}

    }
}
