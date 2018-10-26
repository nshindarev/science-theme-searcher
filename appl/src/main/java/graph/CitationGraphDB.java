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
        // Sessions are lightweight and disposable connection wrappers.
        try (Session session = driver.session())
        {
            // Wrapping Cypher in an explicit transaction provides atomicity
            // and makes handling errors much easier.
//            try (Transaction tx = session.beginTransaction())
//            {
//                tx.run("MERGE (a:Author {surname: {x}, name: {y}})", parameters("x", author.getSurname(), "y", author.getName()));
//                tx.success();  // Mark this write as successful.
//            }
            try (Transaction tx = session.beginTransaction())
            {
                tx.run("MERGE (a:Author {surname: {x}})", parameters("x", author.getSurname()));
                tx.success();  // Mark this write as successful.
            }
        }
    }
    private void addRelation(Author author1, Author author2){
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction())
            {
                tx.run("MATCH (u:Author {name: {x}, surname: {y}}), (r:Author {name: {x1}, surname: {y1}})\n" +
                        "CREATE (u)-[:CO_AUTHORS]->(r)\n", parameters("x", author1.getName(), "y", author1.getSurname(),
                                                                            "x1", author2.getName(), "y1", author2.getSurname()));
                tx.success();
            }
        }
    }
    private void cleanDB (){
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

    public static void main(String... args) {
        CitationGraphDB example = new CitationGraphDB("bolt://localhost:7687", "neo4j", "v3r0n1k4");
        Author a1 = new Author("Ловягин", "Никита", "Юрьевич");
        Author a2 = new Author("Ловягин", "Юрий", "Никитич");

        example.addAuthor(a1);
        example.addAuthor(a2);
        example.addRelation(a1, a2);

//        example.cleanDB();
//        example.close();
    }
}
