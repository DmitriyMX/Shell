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
    private Formatter formatter;

    public ShellPrintStream(OutputStream outputStream) {
        super(outputStream, true);
    }

    public void setConsoleReader(ConsoleReader consoleReader) {
        this.consoleReader = consoleReader;

        if (consoleReader != null) {
            this.writer = new PrintWriter(consoleReader.getOutput());
        }
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    private void _print(String s) {
        writer.print(ConsoleReader.RESET_LINE);
        if (formatter != null) { //TODO убрать проверку null
            s = formatter.format(s);
        }
        writer.print(s);
        cleanTrashLine(s);
        writer.println();
        try {
            consoleReader.drawLine();
        } catch (IOException e) {
            // ignore
        }
        writer.flush();
    }

    @Override
    public void write(byte[] bytes, int off, int len) {
        if (consoleReader != null) {
            if ((char)bytes[len-1] == '\n') len--; //TODO проверить в windows
            _print(new String(bytes, off, len));
        } else {
            super.write(bytes, off, len);
        }
    }

    @Override
    public void print(String s) {
        if (consoleReader != null) {
            _print(s);
        } else {
            super.print(s);
        }
    }

    @Override
    public void println(String s) {
        if (consoleReader != null) {
            _print(s);
        } else {
            super.println(s);
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
