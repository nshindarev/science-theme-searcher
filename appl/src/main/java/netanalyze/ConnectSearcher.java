package netanalyze;


import datamapper.ResearchStarters.Theme;
import main.RinzParser;
import util.SearchType;

//TODO: добавить реализацию для поиска по авторам

public class ConnectSearcher {

    public ConnectSearcher(String query, SearchType type){
        switch (type){
            case THEME: searchByTheme(query);
            case AUTHOR: searchByAuthor(query);
            case PUBLICATION: searchByPublication(query);
        }

    }

    private void searchByTheme(String query){
        Theme theme = new Theme(query);
        RinzParser parser = new RinzParser(theme);
    }
    private void searchByAuthor(String query){

    }
    private void searchByPublication(String query){

    }

}
