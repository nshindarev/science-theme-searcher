package service;

import database.model.Author;

public interface LengthComparator {
    boolean checkPatronymic (Author firstAuthor, Author secondAuthor);

    int getLength(String surname1, String surname2);

    long checkConnections(Author firstAuthor, Author secondAuthor);
}
