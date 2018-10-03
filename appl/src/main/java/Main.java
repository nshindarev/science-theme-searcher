import datamapper.AuthorsDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

/*
    TODO: - добавить анализ страницы автора в Author class
    TODO: - смаппить в список публикаций HTML таблицу публикаций автора
    TODO: - публикация - класс с названием и авторами
    TODO: - сделать выгрузку в Excel авторов и публикаций
    TODO: - сделать рекурсивный поиск авторов и публикаций (???)

*/
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main (String[] args) throws IOException {
        logger.info("Started project");

        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);


        AuthorsDB.authorsStorage = new HashSet<>();
        AuthorsDB.publicationsStorage = new HashMap<>();


        RinzParser parser = new RinzParser();
    }

}
