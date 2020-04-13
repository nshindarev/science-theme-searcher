package graph.gephi;

import database.model.AuthorToAuthor;
import database.operations.StorageHandler;
import org.apache.commons.lang3.ObjectUtils;
import org.gephi.appearance.api.*;
import org.gephi.appearance.plugin.PartitionElementColorTransformer;
import org.gephi.appearance.plugin.palette.Palette;
import org.gephi.appearance.plugin.palette.PaletteManager;
import org.gephi.graph.api.*;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.ImportController;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.util.locale.provider.LocaleServiceProviderPool;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GephiClusterer {

    private final Logger logger = LoggerFactory.getLogger(GephiClusterer.class);

    private List<AuthorToAuthor> a2a;
    private UndirectedGraph authorsSocialNetwork;
    private GraphModel graphModel;


    public  GephiClusterer (){
        a2a = StorageHandler.getA2A();
        authorsSocialNetwork = getGraph();

        executeClustering();
    }

    private UndirectedGraph getGraph(){
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get a graph model - it exists because we have a workspace
        this.graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
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

    private void executeClustering(){

        //Get controllers and models
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        AppearanceController appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
        AppearanceModel appearanceModel = appearanceController.getModel();

        //Run modularity algorithm - community detection
        Modularity modularity = new Modularity();
        modularity.execute(authorsSocialNetwork);

        logger.info("MODULARITY SCORE");
        logger.info(String.valueOf(modularity.getModularity()));

        //Partition with 'modularity_class', just created by Modularity algorithm
        Column modColumn = graphModel.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);
        Function func2 = appearanceModel.getNodeFunction(authorsSocialNetwork, modColumn, PartitionElementColorTransformer.class);
        Partition partition2 = ((PartitionFunction) func2).getPartition();
        System.out.println(partition2.size() + " partitions found");
        Palette palette2 = PaletteManager.getInstance().randomPalette(partition2.size());
        partition2.setColors(palette2.getColors());
        appearanceController.transform(func2);

        //Export
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File("partition2.pdf"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
    }
}
