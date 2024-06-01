package sqlancer.dbms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;

import sqlancer.Main;

import java.io.IOException;

public class TestTiDB {

    @Test
    public void testMySQL() throws IOException {
        String tiDB = System.getenv("TIDB_AVAILABLE");
        boolean tiDBIsAvailable = tiDB != null && tiDB.equalsIgnoreCase("true");
        assumeTrue(tiDBIsAvailable);
        assertEquals(0, Main.executeMain(new String[] { "--random-seed", "0", "--timeout-seconds", TestConfig.SECONDS,
                "--num-queries", "0", "tidb" }));
    }

}
