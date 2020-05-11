package elibrary.preprocessing;

import com.ibm.icu.text.Transliterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public  class Translator {
    public Logger logger = LoggerFactory.getLogger(Translator.class);
    public static final String CYRILLIC_TO_LATIN = "Cyrillic-Latin";

    public static void main(String[] args) {
        String st = "привет мир";

        Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);
        String result = toLatinTrans.transliterate(st);
        System.out.println(result);
    }

    public static String translate(String cyrillic){
        Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);
        String result = toLatinTrans.transliterate(cyrillic);
        return result;
    }
}
