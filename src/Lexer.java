public class Lexer {
	int sourceLines = 0; // Lines of code in original file
	
	TokenList tknList;
	
	String[][] keyword = {{"if", "then", "else", "while", "do", "function", "print", "end"},
						  {"keyword_if", "keyword_then", "keyword_else", "keyword_while", "keyword_do", "keyword_function", "keyword_print", "keyword_end"}};
	
	String [][] two_op = {{"<=", ">=", "==", "~="},
						  {"operator_le", "operator_ge", "operator_eq", "operator_ne"}};
	
	String [][] one_op = {{"<", ">", "+", "-", "*", "/", "(", ")", "="},
			  			  {"operator_lt", "operator_gt", "operator_add", "operator_sub", "operator_mul", "operator_div", "operator_lp", "operator_rp", "operator_assign"}};
	
	String[] id = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
			                   "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	String[] literal_integer = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
	
	String[] spaces = new String[]{" ", "\n", "	", "\\s"};
	
	// Reads through the source code, identifying and printing tokens
	public void Scan(String source) {
		tknList = new TokenList();
		StringBuilder tknBuilder = new StringBuilder();
		String mode = "scan";	
		// scan = Normal reading behavior
		// skip = Stop tokenizing (for comments)
		// ifA = Check for "id" token; add invalid token if absent
		// ifB = Check for operator; add invalid token if absent
		// ifC = Check for another id; add invalid token if absent
		// ifD = Check for a "then"; add invalid token if absent
		
		// Read through each character in the source code
		
		
		
		// For each character within the code
		for (int i=0;i<source.length();i++) {	
			String unknTkn;
			Token newTkn = null;
			
			// Comment check - Set to skip mode
			if (source.substring(i, i+1).equals("/")) {
				if ((source.length() - 1 > 1)&&(source.substring(i, i+2).equals("//"))) {
					mode = "skip";
				}
			}			
			// Skip mode check
			if (mode.equals("skip")) {
				if (source.substring(i, i+1).equals("\n")) {
					mode = "scan";
				} else {
					continue;
				}
			}
			
			// If curChar is an operator or space: Either-or warrants tokenizing the current contents of tknBuilder				
			// First thing first--tokenize contents of tknBuilder, then reset it
			
			// If it's a space, then just keep it moving
			if (arrayContains(spaces, source.substring(i, i+1), 0) != -1) {
				tokenize(tknBuilder.toString(), tknList);			
				if (source.substring(i, i+1).equals("\n")) {
					sourceLines++;
				}
				tknBuilder.setLength(0);
				continue;
			// Then check for two-char operators, and tokenize. Skip a char, then continue
			} else if((source.length() - i > 2)&&(arrayContains(two_op, source.substring(i, i+2), 0) != -1)) {
				tokenize(tknBuilder.toString(), tknList);			
				tknBuilder.setLength(0);
				int tknIndex = arrayContains(two_op, source.substring(i, i+2), 0);
				newTkn = new Token(two_op[1][tknIndex], source.substring(i, i+2));
				tknList.addToken(newTkn);
				i++;
				continue;
			// Finally, one-char check
			} else if(arrayContains(one_op, source.substring(i, i+1), 0) != -1) {
				tokenize(tknBuilder.toString(), tknList);			
				tknBuilder.setLength(0);
				int tknIndex = arrayContains(one_op, source.substring(i, i+1), 0);
				newTkn = new Token(one_op[1][tknIndex], source.substring(i, i+1));
				tknList.addToken(newTkn);
				continue;
			}
			
			// Keyword detection; append to tknBuilder and see if its a keyword
			tknBuilder.append(source.substring(i, i+1));
			unknTkn = tknBuilder.toString();
			if (arrayContains(keyword, unknTkn, 1) != -1) {
				tknBuilder.setLength(0);
				int tknIndex = arrayContains(keyword, unknTkn, 1);
				newTkn = new Token(keyword[1][tknIndex], unknTkn);
				tknList.addToken(newTkn);
				continue;
			}
		}
		
		System.out.println(tknList.toString());
		Parser parser = new Parser(tknList);
	}
	
	// Checks if an array of Strings contains a given String. Returns the index where the match was found, or -1 otherwise
	public int arrayContains(String[] arr, String value, int mode) { 
		for (int i=0;i<arr.length;i++) {
			if (mode == 1) {
				if (value.equals(arr[i])) {
					return(i);
				}
			} else {
				if (value.contains(arr[i])) {
					return(i);
				}
			}
		}
		return(-1);
	}
	
	public int arrayContains(String[][] arr, String value, int mode) { 
		for (int i=0;i<arr[0].length;i++) {
			if (mode == 1) {
				if (value.equals(arr[0][i])) {
					return(i);
				}
			} else {
				if (value.contains(arr[0][i])) {
					return(i);
				}
			}
		}
		return(-1);
	}
	
	// Checks if a given String "value" contains ONLY the substrings of Strings within a given String array "arr"
	public boolean arrayOnlyContains(String[] arr, String value) {
		int matchFound = 0;
		for (int i=0;i<value.length();i++) {
			matchFound = 0;
			for (int j=0;j<arr.length;j++) {
				if (value.substring(i, i+1).equals(arr[j])) {
					//System.out.println(value.substring(i, i+1) + " <-> " + arr[j]);
					matchFound++;
				}
			}
			
			if (matchFound < 1) {
				return(false);
			} else {
				//System.out.println(value.substring(i, i+1) + " checks out (" + matchFound + ")");
			}		
		}	
		return(true);
	}
	
	// Takes the given input and determines if it should be an id, integer, or neither
	public void tokenize(String unknTkn, TokenList tknList) {
		Token newTkn;
		if (unknTkn.length() > 0) {	
			if (arrayOnlyContains(id, unknTkn)) {
				newTkn = new Token("id", unknTkn);
			} else if (arrayOnlyContains(literal_integer, unknTkn)) {
				newTkn = new Token("literal_integer", unknTkn);
			} else {
				newTkn = new Token("UNKNOWN_TOKEN", unknTkn);
			}
			tknList.addToken(newTkn);
		}
	}
}

class Token {
	String type;
	String value;
	Token next = null;
	
	Token(String type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public String toString() {
		return("(\"" + type + "\", \"" + value + "\")");
	}
}

class TokenList {
	private Token headToken = null;
	private int tokenCount = 0;
	
	public void addToken(Token newTkn) {
		if (headToken != null) {
			Token curToken = headToken;			
			while (curToken.next != null) {
				curToken = curToken.next;
			}
			curToken.next = newTkn;
			tokenCount++;
		} else {
			headToken = newTkn;
			tokenCount++;
		}
	}
	
	public Token getToken(int ind) {
		Token curToken = headToken;
		for (int i=0;i<ind;i++) {
			if ((curToken != null)&&(curToken.next != null)) {
				curToken = curToken.next;
			} else {
				break;
			}
		}
		return(curToken);
	}
	
	public int getTokenCount() {
		return(tokenCount);
	}
	
	public String toString() {
		StringBuilder tokenOutput = new StringBuilder();
		Token curToken = headToken;			
		if (curToken != null) {
			int i = 1;
			while (curToken != null) {
				//System.out.println("Prepped token " + i);
				tokenOutput.append(curToken.toString() + "\n");
				curToken = curToken.next;
				i++;
			}
		}
		return("\n" + "---SCANNER OUTPUT---\n" + tokenOutput.toString());
	}
}
