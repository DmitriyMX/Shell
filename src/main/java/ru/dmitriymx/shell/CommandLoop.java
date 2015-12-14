package ru.dmitriymx.shell;

import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;
import ru.dmitriymx.shell.commands.Command;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DmitriyMX <mail@dmitriymx.ru>
 *         2015
 */
public class CommandLoop implements Runnable {
    private static final String[] EMPTY_ARGS = new String[0];
    private static final String RED = Ansi.ansi().fgBright(Ansi.Color.RED).toString();
    private ConsoleReader console;
    private Shell shell;
    protected Map<String, Command> commandMap = new HashMap<>();

    public CommandLoop( Shell shell) {
        this.shell = shell;
        this.console = shell.console;
    }

    @Override
    public void run() {
        Thread loop = Thread.currentThread();

        while (shell.run && !loop.isInterrupted()) {
            try {
                String line;
                if ((line = console.readLine()) != null) {
                    String[] parseLine = Shell.DELIMITER.delimit(line.trim(), console.getCursorBuffer().cursor).getArguments();
                    if (parseLine.length == 0) {
                        continue;
                    }

                    String comandName = parseLine[0].toLowerCase();
                    if (commandMap.containsKey(comandName)) {
                        Command command = commandMap.get(comandName);

                        if (parseLine.length == 1) {
                            command.execute(EMPTY_ARGS);
                        } else {
                            String[] args = new String[parseLine.length - 1];
                            System.arraycopy(parseLine, 1, args, 0, args.length);
                            command.execute(args);
                        }
                    } else {
                        System.err.println(RED + "Unknown command \"" + comandName + "\"" + Ansi.ansi().reset());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // чтобы не нагружать процессор
            safeSleep(1);
        }

        shell.run = false;
    }

    private void safeSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            //ignore
        }
    }
}
