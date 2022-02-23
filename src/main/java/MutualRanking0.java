import java.util.*;

public class MutualRanking0 {
    private Map<Character, List<Character>> wishList;
    public MutualRanking0() {}
    public MutualRanking0(Map<Character, List<Character>> wishList) {
        this.wishList = wishList;
    }

    public boolean hasMutualRanking(char c, int rank) {
        if (!hasWishList(c, rank+1)) {
            return false;
        }

        char choice = wishList.get(c).get(rank);
        if (!hasWishList(choice, rank+1)) {
            return false;
        }

        return c == wishList.get(choice).get(rank);
    }

    public List<Character> changePair(char c, int index) {
        List<Character> results = new ArrayList<>();
        if (hasMutualRanking(c, index)) {
            results.add(wishList.get(c).get(index));
        }

        if (index > 0 && hasWishList(c, index+1)) {
            List<Character> list = wishList.get(c);
            swap(list, index, index-1);
            if (hasMutualRanking(c, index-1)) {
                results.add(c);
            }
            if (hasMutualRanking(list.get(index), index)) {
                results.add(list.get(index));
            }
        }

        return results;
    }

    private boolean hasWishList(char c, int count) {
        return wishList != null
            && wishList.containsKey(c)
            && wishList.get(c).size() >= count;
    }

    private void swap(List<Character> list, int a, int b) {
        char temp = list.get(a);
        list.set(a, list.get(b));
        list.set(b, temp);
    }








    public boolean isMutualTopChoice(Map<String, List<String>> wishlist, String userId) {
        if (!hasWishList(wishlist, userId)) {
            return false;
        }

        String topChoice = wishlist.get(userId).get(0);
        if (!hasWishList(wishlist, topChoice)) {
            return false;
        }

        return userId.equals(wishlist.get(topChoice).get(0));
    }

    private boolean hasWishList(Map<String, List<String>> wishlist, String userId) {
        return wishlist != null
                && wishlist.containsKey(userId)
                && wishlist.get(userId).size() > 0;
    }
}
