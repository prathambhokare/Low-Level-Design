public class ListOperations {

    public void addNode(Node head,Node newNode) {
        while (head.next != null) {
            head = head.next;
        }
        head.next = newnNode;
    }

    public int size(Node head) {
        int nodeCount = 0;
        head = head.next;
        while (head != null) {
            nodeCount = nodeCount + 1;
            head = head.next;
        }
        return nodeCount;
    }

    public Set<V> getValues(Node<V> head) {
        Set<V> set=new HashSet<>();
        head=head.next;
        while (head!=null) {
            set.add(head.data);
            head = head.next;
        }
        return set;
    }
}