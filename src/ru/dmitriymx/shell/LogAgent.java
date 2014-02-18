package ru.dmitriymx.shell;

import java.io.File;
import java.util.logging.*;

public class LogAgent {
    private Logger logger;

    public LogAgent() {
        this(new LogFormatter(), new ConsoleHandler(), null);
    }

    public LogAgent(Formatter formatter) {
        this(formatter, new ConsoleHandler(), null);
    }

    public LogAgent(ConsoleHandler consoleHandler) {
        this(new LogFormatter(), consoleHandler, null);
    }

    public LogAgent(File logFile) {
        this(new LogFormatter(), new ConsoleHandler(), logFile);
    }

    public LogAgent(Formatter formatter, ConsoleHandler consoleHandler, File logFile) {
        logger = Logger.getLogger("Shell");
        logger.setUseParentHandlers(false);

        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            logger.removeHandler(handler);
        }

        consoleHandler.setFormatter(formatter);
        logger.addHandler(consoleHandler);

        if (logFile != null) {
            try {
                FileHandler fHandler = new FileHandler(logFile.getAbsolutePath(), true);
                fHandler.setFormatter(formatter);
                logger.addHandler(fHandler);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed write log to " + logFile.getAbsolutePath(), e);
            }
        }
    }

    public void info(String message) {
        logger.log(Level.INFO, message);
    }

    public void info(String message, Object... params) {
        logger.log(Level.INFO, String.format(message, params));
    }

    public void warning(String message) {
        logger.log(Level.WARNING, message);
    }

    public void warning(String message, Object... params) {
        logger.log(Level.WARNING, String.format(message, params));
    }

    public void warning(String message, Throwable trow) {
        logger.log(Level.WARNING, message, trow);
    }

    public void severe(String message) {
        logger.log(Level.SEVERE, message);
    }

    public void severe(String message, Object... params) {
        logger.log(Level.SEVERE, String.format(message, params));
    }

    public void severe(String message, Throwable trow) {
        logger.log(Level.SEVERE, message, trow);
    }
}