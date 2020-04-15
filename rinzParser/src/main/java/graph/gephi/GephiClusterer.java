package graph.gephi;

import com.itextpdf.text.PageSize;
import database.model.AuthorToAuthor;
import database.operations.StorageHandler;
import org.apache.commons.lang3.ObjectUtils;
import org.gephi.appearance.api.*;
import org.gephi.appearance.plugin.PartitionElementColorTransformer;
import org.gephi.appearance.plugin.RankingLabelSizeTransformer;
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
import org.gephi.appearance.plugin.palette.Palette;
import org.gephi.appearance.plugin.palette.PaletteManager;
import org.gephi.graph.api.*;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.PDFExporter;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.GraphDistance;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.util.locale.provider.LocaleServiceProviderPool;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GephiClusterer {

    private final Logger logger = LoggerFactory.getLogger(GephiClusterer.class);

    private List<AuthorToAuthor> a2a;
    private GraphModel graphModel;
    private UndirectedGraph authorsNetwork;
    private File clusteredGraphFile;

    public GephiClusterer() {
        a2a = StorageHandler.getA2A();
        executeClustering();
        visualizeGraph();
    }


    private void executeClustering() {
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get controllers and models
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        workspace.add(graphModel);

        AppearanceController appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
        AppearanceModel appearanceModel = appearanceController.getModel();

        authorsNetwork = graphModel.getUndirectedGraph();
        a2a.forEach(connection -> {

            Node a1 = graphModel.factory().newNode(connection.getAuthor_first().toString());
            a1.setLabel(connection.getAuthor_first().toString());
            Node a2 = graphModel.factory().newNode(connection.getAuthor_second().toString());
            a2.setLabel(connection.getAuthor_second().toString());
            /**
             * add a1
             */

            try {
                if (!authorsNetwork.contains(a1))
                    authorsNetwork.addNode(a1);
            } catch (IllegalArgumentException ex) {
                logger.warn("node " + a1.getId() + " already exists");
                a1 = authorsNetwork.getNode(a1.getId());
            }

            /**
             *  add a2
             */
            try {
                if (!authorsNetwork.contains(a2))
                    authorsNetwork.addNode(a2);
            } catch (IllegalArgumentException ex) {
                logger.warn("node " + a2.getId() + " already exists");
                a2 = authorsNetwork.getNode(a2.getId());
            }

            /**
             * add edge
             */
            Edge e = graphModel.factory().newEdge(a1, a2, connection.getWeight(), false);
            authorsNetwork.addEdge(e);
        });

        workspace.add(graphModel);

        //Export full graph
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            this.clusteredGraphFile = new File("src/main/resources/graph/clustered_graph.gexf");
            ec.exportFile(this.clusteredGraphFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
    }

    /**
     *  uploads gexf file from memory
     *  saves visualized partition in pdf file
     *  CANNOT BE USED WITHOUT executeCluste
     */
    public void visualizeGraph(){
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get controllers and models
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        AppearanceController appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
        AppearanceModel appearanceModel = appearanceController.getModel();

        //Import file
        Container container;
        try {
            File file = clusteredGraphFile;
            container = importController.importFile(file);
            container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);   //Force DIRECTED
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);

        //See if graph is well imported
        UndirectedGraph graph = graphModel.getUndirectedGraph();

        //Run modularity algorithm - community detection
        Modularity modularity = new Modularity();
        modularity.execute(graphModel);

        logger.info("MODULARITY SCORE");
        logger.info(String.valueOf(modularity.getModularity()));

        //Partition with 'modularity_class', just created by Modularity algorithm
        Column modColumn = graphModel.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);
        Function func2 = appearanceModel.getNodeFunction(graph, modColumn, PartitionElementColorTransformer.class);
        Partition partition2 = ((PartitionFunction) func2).getPartition();
        logger.info(partition2.size() + " partitions found");
        Palette palette2 = PaletteManager.getInstance().randomPalette(partition2.size());
        partition2.setColors(palette2.getColors());
        appearanceController.transform(func2);


        //Get Centrality
        GraphDistance distance = new GraphDistance();
        distance.setDirected(true);
        distance.execute(graphModel);

        //Rank size by centrality
        Column centralityColumn = graphModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
        Function centralityRanking = appearanceModel.getNodeFunction(graph, centralityColumn, RankingNodeSizeTransformer.class);
        RankingNodeSizeTransformer centralityTransformer = (RankingNodeSizeTransformer) centralityRanking.getTransformer();
        centralityTransformer.setMinSize(3);
        centralityTransformer.setMaxSize(8);
        appearanceController.transform(centralityRanking);

        //Rank label size - set a multiplier size
        Function centralityRanking2 = appearanceModel.getNodeFunction(graph, centralityColumn, RankingLabelSizeTransformer.class);
        RankingLabelSizeTransformer labelSizeTransformer = (RankingLabelSizeTransformer) centralityRanking2.getTransformer();
        labelSizeTransformer.setMinSize(1);
        labelSizeTransformer.setMaxSize(3);
        appearanceController.transform(centralityRanking2);

        YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
//        ForceAtlasLayout layout = new ForceAtlasLayout(null);
        layout.setGraphModel(graphModel);
        layout.initAlgo();
        layout.resetPropertiesValues();
        layout.setOptimalDistance(200f);

        for (int i = 0; i < 100 && layout.canAlgo(); i++) {
            layout.goAlgo();
        }
        layout.endAlgo();

        //Export
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File("src/main/resources/graph/clustered_graph.pdf"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
    }
}
