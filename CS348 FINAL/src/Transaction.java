import java.io.Serializable;

public class Transaction implements Serializable {
  // holds the constructor for the "transaction" that will be sent

  private static final long serialVersionUID = 1L;
  private double money;
  private String message;
 
  public Transaction(String message, double money){		//transaction constructor 
    
    this.money = money;
    this.message = message;
    
  }
  
  public double getAmount() {		// get amount being sent from the transaction
	  return money;
  }
  
  public String getMessage() {		// get the transaction reason
	  return message;
  }
  
}