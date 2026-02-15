import java.util.*;


public interface EmailService {

    void sentEmail(String sender,String receiver,String subject,String body);
}