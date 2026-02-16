import java.util.*;


public class LockManager {

    private HashSet<Integer> runningProcess=new ConcurrentHashSet<>();

    public boolean tryLock(int id) {
        runningProcess.add(id);
    }

    public void releaseLock(int id) {
        runningProcess.remove(id);
    }
}