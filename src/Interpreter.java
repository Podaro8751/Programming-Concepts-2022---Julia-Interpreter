import java.util.ArrayList;

public class Interpreter {
	Parser parser = null;
	boolean go = true;
	
	Interpreter(Parser parser) {
		this.parser = parser;
		runCode();
		System.out.println("");
	}
	
	/*
	 * The Final Method; goes through each statement within the parser--updating IDs and following loop logic
	 * Statement Types:
	 ** SYNTAX ERROR = Indicates that the code can not be ran. Primary value holds a string explaining the error cause
	 ** FUNCTION = Uses only the primary value--solely to store the Function ID
	 ** ASSIGN = Primary value stores the id being changed. Value list stores a list of integer/ids and arithmetic operators
	 ** PRINT = Primary value stores the content to be printed
	 ** WHILE = Primary value stores the lefthand comparison. Value list stores the comparision operator & righthand comparison
	 ** WHILE_END = Can be read and ignored
	 ** IF = Read and ignore
	 ** IF_END = Read and ignore
	 ** ELSE = Read and ignore(?)
	 ** END = Read and ignore
	*/
	public void runCode() {
		Statement curStmt = parser.headStmt;
		
		while ((curStmt.nextT != null)||(curStmt.nextF != null)) {
			//System.out.println("> Statement Check");
			if (curStmt.type.equals("FUNCTION")) {
				
			// Remember order of operations
			} else if (curStmt.type.equals("ASSIGN")) {
				ArrayList<Object> curList = curStmt.EXvalues;				
				id target = (id) curStmt.value;
				target.value = calc(curList);
				if (!go) {
					return;
				}
			} else if (curStmt.type.equals("PRINT")) {
				if (curStmt.value instanceof id) {							
					print(((id) curStmt.value).value);
				} else {
					print((int) curStmt.value);
				}
			} else if ((curStmt.type.equals("WHILE"))||(curStmt.type.equals("IF"))) {
				int tempL = calcTemp(curStmt.value);
				String curOp = (String) curStmt.EXvalues.get(0);
				int tempR = calcTemp(curStmt.EXvalues.get(1));
				
				// System.out.println(tempL + curOp + tempR);
				if (compare(tempL, curOp, tempR)) {
					curStmt = curStmt.nextT;
					//System.out.println("> " + curStmt.type);
				} else {
					//System.out.println("> " + curStmt.type);
					curStmt = curStmt.nextF;
					//System.out.println("> " + curStmt.type);
				}
				continue;
			}
			
			curStmt = curStmt.nextT;
		}
	}
	
	private int math(int a, String op, int b) {
		//System.out.println("> " + a + op + b);
		int result = a;
		switch (op) {
			case "+":
				a += b;
				break;
			case "-":
				a -= b;
				break;
			case "*":
				a *= b;
				break;
			case "/":
				try {
					a /= b;
				} catch(Exception e) {
					System.out.println("> RUNTIME_ERROR, Attempted to divide by zero"); 
					go = false;
				}
				break;
			default:
				System.out.println("BREAK");
				break;
		}
		return(a);
	}
	
	private int calc(ArrayList<Object> oldList) {
		ArrayList<Object> curList = (ArrayList<Object>) oldList.clone();
		int tempL;
		int tempR;
		
		// First run; deal with multiplication and division
		for (int i=1; i<curList.size(); i+=2) {
			//System.out.println("> " + i);
			String curOp = (String) curList.get(i);
			if ((curOp.equals("*"))||(curOp.equals("/"))) {
				tempL = calcTemp(curList.get(i-1));
				tempR = calcTemp(curList.get(i+1));
				
				int temp = math(tempL, curOp, tempR);
				if (!go) {
					return(0);
				}
				//System.out.println(tempL + curOp + tempR + "=" + temp);
				curList.add(i-1, temp);
				curList.remove(i);
				curList.remove(i);
				curList.remove(i);
				for (int j=0;j<curList.size();j++) {
					//System.out.print(curList.get(j));
				}
				//System.out.println("");
			}
		}
		
		int newVal = 0;
		if (curList.get(0) instanceof id) {							
			newVal = ((id) curList.get(0)).value;
		} else {
			newVal = (int) curList.get(0);
		}
		
		for (int i=1; i<curList.size(); i+=2) {
			String curOp = (String) curList.get(i);
			if ((curOp.equals("+"))||(curOp.equals("-"))) {
				tempR = calcTemp(curList.get(i+1));
				
				newVal = math(newVal, curOp, tempR);
				if (!go) {
					return(0);
				}
			}
		}
		return(newVal);	
	}
	
	private int calcTemp(Object t) {
		int temp;
		if (t instanceof id) {							
			temp = ((id) t).value;
		} else if (t instanceof ArrayList) {
			temp = calc((ArrayList<Object>) t);
		} else {
			temp = (int) t;
		}
		return(temp);
	}
	
	private boolean compare(int a, String op, int b) {
		//System.out.println("> " + a + op + b);
		switch (op) {
			case "==":
				if (a == b) {
					return(true);
				}
				break;
			case "~=":
				if (a != b) {
					return(true);
				}
				break;
			case ">=":
				if (a >= b) {
					return(true);
				}
				break;
			case "<=":
				if (a <= b) {
					return(true);
				}
				break;
			case ">":
				if (a > b) {
					return(true);
				}
				break;
			case "<":
				if (a < b) {
					return(true);
				}
				break;
			default:
				System.out.println("BREAK");
				break;
		}
		return(false);
	}
	
	private void print(int content) {
		System.out.println(content);
	}
}
