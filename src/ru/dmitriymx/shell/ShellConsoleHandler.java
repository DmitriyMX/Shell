package ru.dmitriymx.shell;

import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.logging.ConsoleHandler;

public class ShellConsoleHandler extends ConsoleHandler {
    private Shell shell;

    public ShellConsoleHandler(Shell shell) {
        super();
        this.shell = shell;
    }

    @Override
    public synchronized void flush() {
        try {
            shell.cReader.print(ConsoleReader.RESET_LINE + "");
            shell.cReader.flush();
            super.flush();
        } catch (IOException e) {
            e.printStackTrace();
            super.flush();
        }
    }
}