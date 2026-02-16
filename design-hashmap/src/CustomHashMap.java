import java.util.*;


public class CustomHashMap<K,V> {

    Node[] keyValuePair;
    HashingAlogrithm<K,Integer> hashingAlogrithm;
    ListOperations listOperations;

    public CustomHashMap() {
        //initializing nodes of key-value pair
        Arrays.fill(this.keyValuePair,new Node(-1));
        this.keyValuePair = new Node[10];
        this.hashingAlogrithm=new StringToIntKeyHashingAlgorithm<>();
        this.listOperations=new ListOperations();
    }

    public void put(K key,V value) {
        int key=getHashKey(key);
        Node head=keyValuePair[key];
        //add node to list
        listOperations.addNode(head,new Node<V>(value))
    }

    public Set<V> get(K key) {
        int key=getHashKey(key);
        return listOperations.getValues(node[key]);
    }

    public void size() {
        int mpSize=0;
        for (int i=0;i<keyValuePair.length;i++) {
            if (listOperations.size(keyValuePair[i]) >= 1) {
                mpSize = mpSize + 1;
            }
        }
        return mpSize;
    }

    public void remove(K key) {
        int hashKey=getHashKey(key);
        this.keyValuePair[hashKey]=new Node(-1);
    }

    public Set<K> keySet() {
        Set<K> keyset=new HashSet<>();
        for (int i=0;i<keyValuePair.length;i++) {
            if (listOperations.size(keyValuePair[i]) >= 1) {
                keyset.add(i);
            }
        }
        return keyset;
    }

    public Set<V> valueSet() {
        Set<K> valueset=new HashSet<>();
        for (int i=0;i<keyValuePair.length;i++) {
            valueset.addAll(this.get(keyValuePair[i]));
        }
        return valueset;
    }

    public void reSize() {

    }

    public int getHashKey(K key) {
       return hashingAlogrithm.hashKey(key)%keyValuePair.size();
    }
}