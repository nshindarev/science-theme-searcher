package main;

import database.model.Author;
import database.model.Cluster;
import database.service.AuthorService;
import database.service.ClusterService;
import database.service.KeywordService;
import elibrary.auth.LogIntoElibrary;
import elibrary.parser.Parser;
import database.model.Keyword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main (String[] args){


        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        /**
         *  method output:
         *  Pages.startPage
         *  Pages.authorSearchPage
         */
        LogIntoElibrary.auth();


        new Parser(new Keyword("социоинженерные атаки")).parse();
    }

}
