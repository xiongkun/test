import java.util.*;
public class Verification {
      /*
      Question comes from https://www.1point3acres.com/bbs/thread-802430-1-1.html
             each line:
             merchant_id,field_name,field_value'
             merchant_id: unique
             field_name: country:US|JP|FR
                                         business_type: individual|company
                                         or one of the values from the table below such as
                                         first_name, support_email. need not validate field_name
             more info may be provided than is required for verification
        all input are valid
        input line maybe in any order. inputs for different merchants may also be interleaved
        individual table
               fields       country1      country2      country3
                                          US                    JP                    FR
                    SSN                    x
                    tax_id                                   x                     x
        companies table
               fields       country1      country2      country3
                                                US                    JP                    FR
              director_name                                                  x
      Output
        print one line per merchant_id in lexicographical order
        if all required fields have been provided according to country and business_type
        print
        merchant_id:VERIFIED
        otherwise
        merchant_id:UNVERIFIED:fieldname1,fieldname2,...
        comma separated list of all the required fields not provided for the merchant in lexicographical order
             - any or both of country and business_type
             - those in related tables
       Ex1:
        input
             1
             acct_123,business_type,company
        output
             acct_123:UNVERIFIED:country
       Ex2:
       input:
        8
        acct_123,country,US
        acct_123,business_type,individual
        acct_123,first_name,Jane
        acct_123,last_name,Doe
        acct_123,date_of_birth,01011970
        acct_123,social_security_number,123456789
        acct_123,email,test@example.com
        acct_123,phone,555555555
       output:
        acct_123:VERIFIED
       EX3:
        input:
        12
        acct_123,tax_id_number,12345689
        acct_123,country,FR
        acct_123,business_type,company
        acct_456,business_type,individual
        acct_456,country,JP
        acct_456,first_name,Mei
        acct_456,last_name,Sato
        acct_456,first_name_kana,Mei
        acct_456,last_name_kana,Sato
        acct_456,data_of_birth,01011970
        acct_456,tax_id_number,123456
        acct_456,email,test@example.com
        output
        acct_123:UNVERIFIED:director_name,name,phone
        acct_456:VERIFIED
              */
              // assume it is available,
              // key is country value+" "+business_type,
              // values are required fields sorted in lexicographically order
//      private static Map<String, List<String>> required = new HashMap<>();
    private Map<String, Map<String,List<String>>> required = new HashMap<>();
    public Verification() {
        Map<String, List<String>> individual =  new HashMap<>();
        Map<String, List<String>> company =  new HashMap<>();
        required.put("company", new HashMap<>());
        // country individual
        List<String> fs = Arrays.asList("first_name", "last_name", "date_of_birth", "social_security_number", "email", "phone");
        Collections.sort(fs);
        individual.put("US", fs);
        fs = Arrays.asList("first_name","last_name","first_name_kana","last_name_kana","date_of_birth","tax_id_number","email");
        Collections.sort(fs);
        individual.put("JP", fs);
        fs = Arrays.asList("first_name", "last_name", "tax_id_number", "email", "phone");
        Collections.sort(fs);
        individual.put("FR", fs);

        required.put("individual", individual);
        // country company
        fs = Arrays.asList("name", "employer_id_number", "email", "phone");
        Collections.sort(fs);
        company.put("US", fs);
        fs = Arrays.asList("name", "tax_id_number", "phone");
        Collections.sort(fs);
        company.put("JP", fs);
        fs = Arrays.asList("name", "director_name", "tax_id_number", "phone");
        Collections.sort(fs);
        company.put("FR", fs);

        required.put("company", company);
      }
      public List<String> verify_merchants(List<String> lines) {
          Map<String, Map<String, String>> merchantInfo = new TreeMap<>();
          for(String line : lines){
              String[] items = line.split(",");
              String mid = items[0].trim();
              String field = items[1].trim();
              String value = items[2].trim();
              merchantInfo.putIfAbsent(mid, new HashMap<>());
              merchantInfo.get(mid).put(field, value);
          }

          List<String> results = new ArrayList<>();
          for(String merchant : merchantInfo.keySet()){
              StringBuilder info = new StringBuilder(merchant);
              Map<String, String> fields = merchantInfo.get(merchant);
              if(fields.containsKey("country") && fields.containsKey("business_type")) {
                  String country = fields.get("country");
                  String type = fields.get("business_type");
                  StringBuilder absentFields = new StringBuilder();
                  for(String requiredField : required.get(type).get(country)){
                      if(!fields.containsKey(requiredField)){
                          absentFields.append(requiredField).append(",");
                      }
                  }
                  if(absentFields.length() > 0) {
                      absentFields.deleteCharAt(absentFields.length() - 1);
                      info.append(":").append("UNVERIFIED").append(":").append(absentFields.toString());
                  }else {
                      info.append(":").append("VERIFIED");
                  }
                  results.add(info.toString());
              }else{
                  info.append(":").append("UNVERIFIED").append(":");
                  if(!fields.containsKey("country")){
                      info.append("country").append(",");
                  }
                  if(!fields.containsKey("business_type")){
                      info.append("business_type").append(",");
                  }
                  info.deleteCharAt(info.length()-1);
                  results.add(info.toString());
              }
          }
        return results;
      }
      public static void main(String[] args) {
        Verification v = new Verification();
        System.out.println(Arrays.toString(v.verify_merchants(Arrays.asList("acct_123,business_type,company")).toArray())
                                            .equals("[acct_123:UNVERIFIED:country]"));

        System.out.println(Arrays.toString(v.verify_merchants(Arrays.asList("acct_123,country,US","acct_123,business_type,individual",
                                                                                                        "acct_123,first_name,Jane",
                                                                                                        "acct_123,last_name,Doe",
                                                                                                        "acct_123,date_of_birth,01011970",
                                                                                                        "acct_123,social_security_number,123456789",
                                                                                                        "acct_123,email,test@example.com",
                                                                                                        "acct_123,phone,555555555")).toArray()));

        System.out.println(Arrays.toString(v.verify_merchants(Arrays.asList("acct_123,tax_id_number,12345689",
                                                                                                        "acct_123,country,FR",
                                                                                                        "acct_123,business_type,company",
                                                                                                        "acct_456,business_type,individual",
                                                                                                        "acct_456,country,JP",
                                                                                                        "acct_456,first_name,Mei",
                                                                                                        "acct_456,last_name,Sato",
                                                                                                        "acct_456,first_name_kana,Mei",
                                                                                                        "acct_456,last_name_kana,Sato",
                                                                                                        "acct_456,date_of_birth,01011970",
                                                                                                        "acct_456,tax_id_number,123456",
                                                                                                        "acct_456,email,test@example.com")).toArray()));

      }
}

