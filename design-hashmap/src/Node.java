public class Node<V> {
    V data;
    Node<V> prev;
    Node<V> next;

    public Node(V data) {
        this.data=data;
        this.prev=null;
        this.next=null;
    }

    public Node(V data,Node<V> prev,Node<V> next) {
        this.data=data;
        this.prev=prev;
        this.next=next;
    }
}