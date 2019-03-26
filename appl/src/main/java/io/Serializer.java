package io;

import datamapper.ResearchStarters.Author;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.*;
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
    public static Set<Author> deserializeData(){

        FileInputStream fis;
        try{
            fis = new FileInputStream("src/main/resources/serialized/authDB.out");

            ObjectInputStream oin = new ObjectInputStream(fis);
            return (Set<Author>) oin.readObject();
        }
        catch (IOException ex){
            try{
                fis = new FileInputStream("appl/src/main/resources/serialized/authDB.out");

                ObjectInputStream oin = new ObjectInputStream(fis);
                return (Set<Author>) oin.readObject();
            }
            catch (ClassNotFoundException ex1){
                logger.error(ex1.getMessage());

            }
            catch (IOException ex1){
                logger.error(ex1.getMessage());

            }
        }
        catch (ClassNotFoundException ex){
            logger.error(ex.getMessage());
        }
        return null;
    }

    public static void serializeData(DefaultDirectedGraph graph){
        try{
            FileOutputStream fos = new FileOutputStream("appl/src/main/resources/serialized/jgraph_obj.out");

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(graph);
            oos.flush();
            oos.close();
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
        }


    }
    public static DefaultDirectedGraph<Author, DefaultEdge> deserializeGraph(){
        try{
            FileInputStream fis = new FileInputStream("src/main/resources/serialized/jgraph_obj.out");

            ObjectInputStream oin = new ObjectInputStream(fis);
            return (DefaultDirectedGraph<Author, DefaultEdge>) oin.readObject();
        }
        catch (IOException ex){
            logger.error(ex.getMessage());
        }
        catch (ClassNotFoundException ex){
            logger.error(ex.getMessage());
        }
        return null;
    }

    public static DefaultDirectedGraph convertDbToGraph(){

        // выгружает граф из памяти
        AuthorsDB.initAuthorsStorage();
        AuthorsDB.initPublicationsStorage();
        AuthorsDB.addToAuthorsStorage(Serializer.deserializeData());

        DefaultDirectedGraph<Author, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (Author auth: AuthorsDB.getAuthorsStorage()){
            graph.addVertex(auth);
            for(Author auth1: auth.coAuthors){
                graph.addVertex(auth1);
                graph.addEdge(auth, auth1);
                graph.addEdge(auth1, auth);

            }
        }

        Serializer.serializeData(graph);
        Serializer.exportGraphToCsv(graph);
        return graph;
    }

    public static void exportGraphToCsv (AbstractGraph g){

        ComponentNameProvider<Author> vertexIdProvider = new ComponentNameProvider<Author>() {
            public String getName(Author author)
            {
//                return author.getSurname();
                return author.toString().replaceAll(" ", "");
                }
        };

        try{
            CSVExporter<Author, DefaultEdge> csv =
                new CSVExporter<>(vertexIdProvider, null, ' ');

            csv.setFormat(CSVFormat.EDGE_LIST);
            csv.exportGraph(g, new File("src/main/resources/authorsGraphCsv"));
        }
        catch (Exception ex){
            logger.error(ex.getMessage());
        }



//        GraphExporter<URI, DefaultEdge> exporter =
//                new DOTExporter<>(vertexIdProvider, vertexLabelProvider, null);
//        Writer writer = new StringWriter();
//        exporter.exportGraph(hrefGraph, writer);
//        System.out.println(writer.toString());
    }


}
