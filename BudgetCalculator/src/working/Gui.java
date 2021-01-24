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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
public class Gui extends JFrame {
	private static final long serialVersionUID = 572773041325318728L;
	
	private static JTextField transactionField = new JTextField(15);
	private static JTextField fileTxtField = new JTextField(25);
	protected static String filePath = null;
	
	//Sets the icon of the application in the top left of the window
	private static void SetWindowIcon(JFrame frame) {
		Image icon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\joeyc\\Pictures\\Wallpapers\\abstract2.png");
		frame.setIconImage(icon);
		
	}
		
	//Creates the menu bar
	private static void CreateMenuBar(JFrame frame) {
		JMenuBar menuBar = new JMenuBar();
		JMenuItem manualItem = new JMenuItem("Manual");
		File manualFile = new File("C:\\Users\\joeyc\\Desktop\\budgetCalculatorManual.txt");
		Desktop desktop = Desktop.getDesktop();
		
		manualItem.addActionListener((event) -> {
			try {
				desktop.open(manualFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		JMenu aboutMenu = new JMenu("About");
		menuBar.add(aboutMenu);
		aboutMenu.add(manualItem);
		
		frame.setJMenuBar(menuBar);
		
	}
	
	//Creates the UI elements for the beginning of the frame.
	private static void CreateIntroUI(JFrame frame) {
		Border border = BorderFactory.createTitledBorder("Description");
		JPanel introPanel = new JPanel();
		LayoutManager layout = new GridLayout(2,0,10,10);
		
		introPanel.setLayout(layout);
		
		introPanel.add(new JLabel("This is my ledger/budgetting program that I will use to help allocate my income."));
		introPanel.setBorder(border);
		
		frame.getContentPane().add(introPanel, BorderLayout.BEFORE_FIRST_LINE);
	}
	
	private static void BodyChooseFileFunctionality(JButton chooseFileButton, JTextField fileField) {
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
				}
		        
			}//end actionPerformed
		});
	}
	
	//Adds body text fields to read and write to csv.
	private static void AddBodyFields(JPanel bodyPanel) {
		JLabel inputTransactionLabel = new JLabel("Transaction:", JLabel.LEFT);
		JLabel fileFieldLabel = new JLabel("File:", JLabel.LEFT);
		JButton chooseFileButton = new JButton("Choose File");
		
		bodyPanel.add(inputTransactionLabel);
		bodyPanel.add(transactionField);
		
		bodyPanel.add(fileFieldLabel);
		bodyPanel.add(fileTxtField);
		
		bodyPanel.add(chooseFileButton);
		BodyChooseFileFunctionality(chooseFileButton,fileTxtField);
	}
	
	//Creates the UI for the body of the frame.
	private static void CreateLeftBodyUI(JFrame frame) {
		Border border = BorderFactory.createTitledBorder("Write/Calculate section");
		JPanel leftBodyPanel = new JPanel();
		
		LayoutManager layout = new GridLayout(7,0,5,20);
		leftBodyPanel.setLayout(layout);
		leftBodyPanel.setBorder(border);
		
		frame.getContentPane().add(leftBodyPanel, BorderLayout.WEST);
		//May add something to the east side of the screen.
		
		AddBodyFields(leftBodyPanel);
				
	}
	
	//Creates the dataset used for the 3D bar chart
	private static CategoryDataset BarChartDataset() {
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
	
	//Adds the bar chart to the panel and frame
	private static Component AddBarChart(JFrame frame) {
		JFreeChart barChart = ChartFactory.createBarChart3D(
				"Monthly Budget", "Transaction Type", "Dollar Amount", 
				BarChartDataset(), PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(barChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(600, 400));
		frame.setMinimumSize(new java.awt.Dimension(600,400));
		frame.getContentPane().add(chartPanel);
		frame.pack();
		return chartPanel;
	}
	
	//Creates the dataset that will be used in the linechart
	private static CategoryDataset LineChartDataset() {
		String series = "Expense trend over the month";
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		Hashtable<Integer, String> expenseHashTableForLineChart = Ops.expenseHashTblForLineChart;
		TreeMap<Integer,String> treeMapOfExpenseHashTable = new TreeMap<Integer,String>(expenseHashTableForLineChart);
		
		Set<Integer> hash_set = treeMapOfExpenseHashTable.keySet();
		Iterator<Integer> iteratorForHashTable = hash_set.iterator();
		
		
		while(iteratorForHashTable.hasNext()) {
			
			int key = iteratorForHashTable.next();
			String[] hashValue = expenseHashTableForLineChart.get(key).split(":");
			float amount = Float.parseFloat(hashValue[0]);
			String date = hashValue[1];
			
			dataset.addValue(amount, series, date);
  
		}
		
        return dataset;
    }
	
	//Adds the line chart ot the panel and frame
	private static Component AddLineChart(JFrame frame) {
		JFreeChart lineChart = ChartFactory.createLineChart3D(
				"Monthly Expense Trend", "Date", "Dollar Amount", LineChartDataset(), PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(600, 400));
		frame.setMinimumSize(new java.awt.Dimension(600,400));
		frame.getContentPane().add(chartPanel);
		frame.pack();
		return chartPanel;
				
	}
	
	//Creates the UI for the body (right side) of the frame
	private static void CreateRightBodyUI(JFrame frame) {
		JTabbedPane tabbedPane = new JTabbedPane();
		
		tabbedPane.addTab("Bar Chart", null, AddBarChart(frame), "Expected/Actual");
		tabbedPane.addTab("Line Chart", null, AddLineChart(frame), "Expense/Date");
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
	}
	
	//Adds functionality to the 'write' button
	private static void FooterWriteButtonFunctionality(JButton write) {
		write.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Writing input to file...");
				String transaction = transactionField.getText();
				try {
					Ops.WriteTransactionToTxtFile(transaction);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				transactionField.setText("");
			}
		});
	}
		
	//Adds functionality to the 'calculate' button
	private static void FooterCalculateButtonFunctionality(JButton calculate, JFrame frame) {
		calculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Calculating budget...");
				Ops.OpsMainDriver();
				CreateRightBodyUI(frame);
			}
		});
	}
	
	//Adds footer buttons to process text inputs.
	private static void AddFooterButtons(JPanel footerPanel, JFrame frame) {
		JButton writeInputToTxt = new JButton("Write");
		JButton calculateInputToTxt = new JButton("Calculate Budget");
		
		footerPanel.add(writeInputToTxt);
		footerPanel.add(calculateInputToTxt);
		
		FooterWriteButtonFunctionality(writeInputToTxt);
		FooterCalculateButtonFunctionality(calculateInputToTxt, frame);
	}
	
	//Creates the UI for the footer of the frame.
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
	
	//Exit window and program
	private static void ExitWindowAndProgram(JFrame frame) {
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				System.out.println("### Window and Program closed.");
				System.exit(0);
			}
			
		});
	}
	
	//Creates the window and calls UI methods to put everything together.
	private static void CreateWindowAndUI() {
		JFrame frame = new JFrame("Ledger Application");
		SetWindowIcon(frame);
		CreateMenuBar(frame);
		CreateIntroUI(frame);
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
