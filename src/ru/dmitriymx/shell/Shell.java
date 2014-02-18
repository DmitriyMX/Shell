package ru.dmitriymx.shell;

import jline.console.ConsoleReader;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Formatter;

/**
 * Командная оболочка
 */
public class Shell implements Runnable {
    private LogAgent log;
    private Thread shellThread;
    private String prompt;
    private LinkedList<IShellCommand> commandList = new LinkedList<>();
    private CommandCompleter commandCompleter;
    protected ConsoleReader cReader;
    protected boolean runned = false;

    /**
     * Создание командной оболочки
     * @throws IOException
     */
    public Shell() throws IOException {
        this(new LogFormatter(), null);
    }

    /**
     * Создание командной оболочки
     * @param logFile файл журналирования
     * @throws IOException
     */
    public Shell(File logFile) throws IOException {
        this(new LogFormatter(), logFile);
    }

    /**
     * Создание командной оболочки
     * @param logFormatter свой вариант форматирования вывода
     * @throws IOException
     */
    public Shell(Formatter logFormatter) throws IOException {
        this(logFormatter, null);
    }

    /**
     * Создание командной оболочки
     * @param logFormatter свой вариант форматирования вывода
     * @param logFile файл журналирования
     * @throws IOException
     */
    public Shell(Formatter logFormatter, File logFile) throws IOException {
        cReader = new ConsoleReader(System.in, System.out);
        cReader.setExpandEvents(false);
        log = new LogAgent(logFormatter, new ShellConsoleHandler(this), logFile);
    }

    /**
     * Получить объект журналирования
     * @return
     */
    public LogAgent getLog() {
        return log;
    }

    /**
     * Установить текст приглашения
     * @param prompt
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /**
     * Добавить команду
     * @param command команда
     */
    public void addCommand(IShellCommand command) {
        commandList.add(command);
    }

    /**
     * Удаление команды
     * @param command удаляемая команда
     */
    public void removeCommand(IShellCommand command) {
        commandList.remove(command);
    }

    /**
     * Удаление команды по её имени
     * @param commandName имя команды
     */
    public void removeCommand(String commandName) {
        IShellCommand foundCmd = null;

        for (IShellCommand command : commandList) {
            if(command.getName().equalsIgnoreCase(commandName)) {
                foundCmd = command;
                break;
            }
        }

        if (foundCmd != null) {
            commandList.remove(foundCmd);
        }
    }

    /**
     * Запуск командной оболочки
     */
    public void start() {
        shellThread = new Thread(this, "Shell Thread");
        try {
            runned = true;
            shellThread.join();
            shellThread.start();
        } catch (InterruptedException e) {
            log.severe("Shell thread exception: ", e);
        }
    }

    /**
     * Остановка командной оболочки
     */
    public void stop() {
        shellThread.interrupt();
    }

    /**
     * Обработчик входящих комманд
     */
    @Override
    public void run() {
        cReader.setPrompt(prompt);
        commandCompleter = new CommandCompleter(commandList);
        cReader.addCompleter(commandCompleter);
        Thread currentThread = Thread.currentThread();

        String line;
        try {
            readerLoop:
            while (!currentThread.isInterrupted() && (line = cReader.readLine()) != null) {
                String[] parseLine = commandCompleter.parseLine(line, cReader.getCursorBuffer().cursor).getArguments();
                if (parseLine.length == 0) {
                    continue;
                }

                String commandName = parseLine[0];
                for (IShellCommand command : commandList) {
                    if (command.getName().equalsIgnoreCase(commandName)) {
                        if (parseLine.length == 1) {
                            command.execute(null);
                        } else {
                            String[] args = new String[parseLine.length - 1];
                            System.arraycopy(parseLine, 1, args, 0, args.length);
                            command.execute(args);
                        }
                        continue readerLoop;
                    }
                }
                log.warning("Unknow command \"%s\"", commandName);
            }
        } catch (IOException e) {
            log.severe("Shell exception:", e);
        }

        cReader.removeCompleter(commandCompleter);
    }
}
