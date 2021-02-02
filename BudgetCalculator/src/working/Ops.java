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
	
	
	private static Hashtable<Character,ArrayList<Ledger>> AddTransactionArrayListsToHashTbl(char transactionChoice){
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
	
	private static void AddToAppropriateArrayList(Ledger ledger) {
		Character transactionChoice = ledger.GetTransactionChoice();
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
			//If we move onto the next date
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
	
	//Parse txt -> add to appropriate arraylist -> add to hash table
	private static void ParseTxt()  {
		Character transactionChoice = '\0';
		try (BufferedReader br = Files.newBufferedReader(Paths.get(Gui.filePath))) {
		    String DELIMITER = ",";
		    String line;
		    while ((line = br.readLine()) != null) {

		        String[] esipTransaction = line.split(DELIMITER);
		        
		        transactionChoice = esipTransaction[0].charAt(0);
		        String transactionTitle = esipTransaction[1];
		        float transactionAmount = Float.parseFloat(esipTransaction[2]);
		        String transactionDate = esipTransaction[3];
		        
		        Ledger ledgerTransaction = new Ledger(transactionChoice,transactionTitle,transactionAmount,transactionDate);
		        AddToAppropriateArrayList(ledgerTransaction);
		        AddTransactionArrayListsToHashTbl(transactionChoice);
		    }
		    
		} catch (IOException ex) {
		    System.out.println("--- IOException occurred in ParseTxt method.");
		    ex.printStackTrace();
		    System.exit(0);
		} catch (NullPointerException ne) {
			System.out.println("--- NullPointerException occurred in the ParseTxt method. \n "
					+ "make sure you has chosen the correct file containing your ESIP info.");
			System.exit(0);
		}
		
		
		AddToExpenseLineChartHashTbl(expenseTransactionArrayList);
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
	
	private static void WriteResultsToTxtFile(float monthlyExpenseTotal,float monthlySavingsTotal,float monthlyInvestmentTotal,float monthlyEmergencyTotal,
	float remainingExpenseAmount,float remainingSavingsAmount,float remainingInvestmentAmount,float remainingEmergencyAmount) {
		try {
			String homeFolder = System.getProperty("user.home");
			File monthlyBudgetFile = new File(homeFolder, "monthly_budget_results.txt");
			System.out.println("### monthly_budget_results.txt was created");
			FileWriter monthlyBudgetFileWriter = new FileWriter(monthlyBudgetFile);
			String textForFile = MessageFormat.format("{0}" +
					 	"This month, you spent in total, Expense: ${1}, Saving: ${2}, Investment: ${3}, Emergency: ${4}" + "\n" +
						"After calculating the monthly expenses, savings, and investments based on a 50/40/5/5 breakdown, " + "\n" + 
						"Expense: ${5}" + "\n" + 
						"Savings: ${6}" + "\n" + 
						"Invest: ${7}" + "\n" +
						"Emergency: ${8}" + "\n",
						budgetGoalString, monthlyExpenseTotal, monthlySavingsTotal, monthlyInvestmentTotal, monthlyEmergencyTotal, 
						remainingExpenseAmount, remainingSavingsAmount, remainingInvestmentAmount, remainingEmergencyAmount);
			monthlyBudgetFileWriter.write(textForFile);
			monthlyBudgetFileWriter.close();
			
			System.out.println("### Results were written to monthly_budget_results.txt file within the users 'home' directory"
					+ " and the file has closed.");
		}
		catch(IOException e) {
			System.out.println("--- An error occurred creating and writing to file due to an IOException.");
		    e.printStackTrace();
		}
	}
	
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
		Character transactionChoice = transaction.charAt(0);
		Character transactionChoiceToUpperCase = Character.toUpperCase(transactionChoice);
		String finalTransaction = transaction.replaceFirst("["+ transactionChoice + "]", transactionChoiceToUpperCase.toString());
		
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		try {
			System.out.println("### Writing [" + finalTransaction.toString() + "] to txt file...");
			bufferedWriter.write(finalTransaction);
			bufferedWriter.newLine();
			System.out.println("### Transactions was successfully written to txt file.");
			
		} catch (NullPointerException ne) { 
			System.out.println("--- NullPointerException occurred in the WriteTransactionToTxtFile method.");
			ne.printStackTrace();
		}
		finally {
			if(bufferedWriter != null) {
				bufferedWriter.flush();
				bufferedWriter.close();
			}
			
		}
		
	}
	
	//Driving method of opsclass
	public static void OpsMainDriver() {
		ParseTxt();
		MonthlyBudgetRatio();
		RemainingMoneyAfterESIP();
	}
}
