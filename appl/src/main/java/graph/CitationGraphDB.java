package graph;

import datamapper.ResearchStarters.Author;
import org.neo4j.driver.v1.*;
import storage.AuthorsDB;

import static org.neo4j.driver.v1.Values.parameters;

public class CitationGraphDB {
    private Driver driver;
    public CitationGraphDB(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public void storeParserResults(){
        for(Author author: AuthorsDB.getAuthorsStorage()){
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
                if (author.getCluster() != null && author.linkToUser!=null)
                    tx.run("MERGE (a:Author" + author.getCluster()+ " {fullname: {x}, link: {y}})", parameters("x", author.toString(), "y", author.linkToUser));
                else if(author.linkToUser!=null)
                    tx.run("MERGE (a:Author {fullname: {x}, link: {y}})", parameters("x", author.toString(), "y", author.linkToUser));
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
                tx.run("MATCH (u:Author {fullname: {x}}), (r:Author {fullname: {x1}})\n" +
                        "CREATE (u)-[:coauthor]->(r)\n", parameters("x", author1.toString(), "x1", author2.toString()));
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
