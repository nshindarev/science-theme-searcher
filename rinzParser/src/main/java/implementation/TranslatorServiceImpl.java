package implementation;

import com.ibm.icu.text.Transliterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.TranslatorService;
import utility.SynonyConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslatorServiceImpl implements TranslatorService {
    private static final Logger logger = LoggerFactory.getLogger(TranslatorServiceImpl.class);
    Transliterator russianToLatinTrans = Transliterator.getInstance(SynonyConstants.CYRILLIC_TO_LATIN.getValue());

    @Override
    public String translateToLatinString(String inputString) {
        if (checkRussian(inputString)) {
            //logger.info("Russian text was found, transliterating...");
            return russianToLatinTrans.transliterate(inputString);
        }
        else {
            //logger.info("Russian text was NOT found.");
            return inputString;
        }
    }

    @Override
    public boolean checkRussian(String inputString) {
        String regex = "[а-яёА-ЯЁ]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(inputString);
        return m.find();
    }
}
