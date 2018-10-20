package datamapper.ResearchStarters;

import datamapper.ResearchPoint;

import java.util.HashSet;

public class Theme extends ResearchPoint {

    private String name;


    public Theme (String name){
        this.name = name;
        super.coAuthors = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString(){
        if(this.name != null && !this.name.isEmpty()){
            return this.name;
        }
        else return new Integer(hashCode()).toString();
    }
}
