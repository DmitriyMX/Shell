package ru.dmitriymx.shell;

import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Обертка над SysOut и SysErr
 *
 * @author DmitriyMX <mail@dmitriymx.ru>
 *         2015
 */
public class ShellPrintStream extends PrintStream {
    private ConsoleReader consoleReader;
    private PrintWriter writer;

    public ShellPrintStream(OutputStream outputStream) {
        super(outputStream, true);
    }

    public void setConsoleReader(ConsoleReader consoleReader) {
        this.consoleReader = consoleReader;

        if (consoleReader != null) {
            this.writer = new PrintWriter(consoleReader.getOutput());
        }
    }

    @Override
    public void print(String s) {
        println(s);
    }

    @Override
    public void println(String s) {
        if (consoleReader != null) {
            writer.print(ConsoleReader.RESET_LINE);
            writer.print(s);
            cleanTrashLine(s);
            writer.println();
            try {
                consoleReader.drawLine();
            } catch (IOException e) {
                // ignore
            }
            writer.flush();
        } else {
            super.print(ConsoleReader.RESET_LINE);
            super.print(s);
        }
    }

    /**
     * Очистка печатной строки от мусора
     */
    private void cleanTrashLine(String string) {
        // очищает полностью строку
        if (consoleReader.getCursorBuffer().buffer.length() + consoleReader.getPrompt().length() > string.length()) {
            for (int i = string.length(); i <= consoleReader.getCursorBuffer().buffer.length() + 2; i++) {
                writer.print(' ');
            }
        }
    }
}
