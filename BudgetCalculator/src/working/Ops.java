package working;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.tinylog.Logger;

public class Ops {
		
	private static HashMap<Character,ArrayList<Ledger>> transactionHashMap = new HashMap<>();
	private static ArrayList<Ledger> expenseTransactionArrayList = new ArrayList<>();
	private static ArrayList<Ledger> savingsTransactionArrayList = new ArrayList<>();
	private static ArrayList<Ledger> investmentTransactionArrayList = new ArrayList<>();
	private static ArrayList<Ledger> emergencyTransactionArrayList = new ArrayList<>();
	
	private static BigDecimal monthlyExpenseGoal = new BigDecimal(0);
	private static BigDecimal monthlySavingsGoal = new BigDecimal(0);
	private static BigDecimal monthlyInvestmentGoal = new BigDecimal(0);
	private static BigDecimal monthlyEmergencyGoal = new BigDecimal(0);
	
	private static String budgetGoalString = null;
		
	protected static BigDecimal [] totalMonthlyTransactionGoal;
	protected static BigDecimal [] totalMonthlyExpenses;
	
	protected static HashMap<Integer, String> expenseHashTblForLineChart = new HashMap<>();
	
	
	private static HashMap<Character,ArrayList<Ledger>> AddTransactionArrayListsToHashMap(char transactionChoice) throws Exception{
		switch(transactionChoice) {
			case 'E':
				transactionHashMap.put(transactionChoice,expenseTransactionArrayList);
				break;
			case 'S':
				transactionHashMap.put(transactionChoice,savingsTransactionArrayList);
				break;
			case 'I':
				transactionHashMap.put(transactionChoice,investmentTransactionArrayList);
				break;
			case 'P':
				transactionHashMap.put(transactionChoice, emergencyTransactionArrayList);
				break;
			default:
				Logger.error("--- Error occurred in the AddTransactionArrayListsToHashTbl method");
				throw new Exception("--- Error occurred in the AddTransactionArrayListsToHashTbl method");
		}
		return transactionHashMap;
	}
	
	private static void AddToAppropriateArrayList(Ledger ledger) throws Exception {
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
			default:
				Logger.error("--- Error occurred in the AddToAppropriateArrayList method");
				throw new Exception("--- Error occurred in the AddToAppropriateArrayList method");
		}
	}
	
	// Adds up transactions per date
	private static void AddToExpenseLineChartHashMap(ArrayList<Ledger> expenses) {
		int resultCount = 0;
		BigDecimal dailyExpense = new BigDecimal(0);
		String tmpDate = null;
		for(Ledger transaction : expenses) {
			if(tmpDate == null) {
				tmpDate = transaction.GetTransactionDate();
				dailyExpense = transaction.GetTransactionAmount();
			}
			// If we move onto the next date
			else if(!tmpDate.equals(transaction.GetTransactionDate())){
				tmpDate = transaction.GetTransactionDate();
				dailyExpense = new BigDecimal(0);
				dailyExpense = dailyExpense.add(transaction.GetTransactionAmount());
			}	
			else {
				dailyExpense = dailyExpense.add(transaction.GetTransactionAmount());
				transaction.SetTransactionAmount(dailyExpense);
			}
			String hashValue = transaction.GetTransactionAmount() + ":" + transaction.GetTransactionDate();
			expenseHashTblForLineChart.put(resultCount,hashValue);
			resultCount++;
		}
	}
	
	// Parse txt -> add to appropriate arraylist -> add to hash table
	private static void ParseTxt() throws NullPointerException, NumberFormatException, StringIndexOutOfBoundsException  {
		Character transactionChoice = '\0';
		try (BufferedReader br = Files.newBufferedReader(Paths.get(Gui.filePath))) {
		    String DELIMITER = ",";
		    String line;
		    while ((line = br.readLine()) != null) {

		        String[] esipTransaction = line.split(DELIMITER);
		        
		        transactionChoice = esipTransaction[0].charAt(0);
		        String transactionTitle = esipTransaction[1];
		        BigDecimal transactionAmount = new BigDecimal(esipTransaction[2]);
		        String transactionDate = esipTransaction[3];
		        
		        Ledger ledgerTransaction = new Ledger(transactionChoice,transactionTitle,transactionAmount,transactionDate);
		        AddToAppropriateArrayList(ledgerTransaction);
		        AddTransactionArrayListsToHashMap(transactionChoice);
		    }
		    
		} catch (IOException ioe) {
		    Logger.error("--- IOException occurred in ParseTxt method.");
		    ioe.printStackTrace();
		    System.exit(0);
		} catch (Exception ex) {
		    Logger.error("--- Exception occurred in ParseTxt method.");
		    ex.printStackTrace();
		    System.exit(0);
		}
		AddToExpenseLineChartHashMap(expenseTransactionArrayList);
	}
	
	// Establishes monthly income then breaks down input into a 50/40/5/5 split for budgeting reasons.
	private static void MonthlyBudgetRatio() {
		BigDecimal monthlyIncome = BigDecimal.valueOf(5156.67);
		BigDecimal ratioOfMonthlyIncomeForExpenses = BigDecimal.valueOf(0.50);
		BigDecimal ratioOfMonthlyIncomeForSavings = BigDecimal.valueOf(0.40);
		BigDecimal ratioOfMonthlyIncomeForInvestmentAndEmergencies = BigDecimal.valueOf(0.05);
		
		monthlyExpenseGoal = monthlyIncome.multiply(ratioOfMonthlyIncomeForExpenses).setScale(2, RoundingMode.CEILING);
		monthlySavingsGoal = monthlyIncome.multiply(ratioOfMonthlyIncomeForSavings).setScale(2, RoundingMode.CEILING);
		monthlyInvestmentGoal = monthlyIncome.multiply(ratioOfMonthlyIncomeForInvestmentAndEmergencies).setScale(2, RoundingMode.CEILING);
		monthlyEmergencyGoal = monthlyIncome.multiply(ratioOfMonthlyIncomeForInvestmentAndEmergencies).setScale(2, RoundingMode.CEILING);
		
		totalMonthlyTransactionGoal = new BigDecimal[]{monthlyExpenseGoal,monthlySavingsGoal,monthlyInvestmentGoal,monthlyEmergencyGoal};
		
		budgetGoalString = "With a 50/40/5/5 split of Expenses, Savings, Investing and Emergencies your budget "
							+ "looks like: " + "\n"
							+ "Expense: $" + monthlyExpenseGoal + "\n" 
							+ "Savings: $" + monthlySavingsGoal + "\n" 
							+ "Invest: $" + monthlyInvestmentGoal + "\n"
							+ "Emergency: $" + monthlyEmergencyGoal + "\n";
	}
	
	private static BigDecimal TotalMonthlyExpenses(ArrayList<Ledger> expenses) {
		BigDecimal monthlyExpenses = new BigDecimal(0);
		for(Ledger transaction : expenses) {
			monthlyExpenses = monthlyExpenses.add(transaction.GetTransactionAmount());
		}
		return monthlyExpenses;
	}
	
	private static BigDecimal TotalMonthlySavings(ArrayList<Ledger> savings) {
		BigDecimal monthlySavings = new BigDecimal(0);
		for(Ledger transaction : savings) {
			monthlySavings = monthlySavings.add(transaction.GetTransactionAmount());
		}
		return monthlySavings;
	}
	
	private static BigDecimal TotalMonthlyInvestments(ArrayList<Ledger> investments) {
		BigDecimal monthlyInvestments = new BigDecimal(0);
		for(Ledger transaction : investments) {
			monthlyInvestments = monthlyInvestments.add(transaction.GetTransactionAmount());
		}
		return monthlyInvestments;
	}
	
	private static BigDecimal TotalMonthlyForEmergency(ArrayList<Ledger> emergencies) {
		BigDecimal monthlyEmergencySetAside = new BigDecimal(0);
		for(Ledger transaction : emergencies) {
			monthlyEmergencySetAside = monthlyEmergencySetAside.add(transaction.GetTransactionAmount());
		}
		return monthlyEmergencySetAside;	
	}
	
	private static void WriteResultsToTxtFile(BigDecimal monthlyExpenseTotal,BigDecimal monthlySavingsTotal,BigDecimal monthlyInvestmentTotal,BigDecimal monthlyEmergencyTotal,
	BigDecimal remainingExpenseAmount,BigDecimal remainingSavingsAmount,BigDecimal remainingInvestmentAmount,BigDecimal remainingEmergencyAmount) {
		try {
			String homeFolder = System.getProperty("user.home");
			File monthlyBudgetFile = new File(homeFolder, "monthly_budget_results.txt");
			Logger.info("### monthly_budget_results.txt was created");
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
			
			Logger.info("### Results were written to monthly_budget_results.txt file within the users 'home' directory"
					+ " and the file has closed.");
		}
		catch(IOException e) {
			Logger.error("--- An error occurred creating and writing to file due to an IOException.");
		    e.printStackTrace();
		}
	}
	
	private static void RemainingMoneyAfterESIP() {
		BigDecimal monthlyExpenseTotal = TotalMonthlyExpenses(expenseTransactionArrayList);
		BigDecimal monthlySavingsTotal = TotalMonthlySavings(savingsTransactionArrayList);
		BigDecimal monthlyInvestmentTotal = TotalMonthlyInvestments(investmentTransactionArrayList);
		BigDecimal monthlyEmergencyTotal = TotalMonthlyForEmergency(emergencyTransactionArrayList);
		
		totalMonthlyExpenses = new BigDecimal[]{monthlyExpenseTotal,monthlySavingsTotal,monthlyInvestmentTotal,monthlyEmergencyTotal};
		
		BigDecimal remainingExpenseAmount = monthlyExpenseGoal.subtract(monthlyExpenseTotal);
		BigDecimal remainingSavingsAmount = monthlySavingsGoal.subtract(monthlySavingsTotal);
		BigDecimal remainingInvestmentAmount = monthlyInvestmentGoal.subtract(monthlyInvestmentTotal);
		BigDecimal remainingEmergencyAmount = monthlyEmergencyGoal.subtract(monthlyEmergencyTotal);
		
		WriteResultsToTxtFile(monthlyExpenseTotal,monthlySavingsTotal,monthlyInvestmentTotal,monthlyEmergencyTotal,
		remainingExpenseAmount,remainingSavingsAmount,remainingInvestmentAmount,remainingEmergencyAmount);
	}
	
	protected static void WriteTransactionToTxtFile(String transaction) throws IOException {
		FileWriter fileWriter = new FileWriter(Gui.filePath,true);
		Character transactionChoice = transaction.charAt(0);
		Character transactionChoiceToUpperCase = Character.toUpperCase(transactionChoice);
		String finalTransaction = transaction.replaceFirst("["+ transactionChoice + "]", transactionChoiceToUpperCase.toString());
		
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		try {
			Logger.info("### Writing [" + finalTransaction + "] to txt file...");
			bufferedWriter.write(finalTransaction);
			bufferedWriter.newLine();
			Logger.info("### Transactions was successfully written to txt file.");
		}
		finally {
			bufferedWriter.flush();
			bufferedWriter.close();
		}
	}
	
	// Driving method of Opsclass
	public static void OpsMainDriver() {
		ParseTxt();
		MonthlyBudgetRatio();
		RemainingMoneyAfterESIP();
	}
}
