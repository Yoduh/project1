/**
 * 
 */
package proj1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author aehandlo
 *
 */
public class Proj1 {

	/**
	 * 
	 */
	public Proj1() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		//create files and scanners to be used by the program
		Scanner in = new Scanner(System.in);
		//System.out.println("Enter a filename (e.g. \"filename.txt\"): ");
		//System.getProperty("user.dir") + "\\" + in.next()
		FileInputStream fileName = new FileInputStream("large.txt");
		PrintStream fileWriter = new PrintStream(new File("large_compressed.txt"));
		Scanner fileReader = new Scanner(fileName);
		
		//loop through input file, processing line by line
	    while (fileReader.hasNextLine()) {
	        try {
	        	//process each line character by character
	        	processLine(fileReader.nextLine(), fileWriter);
	        	if(fileReader.hasNextLine()) {
	        		fileWriter.println();
	        	}
	        } catch (IllegalArgumentException e) {
	            //skip the line
	        }
	    }
	    //close files after program is done
	    fileReader.close();
	    fileWriter.close(); 
	}
	
	/**
	 * Process new line of text.
	 * @param line Line of text to process
	 * @param fileWriter PrintStream writing to file
	 */
	public static void processLine(String line, PrintStream fileWriter) {
		Scanner lineReader = new Scanner(line);
		//change Scanner to scan every character
		lineReader.useDelimiter("");
		ArrayList<String> word = new ArrayList<String>();
		
		while(lineReader.hasNext()) {
			String c = lineReader.next();
			//if character is a letter then add to array as we build a whole word. Don't excluse apostrophes
			if(Character.isLetter(c.charAt(0)) || c.charAt(0) == '\'') {
				word.add(c);
				//need to process word before next line
				if(!lineReader.hasNext()) {
					// just write the word for now, replace this with processing later
					String listString = String.join("",  word);
					System.out.println("word=" + listString);
					fileWriter.print(listString);
					word = new ArrayList<String>();
				}
			} else {
				//else write non-letter character and process the word and prepare to begin a new word on same line
				if(!word.isEmpty()) {
					// just write the word for now, replace this with processing later
					String listString = String.join("",  word);
					System.out.println("word=" + listString);
					fileWriter.print(listString);
					word = new ArrayList<String>();
				}
				fileWriter.print(c);
			}
			
			
		}
	}
	
	
	/**
	 * Provides custom implementation of a recursive
	 * linked list that does not allow for null or duplicate
	 * elements as defined by the equals() method
	 * @author aehandlo
	 * @param <E> parameter for LinkedListRecursive
	 *
	 */
	private class LinkedListRecursive<E> {

		private ListNode front; 
		private int size;
		
		/**
		 * Constructor for LinkedListRecursive, initializes front to
		 * null and size to zero
		 */
		public LinkedListRecursive() {
			front = null;
			size = 0;
		}
		
		/**
		 * Returns whether the LinkedListRecursive is empty
		 * @return true if list is empty, false otherwise
		 */
		public boolean isEmpty() {
			if (size() == 0) {
				return true;
			}
			return false;
			
		}
		
		/**
		 * Returns the size of the LinkedListRecursive
		 * @return size is the size of the list
		 */
		public int size() {
			if (front == null) {
				return 0;
			} else {
				return size;
			}
		}
		
		
		/**
		 * Adds the element to the LinkedListRecursive
		 * @param element is the element to be added 
		 * @return true if the element was added at the index,
		 * false otherwise
		 * @throws NullPointerException if element is null
		 * @throws IllegalArgumentException if the list
		 * already contains element
		 */
		public boolean add(E element) {
			if (element == null) {
				throw new NullPointerException();
			}
		
			if (isEmpty()) {
				front = new ListNode(element, front);
				size++;
				return true;
			} else {
				if (front.contains(element)) {
					throw new IllegalArgumentException();
				}
				return front.add(element);
			
			}
		}
		
		/**
		 * Adds the element to the LinkedListRecursive at the given index
		 * @param element is the element to be added 
		 * @param idx is the index where the element should be added
		 * @throws NullPointerException if element is null
		 * @throws IllegalArgumentException if the list
		 * already contains element
		 * @throws IndexOutOfBoundsException is the index less than zero
		 * or greater than list size
		 */
		public void add(int idx, E element) {
			if (element == null) {
				throw new NullPointerException();
			}
			
			if (idx < 0 || idx > size()) {
				throw new IndexOutOfBoundsException();
			}
			
			if (isEmpty()) {
				front = new ListNode(element, front);
				size++;
		
			} 
			else if (front.contains(element)) {
				throw new IllegalArgumentException();
				
			} 
			else if (idx == 0) {
				front = new ListNode(element, front);
				size++;
			} else {
				front.add(idx, element);
			}
			
		}
		
		/**
		 * Returns the element at the given index
		 * @param idx is the index of the element to be returned
		 * @return element at specified index
		 * @throws IndexOutOfBoundsException is the index less than zero
		 * or greater than list size
		 * 
		 */
		public E get(int idx) {
			
			if (idx < 0 || idx > size - 1) {
				throw new IndexOutOfBoundsException();
			} else if (idx == 0) {
				return front.data;
			} else {
				return front.get(idx);
			}
		}
		
		/**
		 * Removes the first occurence of the given element in the list
		 * @param element is the element to be removed
		 * @return true if element is removed from list, false oter
		 * @throws NullPointerException if element is null
		 * @throws IllegalArgumentException if list is empty
		 * @throws IllegalArgumentException if front is null
		 * 
		 */
		public boolean remove(E element) {
			if (element == null) {
				return false;
			} else if (isEmpty()) {
				return false;
			} else if (front == null) {
				throw new IllegalArgumentException();
			} else {
				if (element.equals(front.data)) {
					front = front.next;
					size--;
					return true;
					}
			} 
			
			return front.remove(element);
			
		}
		
		/**
		 * Removes the element in the list at the specified index
		 * @param idx is the index where element should be removed
		 * @return the element is removed from list
		 * @throws IndexOutOfBoundsException is the index less than zero
		 * or greater than list size - 1
		 * @throws IllegalArgumentException if list is empty
		 * @throws IllegalArgumentException if front is null
		 * 
		 */
		public E remove(int idx) {
			E e = null;
			if (idx < 0 || idx > size - 1) {
				throw new IndexOutOfBoundsException();
			}
			
			if (front == null) {
				throw new IllegalArgumentException();
			}
			
			if (isEmpty()) {
				throw new IllegalArgumentException();
			}
			
			if (idx == 0) {
				e = front.data;
				front = front.next;
				size--;
				return e;
			}
			 
				
			return front.remove(idx);
		}
		
		/**
		 * Sets the given element at the given index and returns the 
		 * existing element if the list is not already empty
		 * @param idx is the index where new element should be placed
		 * @param element is the element to be placed
		 * @return element already at index
		 * @throws IndexOutOfBoundsException is the index less than zero
		 * or greater than list size - 1
		 * @throws NullPointerException if element is null
		 */
		public E set(int idx, E element) {

			if (element == null) {
				throw new NullPointerException();
			}
			
			if (idx < 0 || idx > size - 1) {
				throw new IndexOutOfBoundsException();
			}
			
			return front.set(idx, element);
		}
		
		/**
		 * Checks to see whether list contains specfied element
		 * @param element is element to be searched for in list
		 * @return true if list contains element, false otherwise
		 * @throws NullPointerException if element is null
		 * @throws IllegalArgumentException if list is empty
		 */
		public boolean contains(E element) {
			if (element == null) {
				throw new NullPointerException();
			}
			
			if (front == null) {
				throw new IllegalArgumentException();
			} else {
				return front.contains(element);
			}
		}	
		
		
		/**
		 * Inner class which provides functionality for ListNode
		 * @author aehandlo
		 *
		 */
		private class ListNode {
			
			public E data;
			public ListNode next;
			
			/**
			 * Constructor for ListNode
			 * @param data is the data element for the node
			 * @param next is the link element for the node
			 */
			public ListNode(E data, ListNode next) {
				this.data = data;
				this.next = next;
			}
			

			/**
			 * Sets the given element at the given index and returns the 
			 * existing element if the list is not already empty
			 * @param idx is the index where new element should be placed
			 * @param element is the element to be placed
			 * @return element already at index
			 */
			public E set(int idx, E element) {
				E e = null;
				
				//don't allow duplicates
				if (front.contains(element)) {
					throw new IllegalArgumentException();
				}
				
				//special case, adding to front
				if (idx == 0) {
					e = front.data;
					front.data = element;
					return e;
				} else {
					idx--;
					if (idx == 0) {
						e = next.data;
						next.data = element;
						return e;
					} else {
						return next.set(idx, element);
					}
				}			
			}


			/**
			 * Adds the element to the LinkedListRecursive at the given index
			 * @param element is the element to be added 
			 * @param idx is the index where the element should be added
			 */
			public void add(int idx, E element) {
				idx--;
				// if idx = 0, the next index is what we want to add to
				if (idx == 0) {
					next = new ListNode(element, next);
					size++;
				} else {
					next.add(idx, element);
				}
				
			}


			/**
			 * Checks to see whether list contains specfied element
			 * @param element is element to be searched for in list
			 * @return true if list contains element, false otherwise
			 * @throws NullPointerException if element is null
			 * @throws IllegalArgumentException if list is empty
			 */
			public boolean contains(E element) {
				if (this.data.equals(element)) {
					return true;
				} else if (next == null) {
					return false;
				} else {
					return next.contains(element);
				}

			}

			/**
			 * Adds the element to the LinkedListRecursive
			 * @param element is the element to be added 
			 * @return true if the element was added at the index,
			 * false otherwise
			 */
			public boolean add(E element) {
				
				if (next == null) {
					next = new ListNode(element, next);
					size++;
					return true;
				} else {
					return next.add(element);
				}
					
			}
			
			/**
			 * Returns the element at the given index
			 * @param idx is the index of the element to be returned
			 * @return element at specified index
			 * 
			 */
			public E get(int idx) {
				if (idx == 0) {
					return this.data;
				} else {
					idx--;
					return next.get(idx);
				}
			}
			
			/**
			 * Removes the first occurence of the given element in the list
			 * @param element is the element to be removed
			 * @return true if element is removed from list, false oter
			 * 
			 */
			public boolean remove(E element) {
				if (next == null) {
					return false;
				} else if (next.data.equals(element)) {
					next = next.next;
					size--;
					return true; 
				} else {
					return next.remove(element);
				}
			}
			
			/**
			 * Removes the element in the list at the specified index
			 * @param idx is the index where element should be removed
			 * @return the element is removed from list
			 */
			public E remove(int idx) {
				E e = null;
				idx--;
				if (idx == 0) {
					e = next.data;
					next = next.next;
					size--;
					return e;
				} else {
					return next.remove(idx);	
				}
			}
		}
	}

}


