
public abstract class Employee {
    String name,surname, username,password;
    public Employee(String name, String surname, String username, String password){
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
    }

    public abstract String toString();

}
