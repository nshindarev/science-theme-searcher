package implementation;

import database.model.Author;
import database.model.AuthorToAuthor;
import service.LengthComparatorService;
import service.TranslatorService;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class LengthComparatorServiceImpl implements LengthComparatorService {
    TranslatorService translatorService = new TranslatorServiceImpl();

    @Override
    public boolean samePatronymics (Author firstAuthor, Author secondAuthor) {
        if (firstAuthor.getN()!=null && secondAuthor.getN()!= null) {
            if (firstAuthor.getP() != null && secondAuthor.getP() != null) {
               return translatorService.translateToLatinString(firstAuthor.getN())
                       .equals(translatorService.translateToLatinString(secondAuthor.getN()))
                       && translatorService.translateToLatinString(firstAuthor.getP())
                       .equals(translatorService.translateToLatinString(secondAuthor.getP()));
            } else {
                return  translatorService.translateToLatinString(firstAuthor.getN())
                        .equals(translatorService.translateToLatinString(secondAuthor.getN()));
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
        long counter = 0;
        for (Author author1: firstConnections) {
            for (Author author2: secondConnections) {
                if (samePatronymics(author1, author2)) {
                    if (getLength(translatorService.translateToLatinString(author1.getSurname())
                            , translatorService.translateToLatinString(author2.getSurname())) < 3) {
                        counter++;
                    }
                }
            }
        }
        return counter;
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }
}
