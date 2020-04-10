package implementation;

import database.model.Author;
import database.model.AuthorToAuthor;
import service.LengthComparator;

import java.util.Arrays;
import java.util.stream.Stream;

public class LengthComparatorImpl implements LengthComparator {

    @Override
    public boolean checkPatronymic (Author firstAuthor, Author secondAuthor) {
        return firstAuthor.getN().equals(secondAuthor.getN())&&firstAuthor.getP().equals(secondAuthor.getP());
    }

    //Levenshtein distance algorithm
    @Override
    public int getLength(String surname1, String surname2) {
        if (surname1.isEmpty()) {
            return surname2.length();
        }

        if (surname2.isEmpty()) {
            return surname1.length();
        }

        int substitution = getLength(surname1.substring(1), surname2.substring(1))
                + costOfSubstitution(surname1.charAt(0), surname2.charAt(0));
        int insertion = getLength(surname1, surname2.substring(1)) + 1;
        int deletion = getLength(surname1.substring(1), surname2) + 1;

        return min(substitution, insertion, deletion);
    }

    @Override
    public long checkConnections(Author firstAuthor, Author secondAuthor) {
        Stream<Author> firstConnections = firstAuthor.getIncomingAuthorToAuthors().stream().map(AuthorToAuthor::getAuthor_first);
        Stream<Author> secondConnections = secondAuthor.getIncomingAuthorToAuthors().stream().map(AuthorToAuthor::getAuthor_first);
        return firstConnections.filter(author1 -> secondConnections.anyMatch(author2 -> author1.getId()==author2.getId())).count();
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }
}
