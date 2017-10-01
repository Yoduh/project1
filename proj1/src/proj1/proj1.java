/**
 * 
 */
package proj1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This program compresses and decompresses text files using a linked list 
 * that implements a move-to-front heuristic. The program will ask through 
 * the console for a file and then print the output to console, or
 * input/output redirection can be used instead.
 * @author aehandlo
 *
 */
public class proj1 {

	/** Linked list of Strings that holds all unique words from an input file */
	public LinkedListRecursive<String> wordList = new LinkedListRecursive<String>();
	/** mode = 0 if compressing. mode = 1 if decompressing */
	public int mode;
	/** Used for counting number of bytes (characters) in the input file */
	public int inputBytes = 0;
	/** Used for counting number of bytes (characters) in the output file */
	public int outputBytes = 0;
	
	/**
	 * main method that starts the program
	 * @param args Command line arguments
	 * @throws IOException if an I/O error occurs
	 */
	public static void main(String[] args) throws IOException {
		proj1 obj = new proj1();
		obj.processFile();
	}
	
	/**
	 * Opens/loops through input file and opens second file for writing. 
	 * Also determines if program needs to compress or decompress.
	 * @throws IOException if an I/O error occurs
	 */
	public void processFile() throws IOException {
		//prepare input and output streams
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		if(!br.ready()) {
			System.out.println("Enter a filename (e.g. \"filename.txt\"): ");
			File fileName = new File(br.readLine());
			br = new BufferedReader(new FileReader(fileName));
		}
		BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(System.out));
		
		//determine if we are compressing or decompressing
		String firstLine = br.readLine();
		if((int) firstLine.charAt(0) == 48) {
			//we are decompressing. skip over the "0 " at the start
			firstLine = firstLine.replaceFirst("^0 ", "");
			mode = 1;
		} else {
			//we are compressing. write "0 " at the start of output
			fileWriter.write("0 ");
			mode = 0;
		}
		
		//process the first line of text
		processLine(firstLine, fileWriter);
		fileWriter.newLine();
		
		//loop through the rest of the input file line by line
		String nextLine = br.readLine();
	    while (nextLine != null) {
        	//break early if last line is compression statistics
        	if(!nextLine.isEmpty() && nextLine.charAt(0) == '0') {
        		break;
        	}
        	
        	processLine(nextLine, fileWriter);
        	fileWriter.newLine();
	        nextLine = br.readLine();
	    }
	    
	    //if compressing, add compression statistics
	    if(mode == 0) {
	    	fileWriter.write("0 Uncompressed: " + inputBytes 
	    			+ " bytes;  Compressed: " + outputBytes + " bytes");
	    }
	    
	    //close input and output streams
	    br.close();
	    fileWriter.close();
	}
	
	/**
	 * Process single line of text. Each character is scanned one at a time until
	 * the program is sure an entire word has been found, then the word is sent
	 * to be further processed.
	 * @param line Line of text to process
	 * @param fileWriter BufferedWriter in charge of writing to file
	 * @throws IOException if an I/O error occurs
	 */
	public void processLine(String line, BufferedWriter fileWriter) throws IOException {
		inputBytes += line.length();
		//prepare line scanner to scan character by character (but still of type String)
		Scanner lineReader = new Scanner(line);
		lineReader.useDelimiter("");
		//holds characters until whole word is found
		ArrayList<String> wordArray = new ArrayList<String>();
		//needed to figure out index when reading in numbers 1 digit at a time
		int idx = 0;
		
		while(lineReader.hasNext()) {
			String c = lineReader.next();
			
			//if character is a letter then add to array as we build a whole word
			if(Character.isLetter(c.charAt(0))) {
				wordArray.add(c);
			//else if a digit, calculate index as digits are being read in 1 at a time
			} else if(Character.isDigit(c.charAt(0))) {
				idx = idx * 10  + Character.getNumericValue(c.charAt(0));
			//else we have reached a special character
			} else {
				//if we were previously reading in a word then process the word
				if(!wordArray.isEmpty()) {
					processWord(wordArray, fileWriter);
					wordArray = new ArrayList<String>();
				}
				//if we were previously reading in a number then process the number
				if(idx != 0) {
					processNumber(idx, fileWriter);
					idx = 0;
				}
				//write the special character
				outputBytes += c.length();
				fileWriter.write(c);
			}
			
			//if at end of line but haven't processed word yet (no special character), do so now
			if(!lineReader.hasNext()) {
				if(!wordArray.isEmpty()) {
					processWord(wordArray, fileWriter);
					wordArray = new ArrayList<String>();
				} else if(idx != 0){
					processNumber(idx, fileWriter);
					idx = 0;
				}
			}
		}
		lineReader.close();
	}
	
	/**
	 * Processes single word from input file. Adds the word to the linked list
	 * if it is not in the list yet, otherwise writes index of existing word
	 * to output file then moves word to front of the linked list
	 * @param wordArray Word to be processed
	 * @param fileWriter BufferedWriter in charge of writing to file
	 * @throws IOException if an I/O error occurs 
	 */
	public void processWord(ArrayList<String> wordArray, BufferedWriter fileWriter) throws IOException {
		//word from ArrayList is now a single String
		String word = String.join("",  wordArray);
		//index of word in linked list
		int search = 0;
		
		//try to find index of word already in linked list
		try {
			search = wordList.contains(word);
		//special case: list is currently empty.
		} catch (IllegalArgumentException e) {
			wordList.add(word);
			outputBytes += word.length();
			fileWriter.write(word);
			return;
		}
		
		//new word found. add to linked list, count bytes, write to output.
		if(search < 0) {
			wordList.add(word);
			outputBytes += word.length();
			fileWriter.write(word);
		//existing word found. move to front, count bytes, write index to output.
		} else {
			wordList.remove(word);
			wordList.add(word);
			outputBytes += (int) (Math.log10(search + 1)) + 1;
			fileWriter.write(String.valueOf(search + 1));
		}
	}
	
	/**
	 * Processes number from input file. Finds word that matches index of the digit
	 * and prints that word to the output file then moves the word to the front of the list.
	 * @param idx Index of the word to find in the linked list.
	 * @param fileWriter BufferedWriter in charge of writing to file
	 * @throws IOException if an I/O error occurs
	 */
	public void processNumber(int idx, BufferedWriter fileWriter) throws IOException {
		//subtract 1 because of 0 based indexing
		idx--;
		String word = wordList.get(idx);
		fileWriter.write(word);
		wordList.remove(idx);
		wordList.add(word);
	}
	
	
	/**
	 * Provides custom implementation of a (mostly) recursive
	 * linked list that does not allow for null or duplicate
	 * elements as defined by the equals method. This is a
	 * modified class I originally authored for a CSC 216
	 * project!
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
		 * Adds the element to the front of LinkedListRecursive
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
				front = new ListNode(element, front);
				size++;
				return true;
			
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