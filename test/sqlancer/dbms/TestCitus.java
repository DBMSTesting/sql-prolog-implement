package sqlancer.dbms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;

import sqlancer.Main;

import java.io.IOException;

public class TestCitus {

    @Test
    public void testCitus() throws IOException {
        String citusAvailable = System.getenv("CITUS_AVAILABLE");
        boolean citusIsAvailable = citusAvailable != null && citusAvailable.equalsIgnoreCase("true");
        assumeTrue(citusIsAvailable);
        assertEquals(0,
                Main.executeMain(new String[] { "--random-seed", "0", "--timeout-seconds", TestConfig.SECONDS,
                        "--num-threads", "4", "--num-queries", TestConfig.NUM_QUERIES, "citus", "--connection-url",
                        "postgresql://localhost:9700/test", "--test-collations", "false" }));
    }

}
