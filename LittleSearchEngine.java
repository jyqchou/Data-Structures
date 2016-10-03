package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
//		System.out.println("KeywordsIndex HashMap: ");
//		for (String s : keywordsIndex.keySet()){
//			if (s != null) {
//				String key = s.toString();
//				ArrayList<Occurrence> occs = keywordsIndex.get(s);
//				String st = "";
//				for (int i=0; i<occs.size(); i++) {
//					st = st + occs.get(i).toString();
//				}
//				System.out.println("Key: " + key + "Occurrences: " + st);
//			}
//		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		
		HashMap<String, Occurrence> keys = new HashMap<String, Occurrence>();
		try{
			Scanner scanner = new Scanner(new File(docFile));
			scanner.close();
		} catch (FileNotFoundException e){
			return keys;
		}
		
		Scanner scanner = new Scanner(new File(docFile));
		while (scanner.hasNext()){
			String line = scanner.nextLine();
			if (line != null && !line.trim().isEmpty()){
				String[] tokens = line.split(" ");
				for (int i=0; i<tokens.length; i++){
					String kword = getKeyWord(tokens[i]);
					if (kword != null){
						if (!keys.containsKey(kword)){
							Occurrence newoccur = new Occurrence (docFile, 1);
							keys.put(kword, newoccur);;
						} else {
							keys.get(kword).frequency++;
						}
					}
				}
			}
			
		}
		scanner.close();
		
		return keys;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		for (String s : kws.keySet()){
			if (!keywordsIndex.containsKey(s)){
				ArrayList<Occurrence> tmp = new ArrayList<Occurrence>();
				tmp.add(kws.get(s));
				keywordsIndex.put(s, tmp);
			} else {
				keywordsIndex.get(s).add(kws.get(s));
				insertLastOccurrence(keywordsIndex.get(s));
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		
		String keyw = "";
		String okey = word;
		for (int i=0; i<word.length(); i++){
			if (!Character.isLetter(word.charAt(i))){ // if special character found
				if (word.charAt(i) != '.' && word.charAt(i) != ',' && word.charAt(i) != '?' && word.charAt(i) != ':' && word.charAt(i) != ';' && word.charAt(i) != '!'){
					return null; // if special character is not punctuation 
				}
				keyw = word.substring(0,i);
				for (int j=i; j<word.length(); j++){ 
					if (word.charAt(j) != '.' && word.charAt(j) != ',' && word.charAt(j) != '?' && word.charAt(j) != ':' && word.charAt(j) != ';' && word.charAt(j) != '!'){
						return null; // if special character is found after punctuation
					}
					if (Character.isLetter(word.charAt(j))){ // if letters are found after punctuation
						return null;
					}
				}
			break;
			}
		}
		
		if (keyw == ""){ // if no punctuation found
			keyw = okey;
		}
		
		String keyword = keyw.toLowerCase();
		if (noiseWords.get(keyword) != null){ // if keyword is found in noisewords.txt
			return null;
		}
		
		return keyword;
		
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
		
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {

		if (occs.size() <= 1 || occs == null) {
			return null;
		}

		ArrayList<Integer> ints = new ArrayList<Integer>();

		Occurrence o = occs.get(occs.size() - 1);
		int v = o.frequency;
		int left = 0;
		int right = occs.size() - 2;

		while (left <= right) {
			int mid = (left + right) / 2;
			ints.add(mid);
			if (occs.get(mid).frequency == v) {
				break;
			} else if (occs.get(mid).frequency < v) {
				right = mid - 1;
			} else if (occs.get(mid).frequency > v) {
				left = mid + 1;
			}
		}

		if (ints.size() == 0) {

//			for (int i = 0; i < ints.size() - 1; i++) {
//				System.out.print(ints.get(i) + " ");
//			}

			return ints;
		}

		int ipos = ints.get(ints.size() - 1); // get last midpoint
		if (occs.get(ipos).frequency > v)
			ipos++;
		for (int i = occs.size() - 1; i > ipos; i--) {
			occs.set(i, occs.get(i - 1));
		}
		occs.set(ipos, o);

//		for (int i = 0; i < ints.size() - 1; i++) {
//			System.out.print(ints.get(i) + " ");
//		}

		return ints;

	}
	

	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<Occurrence> list1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> list2 = keywordsIndex.get(kw2);
		ArrayList<String> result = new ArrayList<String>();
		int c = 0, s1 = 0, s2 = 0;
		
		if (list1 == null && list2 == null)
			return null;
		
		if (list1 == null || list2 == null){
			if (list1 == null){
				while (c<5 && s2<list2.size()){
					result.add(list2.get(s2).document);
					c++; s2++;
				}
			}	
			if (list2 == null){
				while (c<5 && s1<list1.size()){
					result.add(list1.get(s1).document);
					c++; s1++;
				}
			}
			if (result.size() == 0)
				return null;
			return result;
		}
			
		while (s1<list1.size() && s2<list2.size() && c<5){
			if (result.contains(list1.get(s1).document)){
				s1++;
				continue;
			}
			if (result.contains(list2.get(s2).document)){
				s2++;
				continue;
			}
			
			int f1 = list1.get(s1).frequency, f2 = list2.get(s2).frequency;
			if (f1 > f2){
				result.add(list1.get(s1).document);
				s1++;
				c++;
			} else if (f2 > f1){
				result.add(list2.get(s2).document);
				s2++;
				c++;
			} else { // frequencies are equal, add document in first list
				result.add(list1.get(s1).document);
				s1++;
				c++;
			}
		}
		
		return result;
	}
}
