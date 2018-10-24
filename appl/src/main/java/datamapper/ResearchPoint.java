package datamapper;

import datamapper.ResearchStarters.Author;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public abstract class ResearchPoint implements Serializable {
    public Set<Publication> publications = new HashSet<>();
    public Set<Author> coAuthors = new HashSet<>();
}
