package main;

import datamapper.ResearchStarters.Author;
import storage.AuthorsDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Navigator;

import java.io.IOException;
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
        logger.trace("Started project");

        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);


        AuthorsDB.initAuthorsStorage();
        AuthorsDB.initPublicationsStorage();

        Navigator.webClient.setTimeout(Navigator.timeOut);

//      RinzParser parser = new RinzParser(new Author("Терехов", "Андрей", "Николаевич"));

        RinzParser parser = new RinzParser(new Author("Ловягин", "Юрий", "Никитич"));
//      RinzParser parser = new RinzParser(new Theme("социоинженерные атаки"));
    }

}
