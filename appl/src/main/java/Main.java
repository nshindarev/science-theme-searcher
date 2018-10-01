import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main (String[] args) throws IOException {
        logger.info("Started project");
        
        RinzParser parser = new RinzParser();
    }

}
