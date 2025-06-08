package rental;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String nama;
    private String alamat;
    private String noTelp;

    // Constructor untuk login result
    public User(int id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    // Constructor untuk register
    public User(String nama, String username, String password, String alamat, String noTelp) {
        this.nama = nama;
        this.username = username;
        this.password = password;
        this.alamat = alamat;
        this.noTelp = noTelp;
        this.role = "user"; // default role
    }

    // Getter
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getNama() { return nama; }
    public String getAlamat() { return alamat; }
    public String getNoTelp() { return noTelp; }
}
