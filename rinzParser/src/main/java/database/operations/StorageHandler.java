package database.operations;

import com.apporiented.algorithm.clustering.visualization.ClusterComponent;
import database.model.*;
import database.service.*;
import elibrary.parser.Navigator;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceException;
import java.util.*;
import java.util.stream.Collectors;

public class StorageHandler  {
    private static final Logger logger = LoggerFactory.getLogger(StorageHandler.class);


    // ===================== UPDATE ===========================
    public static void saveAuthors(Collection<Author> authors){
        AuthorService authorService = new AuthorService();

        // to check if DB already contains author
//        authorService.openConnection();
//        Set<Author> dbAuthorsSnapshot = new HashSet<>(authorService.findAllAuthors());
//        authorService.closeConnection();

        //need this to update authors
//        ArrayList<Author> authorList = new ArrayList(dbAuthorsSnapshot);

        for (Author auth: authors){
                try{
                    authorService.openConnection();
                    Author foundAuth = authorService.findAuthor(auth.getId());
                    if (foundAuth == null){
                        authorService.saveAuthor(auth);
                    }
                    else {
//                        auth.join(foundAuth);
                        foundAuth.join(auth);
                        authorService.updateAuthor(foundAuth);
                    }
                }
                catch (ConstraintViolationException ex){
                    logger.error(ex.getMessage());
                    ex.printStackTrace();
                }
                catch (DataException ex){
                    logger.error(ex.getMessage());
                    ex.printStackTrace();
                }
                catch (Exception ex){
                    logger.error(ex.getMessage());
                }
                finally {
//                    dbAuthorsSnapshot = new HashSet<>(authorService.findAllAuthors());
                    authorService.closeConnection();
                }
        }
    }
    public static void saveCoAuthors(){

        /**
         *  Publications connection
         */
        // publications db
        PublicationService pbService = new PublicationService();
        pbService.openConnection();

        // fill in list with repeated a2a
        List<AuthorToAuthor> a2aList = new LinkedList<>();
        Collection<Publication> allPublications = pbService.findAllPublications();

        allPublications.forEach(publication -> {
            publication.getAuthors().forEach(a1 ->
                    publication.getAuthors().stream().filter(a2 -> !a2.equals(a1)).forEach(a2 ->
                            a2aList.add(new AuthorToAuthor(a1,a2))
                    ));
        });

        // count a2a
        HashMap<AuthorToAuthor, Integer> weightCounter = new HashMap<>();
        for (AuthorToAuthor edge: a2aList){
            if (weightCounter.containsKey(edge)) weightCounter.put(edge, weightCounter.get(edge)+1);
            else weightCounter.put(edge, 1);
        }

        pbService.closeConnection();

        /**
         *  A2A db connection
         */
        AuthorToAuthorService a2aServ = new AuthorToAuthorService();
        a2aServ.openConnection();

        weightCounter.forEach((k,v) ->{

            k.setWeight(v/2);
            try{
                a2aServ.saveAuthorToAuthor(k);
            }
            catch (PersistenceException ex){
                logger.warn(ex.getMessage());
            }
        });

        a2aServ.closeConnection();
    }
    public static void saveCoAuthors(Set<Author> authorsConnected){
        AuthorToAuthorService a2aServ = new AuthorToAuthorService();

        a2aServ.openConnection();
        List<AuthorToAuthor> dbSnapshot = a2aServ.findAllAuthorToAuthor();
        a2aServ.closeConnection();

        authorsConnected.forEach(a1 ->
                authorsConnected.stream().filter(a2 -> !a2.equals(a1)).forEach(a2 ->{
                    try{
                        AuthorToAuthor a2a = new AuthorToAuthor(a1,a2);
                        a2aServ.openConnection();
                        if (dbSnapshot.contains(a2a)){
                            AuthorToAuthor a2aDB = dbSnapshot.get(dbSnapshot.indexOf(a2a));
                            a2aDB.incrementWeight();

                            a2aServ.updateAuthorToAuthor(a2aDB);
                        }
                        else a2aServ.saveAuthorToAuthor(a2a);
                        a2aServ.closeConnection();
                    }
                    catch (NonUniqueObjectException ex){
                        logger.error(ex.getMessage());
                    }
                    catch (HibernateException ex){
                        logger.error(ex.getMessage());
                    }
                }
                ));

        a2aServ.closeConnection();
    }
    /**
     * in current realisation only 1 cluster per author possible
     * @param dataGraph
     */
    public static void _saveClusters(Map<Cluster, Set<String>> dataGraph){

        ClusterService clusterService = new ClusterService();
        clusterService.clearClusters();

        dataGraph.forEach((cluster,listAuthorIds) -> {
                    listAuthorIds.forEach(id -> {
                                getAuthors(listAuthorIds).forEach(cluster::addAuthor);
                            });
            clusterService.openConnection();
            clusterService.saveCluster(cluster);
            clusterService.closeConnection();
        });
    }
    public static void saveClusters(Map<Cluster, Set<String>> dataGraph){

        ClusterService clusterService = new ClusterService();
        clusterService.clearClusters();

        AuthorService as = new AuthorService();
        as.openConnection();
        List<Author> dbSnapshot = as.findAllAuthors();
        as.closeConnection();

        Map<Integer, Author> mapIds = new HashMap<>();
        dbSnapshot.forEach(authDB -> mapIds.put(authDB.getId(), authDB));

        dataGraph.forEach((cluster,listAuthorIds) -> {
            listAuthorIds.forEach(id -> {
                cluster.addAuthor(mapIds.get(Integer.parseInt(id)));
            });
            clusterService.openConnection();
            clusterService.saveCluster(cluster);
            clusterService.closeConnection();
        });
    }
    // ===================== UPDATE ===========================
    public static void updateKeyword(Set<Publication> publications){
        PublicationService ps = new PublicationService();
        ps.openConnection();

        List<Publication> publicationsSnapshot = ps.findAllPublications();

        Map<Integer, Publication> idsPublMap = new HashMap<>();
        publicationsSnapshot.forEach(it -> idsPublMap.put(it.hashCode(), it));

        publications.forEach(publ -> {
            try{
                if (idsPublMap.containsKey(publ.hashCode())){
                    Publication toUpdatePubl = idsPublMap.get(publ.hashCode());
                    toUpdatePubl.addKeyword(Navigator.keyword);

                    ps.updatePublication(toUpdatePubl);
                }

                logger.info("PUBLICATION = "+ publ.getName());
            }
            catch (NullPointerException ex){
                logger.warn(ex.getMessage());
            }
//                if (publicationsSnapshot.contains(publ)){
//                    publicationsSnapshot.get(publicationsSnapshot.indexOf(publ)).addKeyword(Navigator.keyword);
//                }
        });

        ps.closeConnection();
    }

    public static void updateRevision (Author authors){
        AuthorService authorService = new AuthorService();
        authorService.openConnection();
        authorService.updateAuthor(authors);
        authorService.closeConnection();
    }
    public static void updateRevision (Collection<Author> authors){
        AuthorService authorService = new AuthorService();
        authorService.openConnection();

        authors.forEach(authorService::updateAuthor);

        authorService.closeConnection();
    }
    //   ==================== READ =======================
    public static Collection<Author> getAuthorsWithoutRevision(){
        AuthorService authorService = new AuthorService();
        authorService.openConnection();

        Set<Author> res = authorService.findAllAuthors()
                .stream()
                .filter(it -> it.getRevision()==0)
                .collect(Collectors.toSet());

        authorService.closeConnection();
        return res;
    }
    public static List<AuthorToAuthor> getA2A(){
        AuthorToAuthorService a2aServ = new AuthorToAuthorService();
        a2aServ.openConnection();
        List<AuthorToAuthor> a2a = a2aServ.findAllAuthorToAuthor();
        a2aServ.closeConnection();

        return a2a;
    }
    public static DefaultDirectedGraph getAuthorsGraph (){
        DefaultDirectedGraph<Author, DefaultEdge> graph =
                new DefaultDirectedGraph<>(DefaultEdge.class);
        AuthorService authorService = new AuthorService();
        authorService.openConnection();

       authorService.findAllAuthors().forEach(auth -> {
           graph.addVertex(auth);
           auth.getPublications().forEach(publication -> {
              publication.getAuthors().forEach(coAuthor ->{
                  graph.addVertex(coAuthor);
                  if(!auth.equals(coAuthor)){
                      graph.addEdge(auth, coAuthor);
                      graph.addEdge(coAuthor, auth);
                  }
              });
           });
       });

       authorService.closeConnection();
       return graph;
    }
    public static Set<Author> getAuthors(Collection<String> ids){
        AuthorService as = new AuthorService();
        as.openConnection();
        Set<Author> res = new HashSet<>();

        ids.forEach(id ->{
            res.add(as.findAuthor(Integer.parseInt(id)));
        });

        as.closeConnection();
        return res;
    }

    public static Map<Cluster, List<Author>> getTopAuthors (Map<Cluster, List<String>> clusters, int minClusterSize, int topVerticesSize){

        Map<Cluster, List<Author>> res = clusters.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() >= minClusterSize)
                .collect(Collectors.toMap(x -> (Cluster)x.getKey(), x -> (List<Author>)getAuthorsById(x.getValue()).subList(0,topVerticesSize)));

        return res;
    }

    public static List<Author> getAuthorsById(List<String> ids){
        AuthorService as = new AuthorService();
        as.openConnection();

        List<Author> authors = new LinkedList<>();
        ids.forEach(id -> {
            authors.add(as.findAuthor(Integer.parseInt(id)));
        });

        as.closeConnection();
        return authors;
    }
    public static boolean alreadyParsed (String key){
        KeywordService ks = new KeywordService();
        ks.openConnection();
        List<Keyword> keys = ks.findAllKeywords();
        ks.closeConnection();

        for (Keyword dbKey: keys){
            if (dbKey.getKeyword().equals(key)) return true;
        }

        return false;
    }
}
