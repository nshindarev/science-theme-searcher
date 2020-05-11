package service;

import database.model.Cluster;
import database.model.Publication;

import java.util.List;

public interface SuggestingService {
    List<Publication> suggestPublicationsByKeyword(String requestKeyword);

    List<String> executeSuggestionQueryByRating(String keyword, Cluster cluster, int limit);

    List<String> executeSuggestionQueryByYear(String keyword, Cluster cluster, int limit);

    String findClustersAffiliation(Cluster cluster);
}
