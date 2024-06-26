package sqlancer.dbms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;

import sqlancer.Main;

import java.io.IOException;

public class TestH2 {

    @Test
    public void testDuckDB() throws IOException {
        String h2Available = System.getenv("H2_AVAILABLE");
        boolean mariaDBIsAvailable = h2Available != null && h2Available.equalsIgnoreCase("true");
        assumeTrue(mariaDBIsAvailable);
        assertEquals(0, Main.executeMain("--random-seed", "0", "--timeout-seconds", TestConfig.SECONDS,
                "--num-threads", "4", "--num-queries", TestConfig.NUM_QUERIES, "h2"));

    }

}
