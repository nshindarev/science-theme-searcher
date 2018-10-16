package io;

import datamapper.Author;
import storage.AuthorsDB;
import datamapper.Publication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogStatistics {
    private static final Logger logger = LoggerFactory.getLogger(LogStatistics.class);
    private LogStatistics(){

    }
    public static void logAuthorsPublications(Author auth){
        logger.debug("________________________ ");
        logger.debug("___Author statistics____ ");
        logger.debug("________________________ ");

        for(Publication pub: auth.getPublications()){
            logger.debug(pub.toString());

        }
        logger.debug("------------------------ ");
    }
    public static void logAuthorsDB_auth (){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        logger.debug("________________________ ");
        logger.debug("DB CONDITION ON " + dateFormat.format(date));
        logger.debug("STORAGE SIZE "+ AuthorsDB.getAuthorsStorage().size());
        logger.debug("________________________");

        for (Author a : AuthorsDB.getAuthorsStorage()){
            logger.debug(a.getSurname()+" "+a.getN()+". "+a.getP()+".");
        }

    }

}
