# Budget Calculator w/GUI

A simple budget calculator used for budgeting, and tracking spending.

The program takes a given monthly income (**which you have to set in the Ops class**) and the ratio of your income you wish to allocate towards: 

    Examples:
        Expenses : Rent, Spotify, Groceries, Phone, Internet...
        Savings : PC savings, general savings...
        Investments : Apple, GameStop, Tesla...
        Reserve (Emergencies) : Car, Medical...

* By default the income is set to 5K a month with:
  * Expenses : 50%
  * Savings : 40%
  * Investment : 5%
  * Emergency Reserve: 5%


View the code from the _MonthlyBudgetRatio()_ method below:

   		BigDecimal monthlyIncome = BigDecimal.valueOf(5000.00);
		BigDecimal ratioOfMonthlyIncomeForExpenses = BigDecimal.valueOf(0.50);
		BigDecimal ratioOfMonthlyIncomeForSavings = BigDecimal.valueOf(0.40);
		BigDecimal ratioOfMonthlyIncomeForInvestmentAndEmergencies = BigDecimal.valueOf(0.05);
		
		monthlyExpenseGoal = monthlyIncome.multiply(ratioOfMonthlyIncomeForExpenses).setScale(2, RoundingMode.CEILING);
		monthlySavingsGoal = monthlyIncome.multiply(ratioOfMonthlyIncomeForSavings).setScale(2, RoundingMode.CEILING);
		monthlyInvestmentGoal = monthlyIncome.multiply(ratioOfMonthlyIncomeForInvestmentAndEmergencies).setScale(2, RoundingMode.CEILING);
		monthlyEmergencyReserveGoal = monthlyIncome.multiply(ratioOfMonthlyIncomeForInvestmentAndEmergencies).setScale(2, RoundingMode.CEILING);

## The process:
1. You create a txt file where you will create a entry with a transaction choice of:
    * E : Expense
    * S : Savings
    * I : Investment
    * R : Reserve (Emergency)

2. In the transaction text box a user would insert a 'transaction' such as:
   
    E,AmazonShopping,10.00,1/4/2020
   

3. Then specify the path to the txt file where you have written the transactions down using the _'Choose File'_ button.

4. After you are satisfied with the amount of transactions you have input into your txt file, then you are ready to click the _'Calculate Budget'_ button.
   
5. Once the button has been pressed, a report will be generated (in the users home folder) along with a bar graph and a line chart.

    * The Bar Graph
      * Displays two groupings of bars, one dedicated to your monthly spending goal, and the other would be your actual spending that you documented.
    * The Line Chart
      * Displays your Expense spending trend over the duration you were reporting for.
        
You also have the ability to save the bar graph and the line chart to separate png's through the _'File'_ menubar.



