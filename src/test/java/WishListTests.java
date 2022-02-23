import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;



public class WishListTests {
    private static MutualRanking0 mutualRanking0;

    @BeforeEach
    void setUp() {
        mutualRanking0 = new MutualRanking0();
    }

    @Test
    void TestIsMutualTopChoice() {
        Map<String, List<String>> map = new HashMap<>();

        map.put("a", new ArrayList<>(Arrays.asList("b", "c", "d")));
        map.put("b", new ArrayList<>(Arrays.asList("a", "d")));
        map.put("c", new ArrayList<>(Arrays.asList("d")));
        map.put("d", new ArrayList<>(Arrays.asList("a", "b")));

        assertTrue(mutualRanking0.isMutualTopChoice(map, "a"));
        assertFalse(mutualRanking0.isMutualTopChoice(map, "d"));
    }

    @Test
    void TestHasMutualRanking() {
        Map<Character, List<Character>> map = new HashMap<>();
        map.put('a', new ArrayList<>(Arrays.asList('b', 'c', 'd')));
        map.put('b', new ArrayList<>(Arrays.asList('a', 'c', 'd')));
        map.put('c', new ArrayList<>(Arrays.asList('d', 'a')));
        map.put('d', new ArrayList<>(Arrays.asList('a', 'c')));
        mutualRanking0 = new MutualRanking0(map);

        assertTrue(mutualRanking0.hasMutualRanking('a', 1));
        assertFalse(mutualRanking0.hasMutualRanking('b', 1));
    }

    @Test
    void testChangePair() {
        Map<Character, List<Character>> map = new HashMap<>();
        map.put('a', Arrays.asList('b', 'c', 'd'));
        map.put('b', Arrays.asList('a', 'c', 'd'));
        map.put('c', Arrays.asList('d', 'a'));
        map.put('d', Arrays.asList('a', 'c'));
        mutualRanking0 = new MutualRanking0(map);

        List<Character> exp = Arrays.asList('a', 'd');
        List<Character> act = mutualRanking0.changePair('c', 1);
        assertEquals(exp, act);
    }
}
