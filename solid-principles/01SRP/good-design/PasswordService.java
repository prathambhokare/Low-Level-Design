import java.util.*;


public interface PasswordService {

    void hashPassword(String hashType,String password);
    void decryptPassword(String hashPassword);
}