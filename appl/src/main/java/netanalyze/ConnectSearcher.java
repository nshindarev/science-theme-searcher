package netanalyze;

import util.SearchType;

public class ConnectSearcher {

    public ConnectSearcher(String query, SearchType type){
        switch (type){
            case THEME: searchByTheme(query);
            case AUTHOR: searchByAuthor(query);
            case PUBLICATION: searchByPublication(query);
        }

    }

    private void searchByTheme(String query){

    }
    private void searchByAuthor(String query){

    }
    private void searchByPublication(String query){

    }

}
