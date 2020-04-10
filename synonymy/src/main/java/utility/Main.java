package utility;

import implementation.LengthComparatorImpl;
import implementation.TranslatorImpl;
import org.apache.log4j.BasicConfigurator;
import service.LengthComparator;
import service.Translator;

public class Main {
    static String testString1 = "Suleimanov";
    static String testString2 = "Suleymanov";
    static String testString3 = "Suleimenov";
    static String testString4 = "Suleymanoff";

    public static void main(String[] args) {
        BasicConfigurator.configure();
        LengthComparator lengthComparator = new LengthComparatorImpl();

        System.out.println("Result 1 is: "+lengthComparator.getLength(testString1,testString2));
        System.out.println("Result 2 is: "+lengthComparator.getLength(testString1,testString3));
        System.out.println("Result 3 is: "+lengthComparator.getLength(testString1,testString4));

    }
}