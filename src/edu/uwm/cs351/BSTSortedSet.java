//
// CST 751 - Homework 67
// Name: Jonathan Nguyen
// Due Date: 11/11/2019 10:00pm 
//
// Got help from Zach and Isaac in understanding how to implement the remove method. 
// For the nth method I also got help from Daniel and Carsen in understanding how to
// implement this. 
//  
//

package edu.uwm.cs351;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.TestCase;
import edu.uwm.cs.junit.LockedTestCase;


public class BSTSortedSet<E extends Comparable<E>> extends AbstractSet<E> {

	//TODO: class fields
	// We need root and version
	// DO NOT use any field to count the number of nodes
	private Node<E> root;
	private int version = 0;
	
	private static boolean doReport = true;

	private static boolean report(String s) {
		if (doReport)
			System.out.println("Invariant error: " + s);
		return false;
	}
	private static int reportNeg(String s) {
		report(s);
		return -1;
	}
	//TODO: static node class
	// Fields must be named data, left, right, parent, treeSize
	// A helpful toString might help with debugging
	private static class Node<T> {
		private T data;
		private Node<T> parent;
		private Node<T> left;
		private Node<T> right;
		private int treeSize; 
		
		Node(T data) { 
			this.data = data;
		}
	}

	//Helper method to make sure an Object o can be used as type E
	//Only works when tree is not empty (we have an example of type E to compare it against)
	@SuppressWarnings("unchecked")
	private E asElement(Object o) {
		if (root == null)
			return null;
		try {
			E t = (E) o;
			root.data.compareTo(t);
			return t;
		} catch (ClassCastException | NullPointerException e) {
			return null;
		}
	}

	/**
	 * Check the invariant.  
	 */
	private boolean wellFormed() {
		//TODO: Implement invariant similar to last homework
		//For each subtree rooted at n:
		// 1. n's data must not be null.
		// 2. n's data must fall within the appropriate bounds.
		// 3. n's parent must be the node from which you found n (new parameter to check this?)
		// 4. check the two subtrees (with updated bounds)
		// 5. treeSize must be appropriate for the size of the subtrees
		// 6. return the size of the subtree rooted at n,
		//     or -1 if there is a problem (either here, or already detected further down the tree).
		//     Use reportNeg where appropriate.
		if(root != null) {
			int n = checkInRange(root,null,null,root.parent);
			if(n < 0) return false;
			if (n != root.treeSize) return report("treeSize is " + root.treeSize + " but should be " + n);
		}
		return true;
	}
	
	//You may wish to use a recursive helper method to check wellFormed
	//Feel free to use homework 8 as a starting point
	private<E> int checkInRange(Node<E> n,E lo,E hi,Node<E> p) {
		if(n == null) return 0; //*
		if(n.data == null) return reportNeg("n.data is null!");
		if(lo != null && ((Comparable<E>)lo).compareTo(n.data) >= 0) return reportNeg("lo bound >= n.data!");
		if(hi != null && ((Comparable<E>)hi).compareTo(n.data) <= 0) return reportNeg("hi bound <= n.data!");
		if(n != root && n.parent != p) return reportNeg("the child node doesn't have a parent node!");
		if(root.parent != null) return reportNeg("BST is cyclic!");
		if(checkInRange(n.left,lo,n.data,n) == -1 || checkInRange(n.right,n.data,hi,n) == -1) return -1; 
		int countTreeNode = 1 + checkInRange(n.left,lo,n.data,n) + checkInRange(n.right,n.data,hi,n);
		if(countTreeNode != n.treeSize) return reportNeg("treeSize given is " + n.treeSize + ", while acutal treeSize is " + countTreeNode + "!");
		return countTreeNode; //*
	}
	
	/**
	 * Construct empty tree-based set
	 */
	public BSTSortedSet() {
		//TODO: implement constructor
		//assert invariant after constructor
		assert wellFormed() : "invariant fails after constructor";
	}
	
	protected BSTSortedSet(boolean ignored) {} // don't change: used by invariant checker
	
	@Override
	public int size() {
		assert wellFormed() : "Set not well-formed at start of size()";
		//TODO: implement size
		if(isEmpty()) return 0;
		return root.treeSize;
	}
	
	@Override
	public boolean add(E t) {
		assert wellFormed() : "Set not well-formed at start of add()";
		//TODO: implement add
		if(t == null) throw new IllegalArgumentException("can't add null element to set!");
		if(contains(t)) return false;
		Node<E> p = root;
		Node<E> lag = null;
		while(p != null) {
			lag = p;
			if((((Comparable<E>)t).compareTo(p.data) < 0)) p = p.left;
			else if((((Comparable<E>)t).compareTo(p.data) > 0)) p = p.right;
			if(p != null && p != root) p.treeSize++;
		}
		p = new Node<E>(t);
		p.parent = lag;
		if(lag == null) root = p;
		else if((((Comparable<E>)t).compareTo(lag.data) < 0)) lag.left = p;
		else lag.right = p;
		if(p != null && p != root) p.treeSize++;
		root.treeSize++; 
		version++;
		assert wellFormed() : "Set not well-formed at end of add()";
		return true;
	}
	//You may wish to use a recursive helper method for add()

	@Override
	public void clear() {
		//TODO: Implement clear(). Do not update version if data structure state isn't changed
		assert wellFormed() : "Set not well-formed at start of clear()";
		if(root == null) return;
		root.treeSize = 0;
		root = null;
		version++;
	}

	@Override
	public boolean contains(Object o) {
		//TODO: implement contains
		//  Use asElement
		E element = asElement(o);
		if (isEmpty() || element == null) return false;
		Node<E> p = root;
		while(p != null) {
			if((((Comparable<E>)element).compareTo(p.data) == 0)) return true;
			else if((((Comparable<E>)element).compareTo(p.data) < 0)) p = p.left;
			else p = p.right;
		}
		assert wellFormed() : "Set not well-formed at start of contains()";
		return false;
	}
	// You may wish to use a helper method for a recursion

	@Override
	public boolean isEmpty() {
		//TODO: very little work
		assert wellFormed() : "Set not well-formed at start of isEmpty()";
		return root == null;
	}

	@Override
	public boolean remove(Object o) {
		assert wellFormed() : "Set not well-formed at start of remove()";
		//TODO: implement remove
		if(isEmpty() || !contains(o)) return false;
		version++;
		E element = asElement(o);
		root = removeElement(root,element);
		assert wellFormed() : "Set not well-formed at end of remove()";
		return true;
	}
	
	// You may wish to use a helper method for remove
	// You may also wish to have your iterator use this helper method
	// There are multiple removal cases to consider
	
	private Node<E> removeElement(Node<E> r, E e) {
		int c = ((Comparable<E>)e).compareTo(r.data);
		if (c == 0) {
			r.treeSize--;
			if(r.treeSize == 0) {
				return null;
			}
			if (r.left == null) {
				r.right.parent = r.parent;
				return r.right;
			}
			if (r.right == null) {
			    r.left.parent = r.parent;
				return r.left;
			}
			Node<E> subTree = r.left;
			while (subTree.right != null) {
				subTree = subTree.right;
			}
			r.data = subTree.data;
			r.left = removeElement(r.left,r.data);
		} 
		else if (c < 0) {
			r.treeSize--;
			r.left = removeElement(r.left,e);
		} 
		else if (c > 0) {
			r.treeSize--;
			r.right = removeElement(r.right,e);
		}
		return r;
	}
	
	/**
	 * Method to get the nth element in sorted order (according to compareTo)
	 * @param n which ordinality of element to return
	 * @return nth element, 0-indexed
	 */
	public E getNth(int n) {
		//TODO: implement get Nth
		// You may use a recursive helper method
		// Our solution "micromanages" just a little
		assert wellFormed() : "Set not well-formed at start of getNth()";
		if(isEmpty()) throw new IllegalArgumentException("can't find nth element, when set is empty!");
		if(n < 0 || n > root.treeSize - 1) throw new IllegalArgumentException("n is not in range!");
		Node<E> p = findNth(root,n);
		return p.data;
	}
	
	private Node<E> findNth(Node<E> r, int n) {
		int check = 0;
		Node<E> p = r;
		if(p.left != null) check = p.left.treeSize;
		if(n == check) return p;
		if(n < check) p = p.left;
		else if(n > check){
			n = n - check - 1;
			p = p.right;
		}	
		return findNth(p,n);
	}
	
	private Node<E> findFirstNode(){
		Node<E> p = root;
		if(p == null) return null; 
		while(p.left != null) {
			p = p.left;
		}
		return p;
	}
	
	private Node<E> findLastNode(){
		Node<E> p = root;
		if(p == null) return null;
		while(p.right != null) {
			p = p.right;
		}
		return p;
	}
	
	@Override
	public String toString() {
		// TODO optional: make this more useful
		assert wellFormed();
		return "Set of size " + size();
	}

	@Override
	public Iterator<E> iterator() {
		//TODO: very little
		assert wellFormed();
		return new MyIterator();
	}
	

	private class MyIterator implements Iterator<E> {
		private Node<E> nextNode;
		private boolean hasCurrent;
		private int myVersion = version;
		private Node<E> current;
		 
		/*
		 * Iterator design:
		 * nextNode points to the next node to be iterated over
		 * nextNode null means there are no more elements to iterate over
		 * hasCurrent determines whether there is something that can be removed
		 * the thing to be removed is the node that came before nextNode
		 * this can be found by following the appropriate references
		 * when nextNode is null and hasCurrent is true, the current is the final node
		 */
		
		private boolean wellFormed() {
			// TODO: check outer invariant
			// TODO: if versions don't match, accept
			// TODO: check that nextNode is in the tree, or is null
			// TODO: if nextNode is the first node (or tree is empty), hasCurrent must be false
			if(!BSTSortedSet.this.wellFormed()) return report("failed outter invariants!");
			if(version == myVersion) {
				if(nextNode != null && !contains(nextNode.data)) return report("nextNode is either null or not in the set!");
				if(hasCurrent != false && (isEmpty() || nextNode == findFirstNode())) return report("hasCurrent is not false, when nextNode is first node (or tree is empty)!");
			}
			return true;
		}
		
		protected MyIterator(boolean ignored) {} // don't change: used by invariant checker
		
		public MyIterator() {
			// TODO: implement constructor
			nextNode = findFirstNode();
			assert wellFormed() : "invariant broken at end of constructor";
		}
		
		@Override
		public boolean hasNext() {
			assert wellFormed() : "invariant broken in hasNext()";
			//TODO: implement hasNext
			if(version != myVersion) throw new ConcurrentModificationException("iterator version doesn't match collection");
			return nextNode != null;
		}

		@Override
		public E next() {
			assert wellFormed() : "invariant broken at start of next()";
			//TODO: implement next
			if(version != myVersion) throw new ConcurrentModificationException("iterator version doesn't match collection");
			if(!hasNext()) throw new NoSuchElementException("current element doesn't exist!");
			current = nextNode;
			if(current == findLastNode()) nextNode = null;
			else if(nextNode.right != null){
				Node<E> p = nextNode.right;
				while(p.left != null) {
					p = p.left;
				}
				nextNode = p;
			}
			else if(nextNode.right == null) {
				if(nextNode.parent != null && nextNode.parent.right != nextNode) nextNode = nextNode.parent;
				else {
					Node<E> p = nextNode;
					while(p.parent != null && p.parent.right == p) {
						p = p.parent;
					}
					nextNode = p.parent;
				}
			}
			hasCurrent = current != null;
			assert wellFormed(): "invariant broken at end of next()";
			return current.data;
		}
		
		// TODO: override remove
		//   You may be able to reuse a helper method used by remove(Object o)
		
		@Override
		public void remove() {
			assert wellFormed() : "invariant broken at start of remove()";
			if(version != myVersion) throw new ConcurrentModificationException("iterator version doesn't match collection");
			if(!hasCurrent) throw new IllegalStateException("there is no current element that can be remove!");
			root = removeElement(root,current.data);
			myVersion++;
			version++;
			hasCurrent = false;
			assert wellFormed() : "invariant broken at end of remove()";
		}
	}

	public static class TestInternals extends LockedTestCase {
		private BSTSortedSet<Integer> self;
		private BSTSortedSet<Integer>.MyIterator iterator;
		Integer i1, i1a, i2, i3, i4, i5;
		Node<Integer> n0, n1, n1a, n2, n3, n4, n5;
		static {
			System.out.println("In the following tests, you should see 'invariant error' messages");
			System.out.println("If you do not see any, you are failing the tests!");
		}

		public void assertOK(String comment, boolean condition) {
			doReport = true;
			super.assertTrue(comment, condition);
		}

		public void assertNotOK(String message, boolean condition) {
			doReport = false;
			super.assertFalse(message, condition);
		}

		protected void setUp() {
			i1 = 1;
			i1a = 1;
			i2 = 4;
			i3 = 9;
			i4 = 16;
			i5 = 25;
			n0 = new Node<Integer>(null);
			n1 = new Node<Integer>(i1);
			n1a = new Node<Integer>(i1a);
			n2 = new Node<Integer>(i2);
			n3 = new Node<Integer>(i3);
			n4 = new Node<Integer>(i4);
			n5 = new Node<Integer>(i5);
			
			self = new BSTSortedSet<Integer>(false);
			iterator = self.new MyIterator(false);
		}

		public void testEmpty() {
			self.root = null;
			assertOK("empty set is just fine", self.wellFormed());
		}

		public void testNullElement() {
			self.root = n0;
			n0.treeSize = 1;
			assertNotOK("null element not OK", self.wellFormed());
			self.root = n1;
			n1.treeSize = 1;
			assertOK("One element should be OK", self.wellFormed());
			n1.left = n0;
			n0.parent = n1;
			n1.treeSize = 2;
			assertNotOK("null element not OK", self.wellFormed());
		}

		public void testEquivalent1() {
			self.root = n1;
			n1.left = n1a;
			n1a.parent = n1;
			n1.treeSize = 2;
			n1a.treeSize = 1;
			assertNotOK("equivalent values should be disallowed", self.wellFormed());
			n1.left = null;
			n1.right = n1a;
			assertNotOK("equivalent values should be disallowed", self.wellFormed());
		}

		public void testIdentical() {
			self.root = n1;
			n1.left = n1a;
			n1a.data = i1;
			n1a.parent = n1;
			n1.treeSize = 2;
			n1a.treeSize = 1;
			assertNotOK("identical values not OK", self.wellFormed());
			n1.left = null;
			n1.right = n1a;
			assertNotOK("identical values not OK", self.wellFormed());
		}

		public void testEquivalent2() {
			self.root = n2.parent = n1;
			n1.right = n2;
			n2.left = n1a;
			n1a.parent = n2;
			n1.treeSize = 3;
			n2.treeSize = 2;
			n1a.treeSize = 1;
			assertNotOK("equivalent values not OK", self.wellFormed());
		}
		
		public void testTreeSize() {
			self.root = null;
			assertOK("OK so far", self.wellFormed());
			self.root = n3;
			n3.treeSize = 1;
			assertOK("OK so far", self.wellFormed());
			n3.treeSize = 0;
			assertNotOK("treeSize should be 1", self.wellFormed());
			n3.left = n2;
			n3.treeSize = 2;
			n2.parent = n3;
			n2.treeSize = 2;
			assertNotOK("n2 treeSize should be 1", self.wellFormed());
			n2.treeSize = 0;
			assertNotOK("n2 treeSize should be 1", self.wellFormed());
			n2.treeSize = 1;
			assertOK("Parent", self.wellFormed());
		}
		
		public void testTreeSize2() {
			self.root = n3;
			n3.left = n2;
			n2.parent = n3;
			n3.treeSize = 2;
			n2.treeSize = 1;
			assertOK("OK so far", self.wellFormed());
			
			n3.right = n4;
			n4.parent = n3;
			n4.treeSize = 1;
			n3.treeSize = 3;
			n2.left = n1;
			n1.parent = n2;
			n1.treeSize = 1;
			n2.treeSize = 2;
			assertNotOK("root treeSize incorrect", self.wellFormed());
			n3.treeSize = 4;
			n2.treeSize = 1;
			assertNotOK("n2 treeSize incorrect", self.wellFormed());
			n2.treeSize = 2;
			assertOK("OK", self.wellFormed());
		}
		


		public void testParent1() {
			self.root = n3;
			n3.treeSize = 1;
			assertOK("OK so far", self.wellFormed());
			n3.left = n2;
			n3.treeSize++;
			n2.treeSize = 1;
			assertNotOK("No parent", self.wellFormed());
			n2.parent = n3;
			assertOK("Parent", self.wellFormed());
			n3.parent = n3;
			assertNotOK("Cyclic 1", self.wellFormed());
			n3.parent = n2;
			assertNotOK("Cyclic 2", self.wellFormed());
			n2.parent = null;
			assertNotOK("Out of order", self.wellFormed());
		}

		public void testParent2() {
			self.root = n4;
			n4.treeSize = 1;
			assertOK("OK so far", self.wellFormed());
			n4.left = n2;
			n4.treeSize++;
			n2.treeSize = 1;
			assertNotOK("No parent", self.wellFormed());
			n2.parent = n4;
			assertOK("Parent", self.wellFormed());
			n3.treeSize = 1;
			n2.treeSize++;
			n4.treeSize++;
			n2.right = n3;
			assertNotOK("No parent", self.wellFormed());
			n3.parent = n4;
			assertNotOK("skip Parent", self.wellFormed());
			n3.parent = n2;
			assertOK("Parent", self.wellFormed());
			n2.left = n1;
			n1.treeSize = 1;
			n2.treeSize++;
			n4.treeSize++;
			
			assertNotOK("No Parent", self.wellFormed());
			n1.parent = n4;
			assertNotOK("Bad Parent", self.wellFormed());
			n1.parent = n3;
			assertNotOK("Bad Parent", self.wellFormed());
			n1.parent = n2;
			assertOK("Parents OK", self.wellFormed());
			n4.parent = n1;
			assertNotOK("Cyclic 1", self.wellFormed());
			n4.parent = n2;
			assertNotOK("Cyclic 2", self.wellFormed());
			n4.parent = n3;
			assertNotOK("Cyclic 3", self.wellFormed());
		}

		public void testParent3() {
			self.root = n2;
			n2.right = n4;
			n2.treeSize = 2;
			n4.treeSize = 1;
			assertNotOK("Not linked (n2)", self.wellFormed());
			n4.parent = n2;
			assertOK("Linked", self.wellFormed());
			n2.left = n1;
			n2.treeSize++;
			n1.treeSize = 1;
			assertNotOK("Not linked (n1)", self.wellFormed());
			n1.parent = n2;
			assertOK("Linked", self.wellFormed());
			n4.left = n3;
			n3.treeSize = 1;
			n4.treeSize++;
			n2.treeSize++;
			self.root.treeSize = 4;
			assertNotOK("Not linked (n3)", self.wellFormed());
			n3.parent = n2;
			assertNotOK("Badly linked (n3)", self.wellFormed());
			n3.parent = n4;
			n2.parent = n3;
			assertNotOK("Cyclic 1", self.wellFormed());
			n2.parent = null;
			assertOK("OK", self.wellFormed());
			n1a.data = 20;
			n2.parent = n1a;
			assertNotOK("Not in tree", self.wellFormed());
			n1a.left = n2;
			assertNotOK("Not in tree", self.wellFormed());
			self.root = n1a;
			self.root.treeSize = 5;
			assertOK("OK", self.wellFormed());
		}
		
		public void testParent4() {
			self.root = n1;
			n1.right = n2;
			n1.treeSize = 2;
			n2.treeSize = 1;
			n2.parent = n1a;
			n1a.treeSize = 2;
			assertNotOK("Not in tree", self.wellFormed());
			n1a.data = i1;
			assertNotOK("Not in tree", self.wellFormed());
		}

		public void testOutOfOrder1() {
			self.root = n2;
			n2.treeSize = 1;
			assertOK("OK 1", self.wellFormed());
			n2.right = n4;
			n2.treeSize++;
			n4.treeSize = 1;
			n4.parent = n2;
			assertOK("OK 2", self.wellFormed());
			n4.left = n3;
			n3.treeSize = 1;
			n4.treeSize++;
			n2.treeSize++;
			n3.parent = n4;
			assertOK("OK 3", self.wellFormed());
			n3.left = n1;
			n1.parent = n3;
			n1.treeSize = 1;
			n2.treeSize++;
			n3.treeSize++;
			n4.treeSize++;
			assertNotOK("out of order", self.wellFormed());
			n2.left = n1;
			n1.parent = n2;
			n3.left = null;
			n3.treeSize--;
			n4.treeSize--;
			assertOK("OK 4", self.wellFormed());
		}

		public void testOutOfOrder2() {
			self.root = n1;
			n1.treeSize = 1;
			assertOK("OK 1", self.wellFormed());
			n1.right = n3;
			n3.parent = n1;
			n3.treeSize = 1;
			n1.treeSize++;
			assertOK("OK 2", self.wellFormed());
			n3.left = n2;
			n2.treeSize = 1;
			n2.parent = n3;
			n1.treeSize++;
			n3.treeSize++;
			assertOK("OK 3", self.wellFormed());
			n3.right = n4;
			n4.parent = n3;
			++n3.treeSize;
			n4.treeSize = 1;
			++n1.treeSize;;
			assertOK("OK 4", self.wellFormed());
			n4.left = n1a;
			n1a.parent = n4;
			n1a.data = i2;
			++n1.treeSize;
			++n3.treeSize;
			++n4.treeSize;
			n1a.treeSize = 1;
			assertNotOK("out of order", self.wellFormed());
			n4.left = null;
			n4.right = n5;
			n5.parent = n4;
			n5.treeSize = 1;
			assertOK("OK 5", self.wellFormed());
		}

		public void testEmptyIterator() {
			self.root = null;
			assertOK("null cursor should be OK", iterator.wellFormed());
			++self.version;
			assertOK("version bad", iterator.wellFormed());
			++iterator.myVersion;
			assertOK("cursor OK", iterator.wellFormed());
			iterator.hasCurrent = true;
			assertNotOK("iterator on empty set shouldn't have current", iterator.wellFormed());
			iterator.hasCurrent = false;
			iterator.nextNode = n1;
			assertNotOK("cursor lost", iterator.wellFormed());
			self.root = n1;
			n1.treeSize = 1;
			assertOK("initial iterator", iterator.wellFormed());
		}

		public void testIterator() {
			self.version = 456;
			self.root = n2;
			n1.parent = n2;
			n3.parent = n2;
			n2.right = n3;
			n2.left = n1;
			n2.treeSize = 3;
			n1.treeSize = 1;
			n3.treeSize = 1;
			assertOK("", self.wellFormed());
			assertOK("", iterator.wellFormed());
			iterator.nextNode = n1;
			iterator.myVersion = 456;
			assertOK("", iterator.wellFormed());
			iterator.nextNode = n2;
			assertOK("", iterator.wellFormed());
			iterator.nextNode = n4;
			assertNotOK("cursor lost", iterator.wellFormed());
			++iterator.myVersion;
			assertOK("", iterator.wellFormed());
		}

		public void testThroughIterator() {
			self.root = n2;
			n2.left = n1;
			n1.parent = n2;
			n1.treeSize = 1;
			n2.treeSize = 2;
			iterator.nextNode = n1;
			assertEquals("initial state", Tb(1610585648), iterator.wellFormed());
			iterator.hasCurrent = true;
			assertEquals("hasCurrent but no node before nextNode", Tb(1945878069), iterator.wellFormed());
			iterator.nextNode = n2;
			assertEquals("iterated over n1", Tb(323097581), iterator.wellFormed());
			iterator.hasCurrent = false;
			assertEquals("removed something before n2", Tb(1378428561), iterator.wellFormed());
			iterator.nextNode = null;
			iterator.hasCurrent = true;
			assertEquals("iterated over n2 (final element)", Tb(583541388), iterator.wellFormed());
			iterator.hasCurrent = false;
			assertEquals("removed final element", Tb(1014855961), iterator.wellFormed());
		}
	}
}
