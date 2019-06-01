//
// Task 1. Set<T> class (10%)
// This is used in DisjointSets<T> to store actual data in the same sets
//

//You cannot import additonal items
import java.util.AbstractCollection;
import java.util.Iterator;
/**
 * Represents a generic Set which can operate basic functions such as
 * set, size, add, addAll, clear, Iterator
 * 
 * @author SM Nazibullah Touhid
 * 
 */
public class Set<T> extends AbstractCollection<T> {
	//Private classes
	
	/**
	 * Represents a private generic Node class
	 */
	private static class Node<T> {
		T value;
		Node<T> next;
		Node<T> prev;

		public Node(T value) {
			this.value = value;
			this.next = null;
			this.prev = null;
		}
	}
	/**
	 * Represents a private generic Linked List class which uses the Node class 
	 * It has basic operations such as add, size
	 */
	private static class List<T> {

		private Node<T> head;
		private Node<T> tail;
		private int size;
	/**
	 * Initialize the Linked List
	 */
		public List() {
			this.head = null;
			this.tail = null;
		}
	/**
	 * This method adds a value to the linked list
	 * 
	 * @param value the value to be added
	 */
		public void add(T value) {
			Node<T> temp = new Node<>(value);
			if (value != null && head == null) {
				head = temp;
				tail = head;
				size++;
			} else if (value != null) {
				Node<T> current = tail;
				current.next = temp;
				tail = temp;
				temp.prev = current;
				size++;
			} else {
				throw new NullPointerException();
			}
		}
	/**
	 * this method calculates the size of the list
	 * 
	 * @return the size of the list as a type Integer
	 */
		public int size() {
			return this.size;
		}

	}

	private List<T> set;
	private int numOfItem;

	/**
	 * Initialize the Set class
	 */
	public Set() {
		set = new List<>();
		numOfItem = 0;

	}

	/**
	 * this method adds an item to the set
	 * 
	 * @param item the item to be added
	 * 
	 * @return a boolean expression 
	 */
	public boolean add(T item) {
		if(item!=null) {
		set.add(item);
		numOfItem++;
		return true;
		}else {
			throw new NullPointerException();
		}
	}

	/**
	 * This method adds another set to the set
	 * 
	 * @param other The set to be added
	 * 
	 * @return a boolean expression
	 */
	public boolean addAll(Set<T> other) {
		set.tail.next = other.set.head;
		set.tail = other.set.tail;
		numOfItem = numOfItem + other.numOfItem;
		return true;
	}

	/**
	 * This method makes the set empty
	 */
	public void clear() {
		set.head = null;
		numOfItem = 0;
	}

	/**
	 * This method calculates the number of item in the set
	 * 
	 * @return the number of item in the set as a type int
	 */
	public int size() {
		return numOfItem;
	}

	/**
	 * This method iterates through the set
	 * 
	 * @return an iterator
	 */
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			Node<T> current = set.head;

			public T next() {
				T temp = current.value;
				current = current.next;
				return temp;
			}

			public boolean hasNext() {
				return (current != null);
			}
		};
	}

	// main method just for testing
	public static void main(String[] args) {

		Set<Integer> s = new Set<>();
		System.out.println(s);// should return []
		s.add(10);
		s.add(20);
		s.add(30);
		Set<Integer> s2 = new Set<>();
		s2.add(40);
		s2.add(50);
		s2.add(60);
		s.addAll(s2);
		s.add(70);
		Set<Integer> s3 = new Set<>();
		s.addAll(s3);
		System.out.println(s);// should return [10, 20, 30, 40, 50, 60, 70]
		System.out.println(s.size());// should return 7
		s.clear();
		System.out.println(s);// should return []
		System.out.println(s.size());// should return 0

		Set<String> s4 = new Set<>();
		s4.add("A");
		s4.add("B");
		Set<String> s5 = new Set<>();
		s5.add("C");
		s5.add("D");
		s5.add("E");
		s4.addAll(s5);
		Set<String> s6 = new Set<>();
		s6.add("F");
		s6.add("G");
		s4.addAll(s6);
		Set<String> s7 = new Set<>();
		s7.add("H");
		s7.add("I");
		s4.addAll(s7);
		System.out.println(s4);// should return [A, B, C, D, E, F, G, H, I]
		System.out.println(s4.size());// should return 9
		s4.clear();
		System.out.println(s4);// should return []
		System.out.println(s4.size());// should return 0

	}
}
