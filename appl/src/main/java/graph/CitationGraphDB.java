package graph;

import datamapper.ResearchStarters.Author;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.exceptions.ClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.AuthorsDB;

import java.util.Set;

import static org.neo4j.driver.v1.Values.parameters;

public class CitationGraphDB {
    private static final Logger logger = LoggerFactory.getLogger(CitationGraphDB.class);

    private Driver driver;
    public CitationGraphDB(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public void storeParserResults(){

        for(Author author: AuthorsDB.getAuthorsStorage()){
            if (author.getCluster() == null) author.setCluster("0");
            addAuthor(author);

            for(Author coAuthor: author.coAuthors){
                addAuthor(coAuthor);
                addRelation(author, coAuthor);
            }
        }
    }

    private void addAuthor(Author author) {
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction())
            {
                if (author.getCluster() != null)
                    tx.run("MERGE (a:Author {fullname: {x}, cluster: {z}})", parameters("x", author.toString(), "z", author.getCluster()));
                else
                    tx.run("MERGE (a:Author {fullname: {x}})", parameters("x", author.toString()));
                tx.success();
            }
        }
    }
    private void addRelation(Author author1, Author author2){
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction())
            {
                tx.run("MATCH (u:Author {fullname: {x}, cluster: {z}}), (r:Author {fullname: {x1}, cluster: {z1}})\n" +
                        "CREATE (u)-[:coauthor]->(r)\n", parameters("x", author1.toString(), "x1", author2.toString(), "z", author1.getCluster(),"z1", author2.getCluster()));
                tx.success();
            }
        }
    }
    public void cleanDB (){
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction())
            {
                tx.run("MATCH (n)\n" +
                        "OPTIONAL MATCH (n)-[r]-()\n" +
                        "DELETE n,r");
                tx.success();
            }
        }
    }
}
