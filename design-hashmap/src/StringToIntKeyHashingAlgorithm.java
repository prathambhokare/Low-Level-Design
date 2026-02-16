import java.util.*;


public class StringToIntKeyHashingAlgorithm implements HashingAlgorithm<String,Integer> {

    @Override
    public Integer hashKey(String key) {
        int value=0;
        for (int i=0;i<key.length;i++) {
            value = value + (int) key.charAt(i);
        }
        return Integer.valueOf(value);
    }
}