import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/*
Question comes from https://www.1point3acres.com/bbs/thread-796230-1-1.html
Exercise Date and Time operation with JDK 17
Date year month day
Time: hour minutes second, million second
`DateTimeFormatter.ofPattern(String)` // `f`
`new SimpleDateFormat( String)`    // `sf`
The String e.g.:    "yyyy-MM-dd+HH:mm:ss"
      y year
      M month
      m minute
      H hour 24
      h hour 12
      s second
Date d=sf.part(String);
         d.setSeconds(0); // can change value
LocalDateTime dtime = LocalDateTime.parse(string, f);
// used for compare and diff
if (a.isBefore(b)) ...
Duration.between(a, dtime).toSeconds());
Date d=new Date(long_timestamp)
Instant inst = Instant.ofEpochMilli(long_timestamp);
LocalDateTime t = LocalDateTime.ofInstant(inst, TimeZone.getDefault().toZoneId());
*/
/*
logic
-    input is in ascending order by timestamp, not 2 line have the same timestamp
-    log line format: epoch timestamp(in second),event_type,user_email
                        E.g.: 1623834502,invite_requested,john@gmail.com
- bolt: in same minute >= 5 req
- use the fist timestamp for "invite_requested" to calculate the average time
- no duplicated timestamp for    "invite_send" and "invite_activated"
     calculate the average and verify the time of "invite_requested" <"invite_send" < "invite_activated"
         1m2s,send,john
         1m3s,req,john
         1m5s,activated,john
         1m6s,req,john
         1m6s,req,john
         1m6s,req,john
         1m6s,req,john
         1m6s,req,john
- user can activate their beta invite once Stripe has sent it to them.
*/
public class BetaInvites {
    Map<String, Long> requestedUser = new HashMap<>();
    Map<String, Long> invitedUser = new HashMap<>();
    Map<String, Long> intervals = new HashMap<>();
    Set<String> bots = new HashSet<>();
    public void loadLogFile(String logFile, int N) {
        //check encoding
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(logFile))) { //utf-8
            String line;
            while ((line = reader.readLine()) != null) {
                parseLog(line);
                N--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    Map<String, Map<String, Integer>> minutes = new HashMap<>();
    public void parseLog(String line){ // throw parse exceptions
        String[] items = line.split(",");
        if(items.length != 3)
            return;//exception

        long seconds = Long.parseLong(items[0]);
        String type = items[1].trim();
        String uid = items[2].trim();
        if("invite_requested".equals(type)){
            detectBot(uid, seconds);
            //record the earliest requested time
            if(!requestedUser.containsKey(uid)){
                requestedUser.put(uid, seconds);
            }
        }else if("invite_sent".equals(type)){
            invitedUser.put(uid, seconds);
        }else if("invite_activated".equals(type)){
            if(requestedUser.containsKey(uid) && invitedUser.containsKey(uid) && requestedUser.get(uid) < invitedUser.get(uid) && invitedUser.get(uid) <= seconds){
                intervals.put(uid, seconds - requestedUser.get(uid));
            }
        }else{
            // exceptions
        }
    }

    private void detectBot(String uid, long seconds) {
        long msTime = seconds*1000;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm");
        Date date  = new Date(msTime);
        String minuteStr = sdf.format(date);
        minutes.putIfAbsent(uid, new HashMap<>());
        minutes.get(uid).put(minuteStr, minutes.get(uid).getOrDefault(minuteStr, 0)+1);
        if(minutes.get(uid).get(minuteStr) >= 5){
            bots.add(uid);
        }
    }

    public String output(){
        long totalTime = 0, invites=0;
        for(String uid : intervals.keySet()){
            if(!bots.contains(uid)) {
                totalTime += intervals.get(uid);
                invites++;
            }
        }
        return bots.size() +" "+(invites==0?0:totalTime/invites);
    }

    public static void main(String[] args){
        BetaInvites beta = new BetaInvites();
        beta.loadLogFile("data/log.csv", 10);
        System.out.println(beta.output());
    }
}

