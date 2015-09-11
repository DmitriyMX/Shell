package ru.dmitriymx.shell;

import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Командная оболочка
 */
public class Shell implements Runnable {
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
        cReader = new ConsoleReader(System.in, System.out);
        cReader.setExpandEvents(false);
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
        	e.printStackTrace();
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
                System.err.println(String.format("Unknow command \"%s\"", commandName));
                try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
        } catch (IOException e) {
        	System.err.println("Shell exception");
        	e.printStackTrace();
        }

        cReader.removeCompleter(commandCompleter);
    }
}
