package ru.dmitriymx.shell;

public interface IShellCommand {

    public String getName();

    public void execute(final String[] args);
}
