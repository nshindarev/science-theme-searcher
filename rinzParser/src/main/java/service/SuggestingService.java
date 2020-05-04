package service;

import database.model.Cluster;
import database.model.Publication;

import java.util.List;

public interface SuggestingService {
    List<Publication> suggestPublicationsByKeyword(String requestKeyword);

    List<String> executeSuggestionQueryByRating(String keyword, Cluster cluster);

    List<String> executeSuggestionQueryByYear(String keyword, Cluster cluster);

    String findClustersAffiliation(Cluster cluster);
}
