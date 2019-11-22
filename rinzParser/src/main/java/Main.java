import auth.ElibAuthorize;
import io.Parameters;

import org.slf4j.LoggerFactory;
import org.apache.commons.cli.*;
import org.slf4j.Logger;

import java.util.logging.Level;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String theme =          "theme";
    private static final String searchLimit =    "search_limit";

    public static void main (String[] args) {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

        parseParameters(args);
        new ElibAuthorize();

    }
    public static Parameters parseParameters( String... args){

        Options options = new Options();

        options.addOption(theme,      true,  "point name for theme search");
        options.addOption(searchLimit,true,  "limits number of connections to other users during research");


        Parameters parameters = new Parameters();
        boolean params = true;
        CommandLine cl;

        try {
            cl = new DefaultParser().parse(options, args);
            if (!cl.hasOption(theme)){
                logger.error("Start Point wasn't inserted");
                params = false;
            }
            if (cl.hasOption(searchLimit)){
                Parameters.searchLimit = Integer.parseInt(cl.getOptionValue(searchLimit));
            }
            if (cl == null) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.setWidth(132);
                helpFormatter.printHelp("arguments", options);
            }
        }
        catch (ParseException ex){
            logger.error("Couldn't parse line: {}", ex.getLocalizedMessage());
            params = false;
        }

        if(params) return parameters;
        else return null;
    }
}
