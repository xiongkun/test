//问题一
//        Given logs and removed_time calculate the total penalty score
//        logs consist of 0 and 1 in one string
//        ex) "0 0 0 1 0 0"
//        0 means the server was up and running
//        1 means the server was down and not functioning
//
//        if the server is taken down too early you get a +1 penalty for reach the running server after removed_time
//        if the server is taken down too late, you get a +1 penalty for reach a server that was down before removed_time
//
//        Example 1
//
//        "0 0 1 0" remove_time = 2
//
//               | when the server is shut down, there is a penalty of 1 since during time 3-4 log4 indicated it was on when it should have been off.
//        penalty = 1
//
//        Example 2
//        "0 0 1 0" remove_time = 0
//        penalty = 3
//        log 1, 2, 4 was on when it should have been off
//
//        Example 3
//        "1 1 1 0" remove_time = 0
//        penalty = 1
//        only log 4 was on when it should have been off
//
//        问题二 （在问题一的基础上）
//        write another function to give logs find when is the best time to take the server down
//
//        Example
//        log "1 1 1 0"
//        best_time = 0
//        because for time 1 2 3 4 penalties will be 2 3 4 3
//        at time 1 penalty is 1 which is the lowest
//
//
//        问题三 （在问题二的基础上）
//
//        write another function which can take in logs this time with 'BEGIN' 'END' '0' '1' '\n' they can be out of order
//        There are no nested loops,
//        ex) 'BEGIN END BEGIN BEGIN 1 0 0 END END 0 0 1'
//        The only valid sequence is BEGIN 1 0 0 END
//        It must start with BEGIN and ‍‌‍‌‌‍‌‌‍‍‌‍‍‌‍‍‌‌end with END
//        There can be multiple valid sequences, return a list of the best times to take the server down
//
//        Example 1
//        "BEGIN BEGIN 1 0 0 END"
//        return [3]
//        because the valid sequence is "1 0 0" now use the function we wrote in part 2 and return it in a list.
//
//        Example 2
//        "BEGIN BEGIN 1 0 0 END 0 0 0 1 BEGIN 1 1 1 0 END"
//        return [3, 0]
//        valid sequence is "1 0 0" and "1 1 1 0"

import java.util.*;
/*
Throughout this interview, we'll write code to analyze a simple server process uptime log. These logs are much simplified, and are just strings of space separated 0's and 1's. The log is a string of binary digits (e.g. "0 0 1 0"). Each digit corresponds to 1 hour of the server running:
"1" = <crashed>, "down" // server process crashed during the hour
"0" = <didn't crash>, "up" // server process did not crash during the hour
EXAMPLE: A server with log "0 0 1 0" ran for 4 hours and its process crashed during hour #3
   hour: |1|2|3|4|
   log : |0|0|1|0|
              ^
              |
             down during hour #3
We can *permanently remove* a server at the beginning of any hour during its operation. A server is on the network until it is removed. Note that a server stays POWERED ON after removal, it's just not on the network.
We'd like to understand the best times to remove a server. So let's introduce an aggregate metric called a "penalty" for removing a server at a bad time.
EXAMPLE: Remove a server with log "0 0 1 0"
    hour :  | 1 | 2 | 3 | 4 |
    log  :  | 0 | 0 | 1 | 0 |
remove_at:  0   1   2   3   4   // remove_at being `x` means "server removed before hour `x+1`"
            ^               ^
            |               |
     before hour #1         after hour #4
We define our penalty like this:
+1 penalty for each DOWN hour when a server is on the network
+1 penalty for each UP hour after a server has been removed
Further Examples:
EXAMPLE:
   hour :   1 2 3 4     // total penalty = 3  (3 server-up hours after remove)
   log  :   0 0 1 0
           ^
           |
         remove_at = 0
   hour :   1 2 3 4     // total penalty = 1  (1 server-down hour before remove)
   log  :   0 0 1 0
                   ^
                   |
                 remove_at = 4
Note that for a server log of length `n` hours, the remove_at variable can range from 0, meaning "before the first hour" to n, meaning "after the final hour".
1a) Write a function: compute_penalty, that computes the total penalty, given a server log (as a string) AND a time at which we removed the server from the network (call that  variable remove_at). In addition to writing this function, you should use tests to demonstrate that it's correct.
## Examples
compute_penalty("0 0 1 0", 0) should return 3
compute_penalty("0 0 1 0", 4) should return 1
1b) Use your answer for compute_penalty to write another function: find_best_removal_time, that returns the best remove_at hour, given a server log. Again, you should use tests to demonstrate that it's correct.
## Example
find_best_removal_time("0 0 1 1") should return 2
2a) Now that we're able to analyze single server logs, let's analyze some aggregate logs. Aggregate logs are text files that contain lots of logs. The files contain only BEGIN, END, 1, 0, spaces and newlines. Aggregate logs include some servers that aren’t actually finished, so we might have some BEGINs scattered throughout. We'll only consider inner BEGINs and ENDs to be valid log sequences. Put another way, any sequence of 0s and 1s surrounded by BEGIN and END forms a valid sequence. For example, the sequence "BEGIN BEGIN BEGIN 1 1 BEGIN 0 0 EN‍‍‌‍‌‌‌‍‌‌‍‍‍‍‌‌‌‌‍‌D 1 1 BEGIN" has only one valid sequence "BEGIN 0 0 END".
Write a function get_best_removal_times, that takes the file's contents as a parameter, and returns an array of best removal hours for every valid server log in that file.
Note: that logs can span 1 or many lines.
Again, you should use tests to demonstrate that your solution is correct.
## Example
get_best_removal_times("BEGIN BEGIN \nBEGIN 1 1 BEGIN 0 0\n END 1 1 BEGIN") should return an array: [2]

 */
public class RemoveServer0 {

    public static void main(String[] args) {
        System.out.println("hello");

        String case1 = "0 0 1 0";
        int expected = 1;
        int actual = getPenalty(case1, 2);
        assert actual == expected : String.format("expected: %d, but got: %d", expected,  actual);

        String case2 = "0 0 1 0";
        expected = 3;
        actual = getPenalty(case2, 0);
        assert actual == expected : String.format("expected: %d, but got: %d", expected,  actual);


        String case3 ="1 1 1 0";
        expected = 1;
        actual = getPenalty(case3, 0);
        assert actual == expected : String.format("expected: %d, but got: %d", expected,  actual);


        String case4 ="1 1 1 0";
        expected = 0;
        actual = bestTimeToShutDown(case4);
        assert actual == expected : String.format("expected: %d, but got: %d", expected,  actual);


        String case5 = "0 0 1 0";
        expected = 2;
        actual = bestTimeToShutDown(case5);
        assert actual == expected : String.format("expected: %d, but got: %d", expected,  actual);


        String case6 = "BEGIN END BEGIN BEGIN 1 0 0 END END 0 0 1";
        List<Integer> exp = new ArrayList<>(Arrays.asList(3));
        List<Integer> act = parseLogs(case6);
        assert exp.equals(act) : String.format("expected: %s, but got: %s", exp,  act);

        String case7 = "BEGIN BEGIN 1 0 0 END";
        exp = new ArrayList<>(Arrays.asList(3));
        act = parseLogs(case7);
        assert exp.equals(act) : String.format("expected: %s, but got: %s", exp,  act);

        String case8 = "BEGIN BEGIN 1 0 0 END 0 0 0 1 BEGIN 1 1 1 0 END";
        exp = new ArrayList<>(Arrays.asList(3,0));
        act = parseLogs(case8);
        assert exp.equals(act) : String.format("expected: %s, but got: %s", exp,  act);
    }

    public static int getPenalty(String logs, int removed) {
        if (logs == null || logs.length() == 0) {
            return 0;
        }

        String[] parsed = logs.split("\\s");
        int penalty = 0;
        for (int i = 0; i < parsed.length; i++) {
            if ((i < removed && parsed[i].equals("1"))
                    || (i >= removed && parsed[i].equals("0"))) {
                penalty++;
            }
        }

        return penalty;
    }

    public static int bestTimeToShutDown(String logs) {
        if (logs == null || logs.length() == 0) {
            return -1;
        }

        String[] parsed = logs.split("\\s");
        int len = parsed.length;
        int[] onesBefore = new int[len];
        int[] zerosAfter = new int[len];

        for (int i = 0; i < len; i++) {
            int beforeOne = parsed[i].equals("1") ? 1 : 0;
            int afterZero = (parsed[len-i-1].equals("0") ? 1 : 0);
            onesBefore[i] = (i == 0 ? 0 : onesBefore[i-1]) + beforeOne;
            zerosAfter[len-i-1] = (i == 0 ? 0 : zerosAfter[len-i]) + afterZero;
        }

        int min = Integer.MAX_VALUE, curr = 0, ans = 0;
        for (int i = 0; i <= len; i++) {
            curr = (i==0? 0 :onesBefore[i-1]) + (i==len ? 0 : zerosAfter[i]);
            if (curr < min) {
                min = curr;
                ans = i;
            }
        }

        return ans;
    }

    public static List<Integer> parseLogs(String logs) {
        List<Integer> results = new ArrayList<>();
        if (logs == null || logs.length() == 0) {
            return results;
        }

        String begin = "BEGIN", end = "END";
        int start = -1;
        for (int i = 0; i < logs.length(); i++) {
            String signal = logs.substring(i, Math.min(logs.length(), i+5));
            if (signal.equals(begin)) {
                i += 5;
                start = i+1;
                continue;
            }

            signal = logs.substring(i, Math.min(logs.length(), i+3));
            if (signal.equals(end)) {
                if (start >= 0 && (i-2-start >= 0)) {
                    String seq = logs.substring(start, i-1);
                    results.add(bestTimeToShutDown(seq));
                    start = -1;
                }
                i += 3;
            }

        }

        return results;
    }
}
