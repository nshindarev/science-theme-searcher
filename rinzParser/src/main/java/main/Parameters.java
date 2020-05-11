package main;


import database.model.Keyword;
import elibrary.parser.Navigator;


public class Parameters {
    public static String postgresLogin = "postgres";
    public static String postgresPassword = "postgres";

    /**
     * default configuration
     */
    public String keyword = "";
    public int searchLimit = Integer.MAX_VALUE;
    public int searchLevel = Integer.MAX_VALUE;
    public int minClusterSize = 0;
    public int topVerticesCardinality = 3;
    public boolean parser;
    public boolean authorsSynonymy;
    public boolean affiliationsSynonymy;
    public boolean clustererNew;
    public boolean clustererOld;
    public String resultType;


    public Parameters (){
    }

    public void setKeyword(String keyword){
        this.keyword = keyword;
        Navigator.keyword = new Keyword(keyword);
    }
    public void setSearchLimit(int searchLimit){
        this.searchLimit = searchLimit;
        Navigator.searchLimit = searchLimit;
    }
    public void setSearchLevel(int searchLevel){
        this.searchLevel = searchLevel;
        Navigator.searchLevel = searchLevel;
    }
    public void setResultType(String resultType){
        this.resultType = resultType;
    }
}

