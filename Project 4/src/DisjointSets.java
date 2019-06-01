import java.util.ArrayList;
/**
 * Represents a generic Disjoint Set which can operate basic functions such as
 * union, set and find
 * 
 * @author SM Nazibullah Touhid
 * 
 */
public class DisjointSets<T>
{
	private int[] s; //the sets
	private ArrayList<Set<T>> sets; //the actual data for the sets

	/**
	 * Initialize the Disjoint set
	 * 
	 * @param data The data to be inserted
	 */
	public DisjointSets(ArrayList<T> data) {
		if(data!=null) {
		s= new int[data.size()];
		sets= new ArrayList<>();
		
		for(int i=0; i<data.size(); i++) {
			Set<T> tempSet= new Set<>();
			if(data.get(i)!=null) {
			tempSet.add(data.get(i));
			sets.add(tempSet);
			s[i]=-1;
			}else {
				throw new NullPointerException();
			}
		}
	}else {
		throw new NullPointerException();
	}
		
		
	}
	/**
	 * This method computes the union of two sets using rank union by size
	 * 
	 * @param root1 the root of first set
	 * @param root2 the root of second set
	 * @return the new root of the unioned sets
	 * 
	 */
	public int union(int root1, int root2) {
		//throw IllegalArgumentException() if non-roots provided
		if(s[root1]>=0 && s[root2]>=0) {
			throw new IllegalArgumentException();
		}
		//if two sets are equal, root1 is the new root
		else {
		if(s[root1]==s[root2] || s[root1]<s[root2]) {
			s[root1]=s[root1]+s[root2];
			s[root2]=root1;
			sets.get(root1).addAll(sets.get(root2));
			sets.get(root2).clear();
			return root1;
		}else {
			s[root2]=s[root2]+s[root1];
			s[root1]=root2;
			sets.get(root2).addAll(sets.get(root1));
			sets.get(root1).clear();
			return root2;
		}
		}
	}

	/**
	 * This method finds a specific set usinf path compression
	 * 
	 * @param x the set to find
	 * @return the root of the found set
	 */
	public int find(int x) {
		if(x>=0 && x<sets.size()) {
		int root=x;
		ArrayList<Integer> children= new ArrayList<>();
		while(s[root]>=0) {
			children.add(root);
			root=s[root];		
		}
		for(int i=0; i<children.size();i++) {
			s[children.get(i)]=root;
		}
		
		return root;
		}else{
			throw new IllegalArgumentException();
		}
	}
	/**
	 * This method gets all the data from a specific set
	 * 
	 * @param root the root of the sets
	 * @return A set containing all data
	 */
	public Set<T> get(int root) {
		if(root>=0) {
		return sets.get(root); 
		}else{
			throw new IllegalArgumentException();
		}
	}
	
	//main method just for testing
	public static void main(String[] args) {
		ArrayList<Integer> arr = new ArrayList<>();
		for(int i = 0; i < 10; i++)
			arr.add(i);
		
		DisjointSets<Integer> ds = new DisjointSets<>(arr);
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(1)); //should be 1
		System.out.println(ds.union(0, 1)); //should be 0
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(1)); //should be 0
		System.out.println("-----");
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(2)); //should be 2
		System.out.println(ds.union(0, 2)); //should be 0
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(2)); //should be 0
		System.out.println("-----");
		//Note: AbstractCollection provides toString() method using the iterator
		//see: https://docs.oracle.com/javase/8/docs/api/java/util/AbstractCollection.html#toString--
		//so your iterator in Set needs to work for this to print out correctly
		System.out.println(ds.get(0)); //should be [0, 1, 2]
		System.out.println(ds.get(1)); //should be []
		System.out.println(ds.get(3)); //should be [3]
	}
}
