package graph;


import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;

public class Neo4JStart{

    public enum NodeType implements Label {
        Person, Course;
    }
    public enum RelationType implements RelationshipType {
        Knows, BelongsTo;
    }

    public static void main( String... args ) throws Exception{
        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        GraphDatabaseService graphDb = dbFactory.newEmbeddedDatabase(new File("/Users/nshindarev/Documents/neo4j-community-3.4.9/data/graph.db"));

        try (Transaction tx = graphDb.beginTx()) {
            Node bobNode = graphDb.createNode(NodeType.Person);
            bobNode.setProperty("Name", "Bob");
            bobNode.setProperty("Age", 23);
            bobNode.setProperty("Pid", 5001);

            Node aliceNode = graphDb.createNode(NodeType.Person);
            aliceNode.setProperty("Name", "Alice");
            aliceNode.setProperty("Pid", 5002);

            Node eveNode = graphDb.createNode(NodeType.Person);
            eveNode.setProperty("Name", "Eve");

            Node itNode = graphDb.createNode(NodeType.Course);
            itNode.setProperty("Id", 1);
            itNode.setProperty("Name", "IT Beginners");
            itNode.setProperty("Location", "Room 153");


            Node electronicNode = graphDb.createNode(NodeType.Course);
            electronicNode.setProperty("Name", "Electronic advanced");

            bobNode.createRelationshipTo(aliceNode, RelationType.Knows);

            Relationship bobRelIt =  bobNode.createRelationshipTo(itNode, RelationType.BelongsTo);
            bobRelIt.setProperty("Function", "Student");

            Relationship bobRelElectronics =  bobNode.createRelationshipTo(electronicNode, RelationType.BelongsTo);
            bobRelElectronics.setProperty("Function", "Supply Teacher");

            Relationship aliceRelIt =  bobNode.createRelationshipTo(itNode, RelationType.BelongsTo);
            aliceRelIt.setProperty("Function", "Teacher");

            tx.success();
        }
        graphDb.shutdown();
    }

}
