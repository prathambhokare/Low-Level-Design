import java.util.*;


public interface UserRepository {

    void saveUser(User user);
    User getUserById(long userId);
    List<User> getAllUsers();
    void updateUser(long userId,User user);
    void deleteUserById(long userId);
}