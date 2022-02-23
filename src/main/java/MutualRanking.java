import java.sql.Array;
import java.util.*;
public class MutualRanking {
        /*
        Mutual Ranking
        Question comes from https://www.1point3acres.com/bbs/thread-769296-1-1.html
                                                                https://www.1point3acres.com/bbs/thread-674646-1-1.html
                                                                https://www.1point3acres.com/bbs/thread-793699-1-1.html
                                                                https://www.1point3acres.com/bbs/thread-806739-1-1.html
         user and wishlist represented with Map<Character, Character[]> wishlist
         a, [b,c,d]
         b, [a,c,d]
         c, [d,a]
         d, [a,c]
         index 0 is the highest rank
         question 1: boolean hasMutualFirstChoice(String username)
                                             mutual at the first rank
         question 2: boolean hasMutualRanking(char, int)，
                                             hasMutualRanking(a, 1) = true
                                             explain:         a's        ranking 1 is c， c's ranking 1 is a.
                                             hasMutualRanking(a, 0) = true
         question 3-1
                                            changePair(char user, int index) ：swap user rank index with index-1
                                            return matched all users before and after swap with same mutual ranking
                                            changePair(c,1) then c ranking list is [a,d]
                                            result is [a, d], because before swap result is a, after swap result is d
                                            a -> [b, c, d],
                                            b -> [a, d, c],
                                            c -> [d, a, b],
                                            d -> [a, c].
                                            changePair(c, 1)
                                            result is a b d
                                            Every wishlist entry in the network is either "mutually ranked" or "not mutually ranked"
                                            depending on the rank the other user gives that user's apartment in return.
                                            The most common operation in the network is incrementing the rank of a single wishlist entry on a single user.
                                            This swaps the entry with the entry above it in that user's list.
                                            Imagine that, when this occurs, the system must recompute the "mutually-ranked-ness" of any pairings that
                                            may have changed.
                                            Write a function that takes a username and a rank representing the entry whose rank is being bumped up.
                                            Return an array of the users whose pairings with the given user *would* gain or lose mutually-ranked
                                            status as a result of the change, if it were to take place.
                                            Call your function changed_pairings()
                                            data = {
                                             'a': ['c', 'd'],
                                             'b': ['d', 'a', 'c'],
                                             'c': ['a', 'b'],
                                             'd': ['c', 'a', 'b'],
                                            }
                                            if d's second choice becomes their first choice, a and d will no longer be a mutually ranked pair
                                            changed_pairings('d', 1) // returns ['a']
                                            if b's third choice becomes thei‍‌‍‌‌‍‍‌‍‍‌‍‌‍‍‍‍‌r second choice, c and b will become a mutually ranked pair (mutual second-choices)
                                            changed_pairings('b', 2) // returns ['c']
                                            if b's second choice becomes their first choice, no mutually-ranked pairings are affected
                                            changed_pairings('b', 1) // returns []
                                            if d's second choice becomes their first choice, a and d will no longer be a mutually ranked pair
                                            if b's third choice becomes their second choice, c and b will become a mutually ranked pair (mutual second-choices)
                                            if b's second choice becomes their first choice, no mutually-ranked pairings are affected
          question 3-2
                 after swap（rank， rank -1)
                 return the changed part of mutual list.
         question 4 Anti-ra‍‌‍‌‌‍‍‌‍‍‌‍‌‍‍‍‍‌nk
                 boolean hasAntiMutualRank(char user)
                 a: [b,c,d]        b is a's first rank，
                 b: [d,c,a]        a is b's last rank
         boolean hasAntiMutualRankWithSwapOption(char user) can swap
                 assume: index 0 and 1 swap
                                     index N-1 and N-1 swap
          */
        private Map<Character, char[]> ranking;
        public MutualRanking(Map<Character, char[]> wishList) {
            ranking = wishList;
        }
//


    public boolean hasMutualRank(char user, int k) {
        if(!ranking.containsKey(user) || ranking.get(user) == null || ranking.get(user).length <= k){
            return false;
        }
        char kRank = ranking.get(user)[k];

        if(!ranking.containsKey(kRank) || ranking.get(kRank) == null || ranking.get(kRank).length <= k){
            return false;
        }
        return user == ranking.get(kRank)[k];
    }

    public boolean hasMutualRankDelta(char user, int k) {
        if(!ranking.containsKey(user) || ranking.get(user) == null || ranking.get(user).length <= k){
            return false;
        }
        char kRank = ranking.get(user)[k];

        if(!ranking.containsKey(kRank) || ranking.get(kRank) == null || ranking.get(kRank).length <= k+1){
            return false;
        }
        return user == ranking.get(kRank)[k+1];
    }


    public Set<Character> changePair(char c, int k){
        Set<Character> impactedUsers = new HashSet<>();
        //matched => devorce
        if(hasMutualRank(c, k)){
            impactedUsers.add(ranking.get(c)[k]);
        }

        // new matched with c
        if(hasMutualRankDelta(c, k-1)) {
            impactedUsers.add(ranking.get(c)[k - 1]);
        }

        return impactedUsers;
    }


        public static void main(String[] args) {
          Map<Character, char[]> wish = new HashMap<>();

//            a -> [b, c, d],
//            b -> [a, d, c],
//            c -> [d, a, b],
//            d -> [a, c].
          wish.put('a', new char[] {'b', 'c', 'd'});
          wish.put('b', new char[] {'a', 'd', 'c'});
          wish.put('c', new char[] {'d', 'a', 'd'});
          wish.put('d', new char[] {'a', 'c'});
          MutualRanking t = new MutualRanking(wish);
          System.out.println(t.hasMutualRank('a', 1));
          System.out.println(t.hasMutualRank('a', 9));
          System.out.println(t.hasMutualRank('a', 0));
          System.out.println(t.changePair('a', 1));
            System.out.println(t.changePair('d', 2));
          System.out.println(t.changePair('a', 9));
          wish = new HashMap<>();
//            'a': ['c', 'd'],
//            'b': ['d', 'a', 'c'],
//            'c': ['a', 'b'],
//            'd': ['c', 'a', 'b'],
          wish.put('a', new char[] {'c', 'd'});
          wish.put('b', new char[] {'d', 'a', 'c'});
          wish.put('c', new char[] {'a', 'b'});
          wish.put('d', new char[] {'c', 'a', 'b'});
            t = new MutualRanking(wish);
          System.out.println(t.changePair('d', 1));
          System.out.println(t.changePair('b', 2));
          System.out.println(t.changePair('b', 1));
          System.out.println(t.changePair('d', 1));
//            System.out.println(t.changePair('b', 2));


        }
}

