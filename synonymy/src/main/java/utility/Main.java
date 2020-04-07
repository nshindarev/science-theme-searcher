package utility;

import implementation.TranslatorImpl;
import org.apache.log4j.BasicConfigurator;
import service.Translator;

public class Main {
    static String testString1 = "Test me motherfucker";
    static String testString2 = "Проверь меня матерь божья";
    static String testString3 = "Test me матерь божья";

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Translator translator = new TranslatorImpl();

        String result1 = translator.translateToLatinString(testString1);
        System.out.println(result1);

        String result2 = translator.translateToLatinString(testString2);
        System.out.println(result2);

        String result3 = translator.translateToLatinString(testString3);
        System.out.println(result3);
    }
}