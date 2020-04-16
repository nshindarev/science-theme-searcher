package implementation;

import database.model.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.LengthComparatorService;
import service.SynonymyService;

public class SynonymyServiceImpl implements SynonymyService {
    private static final Logger logger = LoggerFactory.getLogger(SynonymyServiceImpl.class);

    @Override
    public boolean checkAuthorsEquality(Author firstAuthor, Author secondAuthor) {
        LengthComparatorService lengthComparatorService = new LengthComparatorServiceImpl();
        if (lengthComparatorService.samePatronymics(firstAuthor, secondAuthor)) {
            if (lengthComparatorService.getLength(firstAuthor.getSurname(), secondAuthor.getSurname()) < 3) {
                if (lengthComparatorService.checkConnections(firstAuthor, secondAuthor) > 0) {
                    logger.debug("Authors probably are the same person");
                    return true;
                }
                else {
                    logger.debug("Authors don't have similar co-authors");
                    return false;
                }
            }
            else {
                logger.debug("Authors have different surnames");
                return false;
            }
        }
        else {
            logger.debug("Authors have different patronymics");
            return false;
        }
    }
}
