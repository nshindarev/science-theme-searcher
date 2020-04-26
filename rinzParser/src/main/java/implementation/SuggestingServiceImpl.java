package implementation;

import database.model.Keyword;
import database.model.Publication;
import database.service.KeywordService;
import database.service.PublicationService;
import service.SuggestingService;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
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
    public List<String> executeSuggestionQuery(String keyword) {
        String url = "jdbc:postgresql://localhost:5432/postgres_sts";
        String user = "postgres";
        String password = "postgres";
        List<String> result = new LinkedList<>();
        try {
            Connection con = DriverManager.getConnection(url, user, password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select name \n" +
                    "from (select distinct sel.name, sel.metric\n" +
                    "from (select p.name, p.metric, c.id, rank() over (partition by c.id order by p.metric desc)\n" +
                    "from science_theme_searcher.publication p,\n" +
                    "\tscience_theme_searcher.author a,\n" +
                    "\tscience_theme_searcher.cluster c,\n" +
                    "\tscience_theme_searcher.keyword k,\n" +
                    "\tscience_theme_searcher.keywordtopublication kp,\n" +
                    "\tscience_theme_searcher.authortopublication ap,\n" +
                    "\tscience_theme_searcher.clustertoauthor ca\n" +
                    "where k.keyword = 'социоинженерные атаки'\n" +
                    "and k.id = kp.id_keyword\n" +
                    "and kp.id_publication = p.id\n" +
                    "and p.id = ap.id_publication\n" +
                    "and ap.id_author = a.id\n" +
                    "and a.id = ca.id_author\n" +
                    "and ca.id_cluster = c.id\n" +
                    "group by c.id, p.id) sel\n" +
                    "where RANK <=3\n" +
                    "order by metric desc) res");

            while (rs.next()) {
                result.add(rs.getString(1));
            }
            return result;

        } catch (Exception ex) {
            System.out.println(ex.getStackTrace());
        }
        return null;
    }
}
