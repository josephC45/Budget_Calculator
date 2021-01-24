package working;

/*
 * Ledger object class
 */

public class Ledger {
	char transaction_choice;
	String transaction_title;
	float transaction_amount;
	String transaction_date;
	
	public Ledger(char transaction_choice, String transaction_title, float transaction_amount, String transaction_date) {
		this.transaction_choice = transaction_choice;
		this.transaction_title = transaction_title;
		this.transaction_amount = transaction_amount;
		this.transaction_date = transaction_date;
	}
	
	//Setter and Getter methods for the ledger object.
	
	public void SetTransactionChoice(char transaction_choice) {
		this.transaction_choice = transaction_choice;
	}
	public char GetTransactionChoice() {
		return this.transaction_choice;
	}
	
	public void SetTransactionTitle(String transaction_title) {
		this.transaction_title = transaction_title;
	}
	
	public String GetTransactionTitle() {
		return this.transaction_title;
	}
	
	public void SetTransactionAmount(float transaction_amount) {
		this.transaction_amount = transaction_amount;
	}
	
	public float GetTransactionAmount() {
		return this.transaction_amount;
	}
	
	public void SetTransactionDate(String transaction_date) {
		this.transaction_date = transaction_date;
	}
	
	public String GetTransactionDate() {
		return this.transaction_date;
	}
}
