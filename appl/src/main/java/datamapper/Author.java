package datamapper;

public class Author {

    private String name;
    private String surname;
    private String patronymic;

    private String linkToUser;

    public Author(String name, String surname){
        this.name = name;
        this.surname = surname;
    }
    public Author(String surname, String name, String patronymic){
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
    }

    public String getName() {
        return name;
    }
    public String getSurname(){
        return surname;
    }
    public String getPatronymic() { return patronymic; }

    public String getFullName(){
        if (!name.isEmpty() && !surname.isEmpty()) return surname + " " + name;
        else if (name.isEmpty() || name.equals(null)) return surname;
        else if (surname.isEmpty() || surname.equals(null)) return name;
        else return "";
    }
}
