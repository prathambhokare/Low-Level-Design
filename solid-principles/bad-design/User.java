import java.util.*;


//This class does not follows SRP Becuase it has a lot of behavioiurs
//This leads to many disadvantages
// 1. Code become tightly coupled
// 2. Unnecessary to test all functionalites even there is small change
// 3. Merge conflict
// 4. Messy class

public class User {

    private String userName;
    private String emaiId;
    private String password;

    public void saveUserToDataBase() {

    }

    public void hashPassword() {

    }

    public void sentMail() {

    }

    public void log(String msg) {

    }
}