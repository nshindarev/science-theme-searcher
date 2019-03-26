package io;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import datamapper.ResearchStarters.Author;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.io.CSVExporter;
import org.jgrapht.io.CSVFormat;
import org.jgrapht.io.ComponentNameProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class FileWriterWrap {

    private static final Logger logger = LoggerFactory.getLogger(FileWriterWrap.class);

    //class without any
    private FileWriterWrap(){

    }
    /**
    * logging
    * @param page to be logged into file as xml
    */
    public static void writePageIntoFile (HtmlPage page){
        // write into file
        try (PrintWriter out = new PrintWriter("appl/src/main/resources/page.xml")) {
            out.println(page.asXml());
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
        }
    }
    public static void writePageIntoFile (HtmlPage page, String filename){
        // write into file
        try (PrintWriter out = new PrintWriter("appl/src/main/resources/"+filename+".xml")) {
            out.println(page.asXml());
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
        }
    }

    public static void printFormsEnum (HtmlPage page){
        logger.debug("_______FORMS LIST FOR CUR PAGE_______");
        logger.debug(page.toString());

        for(HtmlForm form: page.getForms()){
            logger.debug(form.toString());
        }
    }

    private static Author curDebugAuth;
    public static void writeAuthorsSetIntoFile (Set<Author> rows, String filename){
        try {
            FileWriter writer = new FileWriter("appl/src/main/resources/"+ filename + ".txt");
            for(Author str: rows) {
                FileWriterWrap.curDebugAuth = str;
                writer.write(str.getSurname() + " " + str.getN()+ ". " + str.getP() + "." +"\n");
            }
            writer.close();
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
        }
        catch (NullPointerException ex){
            logger.error(ex.getMessage());
        }
    }


}
