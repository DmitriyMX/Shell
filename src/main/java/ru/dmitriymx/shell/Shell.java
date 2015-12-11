package ru.dmitriymx.shell;

import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Командная оболочка
 */
public class Shell implements Runnable {
    private Thread shellThread;
    private String prompt;
    private Map<String, IShellCommand> commandList = new HashMap<>();
    private CommandCompleter commandCompleter;
    private final String[] emptyArray = new String[0];
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
     * @param commandName имя команды
     * @param command команда
     */
    public void addCommand(String commandName, IShellCommand command) {
        commandList.put(commandName.toLowerCase(), command);
    }

    /**
     * Удаление команды
     * @param command удаляемая команда
     */
    public void removeCommand(String commandName, IShellCommand command) {
        commandList.remove(commandName);
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
        commandCompleter = new CommandCompleter(commandList.keySet());
        cReader.addCompleter(commandCompleter);
        Thread currentThread = Thread.currentThread();

        String line;
        try {
            while (!currentThread.isInterrupted() && (line = cReader.readLine()) != null) {
                String[] parseLine = commandCompleter.parseLine(line, cReader.getCursorBuffer().cursor).getArguments();
                if (parseLine.length == 0) {
                    continue;
                }

                String commandName = parseLine[0].toLowerCase();
                if (commandList.containsKey(commandName)) {
                	IShellCommand command = commandList.get(commandName);

                	if (parseLine.length == 1) {
                		command.execute(emptyArray);
                	} else {
                		String[] args = new String[parseLine.length - 1];
                        System.arraycopy(parseLine, 1, args, 0, args.length);
                        command.execute(args);
                	}

                	continue;
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
