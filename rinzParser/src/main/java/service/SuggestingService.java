package service;

import database.model.Publication;

import java.util.List;

public interface SuggestingService {
    List<Publication> suggestPublicationsByKeyword(String requestKeyword);

    List<String> executeSuggestionQuery(String keyword);
}
