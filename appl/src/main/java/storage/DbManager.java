package storage;

import service.AuthorService;
import service.ClusterService;
import service.KeywordService;

public class DbManager {

    private static AuthorService authorService;
    private static KeywordService keywordService;
    private static ClusterService clusterService;

    public static void initStorage(){
        authorService = new AuthorService();
        authorService.openConnection();

        keywordService = new KeywordService();
        keywordService.openConnection();

        clusterService = new ClusterService();
        clusterService.openConnection();
    }

}
