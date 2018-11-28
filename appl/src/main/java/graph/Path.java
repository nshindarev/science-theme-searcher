package graph;

import datamapper.ResearchStarters.Author;

public class Path {

    public Author auth1;
    public Author auth2;
    public int val = 0;

    public Path(Author auth1, Author auth2){
        this.auth1 = auth1;
        this.auth2 = auth2;
    }
}
