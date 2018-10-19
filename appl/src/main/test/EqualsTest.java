import datamapper.ResearchStarters.Author;
import org.junit.Test;

public class EqualsTest {
    @Test
    public void testAuthor(){
        Author a1 = new Author("Shindarev", 'N', 'A');
        Author a2 = new Author("Shindarev", 'A', 'D');
        Author a3 = new Author("Terekhov", 'A', 'N');

        assert !a1.equals(a2);
    }

    @Test
    public void testAuthor1(){
        Author a1 = new Author("Shindarev", 'N', 'A');
        Author a2 = new Author("Shindarev", 'N', 'A');

        assert a2.equals(a1);
    }

    @Test
    public void testAuthor2(){
        Author a1 = new Author("Shindarev", 'N', 'A');
        Author a3 = new Author("Terekhov", 'A', 'N');

        assert !a1.equals(a3);
    }

    @Test
    public void testAuthor3(){
        Author a1 = new Author("Shindarev", "Nikita", "Andreevich");
        Author a2 = new Author("Shindarev", "Nikita", "Dmitrievich");

        assert !a1.equals(a2);
    }

    @Test
    public void testAuthor4(){
        Author a1 = new Author("Терехов", 'А', ' ');
        Author a2 = new Author("Терехов", 'А', 'Н');

        assert a1.equals(a2);
    }
}
