import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {
    private static RemoveServer0 main;
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        main = new RemoveServer0();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void testGetPenalty() {
        String case1 = "0 0 1 0";
        int expected = 1;
        int actual = main.getPenalty(case1, 2);

        assertEquals(expected, actual);
        //assert actual == expected : String.format("expected: %d, but got: %d", expected,  actual);
    }

    @org.junit.jupiter.api.Test
    void bestTimeToShutDown() {
    }

    @org.junit.jupiter.api.Test
    void parseLogs() {
    }
}