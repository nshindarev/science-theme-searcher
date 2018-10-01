package datamapper;

public class User {

    private String name;
    private String surname;

    public User (String name, String surname){
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }
    public String getSurname(){
        return surname;
    }

    public String getFullName(){
        if (!name.isEmpty() && !surname.isEmpty()) return surname + " " + name;
        else if (name.isEmpty() || name.equals(null)) return surname;
        else if (surname.isEmpty() || surname.equals(null)) return name;
        else return "";
    }
}
