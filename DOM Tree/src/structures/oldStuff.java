package structures;

public class oldStuff {
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
			startPrev = 0;
			startPrev += start;
			System.out.println("Start pre: " + start);
			start = sampleLower.indexOf(wordLower, startPrev);
			if (decrement) {
				//startPrev -= word.length();
				//decrement = false;
			}
			if (start == -1){
				if (decrement) {
					startPrev -= word.length();
					decrement = false;
				}
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
			}
			else {
				start += word.length();
				decrement = true;
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
			startPrev = 0;
			startPrev += start;
			System.out.println("Start pre: " + start);
			start = sampleLower.indexOf(wordLower, startPrev);
			if (start == -1){
				if (decrement) {
					startPrev -= word.length();
					decrement = false;
				}
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
			}
			else {
				start += word.length();
				decrement = true;
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
}
