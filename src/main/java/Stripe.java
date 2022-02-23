import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;
public class Stripe {
    /*
    Question comes from https://www.1point3acres.com/bbs/thread-668514-1-1.html
    4 actions
      - 1 CHARGE, customer-> merchant
          2 CONFIRM <-mutually exclusive->REFUND,
          CHARGE->CONFIRM: approved. no refund
          CHARGE->REFUND, no confirm
          3 REFUND: refunded charge's amount should not be included
              in the payout balance. but the card network processing fee
              should be deduced from the payout balance. strip processing
              fee not be deduced in this case
          4 PAYOUT:    stripe-> merchant.
                          up until that point of receiving the action
                          print the merchant's balance from beginning
                          or the previous payout action if any
                          then merchant payout balance is reset to 0
                          include only confirmed charges
                          after deducing card network and stripe
                          processing fee
                          Strip has a fixed processing fee of 2%.
         Input Format
         N
         CARD_NETWORK<space>PERCENT
         no duplicated entries for the same card network
         M
         actions
     Constrains
         2<=N<=5 (Integer)
         0<=M<=100(Integer)
         0.1<=PERCENT<=10.0 (Float)
         CARD_NETWORK (String)
     Actions:
         /charge?network=<CARD_NETWORK>&amount=<AMOUNT>&merchant_id=<MERCHANT_ID>&charge_id=<CHARGE_ID>
         network: credit card String
         amount: 0<=AMOUNT<=4,294,967,295 Integer
         merchant_id: String
         charge_id:String
         /payout?merchant_id=<MERCHANT_ID>
         /confirm?charge_id=<CHARGE_ID>
         /refund?charge_id=<CHARGE_ID>
    Comments on the input
         - All actions are well-formed URLs. need not verifying
         the ordering of query parameters could change
         - charge_id is unique
         - input action are properly ordered. refund or confirm will not
           occur before the charge action itself
         - charge action only use the card network whose processing fee
           percentage is specified in the input
    Output
         for each payout action in the input
          merchant ID, payout balance.
          rounded *up* to the nearest whole number integer
          do not round the numbers after each action. Only the final
          number to be printed
     Example
          2
          visa 2.0
          mastercard 3.0
          3
          /charge?network=visa&amount=100&merchant_id=m001&charge_id=c001
          /confirm?charge_id=c001
          /payout?merchant_id=m001
          output
          m001, 96
          Stripe precessing fee is fixed 2%
          --------
          2
          visa 2.0
          mastercard 3.0
          5
          /charge?network=visa&amount=100&merchant_id=m001&charge_id=c001
          /charge?merchant_id=m001&amount=56network=mastercard&charge_id=c002
          /refund?charge_id=c001
          /confirm?charge_id=c002
          /payout?merchant_id=m001
          output
          m001, 52
          Stripe precessing fee is fixed 2%
          for c001: refund,
                          but need pay visa fee
                          -0.02*100=-2
          for c002: confirmed
              ‍‍‌‍‌‌‌‍‌‌‍‍‍‍‌‌‌‌‍‌              +56*(1-0.03+0.02)=53.2
          So left     51.2
          round_up(51.2)=52
         --------
          2
          visa 2.0
          mastercard 3.0
          8
          /charge?merchant_id=m001&charge_id=c001&amount=1000&network=visa
          /charge?merchant_id=m001&charge_id=c002&amount=1000&network=mastercard
          /confirm?charge_id=c001
          /confirm?charge_id=c002
          /payout?merchant_id=m001
          /charge?merchant_id=m001&charge_id=c003&amount=1000&network=visa
          /confirm?charge_id=c003
          /payout?merchant_id=m001
          output
           m001, 1910
           m001, 960
         Explain
           +1000-20-20
           +1000-20-30
           total 1910
           +1000-20-20
           total 960
         Followup
         merchant has so many refund
         so that the balance is negative, how to handle it???
     */

    private final static double stripFee = 2.0f;
    Map<String, Double> balanceTable;
    Map<String, Long> confirmedCharges;
    Map<String, Double> cardFees;
    Map<String, Long> pendingCharges;
    Map<String, String> merchantTable;
    public Stripe() {
        balanceTable = new HashMap<>();
        confirmedCharges = new HashMap<>();
        cardFees = new HashMap<>();
        pendingCharges = new HashMap<>();
        merchantTable = new HashMap<>();
    }

    public void processActions(List<String> lines) {
        int i = 0;
        int offset = Integer.parseInt(lines.get(i++));
        for(;i<=offset;i++){
            String[] items = lines.get(i).split(" ");
            cardFees.put(items[0], Double.parseDouble(items[1]));
        }
        i++;
        for(;i<lines.size();i++){
            processAction(lines.get(i));
        }
    }

    private void charge(String mid, String cid, String cardNetwork, String amountStr) {
        long amount = Long.parseLong(amountStr);
        balanceTable.putIfAbsent(mid, 0.0);
        //charge card fee
        double balance = balanceTable.get(mid);
        double cardFee = amount * cardFees.get(cardNetwork) / 100;
        balanceTable.put(mid, balance - cardFee);
        //pending charge
        pendingCharges.put(cid, amount);
        merchantTable.put(cid, mid);
    }

    private void confirm(String cid) {
        long amount = pendingCharges.remove(cid);
        String mid = merchantTable.get(cid);
        //update balance
        double balance = balanceTable.get(mid);
        double cardFee = amount * stripFee / 100;
        balanceTable.put(mid, balance + amount - cardFee);
    }

    private void refund(String cid) {
        pendingCharges.remove(cid);
        String mid = merchantTable.get(cid);
        //update balance
        double balance = balanceTable.get(mid);
        balanceTable.put(mid, balance);
    }

    private void payout(String mid) {
        System.out.println(mid+" "+ Math.ceil(balanceTable.get(mid)));
        balanceTable.put(mid, 0.0);
    }

    private void processAction(String action){
        String[] urlParts = action.split("\\?");
        String actionType = urlParts[0].substring(1);
        List<NameValuePair> pairs = URLEncodedUtils.parse(URI.create(action), "UTF-8");
        Map<String, String> map = new HashMap<>();
        for (NameValuePair pair : pairs) {
            map.put(pair.getName(), pair.getValue());
        }

        switch (actionType) {
            case "charge" -> charge(map.get("merchant_id"), map.get("charge_id"), map.get("network"), map.get("amount"));
            case "refund" -> refund(map.get("charge_id"));
            case "confirm" -> confirm(map.get("charge_id"));
            case "payout" -> payout(map.get("merchant_id"));
            default -> {}
        }
    }


    public static void main(String[] args) {
      List<String> lines = new LinkedList<>();
      lines.add("2");
      lines.add("visa 2.0");
      lines.add("mastercard 3.0");
      lines.add("3");
      lines.add("/charge?network=visa&amount=100&merchant_id=m001&charge_id=c001");
      lines.add("/confirm?charge_id=c001");
      lines.add("/payout?merchant_id=m001");
      Stripe t = new Stripe();
      t.processActions(lines);
      // m001, 96
      lines = new LinkedList<>();
      lines.add("2");
      lines.add("visa 2.0");
      lines.add("mastercard 3.0");
      lines.add("5");
      lines.add("/charge?network=visa&amount=100&merchant_id=m001&charge_id=c001");
      lines.add("/charge?merchant_id=m001&amount=56&network=mastercard&charge_id=c002");
      lines.add("/refund?charge_id=c001");
      lines.add("/confirm?charge_id=c002");
      lines.add("/payout?merchant_id=m001");
      t = new Stripe();
      t.processActions(lines);
      // m001, 52
      lines = new LinkedList<>();
      lines.add("2");
      lines.add("visa 2.0");
      lines.add("mastercard 3.0");
      lines.add("8");
      lines.add("/charge?merchant_id=m001&charge_id=c001&amount=1000&network=visa");
      lines.add("/charge?merchant_id=m001&charge_id=c002&amount=1000&network=mastercard");
      lines.add("/confirm?charge_id=c001");
      lines.add("/confirm?charge_id=c002");
      lines.add("/payout?merchant_id=m001");
      lines.add("/charge?merchant_id=m001&charge_id=c003&amount=1000&network=visa");
      lines.add("/confirm?charge_id=c003");
      lines.add("/payout?merchant_id=m001");
      t = new Stripe();
      t.processActions(lines);
      // m001, 1910
      // m001, 960
    }
}

