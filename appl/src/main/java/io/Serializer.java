package io;

import datamapper.ResearchStarters.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.AuthorsDB;

import java.io.*;
import java.util.Set;

public class Serializer {
    private static final Logger logger = LoggerFactory.getLogger(Serializer.class);
    public static void serializeData(){
        try{
            FileOutputStream fos = new FileOutputStream("resources/temp.out");

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(AuthorsDB.getAuthorsStorage());
            oos.flush();
            oos.close();
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
        }
    }
    public static void deserializeData(){
        try{
            FileInputStream fis = new FileInputStream("resources/temp.out");

            ObjectInputStream oin = new ObjectInputStream(fis);
            Set<Author> ts = (Set<Author>) oin.readObject();

            for(Author auth: ts)
                 logger.debug(auth.getName());

            logger.info("");
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
        }
        catch (ClassNotFoundException ex){
            logger.error(ex.getMessage());
        }
    }
}
