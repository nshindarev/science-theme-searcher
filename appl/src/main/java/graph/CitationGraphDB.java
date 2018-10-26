package graph;

import datamapper.ResearchStarters.Author;
import org.neo4j.driver.v1.*;

import static org.neo4j.driver.v1.Values.parameters;

public class CitationGraphDB {

    // Driver objects are thread-safe and are typically made available application-wide.
    Driver driver;

    public CitationGraphDB(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    private void cleanDB (){
        // Sessions are lightweight and disposable connection wrappers.
        try (Session session = driver.session())
        {
            // Wrapping Cypher in an explicit transaction provides atomicity
            // and makes handling errors much easier.
            try (Transaction tx = session.beginTransaction())
            {
                tx.run("MATCH (n)\n" +
                        "OPTIONAL MATCH (n)-[r]-()\n" +
                        "DELETE n,r");
                tx.success();  // Mark this write as successful.
            }
        }
    }
    private void addAuthor(Author author)
    {
        // Sessions are lightweight and disposable connection wrappers.
        try (Session session = driver.session())
        {
            // Wrapping Cypher in an explicit transaction provides atomicity
            // and makes handling errors much easier.
            try (Transaction tx = session.beginTransaction())
            {
                tx.run("MERGE (a:Author {name: {x}, surname: {y}})", parameters("x", author.getName(), "y", author.getSurname()));
                tx.success();  // Mark this write as successful.
            }
        }
    }

    public static void main(String... args)
    {
        CitationGraphDB example = new CitationGraphDB("bolt://localhost:7687", "neo4j", "v3r0n1k4");
        example.addAuthor(new Author("Ловягин", "Никита", "Юрьевич"));
//        example.cleanDB();
//        example.close();
    }
}
