package working;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
public class Gui extends JFrame {
	private static final long serialVersionUID = 0;
	
	private static JTextField transactionField = new JTextField(15);
	private static JTextField fileTxtField = new JTextField(25);
	protected static String filePath = null;
	
	private static JFreeChart barGraphForPNG = null;
	private static JFreeChart lineChartForPNG = null;
	private static int pngWidth = 600;
	private static int pngHeight = 400;
	private static String homeFolder = System.getProperty("user.home");
	
	//Sets the icon of the application in the top left of the window
	private static void SetWindowIcon(JFrame frame) {
		Image icon = Toolkit.getDefaultToolkit().getImage("IMAGE OF YOUR CHOOSING");
		frame.setIconImage(icon);
	}
	
	private static void DisplayErrorMessagePopUp(String message) {
		JOptionPane.showMessageDialog(null, message, "Error!", JOptionPane.ERROR_MESSAGE);
		JDialog dialog = new JDialog();
		dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	private static void CreateBarGraphFileMenuItemAndEvent(JMenuBar menuBar, JMenu fileMenu) {
		JMenuItem saveBarGraphItem = new JMenuItem("Save Bar Graph");
		saveBarGraphItem.addActionListener((event) -> {
			File barGraphPNG = new File(homeFolder,"monthly_budget_bar_graph.png");
			try {
				ChartUtilities.saveChartAsPNG(barGraphPNG, barGraphForPNG, pngWidth, pngHeight);
				System.out.println("### Bar Graph PNG was saved to your home folder.");
			} catch(IOException e){
				e.printStackTrace();
				System.out.println("--- IOException occurred in the CreateFileMenuItemAndEvent method.");
			} catch(IllegalArgumentException iae) {
				String iaeMessage = "Make sure to first calculate your budget before attempting to save the bar graph as a png.";
				DisplayErrorMessagePopUp(iaeMessage);
			}
		});
		
		menuBar.add(fileMenu);
		fileMenu.add(saveBarGraphItem);
	}
	
	private static void CreateLineChartFileMenuItemAndEvent(JMenuBar menuBar, JMenu fileMenu) {
		JMenuItem saveLineChartItem = new JMenuItem("Save Line Chart");
		saveLineChartItem.addActionListener((event) -> {
			File lineChartPNG = new File(homeFolder,"monthly_expense_line_chart.png");
			try {
				ChartUtilities.saveChartAsPNG(lineChartPNG, lineChartForPNG, pngWidth, pngHeight);
			} catch(IOException e){
				e.printStackTrace();
				System.out.println("--- IOException occurred in the CreateMenuBar method.");
			} catch(IllegalArgumentException iae) {
				String iaeMessage = "Make sure to first calculate your budget before attempting to save the line chart as a png.";
				DisplayErrorMessagePopUp(iaeMessage);
			}
			System.out.println("### Line Chart PNG was saved to your home folder.");
			
		});
		
		menuBar.add(fileMenu);
		fileMenu.add(saveLineChartItem);
	}
	
	private static void CreateAboutMenuItemAndEvent(JMenuBar menuBar, JMenu aboutMenu) {
		JMenuItem manualItem = new JMenuItem("Manual");
		File manualFile = new File(".\\budgetCalculatorManual.txt");
		Desktop desktop = Desktop.getDesktop();
		manualItem.addActionListener((event) -> {
			try {
				desktop.open(manualFile);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("--- IOException occurred in the CreateMenuBar method.");
			}
		});
		
		menuBar.add(aboutMenu);
		aboutMenu.add(manualItem);
	}
		
	private static void CreateMenuBar(JFrame frame) {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu aboutMenu = new JMenu("About");

		CreateBarGraphFileMenuItemAndEvent(menuBar, fileMenu);
		fileMenu.addSeparator();
		CreateLineChartFileMenuItemAndEvent(menuBar, fileMenu);
			
		CreateAboutMenuItemAndEvent(menuBar, aboutMenu);
			
		frame.setJMenuBar(menuBar);
	}
	
	private static void CreateHeaderUI(JFrame frame) {
		Border border = BorderFactory.createTitledBorder("Description");
		JPanel headerPanel = new JPanel();
		LayoutManager layout = new GridLayout(2,0,10,10);
		
		headerPanel.setLayout(layout);
		headerPanel.add(new JLabel("This is my ledger/budgetting program that I will use to help allocate my income."));
		headerPanel.setBorder(border);
		frame.getContentPane().add(headerPanel, BorderLayout.BEFORE_FIRST_LINE);
	}
	
	private static void AddChooseFileButtonFunctionality(JButton chooseFileButton, JTextField fileField) {
		chooseFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileToUse = new JFileChooser();
				int returnVal = fileToUse.showSaveDialog(fileToUse);
				try {
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fileToUse.getSelectedFile();
						filePath = file.getAbsolutePath();
						fileTxtField.setText(file.getName());
						fileTxtField.setEditable(false);
					}
				}
				catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("--- Exception (dealing with file) occurred in the AddChooseFileButtonFunctionality method.");
				}
		        
			}//end actionPerformed
		});
	}
	
	private static void AddLeftBodyUIFields(JPanel bodyPanel) {
		JLabel inputTransactionLabel = new JLabel("Transaction:", JLabel.LEFT);
		JLabel fileFieldLabel = new JLabel("File:", JLabel.LEFT);
		JButton chooseFileButton = new JButton("Choose File");
		
		bodyPanel.add(inputTransactionLabel);
		bodyPanel.add(transactionField);
		
		bodyPanel.add(fileFieldLabel);
		bodyPanel.add(fileTxtField);
		
		bodyPanel.add(chooseFileButton);
		AddChooseFileButtonFunctionality(chooseFileButton,fileTxtField);
	}
	
	private static void CreateLeftBodyUI(JFrame frame) {
		Border border = BorderFactory.createTitledBorder("Write/Calculate section");
		JPanel leftBodyPanel = new JPanel();
		
		LayoutManager layout = new GridLayout(7,0,5,20);
		leftBodyPanel.setLayout(layout);
		leftBodyPanel.setBorder(border);
		frame.getContentPane().add(leftBodyPanel, BorderLayout.WEST);
		AddLeftBodyUIFields(leftBodyPanel);
				
	}
	
	private static CategoryDataset Create3DBarChartDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(Ops.totalMonthlyTransactionGoal[0], "Expense", "Expected Expense");
        dataset.addValue(Ops.totalMonthlyExpenses[0], "Expense", "Actual Expense");

        dataset.addValue(Ops.totalMonthlyTransactionGoal[1], "Savings", "Expected Expense");
        dataset.addValue(Ops.totalMonthlyExpenses[1], "Savings", "Actual Expense");

        dataset.addValue(Ops.totalMonthlyTransactionGoal[2], "Investment", "Expected Expense");
        dataset.addValue(Ops.totalMonthlyExpenses[2], "Investment", "Actual Expense");

        dataset.addValue(Ops.totalMonthlyTransactionGoal[3], "Emergency", "Expected Expense");
        dataset.addValue(Ops.totalMonthlyExpenses[3], "Emergency", "Actual Expense");
        
        return dataset;
    }
	
	private static Component AddBarChartToFrame(JFrame frame) {
		JFreeChart barChart = ChartFactory.createBarChart3D(
				"Monthly Budget", "Transaction Type", "Dollar Amount", 
				Create3DBarChartDataset(), PlotOrientation.VERTICAL, true, true, false);
		barGraphForPNG = barChart;
		ChartPanel chartPanel = new ChartPanel(barChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(600, 400));
		frame.setMinimumSize(new java.awt.Dimension(600,400));
		frame.getContentPane().add(chartPanel);
		frame.pack();
		return chartPanel;
	}
	
	private static CategoryDataset Create3DLineChartDataset() {
		String series = "Expense trend over the month";
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		Hashtable<Integer, String> expenseHashTableForLineChart = Ops.expenseHashTblForLineChart;
		TreeMap<Integer,String> treeMapOfExpenseHashTable = new TreeMap<Integer,String>(expenseHashTableForLineChart);
		
		Set<Integer> hash_set = treeMapOfExpenseHashTable.keySet();
		Iterator<Integer> iteratorForSet = hash_set.iterator();
		
		while(iteratorForSet.hasNext()) {
			
			int key = iteratorForSet.next();
			String[] hashValue = expenseHashTableForLineChart.get(key).split(":");
			BigDecimal amount = new BigDecimal(hashValue[0]);
			String date = hashValue[1];
			dataset.addValue(amount, series, date);
		}
		
        return dataset;
    }
	
	private static Component AddLineChartToFrame(JFrame frame) {
		JFreeChart lineChart = ChartFactory.createLineChart3D(
				"Monthly Expense Trend", "Date", "Dollar Amount", Create3DLineChartDataset(),
				PlotOrientation.VERTICAL, true, true, false);
		lineChartForPNG = lineChart;
		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(600, 400));
		frame.setMinimumSize(new java.awt.Dimension(600,400));
		frame.getContentPane().add(chartPanel);
		frame.pack();
		return chartPanel;
				
	}
	
	private static void CreateRightBodyUI(JFrame frame) {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Bar Chart", null, AddBarChartToFrame(frame), "Expected/Actual");
		tabbedPane.addTab("Line Chart", null, AddLineChartToFrame(frame), "Expense/Date");
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
	}
	
	private static void FooterWriteButtonFunctionality(JButton write) {
		write.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String transaction = transactionField.getText();
				try {
					Ops.WriteTransactionToTxtFile(transaction);
				} catch (IOException ioe) {
					System.out.println("--- IOException occurred in the FooterWriteButtonFunctionality method.");
					ioe.printStackTrace();
				} catch (StringIndexOutOfBoundsException siobe) {
					String siobeMessage = "Make sure you supply valid input to the transaction field.";
					DisplayErrorMessagePopUp(siobeMessage);
				} catch (NullPointerException ne) { 
					String neMessage = "Make sure to first supply a valid string and choose the correct txt file.";
					DisplayErrorMessagePopUp(neMessage);
				} catch (NumberFormatException nfe) {
					String nfeMessage = "Make sure to supply a valid number for the amount and for the date.";
					DisplayErrorMessagePopUp(nfeMessage);
				}
				transactionField.setText("");
			}
		});
	}
		
	private static void FooterCalculateButtonFunctionality(JButton calculate, JFrame frame) {
		calculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("### Calculating budget...");
				try {
					Ops.OpsMainDriver();
					CreateRightBodyUI(frame);
				} catch (NullPointerException ne) {
					String neMessage = "Make sure you have chosen the correct file containing your ESIP info.";
					DisplayErrorMessagePopUp(neMessage);
				} catch (NumberFormatException nfe) {
					String nfeMessage = "Make sure to supply a valid number for the amount and for the date.";
					DisplayErrorMessagePopUp(nfeMessage);
				}
			}
		});
	}
	
	private static void AddFooterButtons(JPanel footerPanel, JFrame frame) {
		JButton writeInputToTxt = new JButton("Write");
		JButton calculateInputToTxt = new JButton("Calculate Budget");
		footerPanel.add(writeInputToTxt);
		footerPanel.add(calculateInputToTxt);
		
		FooterWriteButtonFunctionality(writeInputToTxt);
		FooterCalculateButtonFunctionality(calculateInputToTxt, frame);
	}
	
	private static void CreateFooterUI(JFrame frame) {
		Border border = BorderFactory.createTitledBorder("Actions");
		JPanel footerPanel = new JPanel();
		
		LayoutManager layout = new GridLayout(3,0,5,5);
		footerPanel.setLayout(layout);
		
		footerPanel.add(new JLabel("This is where the buttons to read/write to/from txt and to calculate budget."));
		footerPanel.setBorder(border);
		frame.getContentPane().add(footerPanel, BorderLayout.AFTER_LAST_LINE);
		AddFooterButtons(footerPanel,frame);
	}
	
	private static void ExitWindowAndProgram(JFrame frame) {
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				System.out.println("### Window and Program closed.");
				System.exit(0);
			}
			
		});
	}
	
	//Creates the window and calls UI methods to put Header, Body and Footer UI together.
	private static void CreateWindowAndUI() {
		JFrame frame = new JFrame("Ledger Application");
		SetWindowIcon(frame);
		CreateMenuBar(frame);
		CreateHeaderUI(frame);
		CreateLeftBodyUI(frame);
		CreateFooterUI(frame);
		ExitWindowAndProgram(frame);
		
		frame.setSize(800,600);
		frame.setLocation(EXIT_ON_CLOSE, ABORT);
		frame.setVisible(true);
		
	}
	
	//Driving method for the Gui class.
	public static void CreateGui() {
		CreateWindowAndUI();
	}
}
