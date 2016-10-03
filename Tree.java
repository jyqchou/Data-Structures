package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode,
 * with fields for tag/text, first child and sibling.
 * 
 */
public class Tree {

	/**
	 * Root node
	 */
	TagNode root = null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;

	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc
	 *            Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	/**
	 * Builds the DOM tree from input HTML file. The root of the tree is stored
	 * in the root field.
	 */
	public void build() {
		Stack<TagNode> dom = new Stack<TagNode>();
		TagNode ptr = null;
		String s = null;
		boolean pt = false;

		while (sc.hasNext()) {
			s = sc.nextLine();

			if (root == null) {
				// System.out.println("root = null");
				root = new TagNode(removeBrackets(s), null, null);
				ptr = root;
				dom.push(ptr);
				pt = true;
				// System.out.println(root.tag);
			} else {
				if (isTag(s)) {
					// System.out.println(s + "is a tag");
					s = removeBrackets(s);
					if (s.charAt(0) == '/') {
						while (!dom.peek().tag.equals(s.substring(1))) {
							dom.pop();
						}
						ptr = dom.peek();
						pt = true;
					} else {
						// System.out.println("went into else statement");
						if (pt == true && ptr.firstChild == null) {
							ptr.firstChild = new TagNode(s, null, null);
							ptr = ptr.firstChild;
							pt = true;
							// System.out.println("ptr's first child is: " +
							// ptr);
						} else {
							ptr.sibling = new TagNode(s, null, null);
							// System.out.println("ptr: " + ptr);
							ptr = ptr.sibling;
							pt = true;
							// System.out.println("ptr's sibling is: " + ptr);
						}
						dom.push(ptr);
					}
				} else { // s is not a Tag
					// System.out.println(s + "is not a tag");
					if (pt == true && ptr.firstChild == null) {
						ptr.firstChild = new TagNode(s, null, null);
						ptr = ptr.firstChild;
						pt = false;
						// System.out.println("ptr's first child is: " + ptr);
					} else {
						ptr.sibling = new TagNode(s, null, null);
						ptr = ptr.sibling;
						pt = false;
						// System.out.println("ptr's sibling is: " + ptr);
					}
					dom.push(ptr);
				}

				// System.out.println("ptr: " + ptr.tag);

			}
		}

	}

	private static String removeBrackets(String s) {
		String n = "";
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '<' || s.charAt(i) == '>') {
				continue;
			} else {
				n += s.charAt(i);
			}
		}
		return n;
	}

	private static boolean isTag(String t) {
		if (!t.contains("<") || !t.contains(">")) {
			return false;
		} else {
			String s = removeBrackets(t);
			if (s.equals("html") || s.equals("body") || s.equals("p") || s.equals("em") || s.equals("b")
					|| s.equals("table") || s.equals("tr") || s.equals("td") || s.equals("ol") || s.equals("ul")
					|| s.equals("li")) {
				return true;
			} else if (s.equals("/html") || s.equals("/body") || s.equals("/p") || s.equals("/em") || s.equals("/b")
					|| s.equals("/table") || s.equals("/tr") || s.equals("/td") || s.equals("/ol") || s.equals("/ul")
					|| s.equals("/li")) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag
	 *            Old tag
	 * @param newTag
	 *            Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		rrep(root, oldTag, newTag);
	}

	private void rrep(TagNode ptr, String oldTag, String newTag) {
		if (ptr == null) {
			return;
		}
		if (ptr.tag.equals(oldTag)) {
			ptr.tag = newTag;
		}
		rrep(ptr.firstChild, oldTag, newTag);
		rrep(ptr.sibling, oldTag, newTag);
	}

	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The
	 * boldface (b) tag appears directly under the td tag of every column of
	 * this row.
	 * 
	 * @param row
	 *            Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		rbold(root, row);
	}

	private void rbold(TagNode ptr, int row) {
		if (ptr == null) {
			return;
		}
		if (ptr.tag.equals("table")) {
			int c = 0;
			TagNode p = ptr.firstChild;
			while (p != null) {
				if (p.tag.equals("tr")) {
					c++;
				}
				if (c == row) {
					p.tag = "b";
					p.firstChild = new TagNode("tr", p.firstChild, null);
				}
				p = p.sibling;
			}
		}
		rbold(ptr.firstChild, row);
		rbold(ptr.sibling, row);
	}

	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em,
	 * or b, all occurrences of the tag are removed. If the tag is ol or ul,
	 * then All occurrences of such a tag are removed from the tree, and, in
	 * addition, all the li tags immediately under the removed tag are converted
	 * to p tags.
	 * 
	 * @param tag
	 *            Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		if (tag.equals("p") || tag.equals("em") || tag.equals("b")) {
			rremove1(root, tag);
		} else if (tag.equals("ol") || tag.equals("ul")) {
			rremove2(root, tag);
		} else {
			return;
		}
	}

	private void rremove1(TagNode ptr, String tag) {
		if (ptr == null) {
			return;
		}
		if (ptr.tag.equals(tag)) {
			ptr.tag = ptr.firstChild.tag;
			TagNode x = ptr.firstChild;
			while (x.sibling != null) {
				x = x.sibling;
			}
			x.sibling = ptr.sibling;
			if (ptr.firstChild.sibling != null) {
				ptr.sibling = ptr.firstChild.sibling;
			}
			ptr.firstChild = ptr.firstChild.firstChild;
		}
		rremove1(ptr.firstChild, tag);
		rremove1(ptr.sibling, tag);
	}

	private void rremove2(TagNode ptr, String tag) {
		if (ptr == null) {
			return;
		}
		if (ptr.tag.equals(tag)) {
			if (ptr.firstChild.tag.equals("li")) {
				ptr.firstChild.tag = "p";
			}
			ptr.tag = ptr.firstChild.tag;
			TagNode x = ptr.firstChild;
			System.out.println("found tag, childNode: " + ptr);
			while (x.sibling != null) {
				x = x.sibling;
				System.out.println("sibling: " + ptr);
				if (x.tag.equals("li")) {
					x.tag = "p";
					System.out.println("changed li to p");
				}
			}
			x.sibling = ptr.sibling;
			ptr.sibling = ptr.firstChild.sibling;
			System.out.println();
			ptr.firstChild = ptr.firstChild.firstChild;
		}
		rremove2(ptr.firstChild, tag);
		rremove2(ptr.sibling, tag);
	}

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word
	 *            Word around which tag is to be added
	 * @param tag
	 *            Tag to be added
	 */
	public void addTag(String word, String tag) {
		radd(root, word, tag);
	}

	private void radd(TagNode ptr, String origword, String tag){
		if (ptr == null){
			return;
		}
		String word = origword.toLowerCase();
		if (ptr.tag.toLowerCase().contains(word)){
			TagNode sib = ptr.sibling;
			String q = ptr.tag;
			String[] arr = q.split(" ");
			int size = arr.length;
			String nottag = null;
			for (int i = 0; i < size; i ++){ 
				String fword = arr[i].toLowerCase();
				if ((fword.equals(word)) || (fword.substring(0,word.length()).equals(word) && !Character.isLetter(fword.charAt(word.length())) && fword.length() == (word.length() + 1))){
					if (nottag == null){
						ptr.tag = tag;
						ptr.firstChild = new TagNode (arr[i], null, null);
						nottag = "";
					} else if (nottag.equals("")){
						TagNode addedt = new TagNode(arr[i],null,null);
						ptr.sibling = new TagNode (tag, addedt, null);
						ptr = ptr.sibling;
					} else {
						if (ptr.tag.equals(q)){
							ptr.tag = nottag;
							TagNode addedt = new TagNode(arr[i], null, null);
							ptr.sibling = new TagNode(tag, addedt, null);
							ptr = ptr.sibling;
							nottag = "";
						} else {
							ptr.sibling = new TagNode (nottag, null, null);
							ptr = ptr.sibling;
							TagNode addedt = new TagNode(arr[i], null, null);
							ptr.sibling = new TagNode(tag, addedt, null);
							ptr = ptr.sibling;
							nottag = "";
						}
					}
				} else {
					if (nottag == null){
						nottag = "";
					}
					nottag += arr[i] + " ";
				}
			}
			if (!nottag.equals("")){
				if (!ptr.tag.equals(nottag.substring(0,(nottag.length()-1)))){
					ptr.sibling = new TagNode(nottag, null, null);
					ptr = ptr.sibling;
				}
			}
			ptr.sibling = sib;
			radd(ptr.sibling,word,tag);
		} else {
		radd(ptr.firstChild,word,tag);
		radd(ptr.sibling,word,tag);
		}
	}

	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the input
	 * file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines.
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}

	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");
			}
		}
	}

}
