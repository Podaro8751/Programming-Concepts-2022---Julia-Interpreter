import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Element;

import javax.swing.UIManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.event.WindowAdapter;

public final class GUI extends JFrame implements ActionListener, ItemListener, WindowStateListener {
	static UIManager UIManager = new UIManager();
	JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
	
	JSplitPane mainPanel1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    
    // JPanel sub2PanelLeft = new JPanel(new GridLayout(1, 2));
    JSplitPane sub2PanelLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT); // Holds the Sub 3s
    JSplitPane sub3PanelLeft1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); // Holds TextArea and Char Count
    JSplitPane sub3PanelLeft2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT); // Holds Label and Buttons
    
    JPanel buttonPanelLeft = new JPanel();  
    
    JPanel subP_JavaTextArea = new JPanel();
    JPanel subP_LeftButtons = new JPanel();
    JPanel subP_RightButtons = new JPanel();
    
    JMenuBar menuBar = new JMenuBar();
	
	JLabel label_Name = new JLabel("");
	JTextArea textArea_Main = new JTextArea(12, 27);
	JTextArea margin_textAreaMain = new JTextArea("1");
	JTextField textField_textAreaMain = new JTextField("Lines: ; Characters: ", 27);
	JScrollPane jsp_Main = new JScrollPane();
	JButton button_Save = new JButton("Save");
	JButton button_Load = new JButton("Load");	
	JButton button_Run = new JButton("Run");
	
	Lexer lexer = new Lexer();
	
	public GUI() {
		// Initial Token Print
		System.out.println("---TOKENS---");
		for (int i=0;i<lexer.keyword[0].length;i++) {
			System.out.println(lexer.keyword[0][i] + " - " + lexer.keyword[1][i]);
		}
		for (int i=0;i<lexer.one_op[0].length;i++) {
			System.out.println(lexer.one_op[0][i] + " - " + lexer.one_op[1][i]);
		}
		for (int i=0;i<lexer.two_op[0].length;i++) {
			System.out.println(lexer.two_op[0][i] + " - " + lexer.two_op[1][i]);
		}
		
		// Actual GUI Stuff
		setTitle("Julia Interpreter");
		setSize(650,540);
		//setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	    addWindowListener(new WindowAdapter() {
	    	@Override
	    	public void windowClosing(WindowEvent e) {
	    		if(JOptionPane.showConfirmDialog(null, "Would you like to close the program?", "Closing Interpreter", 0, 0, null)==0) {
	    			setVisible(false);
	    			dispose();
					System.exit(0);
	    		}
	    	}	    	
	    });
	    
	    fileChooser.setFileFilter(new FileNameExtensionFilter("Julia File (.jl)", "jl"));
		fileChooser.setMultiSelectionEnabled(false);
		    
	    label_Name.setFont(new Font("Calibri", Font.PLAIN, 40));
	    
	    // --
	    
	    button_Load.addActionListener(this);
	    button_Load.setActionCommand("LoadFile");
	    
	    button_Save.addActionListener(this);
	    button_Save.setActionCommand("SaveFile");
	    
	    button_Run.addActionListener(this);
	    button_Run.setActionCommand("Run");
	    
	    //list_SegSelect.addListSelectionListener(listSeleMod_SegSelect);
	    
		textArea_Main.getDocument().addDocumentListener(new DocumentListener() {
			public String getText() {
				int docLength = textArea_Main.getDocument().getLength();
				Element root = textArea_Main.getDocument().getDefaultRootElement();
				String text = "1" + System.getProperty("line.separator");
				for (int i=2;i<root.getElementIndex(docLength)+2;i++) {
					text += i + System.getProperty("line.separator");
				}
				return text;
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateCharCount();
				margin_textAreaMain.setText(getText());
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateCharCount();	
				margin_textAreaMain.setText(getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateCharCount();	
				margin_textAreaMain.setText(getText());
			}
		});		
		jsp_Main.getViewport().add(textArea_Main);
		jsp_Main.setRowHeaderView(margin_textAreaMain);
	    
	    // --
	    
	    mainPanel1.resetToPreferredSizes();
	    mainPanel1.setDividerLocation(325);
	    mainPanel1.setDividerSize(-1);
	    
	    sub2PanelLeft.resetToPreferredSizes();
	    sub2PanelLeft.setDividerLocation(460);
	    sub2PanelLeft.setDividerSize(-1);
	    
	    sub3PanelLeft1.resetToPreferredSizes();
	    sub3PanelLeft1.setDividerLocation(440);
	    sub3PanelLeft1.setDividerSize(-1);
	    
	    sub3PanelLeft2.resetToPreferredSizes();
	    sub3PanelLeft2.setDividerLocation(90);
	    sub3PanelLeft2.setDividerSize(-1);
	    
	    margin_textAreaMain.setEditable(false);
	    textField_textAreaMain.setEditable(false);
	    
	    
	    
	    // --
	    
	    buttonPanelLeft.add(button_Save);
	    buttonPanelLeft.add(button_Load);
	    buttonPanelLeft.add(button_Run);
	    
	    sub3PanelLeft1.add(jsp_Main);
	    sub3PanelLeft1.add(textField_textAreaMain);
	    sub3PanelLeft2.add(label_Name);
	    sub3PanelLeft2.add(buttonPanelLeft);
	    
	    sub2PanelLeft.add(sub3PanelLeft1);
	    sub2PanelLeft.add(sub3PanelLeft2);
	    
	    // --  
	    
	    // --
	    
	    mainPanel1.add(sub2PanelLeft);
	    
		setJMenuBar(menuBar);
	    add(mainPanel1);
	    addComponentListener(new ComponentAdapter() {
	    	public void componentResized(ComponentEvent e) {
	    		resizeComponents(getSize().getHeight(), getSize().getWidth());	    	    
	    	}
	    });
	    addWindowStateListener(this);
		setVisible(true);
	}
	
	//Checks Actions
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		int returnVal;
			if (cmd.equals("LoadFile")) {
	            returnVal = fileChooser.showOpenDialog(this);
	            if (returnVal==0) {
	            	String currentFile = fileChooser.getSelectedFile().getAbsolutePath();
	            	String fetchedCode = getTextFrom("", currentFile);
	            	if (fetchedCode != null) {
	            		textArea_Main.setText(fetchedCode);
	            	}	            	
	            }
			} else if (cmd.equals("SaveFile")) {			
				returnVal = fileChooser.showSaveDialog(this);
	        	if (returnVal==0) {
	        		String currentFile = fileChooser.getSelectedFile().getAbsolutePath();
	        		String fileName = fileChooser.getSelectedFile().getName();
	            	String extension = ".jl";
	        		String curExtension = currentFile.substring(currentFile.length()-extension.length(), currentFile.length());
	            	if (!(curExtension.equals(extension))) {
	            		currentFile += extension;
	            		fileName += extension;
	            	}
	            	writeTextTo(textArea_Main.getText(), currentFile);
	        	}
			} else if (cmd.equals("Run")) {
				lexer.Scan(textArea_Main.getText());				
			} else if (cmd.equals("UpdateSize")) {
				resizeComponents(getSize().getHeight(), getSize().getWidth());
			}
	}
	
	// Checks All State Changes
	public void itemStateChanged(ItemEvent e) {
		
	}
	
	@Override
	public void windowStateChanged(WindowEvent e) {				
		Timer count = new Timer(50, this);
		count.setActionCommand("UpdateSize");
		count.setRepeats(false);
		count.start();
	}
	
	private void resizeComponents(double height, double width) {
		int nWidth = (int) (width * .5);
		int nHeight1 = (int) (height - 190);
		int nHeight2 = (int) (nHeight1 - 20);
		
	    mainPanel1.setDividerLocation(nWidth);	    	    
	    sub2PanelLeft.setDividerLocation(nHeight1);	    	    
	    sub3PanelLeft1.setDividerLocation(nHeight2);	    	     	    
	}
	
	private void updateCharCount() {
		int charCount = 0;
		int lineCount = 1;
		String text = textArea_Main.getText();
		
		for (int i=0;i<text.length();i++) {
			if (text.substring(i, i+1).equals("\n")) {
				lineCount++;
				charCount--;
			}
			charCount++;
		}
		
		textField_textAreaMain.setText("Lines: " + lineCount + "; Characters: " + charCount);
	}
	
	// Starts-Up the App	
	public static void main(String args[]) {
		int fontSize = 12;
		String[] keys = new String[]{"Label.font",
									 "TextField.font",
									 "TextArea.font",
									 "Button.font",
									 "CheckBox.font",
									 "ComboBox.font",
									 "MenuBar.font",
									 "Menu.font",
									 "MenuItem.font",
									 "ScrollPane.font",
									 "Frame.font",
									 "Panel.font",
									 "OptionPane.font",
									 "FileChooser.font",
									 "CheckBox.font",
									 "CheckBoxMenuItem.font",
									 "List.font",
									 "SplitPane.font",
									 "TabbedPane.font"};
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			for (int i=0;i<keys.length;i++) {
				UIManager.put(keys[i], new Font("Sans_Serif",Font.PLAIN,fontSize));
			}        
		} catch(Exception e) {}
		GUI app = new GUI();
	}
	
	public String getTextFrom(String header, String loca) {
		String newText = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(loca));
			String s;
			while ((s = br.readLine()) != null) {
				newText += s + "\n";
			}
			if ((newText.length()<header.length())||(!newText.substring(0, header.length()).equals(header))) {
				//System.out.println("Header Mismatch in " + loca);
				newText = null;
			}
			br.close();
		} catch(Exception e) {
			//System.out.println("Error in " + loca);
			newText = null;
		}
		return(newText);
	}
	
	public void writeTextTo(String text, String loca) {
		System.out.println("Starting Write");
		try {
			FileWriter fr = new FileWriter(loca);
			fr.write(text);
			fr.close();
		} catch(Exception e) {
			System.out.println("Error");
		}
	}
}
