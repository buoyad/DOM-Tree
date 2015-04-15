package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {

	/**
	 * Root node
	 */
	TagNode root=null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;

	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	public void build() {
		Stack<TagNode> main = new Stack<TagNode>();
		Stack<TagNode> sub = new Stack<TagNode>();
		while(sc.hasNext()) {
			String line = sc.nextLine();
			if (line.charAt(0) == '<' && line.charAt(1) != '/') {
				TagNode tmp = new TagNode(line.substring(1, line.length() - 1), null, null);
				main.push(tmp);
			}
			else if (line.charAt(0) != '<') {
				TagNode tmp = new TagNode(line, null, null);
				main.push(tmp);
			}
			else if (line.charAt(0) == '<' && line.charAt(1) == '/') {
				String endTag = line.substring(2, line.length() - 1);
				while(!main.isEmpty() && !main.peek().tag.equals(endTag)) {
					sub.push(main.pop());
				}
				TagNode subRoot = main.pop();
				subRoot.firstChild = sub.pop();
				TagNode sibling = subRoot.firstChild;
				while(!sub.isEmpty()) {
					sibling.sibling = sub.pop();
					sibling = sibling.sibling;
				}
				main.push(subRoot);
			}
		}
		this.root = main.pop();
	}


	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		replaceTag(oldTag, newTag, root);
	}

	private void replaceTag(String oldTag, String newTag, TagNode sub) {
		if (sub.sibling != null) {
			if (sub.sibling.tag.equals(oldTag)) sub.sibling.tag = newTag;
			replaceTag(oldTag, newTag, sub.sibling);
		}
		if(sub.firstChild != null) {
			if (sub.firstChild.tag.equals(oldTag)) sub.firstChild.tag = newTag;
			replaceTag(oldTag, newTag, sub.firstChild);
		}
	}

	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		TagNode c = root;
		c = c.firstChild;
		c = c.firstChild;
		while(c != null) {
			if(c.tag.equals("table")) break;
			c = c.sibling;
		}
		if(c == null) return;
		c = c.firstChild;
		for (int i = 1; i<row; i++) {
			if (c == null) return;
			c = c.sibling;
		}
		if(c == null) return;
		c = c.firstChild;
		while(c != null) {
			c.firstChild = new TagNode("b",c.firstChild, null);
			c = c.sibling;
		}
	}

	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		boolean complex;
		if (tag.equals("p") || tag.equals("b") || tag.equals("em")) complex = false;
		else complex = true;
		removeTag(tag, root, complex);
	}

	private void removeTag(String tag, TagNode sub, boolean complex) {
		if (sub.sibling != null) {
			if (sub.sibling.tag.equals(tag)) {
				if (!complex) simple(sub, tag);
				if (complex) complex(sub, tag);
			}
			removeTag(tag, sub.sibling, complex);
		}
		if(sub.firstChild != null) {
			if (sub.firstChild.tag.equals(tag)) {
				if (!complex) simple(sub, tag);
				if (complex) complex(sub, tag);
			}
			removeTag(tag, sub.firstChild, complex);
		}
	}

	private void simple(TagNode c, String tag) {
		if(c.firstChild != null && c.firstChild.tag.equals(tag)) {
			TagNode sib = c.firstChild.sibling;
			TagNode ptr = c.firstChild.firstChild;
			while (ptr.sibling != null) ptr = ptr.sibling;
			c.firstChild = c.firstChild.firstChild;
			ptr.sibling = sib;
		}
		if(c.sibling != null && c.sibling.tag.equals(tag)) {
			TagNode sib = c.sibling.sibling;
			TagNode ptr = c.sibling.firstChild;
			while (ptr.sibling != null) ptr = ptr.sibling;
			c.sibling = c.sibling.firstChild;
			ptr.sibling = sib;
		}
	}

	private void complex(TagNode c, String tag) {
		if(c.firstChild != null && c.firstChild.tag.equals(tag)) {
			TagNode sib = c.firstChild.sibling;
			TagNode ptr = c.firstChild.firstChild;
			while(ptr.sibling != null) {
				ptr.tag = "p";
				ptr = ptr.sibling;
			}
			ptr.tag = "p";
			c.firstChild = c.firstChild.firstChild;
			ptr.sibling = sib;
		}
		if(c.sibling != null && c.sibling.tag.equals(tag)) {
			TagNode sib = c.sibling.sibling;
			TagNode ptr = c.sibling.firstChild;
			while(ptr.sibling != null) {
				ptr.tag = "p";
				ptr = ptr.sibling;
			}
			ptr.tag = "p";
			c.sibling = c.sibling.firstChild;
			ptr.sibling = sib;
		}
	}

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		addTag(word, tag, root);
	}

	private void addTag(String word, String tag, TagNode sub) {
		if (sub.sibling != null) {
			if (sub.sibling.firstChild == null) addTagSibling(word, tag, sub);
			addTag(word, tag, sub.sibling);
		}
		if (sub.firstChild != null) {
			if (sub.firstChild.firstChild == null) addTagFirstChild(word, tag, sub);
			addTag(word, tag, sub.firstChild);
		}
	}

	private void addTagSibling(String word, String tag, TagNode relative) {
		String wordLower = word.toLowerCase();
		//if (relative.tag.equals(tag)) return;
		String sample = relative.sibling.tag;
		String sampleLower = sample.toLowerCase();
		if(sampleLower.indexOf(wordLower) == -1) return;
		Stack<TagNode> nodesRev = new Stack<TagNode>();
		int start = 0;
		int startPrev = 0;
		boolean decrement = false;
		while (start != sample.length()) {
			if(!decrement) {
				startPrev = 0;
				startPrev += start;
			}
			System.out.println("Start pre: " + start);
			start = sampleLower.indexOf(wordLower, start);
			//if (decrement) {
			//startPrev -= word.length();
			//decrement = false;
			//}
			if (start == -1){
				//for (int i = decrement; i >0; i--) {
					//startPrev -= word.length();
				//}
				nodesRev.push(new TagNode(sample.substring(startPrev), null, null));
				break;
			}
			//else start += startPrev;
			System.out.println("startPrev: " + startPrev + " Start: " +  start + " String: " + sample);		
			if(taggable(word, sample, start)) {
				System.out.println("taggable start: " + start + "String: " + sample);
				int l = word.length();
				String before = sample.substring(startPrev, start);
				String surround;
				if(start + l != sample.length() && sample.charAt(start + l) != ' ') {
					System.out.println("yes puncuation");
					surround = sample.substring(start, start + l + 1);
					start = start + l + 1;
				}
				else {
					surround = sample.substring(start, start + l);
					start = start + l;
					System.out.println("no puncuation + new start = " + start);
				}
				if(!before.equals("")) nodesRev.push(new TagNode(before, null, null));
				TagNode baby = new TagNode(surround, null, null);
				TagNode newTag = new TagNode(tag, baby, null);
				nodesRev.push(newTag);
				System.out.println("Pushed new tag");
				decrement = false;
			}
			else {
				start += word.length();
				decrement = true;;
				System.out.println("Added to decrement " + decrement);
			}
			System.out.println("End while start: " + start);
		}
		Stack<TagNode> nodes = new Stack<TagNode>();
		while (!nodesRev.isEmpty()) nodes.push(nodesRev.pop());

		TagNode first = nodes.pop();
		TagNode ptr = first;
		while (!nodes.isEmpty()) {
			ptr.sibling = nodes.pop();
			ptr = ptr.sibling;
		}
		TagNode originalSibling = relative.sibling.sibling;
		ptr.sibling = originalSibling;
		relative.sibling = first;
	}

	private void addTagFirstChild(String word, String tag, TagNode relative) {
		String wordLower = word.toLowerCase();
		if (relative.tag.equals(tag)) return;
		String sample = relative.firstChild.tag;
		String sampleLower = sample.toLowerCase();
		if(sampleLower.indexOf(wordLower) == -1) return;
		Stack<TagNode> nodesRev = new Stack<TagNode>();
		int start = 0;
		int startPrev = 0;
		boolean decrement = false;
		while (start != sample.length()) {
			if (!decrement) {
				startPrev = 0;
				startPrev += start;
			}
			System.out.println("Start pre: " + start);
			start = sampleLower.indexOf(wordLower, start);
			if (start == -1){
				//for (int i = decrement; i > 0; i--) {
				//startPrev -= word.length();
				//}
				nodesRev.push(new TagNode(sample.substring(startPrev), null, null));
				break;
			}
			//else start += startPrev;
			System.out.println("startPrev: " + startPrev + " Start: " +  start + " String: " + sample);		
			if(taggable(word, sample, start)) {
				System.out.println("taggable start: " + start + "String: " + sample);
				int l = word.length();
				String before = sample.substring(startPrev, start);
				String surround;
				if(start + l != sample.length() && sample.charAt(start + l) != ' ') {
					System.out.println("no puncuation");
					surround = sample.substring(start, start + l + 1);
					start = start + l + 1;
				}
				else {
					surround = sample.substring(start, start + l);
					start = start + l;
					System.out.println("no puncuation + new start = " + start);
				}
				if(!before.equals("")) nodesRev.push(new TagNode(before, null, null));
				TagNode baby = new TagNode(surround, null, null);
				TagNode newTag = new TagNode(tag, baby, null);
				nodesRev.push(newTag);
				System.out.println("Pushed new tag");
				decrement = false;
			}
			else {
				start += word.length();
				decrement = true;
				System.out.println("added to decrement " + decrement);
			}
			System.out.println("End while start: " + start);
		}
		Stack<TagNode> nodes = new Stack<TagNode>();
		while (!nodesRev.isEmpty()) nodes.push(nodesRev.pop());

		TagNode first = nodes.pop();
		TagNode ptr = first;
		while (!nodes.isEmpty()) {
			ptr.sibling = nodes.pop();
			ptr = ptr.sibling;
		}
		TagNode originalSibling = relative.firstChild.sibling;
		ptr.sibling = originalSibling;
		relative.firstChild = first;
	}

	private boolean taggable(String word, String sample, int startIndex) {
		int endIndex = startIndex + word.length();
		System.out.println("taggable checking: " + sample + " at " + startIndex + endIndex + " against " + word);
		word = word.toLowerCase();
		sample = sample.toLowerCase();
		String punc = ".,?!:; ";
		if (startIndex == 0) {
			if (endIndex == sample.length()) return true;
			if (punc.indexOf(sample.charAt(endIndex)) >= 0){
				if (endIndex + 1 == sample.length()) return true;
				if (sample.substring(endIndex,endIndex + 1).equals(" ")) return true;
			}
			return false;
		}
		//System.out.println("Checking string \"" + sample + "\" at index " + (startIndex-1));
		else if (sample.charAt(startIndex - 1) == ' ') {
			if (endIndex == sample.length()) return true;
			if (punc.indexOf(sample.charAt(endIndex)) >= 0) {
				if (endIndex + 1 == sample.length()) return true;
				if (sample.charAt(endIndex + 1) == ' ') return true;
				return true;
			}
		}

		return false;
	}


	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}

	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
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
