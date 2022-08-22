import java.util.ArrayList;

public class Parser {
	TokenList sourceList;
	Statement headStmt = null;
	idList ids = new idList();		// IDs, used to keep track of what IDs are in use, as well as directly reference from Segments
	private Statement[] ifElseTempHold = null;
	
	
	// Constructor Method
	Parser(TokenList sourceList) {
		this.sourceList = sourceList;
		ifElseTempHold = new Statement[sourceList.getTokenCount()];
		setUpTree(false, 0, null, null);
		ifElseTempHold = null;
		Statement curStmt = headStmt;
		System.out.println("---PARSER OUTPUT---");
		while (curStmt != null) {			
			System.out.print("> " + curStmt.type);
			
			if (curStmt.value instanceof String) {
				System.out.print(", " + curStmt.value);
			} else if (curStmt.value instanceof id) {
				System.out.print("|" + curStmt.value.toString() + "|");
				id temp = (id) curStmt.value;
				System.out.print("|" + temp.toString() + "|");
				System.out.print(", " + temp.name);
			}
			
			if (curStmt.EXvalues != null) {
				System.out.print("; " + curStmt.EXvalues.size());
			}
			
			System.out.println("");
			
			if (curStmt != null) {
				if ((curStmt.nextT != null)&&(curStmt.type.equals("END"))&&(curStmt.nextT.type.equals("WHILE"))) {
					curStmt = curStmt.nextT.nextF;
				} else if (curStmt.type.equals("IF")) {
					curStmt = curStmt.nextF;
				} else {
					curStmt = curStmt.nextT;
				}
			}
		}
		
		System.out.println("\n---INTERPRETER OUTPUT---");
		Interpreter inter = new Interpreter(this);
		
	}
	
	/*
	Recursive method to set up a statement tree
	1)	If inFunct = false, then the first thing done is a search for a function keyword in the sourceList
		After its found, an id is looked for immediately afterwards. Then the two parenthesis. If either are not found, a syntax error Statement is created
		If everything clears, inFunct is changed to true, and a function statement is created
	2)	ind = i from the current loop. Can be used to skip
	*/
	/*
	 	INITIATE FOR THE FIRST TIME WITH setUpTree(false, 0, null, null)
	*/
	public int setUpTree(boolean inFunct, int index, Statement baseStmt, Statement branchTerminal) {
		// Statement newStmt;
		boolean needID = false;
		boolean needInt = false;
		int nested = 0;
		Token curToken;
		Statement curStmt = baseStmt;
		
		int ind = 0;
		for (ind = index; ind<sourceList.getTokenCount(); ind++) {
		//while (ind<sourceList.getTokenCount()) {
			Token curOriTkn = sourceList.getToken(ind);		
			// First check for a function token. Make sure that's syntactically correct & ready
			if ((curOriTkn.type.equals("keyword_function"))&&(!inFunct)) {
				inFunct = true;
				ind++;
				if (sourceList.getToken(ind).type.equals("id")) {
					id tempId = new id(sourceList.getToken(ind).value, 0);
					ind++;
					if (sourceList.getToken(ind).type.equals("operator_lp")) {
						ind++;
						if (sourceList.getToken(ind).type.equals("operator_rp")) {
							Statement newStmt = new Statement("FUNCTION", tempId, null);
							// Index 0 ID
							ids.addId(tempId);						
							headStmt = newStmt;
							curStmt = newStmt;
							continue;
						} else {
							headStmt = new Statement("SYNTAX_ERROR", "Expected ')' parenthesis after '('", null);
							return(ind);
						}
					} else {
						headStmt = new Statement("SYNTAX_ERROR", "Expected '(' after '" + tempId.name + "'", null);
						return(ind);
					}
				} else {
					headStmt = new Statement("SYNTAX_ERROR", "Expected id after 'function'", null);
					return(ind);
				}
			} else if ((curOriTkn.type.equals("keyword_function"))&&(inFunct)) {
				headStmt = new Statement("SYNTAX_ERROR", "Multiple functions declared in file", null);
				return(ind);
			} else if(!inFunct) {
				ind++;
				continue;
			}
			
			// Check if its an id...
			if(sourceList.getToken(ind).type.equals("id")) {
				id newId1 = null;
				if (ids.getIdIndex(sourceList.getToken(ind).value) != 0) {
					newId1 = ids.getId(sourceList.getToken(ind).value);
					if (newId1 == null) {
						newId1 = new id(sourceList.getToken(ind).value, 0);
						ids.addId(newId1);
					}
				} else {
					headStmt = new Statement("SYNTAX_ERROR", "'" + sourceList.getToken(ind).value + "' is the function id", null);
					return(ind);
				}
				ind++;
				// ... Followed by an assign operator
				if (sourceList.getToken(ind).type.equals("operator_assign")) {
					ind++;
					// ... Followed by an id or integer
					if ((sourceList.getToken(ind).type.equals("id"))||(sourceList.getToken(ind).type.equals("literal_integer"))) {
						ArrayList<Object> values = new ArrayList();
						id newId2 = null;
						// If its an id, then repeat the same id verification process from above, and add it to the ArrayList
						if (sourceList.getToken(ind).type.equals("id")) {
							if (ids.getIdIndex(sourceList.getToken(ind).value) != 0) {
								newId2 = ids.getId(sourceList.getToken(ind).value);
								if (newId2 == null) {
									newId2 = new id(sourceList.getToken(ind).value, 0);
									ids.addId(newId2);
								}
								values.add(newId2);
							} else {
								headStmt = new Statement("SYNTAX_ERROR", "'" + sourceList.getToken(ind).value + "' is the function id", null);
								return(ind);
							}
						// Otherwise, just convert the token's value to an int, then add that int to the ArrayList
						} else if (sourceList.getToken(ind).type.equals("literal_integer")) {
							int newInt = Integer.parseInt(sourceList.getToken(ind).value);
							values.add(newInt);
						}
						
						// Repeat the process, checking for add/sub/mul/div operators and then ids/ints.
						// Safely break if an int is followed by neither
						// Give an error if an operator isn't followed by an int
						// ------------------------------------------------------
						while (true) {
							ind++;
							if ((sourceList.getToken(ind).type.equals("operator_add"))||
								(sourceList.getToken(ind).type.equals("operator_sub"))||
								(sourceList.getToken(ind).type.equals("operator_mul"))||
								(sourceList.getToken(ind).type.equals("operator_div"))) {
								values.add(sourceList.getToken(ind).value);
							} else {
								ind--;
								break;
							}
							
							// id = id verification process from above
							ind++;
							if (sourceList.getToken(ind).type.equals("id")) {
								if (ids.getIdIndex(sourceList.getToken(ind).value) != 0) {
									newId2 = ids.getId(sourceList.getToken(ind).value);
									if (newId2 == null) {
										newId2 = new id(sourceList.getToken(ind).value, 0);
										ids.addId(newId2);
									}
									values.add(newId2);
								} else {
									headStmt = new Statement("SYNTAX_ERROR", "'" + sourceList.getToken(ind).value + "' is the function id - C", null);
									return(ind);
								}
							// int =  convert the token's value to an int
							} else if (sourceList.getToken(ind).type.equals("literal_integer")) {
								int newInt = Integer.parseInt(sourceList.getToken(ind).value);
								values.add(newInt);
							} else {
								headStmt = new Statement("SYNTAX_ERROR", "Expected integer after " + values.get(values.size() - 1), null);
								return(ind);
							}
						}
						// ------------------------------------------------------
						Statement newStmt = new Statement("ASSIGN", newId1, values);
						curStmt.nextT = newStmt;
						//System.out.println("<assign>" + curStmt.type);
						curStmt = curStmt.nextT;
					} else {
						headStmt = new Statement("SYNTAX_ERROR", "Expected integer after '='", null);
						return(ind);
					}
				} else {
					headStmt = new Statement("SYNTAX_ERROR", "Expected '=' after '" + newId1.name + "'", null);
					return(ind);
				}
			
			// Check for print->left paren->id or int->right paren
			} else if (sourceList.getToken(ind).type.equals("keyword_print")) {
				ind++;
				if (sourceList.getToken(ind).type.equals("operator_lp")) {
					ind++;
					Object temp = null;					
					if ((sourceList.getToken(ind).type.equals("id"))||(sourceList.getToken(ind).type.equals("literal_integer"))) {
						if (sourceList.getToken(ind).type.equals("id")) {
							if (ids.getIdIndex(sourceList.getToken(ind).value) > 0) {
								temp = ids.getId(sourceList.getToken(ind).value);
							} else {
								headStmt = new Statement("SYNTAX_ERROR", "'" + sourceList.getToken(ind).value + "' is the function id or has not been declared", null);
								return(ind);
							}
						} else if (sourceList.getToken(ind).type.equals("literal_integer")) {
							int tempInt = Integer.parseInt(sourceList.getToken(ind).value);
							temp = tempInt;
						}					
						ind++;
						if (sourceList.getToken(ind).type.equals("operator_rp")) {
							Statement newStmt = new Statement("PRINT", temp, null);
							curStmt.nextT = newStmt;
							//System.out.println("<print>" + curStmt.type);
							curStmt = curStmt.nextT;
						} else {
							headStmt = new Statement("SYNTAX_ERROR", "Expected ')' after integer", null);
							return(ind);
						}
					} else {
						headStmt = new Statement("SYNTAX_ERROR", "Expected integer after '('", null);
						return(ind);
					}
				} else {
					headStmt = new Statement("SYNTAX_ERROR", "Expected '(' after 'print'" + ". Recieved '" + sourceList.getToken(ind).type + "' instead", null);
					return(ind);
				}
			
			// Check for while->id->comp->id->do, then recurse 
			} else if (sourceList.getToken(ind).type.equals("keyword_while")) {
				ind++;
				Object tempA = null;
				ArrayList<Object> values = new ArrayList();
				// id 1
				if ((sourceList.getToken(ind).type.equals("id"))||(sourceList.getToken(ind).type.equals("literal_integer"))) {
					if (sourceList.getToken(ind).type.equals("id")) {
						if (ids.getIdIndex(sourceList.getToken(ind).value) > 0) {
							tempA = ids.getId(sourceList.getToken(ind).value);
						} else {
							headStmt = new Statement("SYNTAX_ERROR", "'" + sourceList.getToken(ind).value + "' is the function id or has not been declared", null);
							return(ind);
						}
					} else if (sourceList.getToken(ind).type.equals("literal_integer")) {
						int tempInt = Integer.parseInt(sourceList.getToken(ind).value);
						tempA = tempInt;
					}					
					ind++;
					// comp
					if ((sourceList.getToken(ind).type.equals("operator_le"))||
						(sourceList.getToken(ind).type.equals("operator_ge"))||
						(sourceList.getToken(ind).type.equals("operator_eq"))||
						(sourceList.getToken(ind).type.equals("operator_ne"))||
						(sourceList.getToken(ind).type.equals("operator_lt"))||
						(sourceList.getToken(ind).type.equals("operator_gt"))) {
						values.add(sourceList.getToken(ind).value);
						ind++;
						// id 2
						if ((sourceList.getToken(ind).type.equals("id"))||(sourceList.getToken(ind).type.equals("literal_integer"))) {
							if (sourceList.getToken(ind).type.equals("id")) {
								if (ids.getIdIndex(sourceList.getToken(ind).value) > 0) {
									values.add(ids.getId(sourceList.getToken(ind).value));
								} else {
									headStmt = new Statement("SYNTAX_ERROR", "'" + sourceList.getToken(ind).value + "' is the function id or has not been declared", null);
									return(ind);
								}
							} else if (sourceList.getToken(ind).type.equals("literal_integer")) {
								int tempInt = Integer.parseInt(sourceList.getToken(ind).value);
								values.add(tempInt);
							}				
							ind++;
							// do
							if (sourceList.getToken(ind).type.equals("keyword_do")) {
								Statement newStmt = new Statement("WHILE", tempA, values);
								curStmt.nextT = newStmt;
								//System.out.println("<doA>" + curStmt.type);
								curStmt = curStmt.nextT;
								// recurse
								ind++;
								ind = setUpTree(inFunct, ind, curStmt, null);
								if (headStmt.type.equals("SYNTAX_ERROR")) {
									return(ind);
								} else {
									newStmt = new Statement("WHILE_END", null, null);
									curStmt.nextF = newStmt;
									//System.out.println("<doB>" + curStmt.type);
									curStmt = curStmt.nextF;
								}
							} else {
								headStmt = new Statement("SYNTAX_ERROR", "Expected 'do' after integer", null);
								return(ind);
							}
						} else {
							headStmt = new Statement("SYNTAX_ERROR", "Expected integer after comparison", null);
							return(ind);
						}
					} else {
						headStmt = new Statement("SYNTAX_ERROR", "Expected comparison after integer", null);
						return(ind);
					}
				} else {
					headStmt = new Statement("SYNTAX_ERROR", "Expected integer after 'while'", null);
					return(ind);
				}
			
			// Check for if->id->comp->id->then, recurse, else stuff
			} else if (sourceList.getToken(ind).type.equals("keyword_if")) {
				ind++;
				Object tempA = null;
				ArrayList<Object> values = new ArrayList();
				// id 1
				if ((sourceList.getToken(ind).type.equals("id"))||(sourceList.getToken(ind).type.equals("literal_integer"))) {
					if (sourceList.getToken(ind).type.equals("id")) {
						if (ids.getIdIndex(sourceList.getToken(ind).value) > 0) {
							tempA = ids.getId(sourceList.getToken(ind).value);
						} else {
							headStmt = new Statement("SYNTAX_ERROR", "'" + sourceList.getToken(ind).value + "' is the function id or has not been declared", null);
							return(ind);
						}
					} else if (sourceList.getToken(ind).type.equals("literal_integer")) {
						int tempInt = Integer.parseInt(sourceList.getToken(ind).value);
						tempA = tempInt;
					}					
					ind++;
					// comp
					if ((sourceList.getToken(ind).type.equals("operator_le"))||
						(sourceList.getToken(ind).type.equals("operator_ge"))||
						(sourceList.getToken(ind).type.equals("operator_eq"))||
						(sourceList.getToken(ind).type.equals("operator_ne"))||
						(sourceList.getToken(ind).type.equals("operator_lt"))||
						(sourceList.getToken(ind).type.equals("operator_gt"))) {
						values.add(sourceList.getToken(ind).value);
						ind++;
						// id 2
						if ((sourceList.getToken(ind).type.equals("id"))||(sourceList.getToken(ind).type.equals("literal_integer"))) {
							if (sourceList.getToken(ind).type.equals("id")) {
								if (ids.getIdIndex(sourceList.getToken(ind).value) > 0) {
									values.add(ids.getId(sourceList.getToken(ind).value));
								} else {
									headStmt = new Statement("SYNTAX_ERROR", "'" + sourceList.getToken(ind).value + "' is the function id or has not been declared", null);
									return(ind);
								}
							} else if (sourceList.getToken(ind).type.equals("literal_integer")) {
								int tempInt = Integer.parseInt(sourceList.getToken(ind).value);
								values.add(tempInt);
							}				
							ind++;
							// then
							if (sourceList.getToken(ind).type.equals("keyword_then")) {
								Statement newStmtA = new Statement("IF", tempA, values);
								Statement newStmtB = new Statement("IF_END", tempA, values);
								curStmt.nextT = newStmtA;
								//System.out.println("<then>" + curStmt.type);
								curStmt = curStmt.nextT;
								// recurse
								ind++;
								//System.out.println(ind + " " + curStmt.type + " " + newStmtB.type);
								ind = setUpTree(inFunct, ind, curStmt, newStmtB);
								if (headStmt.type.equals("SYNTAX_ERROR")) {
									return(ind);
								} else {
									if (ifElseTempHold != null) {
										//System.out.println("Loading " + ifElseTempHold[ind].type);
										curStmt = ifElseTempHold[ind];
									}
									/* test 1
									curStmt = curStmt.nextF;
									while (curStmt.nextT != null) {
										curStmt = curStmt.nextT;
									}
									curStmt.nextT = newStmtB;
									curStmt = curStmt.nextT;
									*/
									
									/* old
									curStmt.nextF = newStmtB;
									curStmt = curStmt.nextF;
									 */
								}
							} else {
								headStmt = new Statement("SYNTAX_ERROR", "Expected 'do' after integer", null);
								return(ind);
							}
						} else {
							headStmt = new Statement("SYNTAX_ERROR", "Expected integer after comparison", null);
							return(ind);
						}
					} else {
						headStmt = new Statement("SYNTAX_ERROR", "Expected comparison after integer", null);
						return(ind);
					}
				} else {
					headStmt = new Statement("SYNTAX_ERROR", "Expected integer after 'if'", null);
					return(ind);
				}
			
			// Act like an end, but keep track of the last statement in each branch, so that their nextT can converge
			} else if (sourceList.getToken(ind).type.equals("keyword_else")) {
				if ((baseStmt != null)&&(baseStmt.type.equals("IF"))) {
					curStmt.nextT = branchTerminal;
					Statement newStmt = new Statement("ELSE", null, null);
					baseStmt.nextF = newStmt;
					curStmt = baseStmt.nextF;
					// recurse
					ind++;
					//System.out.println(ind + " " + curStmt.type + " " + branchTerminal.type);
					ind = setUpTree(inFunct, ind, curStmt, branchTerminal);
					return(ind);
				} else {
					headStmt = new Statement("SYNTAX_ERROR", "Unexpected token '" + sourceList.getToken(ind).value + "'", null);
					return(ind);
				}
				
			// Check if its an end keyword. Just add the end token and return
			} else if (sourceList.getToken(ind).type.equals("keyword_end")) {
				if ((baseStmt != null)&&((baseStmt.type.equals("ELSE"))||(baseStmt.type.equals("IF")))) {
					//System.out.println(">><<" + branchTerminal.type);
					//System.out.println("<><><><>" + curStmt.type);
					curStmt.nextT = branchTerminal;
					ifElseTempHold[ind] = curStmt.nextT;
					//System.out.println("Saved " + ifElseTempHold[ind].type);
					break;
				} else {
					Statement newStmt = new Statement("END", null, null);
					if ((baseStmt != null)&&(baseStmt.type.equals("WHILE"))) {
						newStmt.nextT = baseStmt;
					}
					curStmt.nextT = newStmt;
					if (baseStmt != null) {
						//System.out.println("<end>" + curStmt.type + " " + baseStmt.type);
					}
					curStmt = curStmt.nextT;
					break;
				}
			} else {
				headStmt = new Statement("SYNTAX_ERROR", "Unexpected token '" + sourceList.getToken(ind).value + "' after '" + curStmt.type + "'", null);
				return(ind);
			}
			// End
		}
		//System.out.println("<> Reached natural end at " + ind + "/" + sourceList.getTokenCount());
		return(ind);
	}
	
	/*public void addStatement(Statement newStmt, boolean isTrue) {
		if (headStmt == null) {
			headStmt = newStmt;
			return;
		} else {
			Statement curStmt = headStmt;
			while (curStmt.next != null) {
				
			}
		}				
	}*/
}

class Statement {
	String type;	// In the case 
	Object value;	// Should be used for holding ids, Strings, or integers
	ArrayList<Object> EXvalues;
	Statement nextT = null;
	Statement nextF = null;
	
	Statement(String type, Object value, ArrayList<Object> EXvalues) {
		this.type = type;
		this.value = value;
		this.EXvalues = EXvalues;
	}
	
	public void addStatementAfter(Statement newStmt, boolean isTrue) {
		if (isTrue) {
			nextT = newStmt;
		} else {
			nextF = newStmt;
		}
	}
}

class id {
	String name;
	int value;
	id next = null;
	
	id(String name, int value) {
		this.name = name;
		this.value = value;
	}
}

class idList {
	private id firstId = null;
	private int idCount;
	
	public void addId(id newId) {
		if (firstId == null) {
			firstId = newId;
		} else {
			id curId = firstId;
			while (curId.next != null) {
				curId = curId.next;
			}
			curId.next = newId;
		}
		idCount++;
	}
	
	public id getId(String name) {
		id curId = firstId;	
		
		if ((curId == null)||(curId.name.equals(name))) {
			return(curId);
		}
		
		while (curId.next != null) {
			curId = curId.next;
			if (curId.name.equals(name)) {
				return(curId);
			}
		}
		
		return(null);
	}
	
	public int getIdIndex(String name) {
		id curId = firstId;	
		int i = 0;
		
		if (curId.name.equals(name)) {
			return(0);
		} else if (curId != null) {
			while (curId.next != null) {
				i++;
				curId = curId.next;
				if (curId.name.equals(name)) {
					return(i);
				}
			}
		}
		return(-1);
	}
}
