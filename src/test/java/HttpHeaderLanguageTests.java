import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class HttpHeaderLanguageTests {
    private static HttpHeaderLangResolver resolver;

//    @BeforeEach
//    void setUp() {
//        resolver  = new HttpHeaderLangResolver();
//    }

    @Test
    void testParseAcceptLanguage1() {
        resolver = new HttpHeaderLangResolver(new HashSet<>(Arrays.asList("fr-FR", "en-US")));
        String input = "en-US, fr-CA, fr-FR";

        List<String> exp = new ArrayList<>(Arrays.asList("en-US", "fr-FR"));
        List<String> act = resolver.getAcceptedLang1(input);
        assertEquals(exp, act);

        resolver = new HttpHeaderLangResolver(new HashSet<>(Arrays.asList("fr-FR", "en-US")));
        input = "fr-CA, fr-FR";
        exp = new ArrayList<>(Arrays.asList("fr-FR"));
        act = resolver.getAcceptedLang1(input);
        assertEquals(exp, act);

        resolver = new HttpHeaderLangResolver(new HashSet<>(Arrays.asList("fr-CA", "en-US")));
        input = "en-US";
        exp = new ArrayList<>(Arrays.asList("en-US"));
        act = resolver.getAcceptedLang1(input);
        assertEquals(exp, act);
    }

    @Test
    void testParseAcceptLanguage2() {
        String input = "en";
        resolver = new HttpHeaderLangResolver(new HashSet<>(Arrays.asList("en-US", "fr-CA", "fr-FR")));
        List<String> exp = new ArrayList<>(Arrays.asList("en-US"));
        List<String> act = resolver.getAcceptedLang2(input);
        assertEquals(exp, act);

        resolver = new HttpHeaderLangResolver(new HashSet<>(Arrays.asList("en-US", "fr-CA", "fr-FR")));
        input = "fr";
        exp = new ArrayList<>(Arrays.asList("fr-CA", "fr-FR"));
        act = resolver.getAcceptedLang2(input);
        assertTrue(exp.size() == act.size() && exp.containsAll(act));

        resolver = new HttpHeaderLangResolver(new HashSet<>(Arrays.asList("en-US", "fr-CA", "fr-FR")));
        input = "fr-FR, fr";
        exp = new ArrayList<>(Arrays.asList("fr-FR", "fr-CA"));
        act = resolver.getAcceptedLang2(input);
        assertTrue(exp.size() == act.size() && exp.containsAll(act) && exp.get(0).equals(act.get(0)));
    }

    @Test
    void testParseAcceptLanguage3() {
        String input = "en-US, *";
        resolver = new HttpHeaderLangResolver(new HashSet<>(Arrays.asList("en-US", "fr-CA", "fr-FR")));
        List<String> exp = new ArrayList<>(Arrays.asList("en-US", "fr-CA", "fr-FR"));
        List<String> act = resolver.getAcceptedLang3(input);
        assertTrue(exp.size() == act.size() && exp.containsAll(act) && exp.get(0).equals(act.get(0)));

        resolver = new HttpHeaderLangResolver(new HashSet<>(Arrays.asList("en-US", "fr-CA", "fr-FR")));
        input = "fr-FR, fr, *";
        exp = new ArrayList<>(Arrays.asList("fr-FR", "fr-CA", "en-US"));
        act = resolver.getAcceptedLang3(input);
        assertTrue(exp.size() == act.size() && exp.containsAll(act));
    }
}
