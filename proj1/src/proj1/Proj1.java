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
 * Currently done (compression things):
 * 1) reading and writing from and to files
 * 2) line by line processing
 * 3) separating words from punctuation
 * 4) processing occurrence of words into linked list
 * 
 * TO-DO:
 * 1) decompression
 * 2) prepending and appending "0 " to output file
 * 3) appending compression summary to end of output file
 * 4) adding logic to determine if file is to be compressed/decompressed based on "0 " flag
 * 5) change back to accepting System.in filenames.
 * 6) move code into more methods for better readability
 * @author aehandlo
 *
 */
public class Proj1 {

	public LinkedListRecursive<String> fileText = new LinkedListRecursive<String>();

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Proj1 obj = new Proj1();
		//create files and scanners to be used by the program
		Scanner in = new Scanner(System.in);
		//System.out.println("Enter a filename (e.g. \"filename.txt\"): ");
		//System.getProperty("user.dir") + "\\" + in.next()
		FileInputStream fileName = new FileInputStream("small.txt");
		PrintStream fileWriter = new PrintStream(new File("small_compressed.txt"));
		Scanner fileReader = new Scanner(fileName);
		
		//loop through input file, processing line by line
	    while (fileReader.hasNextLine()) {
	        try {
	        	//process each line character by character
	        	obj.processLine(fileReader.nextLine(), fileWriter);
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
	 * @param fileWriter PrintStream in charge of writing to file
	 */
	public void processLine(String line, PrintStream fileWriter) {
		Scanner lineReader = new Scanner(line);
		//change Scanner to scan every character
		lineReader.useDelimiter("");
		ArrayList<String> wordArray = new ArrayList<String>();
		
		while(lineReader.hasNext()) {
			String c = lineReader.next();
			//if character is a letter then add to array as we build a whole word. Don't excluse apostrophes
			if(Character.isLetter(c.charAt(0)) || c.charAt(0) == '\'') {
				wordArray.add(c);
				//need to process word before next line
				if(!lineReader.hasNext()) {
					//process the word via linked list
					processWord(wordArray, fileWriter);
					wordArray = new ArrayList<String>();
				}
			} else {
				//else write non-letter character and process the word and prepare to begin a new word on same line
				if(!wordArray.isEmpty()) {
					//process the word via linked list
					processWord(wordArray, fileWriter);
					wordArray = new ArrayList<String>();
				}
				fileWriter.print(c);
			}
			
			
		}
	}
	
	/**
	 * Process single word from input file. Add the word to the linked list
	 * if it is not in the list yet, otherwise write index of existing word
	 * to output file then move word to front of the linked list
	 * @param wordArray Word to be processed
	 * @param fileWriter PrintStream in charge of writing to file
	 */
	public void processWord(ArrayList<String> wordArray, PrintStream fileWriter) {
		String word = String.join("",  wordArray);
		int search = 0;
		try {
			search = fileText.contains(word);
		} catch (IllegalArgumentException e) {
			//special case: list is empty.
			fileText.add(0, word);
			fileWriter.print(word);
			return;
		}
		//new word found
		if(search < 0) {
			fileText.add(0, word);
			fileWriter.print(word);
		//existing word found
		} else {
			fileText.remove(word);
			fileText.add(0, word);
			//add 1 because of 0 based indexing
			fileWriter.print(search + 1);
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
		private int index;
		
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
				index = 0;
				if (front.contains(element, index) >= 0) {
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
			index = 0;
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
			else if (front.contains(element, index) >= 0) {
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
		 * Checks to see whether list contains specified element
		 * @param element is element to be searched for in list
		 * @return true if list contains element, false otherwise
		 * @throws NullPointerException if element is null
		 * @throws IllegalArgumentException if list is empty
		 */
		public int contains(E element) {
			if (element == null) {
				throw new NullPointerException();
			}
			
			if (front == null) {
				throw new IllegalArgumentException();
			} else {
				index = 0;
				return front.contains(element, index);
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
			public int index;
			
			/**
			 * Constructor for ListNode
			 * @param data is the data element for the node
			 * @param next is the link element for the node
			 */
			public ListNode(E data, ListNode next) {
				this.data = data;
				this.next = next;
				this.index = 0;
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
			 * Checks to see whether list contains specified element
			 * @param element is element to be searched for in list
			 * @return true if list contains element, false otherwise
			 * @throws NullPointerException if element is null
			 * @throws IllegalArgumentException if list is empty
			 */
			public int contains(E element, int index) {
				if (this.data.equals(element)) {
					return index;
				} else if (next == null) {
					return -1;
				} else {
					index++;
					return next.contains(element, index);
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


