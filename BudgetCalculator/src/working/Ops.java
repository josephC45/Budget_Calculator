package working;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;

public class Ops {
		
	private static Hashtable<Character,ArrayList<Ledger>> transactionHashTbl = new Hashtable<Character,ArrayList<Ledger>>();
	
	private static ArrayList<Ledger> expenseTransactionArrayList = new ArrayList<Ledger>();
	private static ArrayList<Ledger> savingsTransactionArrayList = new ArrayList<Ledger>();
	private static ArrayList<Ledger> investmentTransactionArrayList = new ArrayList<Ledger>();
	private static ArrayList<Ledger> emergencyTransactionArrayList = new ArrayList<Ledger>();
	
	private static float monthlyExpenseGoal = 0;
	private static float monthlySavingsGoal = 0;
	private static float monthlyInvestmentGoal = 0;
	private static float monthlyEmergencyGoal = 0;
	
	private static String budgetGoalString = null;
		
	protected static float [] totalMonthlyTransactionGoal;
	protected static float [] totalMonthlyExpenses;
	
	protected static Hashtable<Integer, String> expenseHashTblForLineChart = new Hashtable<Integer,String>();
	
	
	//Adds ESIP transactions to the hashtable
	private static Hashtable<Character,ArrayList<Ledger>> AddTransactionsToHashTbl(char transactionChoice){
		switch(transactionChoice) {
			case 'E':
				transactionHashTbl.put(transactionChoice,expenseTransactionArrayList);
				break;
			case 'S':
				transactionHashTbl.put(transactionChoice,savingsTransactionArrayList);
				break;
			case 'I':
				transactionHashTbl.put(transactionChoice,investmentTransactionArrayList);
				break;
			case 'P':
				transactionHashTbl.put(transactionChoice, emergencyTransactionArrayList);
				break;
		}
		
		return transactionHashTbl;
		
	}
	
	//Adds ESIP transactions to their respective arraylists
	private static void AddToAppropriateArrayList(Ledger ledger) {
		char transactionChoice = ledger.GetTransactionChoice();
		
		switch(transactionChoice) {
			case 'E':
				expenseTransactionArrayList.add(ledger);
				break;
			case 'S':
				savingsTransactionArrayList.add(ledger);
				break;
			case 'I':
				investmentTransactionArrayList.add(ledger);
				break;
			case 'P':
				emergencyTransactionArrayList.add(ledger);
				break;
		}
	}
	
	//Adds up transactions per date
	private static void AddToExpenseLineChartHashTbl(ArrayList<Ledger> expenses) {
		int resultCount = 0;
		float dailyExpense = 0;
		String tmpDate = null;
		for(Ledger transaction : expenses) {
			if(tmpDate == null) {
				tmpDate = transaction.GetTransactionDate();
				dailyExpense = transaction.GetTransactionAmount();
			}
			else if(!tmpDate.equals(transaction.GetTransactionDate())){
				tmpDate = transaction.GetTransactionDate();
				dailyExpense = 0;
				dailyExpense = transaction.GetTransactionAmount();
			}	
			else {
				dailyExpense += transaction.GetTransactionAmount();
				transaction.SetTransactionAmount(dailyExpense);
			}
			String hashValue = transaction.GetTransactionAmount() + ":" + transaction.GetTransactionDate();
			expenseHashTblForLineChart.put(resultCount,hashValue);
			resultCount++;
		}
	}
	
	/*
	 * Method should parse the txt file and call appropriate methods to add ledger objects to their 
	 * respective arraylists and adding them all to the hashtable. *Needs work
	 */
	private static Hashtable<Character,ArrayList<Ledger>> ParseTxt() {
		char transactionChoice = '\0';
		try (BufferedReader br = Files.newBufferedReader(Paths.get(Gui.filePath))) {
		    String DELIMITER = ",";
		    String line;
		    while ((line = br.readLine()) != null) {

		        String[] esipTransaction = line.split(DELIMITER);
		        
		        transactionChoice = esipTransaction[0].charAt(0);
		        String transactionTitle = esipTransaction[1];
		        float transactionAmount = Float.parseFloat(esipTransaction[2]);
		        String transactionDate = esipTransaction[3];
		        
		        Ledger ledger_transaction = new Ledger(transactionChoice,transactionTitle,transactionAmount,transactionDate);
		        AddToAppropriateArrayList(ledger_transaction);
		        AddTransactionsToHashTbl(transactionChoice);
		    }
		    
		} catch (IOException ex) {
		    ex.printStackTrace();
		    System.out.println("Error occurred in parsecsv method due to an IOException.");
		}
		
		AddToExpenseLineChartHashTbl(expenseTransactionArrayList);
		return null;
	}
	
	private static void PrintContents(Hashtable<Character,ArrayList<Ledger>> transactionHashTbl) {
		int index = 0;
		int hashTblSize = transactionHashTbl.values().size();
		while(index < hashTblSize) {
			for(ArrayList<Ledger> ledger : transactionHashTbl.values()) {
				int ledgerArrayListSize = ledger.size();
				if(index < ledgerArrayListSize) {
					System.out.println("Choice: " + ledger.get(index).GetTransactionChoice() + "\n"
							+ "Title: " + ledger.get(index).GetTransactionTitle() + "\n"
							+ "Amount: " + ledger.get(index).GetTransactionAmount() + "\n"
							+ "Date: " + ledger.get(index).GetTransactionDate());
					System.out.println();
				}
			}
			index++;
		}
	}
	
	//Establishes monthly income then breaks down input into a 50/40/5/5 split for budegetting reasons.
	private static void MonthlyBudgetRatio() {
		float monthlyIncome = (float) 5000.00;
		monthlyExpenseGoal = (float) (monthlyIncome * 0.50);
		monthlySavingsGoal = (float) (monthlyIncome * 0.40);
		monthlyInvestmentGoal = (float) (monthlyIncome * 0.05);
		monthlyEmergencyGoal = (float) (monthlyIncome * 0.05);
		
		totalMonthlyTransactionGoal = new float[]{monthlyExpenseGoal,monthlySavingsGoal,monthlyInvestmentGoal,monthlyEmergencyGoal};
		
		budgetGoalString = "With a 50/40/5/5 split of Expenses, Savings, Investing and Emergencies your budget "
							+ "looks like: " + "\n"
							+ "Expense: $" + monthlyExpenseGoal + "\n" 
							+ "Savings: $" + monthlySavingsGoal + "\n" 
							+ "Invest: $" + monthlyInvestmentGoal + "\n"
							+ "Emergency: $" + monthlyEmergencyGoal + "\n";
		
	}
	
	private static float TotalMonthlyExpenses(ArrayList<Ledger> expenses) {
		float monthlyExpenses = 0;
		for(Ledger transaction : expenses) {
			monthlyExpenses += transaction.GetTransactionAmount();
		}
		return monthlyExpenses;
	}
	
	private static float TotalMonthlySavings(ArrayList<Ledger> savings) {
		float monthlySavings = 0;
		for(Ledger transaction : savings) {
			monthlySavings += transaction.GetTransactionAmount();
		}
		return monthlySavings;
	}
	
	private static float TotalMonthlyInvestments(ArrayList<Ledger> investments) {
		float monthlyInvestments = 0;
		for(Ledger transaction : investments) {
			monthlyInvestments += transaction.GetTransactionAmount();
		}
		return monthlyInvestments;
	}
	
	private static float TotalMonthlyForEmergency(ArrayList<Ledger> emergencies) {
		float monthlyEmergencySetAside = 0;
		for(Ledger transaction : emergencies) {
			monthlyEmergencySetAside += transaction.GetTransactionAmount();
		}
		return monthlyEmergencySetAside;
	}
	
	//TODO: Separate into multiple methods to improve readability
	//Write results to txt file for future reference.
	private static void WriteResultsToTxtFile(float monthlyExpenseTotal,float monthlySavingsTotal,float monthlyInvestmentTotal,float monthlyEmergencyTotal,
	float remainingExpenseAmount,float remainingSavingsAmount,float remainingInvestmentAmount,float remainingEmergencyAmount) {
		try {
			File monthlyBudgetFile = new File("C:\\Users\\joeyc\\Desktop\\monthly_budget_results.txt");
			System.out.println("### monthly_budget_results.txt was created");
			FileWriter monthlyBudgetFileWriter = new FileWriter(monthlyBudgetFile);
			String textForFile = MessageFormat.format("{0}" +
					 	"This month, you spent in total, Expense: ${1}, Saving: ${2}, Investment: ${3}, Emergency: ${4}" + "\n" +
						"After calculating the monthly expenses, savings, and investments based on a 50/40/5/5 breakdown, " + "\n" + 
						"Expense: ${5}" + "\n" + 
						"Savings: ${6}" + "\n" + 
						"Invest: ${7}" + "\n" +
						"Emergency: ${8}" + "\n",
						budgetGoalString, monthlyExpenseTotal, monthlySavingsTotal, monthlyInvestmentTotal,
						monthlyEmergencyTotal, remainingExpenseAmount, remainingSavingsAmount,
						remainingInvestmentAmount, remainingEmergencyAmount);
			monthlyBudgetFileWriter.write(textForFile);
			monthlyBudgetFileWriter.close();
			
			System.out.println("### Results were written to monthly_budget_results file and the file has closed.");
		}
		catch(IOException e) {
			System.out.println("An error occurred creating and writing to file due to an IOException.");
		    e.printStackTrace();
		}
	}
	
	//TODO: Separate into another method to improve readability
	//Prints the remaining amount (if any) for the expense, savings, and investment after the monthly use.
	private static void RemainingMoneyAfterESIP() {
		float monthlyExpenseTotal = TotalMonthlyExpenses(expenseTransactionArrayList);
		float monthlySavingsTotal = TotalMonthlySavings(savingsTransactionArrayList);
		float monthlyInvestmentTotal = TotalMonthlyInvestments(investmentTransactionArrayList);
		float monthlyEmergencyTotal = TotalMonthlyForEmergency(emergencyTransactionArrayList);
		
		totalMonthlyExpenses = new float[]{monthlyExpenseTotal,monthlySavingsTotal,monthlyInvestmentTotal,monthlyEmergencyTotal};
		
		float remainingExpenseAmount = monthlyExpenseGoal - monthlyExpenseTotal;
		float remainingSavingsAmount = monthlySavingsGoal - monthlySavingsTotal;
		float remainingInvestmentAmount = monthlyInvestmentGoal - monthlyInvestmentTotal;
		float remainingEmergencyAmount = monthlyEmergencyGoal - monthlyEmergencyTotal;
		
		WriteResultsToTxtFile(monthlyExpenseTotal,monthlySavingsTotal,monthlyInvestmentTotal,monthlyEmergencyTotal,
		remainingExpenseAmount,remainingSavingsAmount,remainingInvestmentAmount,remainingEmergencyAmount);
	}
	
	//Method used by the GUI's 'write' button
	protected static void WriteTransactionToTxtFile(String transaction) throws IOException {
		FileWriter fileWriter = new FileWriter(Gui.filePath,true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		try {
			
			bufferedWriter.write(transaction);
			bufferedWriter.newLine();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("An exception occurred check the WriteTransactionToTxtFile method.");
		} finally {
			if(bufferedWriter != null) {
				bufferedWriter.flush();
				bufferedWriter.close();
			}
			
		}
		
	}
	
	public static void OpsMainDriver() {
		ParseTxt();
		PrintContents(transactionHashTbl);
		MonthlyBudgetRatio();
		RemainingMoneyAfterESIP();
	}
}
