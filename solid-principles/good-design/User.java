import java.util.*;


public class User {

    private String userName;
    private String password;
    private String emailId;

    public String gerUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEmailId() {
        return this.emailId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmailId(String emailId) {
        this.emailId=emailId;
    }
}