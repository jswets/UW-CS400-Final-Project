package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Implementation of a B+ tree to allow efficient access to
 * many different indexes of a large data set. 
 * BPTree objects are created for each type of index
 * needed by the program.  BPTrees provide an efficient
 * range search as compared to other types of data structures
 * due to the ability to perform log_m N lookups and
 * linear in-order traversals of the data items.
 * 
 * @author sapan (sapan@cs.wisc.edu)
 *
 * @param <K> key - expect a string that is the type of id for each item
 * @param <V> value - expect a user-defined type that stores all data for a food item
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

	// Root of the tree
	private Node root;

	// Branching factor is the number of children nodes 
	// for internal nodes of the tree
	private int branchingFactor;

	/**
	 * Public constructor
	 * 
	 * @param branchingFactor 
	 */
	public BPTree(int branchingFactor) {
		if (branchingFactor <= 2) {
			throw new IllegalArgumentException(
					"Illegal branching factor: " + branchingFactor);
		}

		this.branchingFactor = branchingFactor;
		root = new LeafNode();
	}


	/*
	 * (non-Javadoc)
	 * @see BPTreeADT#insert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void insert(K key, V value) {
		root.insert(key, value);
	}


	/*
	 * (non-Javadoc)
	 * @see BPTreeADT#rangeSearch(java.lang.Object, java.lang.String)
	 */
	@Override
	public List<V> rangeSearch(K key, String comparator) {
		if (!comparator.contentEquals(">=") && 
				!comparator.contentEquals("==") && 
				!comparator.contentEquals("<=") )
			return new ArrayList<V>();

		if (root == null) return new ArrayList<V>();
		
		return root.rangeSearch(key, comparator);
	}


	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Queue<List<Node>> queue = new LinkedList<List<Node>>();
		queue.add(Arrays.asList(root));
		StringBuilder sb = new StringBuilder();
		while (!queue.isEmpty()) {
			Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
			while (!queue.isEmpty()) {
				List<Node> nodes = queue.remove();
				sb.append('{');
				Iterator<Node> it = nodes.iterator();
				while (it.hasNext()) {
					Node node = it.next();
					sb.append(node.toString());
					if (it.hasNext())
						sb.append(", ");
					if (node instanceof BPTree.InternalNode)
						nextQueue.add(((InternalNode) node).children);
				}
				sb.append('}');
				if (!queue.isEmpty())
					sb.append(", ");
				else {
					sb.append('\n');
				}
			}
			queue = nextQueue;
		}
		return sb.toString();
	}


	/**
	 * This abstract class represents any type of node in the tree
	 * This class is a super class of the LeafNode and InternalNode types.
	 * 
	 * @author sapan
	 */
	private abstract class Node {

		// List of keys
		List<K> keys;

		/**
		 * Package constructor
		 */
		Node() {
			keys = new ArrayList<K>();
		}

		/**
		 * Inserts key and value in the appropriate leaf node 
		 * and balances the tree if required by splitting
		 *  
		 * @param key
		 * @param value
		 */
		abstract void insert(K key, V value);

		/**
		 * Gets the first leaf key of the tree
		 * 
		 * @return key
		 */
		abstract K getFirstLeafKey();

		/**
		 * Gets the new sibling created after splitting the node
		 * 
		 * @return Node
		 */
		abstract Node split();

		/*
		 * (non-Javadoc)
		 * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
		 */
		abstract List<V> rangeSearch(K key, String comparator);

		/**
		 * 
		 * @return boolean
		 */
		abstract boolean isOverflow();

		public String toString() {
			return keys.toString();
		}

	} // End of abstract class Node

	/**
	 * This class represents an internal node of the tree.
	 * This class is a concrete sub class of the abstract Node class
	 * and provides implementation of the operations
	 * required for internal (non-leaf) nodes.
	 * 
	 * @author sapan
	 */
	private class InternalNode extends Node {

		// List of children nodes
		List<Node> children;

		/**
		 * Package constructor
		 */
		InternalNode() {
			super();
			children = new ArrayList<Node>();
		}

		/**
		 * (non-Javadoc)
		 * @see BPTree.Node#getFirstLeafKey()
		 */
		K getFirstLeafKey() {
			return children.get(0).getFirstLeafKey();
		}

		/**
		 * (non-Javadoc)
		 * @see BPTree.Node#isOverflow()
		 */
		boolean isOverflow() {
			return children.size() > branchingFactor;
		}

		/**
		 * (non-Javadoc)
		 * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
		 */
		void insert(K key, V value) {
			//find which child it should be inserted into
			int childIndex = this.getChildIndex(key);
			Node child = children.get(childIndex);
			//insert it (could be internal or leaf)
			child.insert(key, value);
			//clean up any overflow problems from inserting
			if (child.isOverflow()) {
				Node sibling = child.split();
				keys.add(childIndex, sibling.getFirstLeafKey()); //new key bubbles up at same index as the child index
				children.add(childIndex + 1, sibling); //sibling gets added to right of old child
			}
			//parent insert call will clean up overflow for this node unless it's the root
			if (root.isOverflow()) {
				Node sibling = this.split();
				InternalNode newRoot = new InternalNode();
				newRoot.keys.add(sibling.getFirstLeafKey());
				newRoot.children.add(this);
				newRoot.children.add(sibling);
				root = newRoot;
			}

		}

		/**
		 * Given a key, returns the index for which child of the internal node the key should belong to
		 * @param key - key to evaluate
		 * @return - index for child
		 */
		int getChildIndex(K key) {
			int keyCount = keys.size();
			for (int i = 0; i<keyCount; i++) {
				K tmpKey = keys.get(i);
				if (tmpKey.compareTo(key) >= 0) {
					return i;
				}
			}
			return keyCount;
		}

		/**
		 * (non-Javadoc)
		 * @see BPTree.Node#split()
		 */
		Node split() {
			//create new node
			InternalNode sibling = new InternalNode();

			int numKeys = keys.size();
			int numChildren = children.size();

			sibling.keys.addAll(keys.subList((numKeys+1)/2,numKeys));
			sibling.children.addAll(children.subList(numChildren/2, numChildren));
			
			for (int i = numKeys-1; i >= numKeys/2; i--) { //loop backward so the list doesn't reorder as we remove
				keys.remove(i);
			}
			
			for (int i = numChildren-1; i >= numChildren/2; i--) { //loop backward so the list doesn't reorder as we remove
				children.remove(i);
			}
			//return newly created node
			return sibling;
		}

		/**
		 * (non-Javadoc)
		 * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
		 */
		List<V> rangeSearch(K key, String comparator) {
			//find child which contains this key, or where it would go
			if (comparator.contentEquals("<=")) {
				return children.get(0).rangeSearch(key, comparator);
			}
			int index = this.getChildIndex(key);
			Node child = children.get(index);
			//call rangeSearch on that child... ultimately have to get to leaf node
			return child.rangeSearch(key, comparator);
		}

	} // End of class InternalNode



	/**
	 * This class represents a leaf node of the tree.
	 * This class is a concrete sub class of the abstract Node class
	 * and provides implementation of the operations that
	 * required for leaf nodes.
	 * 
	 * @author sapan
	 */
	private class LeafNode extends Node {

		// List of values
		List<V> values;

		// Reference to the next leaf node
		LeafNode next;

		// Reference to the previous leaf node
		//LeafNode previous;
		// could build logic to utilize previous, but isn't necessary

		/**
		 * Package constructor
		 */
		LeafNode() {
			super();
			values = new ArrayList<V>();
		}

		/**
		 * (non-Javadoc)
		 * @see BPTree.Node#getFirstLeafKey()
		 */
		K getFirstLeafKey() {
			return keys.get(0);
		}

		/**
		 * (non-Javadoc)
		 * @see BPTree.Node#isOverflow()
		 */
		boolean isOverflow() {
			return values.size() > branchingFactor - 1;
		}

		/**
		 * (non-Javadoc)
		 * @see BPTree.Node#insert(Comparable, Object)
		 */
		void insert(K key, V value) {

			int index = 0;
			if(keys.size() > 0) {
				while ((index < keys.size()) && (keys.get(index).compareTo(key) < 0)) {
					index++;
				}
			}
			
			keys.add(index, key);
			values.add(index, value);
			
			if (root.isOverflow()) {
				Node sibling = split();
				InternalNode newRoot = new InternalNode();
				newRoot.keys.add(sibling.getFirstLeafKey());
				newRoot.children.add(this);
				newRoot.children.add(sibling);
				root = newRoot;
			}
		}

		/**
		 * (non-Javadoc)
		 * @see BPTree.Node#split()
		 */
		Node split() {
			LeafNode sibling = new LeafNode();

			int middle = (keys.size()) / 2;

			sibling.keys.addAll(keys.subList(middle, keys.size()));
			sibling.values.addAll(values.subList(middle, values.size()));
			
			for (int i=keys.size()-1; i >= middle; i--) {
				keys.remove(i);
				values.remove(i);
			}
			
			sibling.next = next;
			next = sibling;

			return sibling;
		}

		/**
		 * (non-Javadoc)
		 * @see BPTree.Node#rangeSearch(Comparable, String)
		 */
		List<V> rangeSearch(K key, String comparator) {
			List<V> result = new ArrayList<V>();

			if (key == null || comparator == null) {
				return result;
			}

			if (!comparator.contentEquals(">=") && 
					!comparator.contentEquals("==") && 
					!comparator.contentEquals("<=") ) {
				return result;
			}

			if (comparator.contentEquals("==")) {
				LeafNode tmpNode = this;
				int index = 0;
				while (tmpNode.keys.get(index).compareTo(key) <= 0) {
					if (tmpNode.keys.get(index).compareTo(key) < 0) {
						index++;
						if (index >= tmpNode.keys.size()) {
							tmpNode = tmpNode.next;
							index = 0;
						}
					}
					else {
						result.add(tmpNode.values.get(index));
						index++;
						if (index >= tmpNode.keys.size()) {
							tmpNode = tmpNode.next;
							index = 0;
						}
					}
				}
			}

			else if (comparator.contentEquals("<=")) {

				if (keys.get(0).compareTo(key) > 0) {
					return result;
				}

				LeafNode tmpNode = this;
				int index = 0;
				while (tmpNode.keys.get(index).compareTo(key) <= 0) {
					result.add(tmpNode.values.get(index));
					//potentially could have a small performance improvement here by checking last key in node first
					//and adding all if it still passes the comparator
					index++;
					if (index >= tmpNode.keys.size()) {
						tmpNode = tmpNode.next;
						if (tmpNode == null) break;
						index = 0;
					}
				}

			}

			else { // >= case

				int index = 0;

				while (index < keys.size() && keys.get(index).compareTo(key) < 0) {
					index++;
				}

				LeafNode tmpNode = this;
				for (int i = index; i < tmpNode.keys.size(); i++) {
					result.add(tmpNode.values.get(i));
				}
				while (tmpNode.next != null) {
					tmpNode = tmpNode.next;
					result.addAll(tmpNode.values);
				}
				
			}
			return result;
		}

	} // End of class LeafNode


	/**
	 * Contains a basic test scenario for a BPTree instance.
	 * It shows a simple example of the use of this class
	 * and its related types.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// create empty BPTree with branching factor of 3
		BPTree<Double, Double> bpTree = new BPTree<>(3);

		// create a pseudo random number generator
		Random rnd1 = new Random();

		// some value to add to the BPTree
		Double[] dd = {0.0d, 0.5d, 0.2d, 0.8d};

		// build an ArrayList of those value and add to BPTree also
		// allows for comparing the contents of the ArrayList 
		// against the contents and functionality of the BPTree
		// does not ensure BPTree is implemented correctly
		// just that it functions as a data structure with
		// insert, rangeSearch, and toString() working.
		List<Double> list = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			Double j = dd[rnd1.nextInt(4)];
			list.add(j);
			bpTree.insert(j, j);
			System.out.println("\n\nTree structure:\n" + bpTree.toString());
		}
		List<Double> filteredValues = bpTree.rangeSearch(0.4d, "==");
		System.out.println("Filtered values: " + filteredValues.toString());
	}

} // End of class BPTree