package io;

import datamapper.ResearchStarters.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.AuthorsDB;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Serializer {
    private static final Logger logger = LoggerFactory.getLogger(Serializer.class);

    //сохраняем поле соавторов в файл
    public static void serializeData(Author auth){
        try{
            FileOutputStream fos = new FileOutputStream("appl/src/main/resources/serialized/"+auth.getSurname()+".out");

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(auth);
            oos.flush();
            oos.close();
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
        }
    }
    public static void serializeData(){
        try{
            FileOutputStream fos = new FileOutputStream("appl/src/main/resources/serialized/authDB.out");

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(AuthorsDB.getAuthorsStorage());
            oos.flush();
            oos.close();
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
        }

        for (Author auth: AuthorsDB.getAuthorsStorage()){
            serializeData(auth);
        }
    }
    public static void deserializeData(Author auth){
        try{
            FileInputStream fis = new FileInputStream("appl/src/main/resources/serialized/"+auth.getSurname()+".out");

            ObjectInputStream oin = new ObjectInputStream(fis);
            Author ts = (Author) oin.readObject();
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
        }
        catch (ClassNotFoundException ex){
            logger.error(ex.getMessage());
        }
    }
    public static void deserializeData(){
        try{
            FileInputStream fis = new FileInputStream("appl/src/main/resources/serialized/authDB.out");

            ObjectInputStream oin = new ObjectInputStream(fis);
            Set<Author> ts = (Set<Author>) oin.readObject();

            for(Author _auth: ts){
                deserializeData(_auth);
                ts.remove(_auth);
                ts.add(_auth);
            }

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
