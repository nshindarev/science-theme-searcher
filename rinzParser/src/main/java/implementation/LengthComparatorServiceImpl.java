package implementation;

import database.model.Author;
import database.model.AuthorToAuthor;
import service.LengthComparatorService;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class LengthComparatorServiceImpl implements LengthComparatorService {

    @Override
    public boolean samePatronymics (Author firstAuthor, Author secondAuthor) {
        if (firstAuthor.getN()!=null && secondAuthor.getN()!= null) {
            if (firstAuthor.getP() != null && secondAuthor.getP() != null) {
               return firstAuthor.getN().equals(secondAuthor.getN())&&firstAuthor.getP().equals(secondAuthor.getP());
            } else {
                return  firstAuthor.getN().equals(secondAuthor.getN());
            }
        } else {
            return false;
        }
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
        Set<Author> firstConnections = firstAuthor.getIncomingAuthorToAuthors().stream().map(AuthorToAuthor::getAuthor_first).collect(Collectors.toSet());
        Set<Author>  secondConnections = secondAuthor.getIncomingAuthorToAuthors().stream().map(AuthorToAuthor::getAuthor_first).collect(Collectors.toSet());
        firstConnections.retainAll(secondConnections);
        return firstConnections.size();
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }
}
