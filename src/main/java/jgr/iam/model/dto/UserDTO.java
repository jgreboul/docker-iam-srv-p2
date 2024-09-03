package jgr.iam.model.dto;

// External Objects
import lombok.Data; // https://projectlombok.org/features/Data
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.Setter; // https://projectlombok.org/features/GetterSetter
import lombok.NoArgsConstructor; // https://projectlombok.org/features/constructor
import javax.persistence.Entity; // https://docs.oracle.com/javaee%2F7%2Fapi%2F%2F/javax/persistence/package-summary.html
import javax.persistence.GeneratedValue; // https://docs.oracle.com/javaee%2F7%2Fapi%2F%2F/javax/persistence/package-summary.html
import javax.persistence.GenerationType; // https://docs.oracle.com/javaee%2F7%2Fapi%2F%2F/javax/persistence/package-summary.html
import javax.persistence.Id; // https://docs.oracle.com/javaee%2F7%2Fapi%2F%2F/javax/persistence/package-summary.html

//CREATE TABLE IF NOT EXISTS User (
//    id INT AUTO_INCREMENT PRIMARY KEY,
//    username VARCHAR(50) NOT NULL UNIQUE,
//    email VARCHAR(255) NOT NULL UNIQUE,
//    passwordHash BINARY(64) NOT NULL,
//    passwordSalt BINARY(32) NOT NULL,
//    firstName VARCHAR(255) NOT NULL,
//    lastName VARCHAR(255) NOT NULL,
//    active BOOLEAN NOT NULL DEFAULT TRUE,
//    archived BOOLEAN NOT NULL DEFAULT FALSE );
@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String email;
    private byte[] passwordHash;
    private byte[] passwordSalt;
    private String firstName;
    private String lastName;
    private boolean active = true;
    private boolean archived = false;

    // Constructor with parameters
    public UserDTO(String username, String email, byte[] passwordHash, byte[] passwordSalt,
                   String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
