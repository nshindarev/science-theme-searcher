package io;

import datamapper.ResearchPoint;
import datamapper.ResearchStarters.Author;
import storage.AuthorsDB;
import datamapper.Publication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class LogStatistics {
    private static final Logger logger = LoggerFactory.getLogger(LogStatistics.class);
    private LogStatistics(){

    }
    public static void logAuthorsPublications(ResearchPoint auth){
        logger.debug("___________________________ ");
        logger.debug("___Statistics For Point____ ");
        logger.debug("___________________________ ");

        logger.debug(auth.toString());
        for(Publication pub: auth.publications){
            logger.debug(pub.toString());
        }
        logger.debug("------------------------ ");
    }
    public static void logAuthorsDB_auth (){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        logger.debug("________________________ ");
        logger.debug("DB STATE ON " + dateFormat.format(date));
        logger.debug("STORAGE SIZE "+ AuthorsDB.getAuthorsStorage().size());
        logger.debug("________________________");

        for (Author a : AuthorsDB.getAuthorsStorage()){
            logger.debug(a.getSurname()+" "+a.getN()+". "+a.getP()+".");
        }

    }
    public static void logAuthorsDB_auth (Set<Author> authors){
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
