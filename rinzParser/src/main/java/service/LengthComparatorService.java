package service;

import database.model.Author;

public interface LengthComparatorService {
    boolean samePatronymics (Author firstAuthor, Author secondAuthor);

    int getLength(String surname1, String surname2);

    long checkConnections(Author firstAuthor, Author secondAuthor);
}
