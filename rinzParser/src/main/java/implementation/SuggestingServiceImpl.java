package implementation;

import database.model.*;
import database.service.KeywordService;
import database.service.PublicationService;
import main.Parameters;
import service.SuggestingService;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SuggestingServiceImpl implements SuggestingService {

    @Override
    public List<Publication> suggestPublicationsByKeyword(String requestKeyword) {
        PublicationService publicationService = new PublicationService();
        KeywordService keywordService = new KeywordService();
        Keyword keyword = keywordService.findByKeyword(requestKeyword);
        List<Publication> resultList = publicationService.findAllPublications()
                .stream()
                .filter(publication -> publication.getKeywords().contains(keyword))
                .collect(Collectors.toList());

        return resultList;
    }

    @Override
    public List<String> executeSuggestionQueryByRating(String keyword, Cluster cluster) {
        String url = "jdbc:postgresql://localhost:5432/postgres_sts";
        String user = Parameters.postgresLogin;
        String password = Parameters.postgresPassword;
        List<String> result = new LinkedList<>();
        try {
            Connection con = DriverManager.getConnection(url, user, password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select ans.name from\n" +
                    "(select distinct sel.name from \n" +
                    "(select  *\n" +
                    "from science_theme_searcher.publication p,\n" +
                    "\t\t\tscience_theme_searcher.keyword k,\n" +
                    "\t\t\tscience_theme_searcher.keywordtopublication kp,\n" +
                    "\t\t\tscience_theme_searcher.authortopublication ap,\n" +
                    "\t\t\tscience_theme_searcher.clustertoauthor ca\n" +
                    "\t\t\twhere k.keyword = '"+keyword+"'\n" +
                    "\t\t\tand k.id = kp.id_keyword\n" +
                    "\t\t\tand kp.id_publication = p.id\n" +
                    "\t\t\tand p.id = ap.id_publication\n" +
                    "\t\t\tand ap.id_author = ca.id_author\n" +
                    "\t\t\tand ca.id_cluster = "+cluster.getId()+"\n" +
                    "order by metric desc) sel\n" +
                    " )ans\n" +
                    " limit 3");

            while (rs.next()) {
                result.add(rs.getString(1));
            }
            return result;

        } catch (Exception ex) {
            System.out.println(ex.getStackTrace());
        }
        return null;
    }


    @Override
    public List<String> executeSuggestionQueryByYear(String keyword, Cluster cluster) {
        String url = "jdbc:postgresql://localhost:5432/postgres_sts";
        String user = Parameters.postgresLogin;
        String password = Parameters.postgresPassword;
        List<String> result = new LinkedList<>();
        try {
            Connection con = DriverManager.getConnection(url, user, password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select ans.name from\n" +
                    "(select distinct sel.name from \n" +
                    "(select  *\n" +
                    "from science_theme_searcher.publication p,\n" +
                    "\t\t\tscience_theme_searcher.keyword k,\n" +
                    "\t\t\tscience_theme_searcher.keywordtopublication kp,\n" +
                    "\t\t\tscience_theme_searcher.authortopublication ap,\n" +
                    "\t\t\tscience_theme_searcher.clustertoauthor ca\n" +
                    "\t\t\twhere k.keyword = '"+keyword+"'\n" +
                    "\t\t\tand k.id = kp.id_keyword\n" +
                    "\t\t\tand kp.id_publication = p.id\n" +
                    "\t\t\tand p.id = ap.id_publication\n" +
                    "\t\t\tand ap.id_author = ca.id_author\n" +
                    "\t\t\tand ca.id_cluster = "+cluster.getId()+"\n" +
                    "order by year desc) sel\n" +
                    " )ans\n" +
                    " limit 3");

            while (rs.next()) {
                result.add(rs.getString(1));
            }
            return result;

        } catch (Exception ex) {
            System.out.println(ex.getStackTrace());
        }
        return null;
    }

    @Override
    public String findClustersAffiliation(Cluster cluster) {
        Set<Author> authorSet = cluster.getAuthors();
        HashMap<String, Integer> affiliationsMap = new HashMap<>();
        int max = 0;
        String resultAffiliation = "";
        for (Author author: authorSet) {
            for (Affiliation affiliation: author.getAffiliations()) {
                int nextCount = affiliationsMap.getOrDefault(affiliation.getName(), 0)+1;
                if (max < nextCount) {
                    max = nextCount;
                    resultAffiliation = affiliation.getName();
                }
                affiliationsMap.put(affiliation.getName(), nextCount);
            }
        }
        return resultAffiliation;
    }
}
