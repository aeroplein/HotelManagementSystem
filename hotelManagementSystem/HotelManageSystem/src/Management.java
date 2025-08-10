public class Management extends Employee {
    String role;
    double salary;

    public Management(String name, String surname, String username, String password,String role, double salary){
        super(name, surname, username, password);
        this.role = role;
        this.salary = salary;
    }

    public String toString(){
        return name+ "," + surname + "," + username + "," + password + "," + role + "," + salary;
    }

}
