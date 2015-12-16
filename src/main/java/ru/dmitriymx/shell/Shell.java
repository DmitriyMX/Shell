package ru.dmitriymx.shell;

import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import ru.dmitriymx.shell.commands.Command;
import ru.dmitriymx.shell.commands.ExitCommand;

import java.io.IOException;
import java.io.PrintStream;

/**
 * @author DmitriyMX <mail@dmitriymx.ru>
 *         2015
 */
public class Shell {
    public static final ArgumentCompleter.ArgumentDelimiter DELIMITER = new ArgumentCompleter.WhitespaceArgumentDelimiter();

    private PrintStream sysErr;
    private ShellPrintStream newErr;
    private String promt;
    protected ConsoleReader console;
    private CommandLoop commandLoop;
    private CommandCompleter commandCompleter;
    private Formatter formatter;
    protected boolean run = false;

    public void start() throws IOException, InterruptedException {
        if (sysErr == null) overrideSysOutErr();

        console = new ConsoleReader(System.in, sysErr);
        if (promt == null) promt = ":";
        console.setPrompt(ConsoleReader.RESET_LINE + promt);
        console.addCompleter((commandCompleter = new CommandCompleter()));
        newErr.setConsoleReader(console);
        commandLoop = new CommandLoop(this);

        if (!commandLoop.commandMap.containsKey("exit")) {
            addCommand(new ExitCommand());
        }

        Thread loopCommandReader = new Thread(commandLoop, "Command reader loop");
        loopCommandReader.join();
        loopCommandReader.start();
        run = true;
    }

    public void shutdown() {
        run = false;

        newErr.setConsoleReader(null);
        console.shutdown();
    }

    public void setPromt(String promt) { //FIXME коостыли!!
        if (console == null) {
            this.promt = promt;
        } else {
            console.setPrompt(ConsoleReader.RESET_LINE + promt);
        }
    }

    public void addCommand(Command command) {
        command.setShell(this);
        String name = command.getName().toLowerCase();
        commandLoop.commandMap.put(name, command);
        commandCompleter.stringsCompleter.getStrings().add(name);
    }

    public boolean isRunning() {
        return run;
    }

    public void setFormatter(Formatter formatter) {
        if (newErr != null) {
            newErr.setFormatter(formatter);
        } else {
            this.formatter = formatter;
        }
    }

    /**
     * Подмена стандартных SysErr и SysOut
     */
    public void overrideSysOutErr() {
        sysErr = System.err;
        newErr = new ShellPrintStream(sysErr);
        if (formatter != null) newErr.setFormatter(formatter);
        System.setErr(newErr);
        System.setOut(newErr);
    }
}
