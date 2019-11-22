package io;

import datamapper.ResearchPoint;
import datamapper.ResearchStarters.Author;
import datamapper.ResearchStarters.Theme;
import util.Navigator;
import util.SearchType;

import java.util.Set;

public class Parameters {
    public int searchLimit;
    public ResearchPoint startPoint;

    // if true => all data stored in appl/src/main/resources/serialized/authDB.out
    public boolean deserializeMode;

    public Parameters (){
    }

    public Parameters (String publName){
        this.startPoint = new Theme(publName);
    }
    public Parameters (String surname, String name){
        this.startPoint = new Author(surname, name);
    }
    public Parameters (String surname, String name, String patronymic){
        this.startPoint = new Author(surname, name, patronymic);
    }
    public Parameters (String surname, String name, int searchLimit){
        this.startPoint = new Author(surname, name);
        this.searchLimit = searchLimit;
    }
    public Parameters (String surname, String name, String patronymic, int searchLimit){
        this.startPoint = new Author(surname, name, patronymic);
        this.searchLimit = searchLimit;
    }

    public void setSearchLimit(int searchLimit){
        this.searchLimit = searchLimit;
        Navigator.searchLimit = searchLimit;
    }
}
