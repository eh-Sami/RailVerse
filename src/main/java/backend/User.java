package backend;
public abstract class User {
    protected final int id;
    protected String name;
    protected final String nid; // changed
    protected String email; // changed
    protected String address;
    protected String password;

    public User(int id, String name, String nid, String email, String address, String password) {
        this.id = id;
        this.name = name;
        this.nid = nid;
        this.email = email;
        this.address = address;
        this.password = password;
    }

    public static boolean emailValidityCheck(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public static boolean nidValidityCheck(String nid) {
        return nid != null && nid.length() == 10 && nid.matches("\\d+");
    }

    public static boolean passwordValidityCheck(String password) {
        return password != null && password.length() >= 6;
    }

    public abstract String getRole();
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getNid() {
        return nid;
    }
    public String getEmail() {
        return email;
    }
    public String getAddress() {
        return address;
    }
    public String getPassword() {
        return password;
    }
//    public void setName(String name) {
//        this.name = name;
//    }
//    public void setAddress(String address) {
//        this.address = address;
//    }
//    public void setPassword(String password) {
//        this.password = password;
//    }
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}

