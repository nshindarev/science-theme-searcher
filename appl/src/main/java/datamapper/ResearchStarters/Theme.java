package datamapper.ResearchStarters;

import datamapper.ResearchStarter;

import java.util.HashSet;
import java.util.Set;

public class Theme extends ResearchStarter {

    private String name;


    public Theme (String name){
        this.name = name;
        super.coAuthors = new HashSet<>();
    }

    public String getName() {
        return name;
    }
}
