package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;
import java.util.NoSuchElementException;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire
 * Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {

	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;

	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a
	 * circular linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i = 0; i < cardValues.length; i++) {
			cardValues[i] = i + 1;
		}

		// shuffle the cards
		Random randgen = new Random();
		for (int i = 0; i < cardValues.length; i++) {
			int other = randgen.nextInt(28);
			int temp = cardValues[i];
			cardValues[i] = cardValues[other];
			cardValues[other] = temp;
		}

		// create a circular linked list from this deck and make deckRear point
		// to its last node
		CardNode cn = new CardNode();
		cn.cardValue = cardValues[0];
		cn.next = cn;
		deckRear = cn;
		for (int i = 1; i < cardValues.length; i++) {
			cn = new CardNode();
			cn.cardValue = cardValues[i];
			cn.next = deckRear.next;
			deckRear.next = cn;
			deckRear = cn;
		}
	}

	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
			cn.cardValue = scanner.nextInt();
			cn.next = cn;
			deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
			cn.cardValue = scanner.nextInt();
			cn.next = deckRear.next;
			deckRear.next = cn;
			deckRear = cn;
		}
	}

	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	void jokerA() {

		// System.out.println("Original: ");
		// CardNode print = deckRear.next;
		// do {
		// System.out.print(print.cardValue + " ");
		// print = print.next;
		// } while (print != deckRear.next);

		if (deckRear == null) {
			return;
		}
		if (deckRear == deckRear.next) {
			return;
		}

		CardNode ptr = deckRear.next, ptrafter = ptr.next;
		do {
			if (ptr.cardValue == 27) {
				ptr.cardValue = ptrafter.cardValue;
				ptrafter.cardValue = 27;
				break;
			}
			ptr = ptr.next;
			ptrafter = ptrafter.next;
		} while (ptr != deckRear.next);

		// System.out.println();
		// System.out.println("JOKER A: ");
		// print = deckRear.next;
		// do {
		// System.out.print(print.cardValue + " ");
		// print = print.next;
		// } while (print != deckRear.next);

	}

	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() {
		if (deckRear == null) {
			return;
		}
		if (deckRear == deckRear.next || deckRear == deckRear.next.next) {
			return;
		}

		CardNode ptr = deckRear.next, ptrafter = ptr.next, ptr2after = ptrafter.next;
		do {
			if (ptr.cardValue == 28) {
				int tmp = ptrafter.cardValue;
				ptrafter.cardValue = ptr2after.cardValue;
				ptr2after.cardValue = 28;
				ptr.cardValue = tmp;
				break;
			}
			ptr = ptr.next;
			ptrafter = ptrafter.next;
			ptr2after = ptr2after.next;
		} while (ptr != deckRear.next);

		// System.out.println();
		// System.out.println("JOKER B: ");
		// CardNode print = deckRear.next;
		// do {
		// System.out.print(print.cardValue + " ");
		// print = print.next;
		// } while (print != deckRear.next);

	}

	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		if (deckRear == null) {
			return;
		}
		CardNode ptr = deckRear.next;
		CardNode j1 = null, j2 = null;
		do {
			if (ptr.cardValue == 27 || ptr.cardValue == 28) {
				if (j1 == null) {
					j1 = ptr;
				} else {
					j2 = ptr;
					break;
				}
			}
			ptr = ptr.next;
		} while (ptr != deckRear.next);

		if ((j1 == deckRear.next && j2 == deckRear) || (j1 == deckRear && j2 == deckRear.next)) {
			return;
		} else if (j1 == deckRear.next && j2 != deckRear) {
			deckRear = j2;
		} else if (j2 == deckRear && j1 != deckRear.next) {
			CardNode newr = deckRear.next;
			do {
				if (newr.next == j1) {
					break;
				}
				newr = newr.next;
			} while (newr != deckRear.next);
			deckRear = newr;
		} else {
			CardNode newr = deckRear.next;
			do {
				if (newr.next == j1) {
					break;
				}
				newr = newr.next;
			} while (newr != deckRear.next);

			CardNode front = deckRear.next;
			deckRear.next = j1;
			CardNode newf = j2.next;
			j2.next = front;
			deckRear = newr;
			newr.next = newf;
		}

		// System.out.println();
		// System.out.println("Triple Cut: ");
		// CardNode print = deckRear.next;
		// do {
		// System.out.print(print.cardValue + " ");
		// print = print.next;
		// } while (print != deckRear.next);
	}

	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() {
		int cut = deckRear.cardValue;
		if (cut == 28) {
			cut = 27;
		}
		CardNode front = deckRear.next, ptr = deckRear;
		for (int i = 0; i < cut; i++) {
			ptr = ptr.next;
		}

		CardNode b4last = deckRear.next;
		while (b4last.next != deckRear) {
			b4last = b4last.next;
		}

		if (ptr != b4last) {

			CardNode newf = ptr.next;
			b4last.next = front;
			ptr.next = deckRear;
			deckRear.next = newf;

		}

		// System.out.println();
		// System.out.println("Count Cut: ");
		// CardNode print = deckRear.next;
		// do {
		// System.out.print(print.cardValue + " ");
		// print = print.next;
		// } while (print != deckRear.next);

	}

	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count
	 * Cut, then counts down based on the value of the first card and extracts
	 * the next card value as key. But if that value is 27 or 28, repeats the
	 * whole process (Joker A through Count Cut) on the latest (current) deck,
	 * until a value less than or equal to 26 is found, which is then returned.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {
		jokerA();
		jokerB();
		tripleCut();
		countCut();

		int k = deckRear.next.cardValue;
		if (k == 28) {
			k = 27;
		}

		CardNode key = deckRear;
		for (int i = 0; i < k; i++) {
			key = key.next;
		}

		key = key.next;
		int kvalue = key.cardValue;
		if (kvalue == 27 || kvalue == 28) {

			// System.out.println();
			// System.out.println("invalid key: " + kvalue);

			return getKey();
		}

		// System.out.println();
		// System.out.println("key: " + kvalue);
		return kvalue;
	}

	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear
	 *            Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) {
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message
	 *            Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {
		String s = "";
		int size = message.length();
		for (int i = 0; i < size; i++) {
			if (Character.isLetter(message.charAt(i))) {
				s = s + message.charAt(i);
			}
		}
		s = s.toUpperCase();

		// System.out.println("Modified message: " + s);
		// jokerA();
		// jokerB();
		// tripleCut();
		// countCut();

		String emes = "";
		int len = s.length();
		for (int i = 0; i < len; i++) {
			int v = s.charAt(i) - 'A' + 1;
			v = v + getKey();
			if (v > 26) {
				v = v - 26;
			}
			char j = (char) (v - 1 + 'A');
			emes = emes + j;
		}

		// System.out.println();
		// System.out.println(emes);

		return emes;
	}

	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message
	 *            Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {
		String dmes = "";
		int len = message.length();
		for (int i = 0; i < len; i++) {
			int v = message.charAt(i) - 'A' + 1;
			v = v - getKey();
			if (v < 0) {
				v = v + 26;
			}
			char j = (char) (v - 1 + 'A');
			dmes = dmes + j;
		}

		// System.out.println();
		// System.out.println(dmes);

		return dmes;
	}
}
