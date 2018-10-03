package io;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

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
}
