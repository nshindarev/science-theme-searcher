package graph.gephi;

import database.model.AuthorToAuthor;
import database.operations.StorageHandler;
import org.apache.commons.lang3.ObjectUtils;
import org.gephi.graph.api.*;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GephiClusterer {

    private final Logger logger = LoggerFactory.getLogger(GephiClusterer.class);

    private List<AuthorToAuthor> a2a;
    private UndirectedGraph authorsSocialNetwork;


    public  GephiClusterer (){
        a2a = StorageHandler.getA2A();
        authorsSocialNetwork = getGraph();
    }

    private UndirectedGraph getGraph(){
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get a graph model - it exists because we have a workspace
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        UndirectedGraph authorsNetwork = graphModel.getUndirectedGraph();

        Set<Node> nodes = new HashSet<>();
        Set<Edge> edges = new HashSet<>();

        a2a.forEach(connection -> {

            Node a1 = graphModel.factory().newNode(connection.getAuthor_first().toString());
            Node a2 = graphModel.factory().newNode(connection.getAuthor_second().toString());

            /**
             * add a1
             */

            try{
                if (!authorsNetwork.contains(a1))
                    authorsNetwork.addNode(a1);
            }
            catch (IllegalArgumentException ex){
                logger.warn("node " + a1.getId() + " already exists");
                a1 = authorsNetwork.getNode(a1.getId());
            }

            /**
             *  add a2
             */
            try{
                if (!authorsNetwork.contains(a2))
                    authorsNetwork.addNode(a2);
            }
            catch (IllegalArgumentException ex){
               logger.warn("node " + a2.getId() + " already exists");
               a2 = authorsNetwork.getNode(a2.getId());
            }

            /**
             * add edge
             */
            Edge e = graphModel.factory().newEdge(a1, a2, connection.getWeight(), false);
            authorsNetwork.addEdge(e);
        });
        return authorsNetwork;
    }
}
