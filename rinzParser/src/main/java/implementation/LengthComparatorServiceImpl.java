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
        if (firstAuthor.getN()!=null && !firstAuthor.getN().equals(" ")&& secondAuthor.getN()!= null && !secondAuthor.getN().equals(" ")) {
            if (firstAuthor.getP() != null && !firstAuthor.getP().equals(" ") && secondAuthor.getP() != null && !secondAuthor.getP().equals(" ")) {
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
    public int getLength(String x, String y) {
        int[][]dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j]= j;
                }
                else if (j == 0) {
                    dp[i][j]= i;
                }
                else {
                    dp[i][j]= min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j]+ 1,
                            dp[i][j - 1]+ 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }

    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    public static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
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
}
