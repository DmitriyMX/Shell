package test;

import org.junit.Before;
import org.junit.Test;
import ru.dmitriymx.shell.Shell;

import java.io.*;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * @author DmitriyMX <mail@dmitriymx.ru>
 *         2015
 */
public class TestShell {
    PrintStream origOut;
    PrintStream origErr;
    InputStream origIn;

    @Before
    public void overrideSys() {
        origOut = System.out;
        origErr = System.err;
        origIn = System.in;
    }

    public void returnSys() {
        System.setIn(origIn);
        System.setOut(origOut);
        System.setErr(origErr);
    }

    @Test
    public void testSysOutErr() throws IOException, InterruptedException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream shellout = new PrintStream(baos);
        System.setOut(shellout);
        System.setErr(shellout);

        ByteArrayInputStream bais = new ByteArrayInputStream("qwe\nexit\n".getBytes());
        System.setIn(bais);

        Shell shell = new Shell();
        shell.start();

        while (shell.isRunning()) {
            Thread.sleep(1);
        }

        String resultOut = baos.toString();
        returnSys();

        assertTrue(resultOut.contains("Unknown command"));
    }

    // not work in gradle
    @Test
    public void testLoggerOut() throws IOException, InterruptedException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream shellout = new PrintStream(baos);
        System.setOut(shellout);
        System.setErr(shellout);

        ByteArrayInputStream bais = new ByteArrayInputStream("qwe\nexit\n".getBytes());
        System.setIn(bais);

        Shell shell = new Shell();
        shell.start();

        Logger logger = Logger.getLogger("testLogger");
        logger.info("Hello");
        logger.warning("world");
        logger.severe("123");

        while (shell.isRunning()) {
            Thread.sleep(1);
        }

        String resultOut = baos.toString();
        returnSys();

        assertTrue(resultOut.contains("Hello"));
        assertTrue(resultOut.contains("world"));
        assertTrue(resultOut.contains("123"));
        assertTrue(resultOut.contains("Unknown command"));
    }
}