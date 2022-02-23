import java.util.*;

public class Capital {
  /*
     Question comes from strip capital
     https://www.1point3acres.com/bbs/thread-795712-1-1.html
     load:  charges a fixed load fee on top of the original loan amount.
     some percentage of the merchant's future sales goes towards repayment, until the total owed amount is repaid
     Design a bookkeeping system for a modified version of Stripe Capital.
     4 API
      1 merchant create a loan
      2 merchant pay down a loan manually
      3 merchant process transactions, from which some percentage of the processed amount goes towards repayment towards
        a loan
      4 merchant increase an existing loan's amount.
    Task:
      evaluate each line of stdin: API method: comma seprated parameters for the API
      all evaluating all actions, print out a list of $merchant_id, $outstanding_debt pairs
          skipping over merchants who do not have an outstanding balance.
          the list should be lexicographically sorted by the merchant ID
   Keep in mind:
     input format are validate and parsable
     code should be correct and with good quality
  System Behavior
     - The version of Capital will represent all monetary amount as U.S. cents in integers
       e.g. amount =1000 => $10.00 USD
     - A merchant may have multiple outstanding loans
     - loan IDs are unique to a given merchant only
     - a loan's outstanding balance should never to negative. ignore the remaining amount in
       the casing of overpayment.
     - after a loan is fully paid off it becomes inactive and a merchant can not increase its amount
     - truncate repayments when applicable. e.g. if withholding from a transaction is 433.64 cents
       truncate to 433 cents
     - handle invalid API actions, ex: attempting to pay-off a nonexistent loan
     */

  private Map<String, Map<String, Integer>> ledger;
  public Capital() {
    ledger = new HashMap<>();
  }
  /*
  Assume input is ready:
      List<String> lines=new LinkedList<>();
      try(BufferedReader br=  Files.newBufferedReader(Paths.get(""))){
       String line;
       while((line=br.readLine())!=null){
        lines.add(line);
     }
     evaluateAndPrint(lines);
   }
   */
  public void evaluateAndPrint(List<String> lines) {
    for (String line : lines) {
      String[] fp = line.split(": ");
      String[] ps = fp[1].split(",");
      String method = fp[0];
      String merchantId = ps[0];
      String loanId = ps[1];
      int amount = Integer.parseInt(ps[2]);

      switch (method) { // use reflection?
        case "CREATE_LOAN":
//          validParameters("CREATE_LOAN", ps, 3);
          createLoan(merchantId, loanId, amount);
          break;
        case "PAY_LOAN":
//          validParameters("PAY_LOAN", ps, 3);
          payLoan(merchantId, loanId, amount);
          break;
        case "INCREASE_LOAN":
//          validParameters("INCREASE_LOAN", ps, 3);
          increase_loan(merchantId, loanId, amount);
          break;
        case "TRANSACTION_PROCESSED":
//          validParameters("TRANSACTION_PROCESSED", ps, 4);
          int repayPercent = Integer.parseInt(ps[3]);
          transaction_processed(merchantId, loanId, amount, repayPercent);
          break;
        default:
          throw new RuntimeException("not defined API");
      }
    }
    print();
  }
  private void validParameters(String api, String[] ps, int expected) {
    if (ps.length != expected) throw new RuntimeException(api + " API parameters number is wrong");
  }
  /*
   lexicographically sorted by the merchant ID
  */
  private void print() {
    Map<String, Integer> r = new TreeMap<>();
    for (String m : ledger.keySet()) {
      int total = 0;
      for (int av : ledger.get(m).values()) {
        total += av;
      }
      if (total > 0) r.put(m, total);
    }
    for (Map.Entry<String, Integer> e : r.entrySet()) {
      System.out.println(e.getKey() + " " + e.getValue());
    }
  }
  /*
  merchant_id is non-empty
  loan_id is non-empty
  amount:  >=0, initial loan amount
  Ex:  CREATE_LOAD: merchant1, loan1, 1000
   */
  public void createLoan(String merchant_id, String loan_id, Integer amount) {
    ledger.putIfAbsent(merchant_id, new HashMap<>());
    if (ledger.get(merchant_id).containsKey(loan_id))
      throw new RuntimeException("account exists already");
    ledger.get(merchant_id).put(loan_id, amount);
  }
  /*
  Merchant pays off their loan on a one-time basis
   merchant_id is non-empty
   loan_id is non-empty
   amount:  >=0, The amount given back to Stripe
  Ex: PAY_LOAN: merchant1, loan1, 1000
   */
  public void payLoan(String merchant_id, String loan_id, Integer amount) {
//    validate(merchant_id, loan_id);
    if (!ledger.containsKey(merchant_id) || !ledger.get(merchant_id).containsKey(loan_id))
      throw new RuntimeException("account not exists or be inactive already");

    int balance = ledger.get(merchant_id).get(loan_id);
    ledger.get(merchant_id).put(loan_id, balance - amount);
    if (balance - amount <= 0) {
      ledger.get(merchant_id).remove(loan_id); // inactive and disappear.
    }
  }
  /*
  Merchant increases an existing loan
  merchant_id is non-empty
  loan_id is non-empty
  amount:  >=0, the  amount to increase the loan by
  Ex: INCREASE_LOAN: merchant1, loan1, 100
   */
  public void increase_loan(String merchant_id, String loan_id, Integer amount) {
    if (!ledger.containsKey(merchant_id) || !ledger.get(merchant_id).containsKey(loan_id))
      throw new RuntimeException("account not exists or be inactive already");

    int balance = ledger.get(merchant_id).get(loan_id);
    if (balance + amount < 0)
      throw new RuntimeException("invalid amount increase");

    ledger.get(merchant_id).put(loan_id, balance + amount);
  }
  /*
   A single transaction,
   A portion of the transaction amount is withheld to pay down the merchant's outstanding loans.
   merchant_id is non-empty
   loan_id is non-empty
   amount:  >=0, the amount of transaction processed
   repayment_percentage: the percentage of the transaction amount that goes towards repayment
                         1<=x<=100
   Ex: TRANSACTION_PROCESSED: merchant1, loan1, 500, 10
  */
  public void transaction_processed(
      String merchant_id, String loan_id, Integer amount, Integer repayment_percentage) {
    if (!ledger.containsKey(merchant_id) || !ledger.get(merchant_id).containsKey(loan_id))
      throw new RuntimeException("account not exists or be inactive already");

    int balance = ledger.get(merchant_id).get(loan_id);
    int repay = amount * repayment_percentage / 100;
    ledger.get(merchant_id).put(loan_id, balance - repay);

    if (balance - repay <= 0) {
      ledger.get(merchant_id).remove(loan_id); // inactive and disappear.
    }
  }
//  private void validate(String merchant_id, String loan_id) {
//    if (!ledger.containsKey(merchant_id) && ledger.get(merchant_id).containsKey(loan_id))
//      throw new RuntimeException("account not exists or be inactive already");
//  }



  public static void main(String[] args) {
    Capital t = new Capital();
    List<String> input = new ArrayList<>();
    input.add("CREATE_LOAN: acct_foobar,loan1,5000");
    input.add("PAY_LOAN: acct_foobar,loan1,1000");
    List<String> output = new ArrayList<>();
    t.evaluateAndPrint(input);
    output.add("acct_foobar,4000");
    input = new ArrayList<>();
    input.add("CREATE_LOAN: acct_foobar,loan1,5000");
    input.add("CREATE_LOAN: acct_foobar,loan2,5000");
    input.add("TRANSACTION_PROCESSED: acct_foobar,loan1,500,10");
    input.add("TRANSACTION_PROCESSED: acct_foobar,loan2,500,1");
    t = new Capital();
    t.evaluateAndPrint(input);
    output = new ArrayList<>();
    output.add("acct_foobar,9945");
    input = new ArrayList<>();
    input.add("CREATE_LOAN: acct_foobar,loan1,1000");
    input.add("CREATE_LOAN: acct_foobar,loan2,2000");
    input.add("CREATE_LOAN: acct_barfoo,loan1,3000");
    input.add("TRANSACTION_PROCESSED: acct_foobar,loan1,100,1");
    input.add("PAY_LOAN: acct_barfoo,loan1,1000");
    input.add("INCREASE_LOAN: acct_foobar,loan2,1000");
    t = new Capital();
    t.evaluateAndPrint(input);
    output = new ArrayList<>();
    output.add("acct_barfoo,2000");
    output.add("acct_foobar,3999");
  }
}
