package org.writer.exception;

public class ClassNotForWriteException extends Exception{
    private static final String DEFAULT_MESSAGE = "Файл %s не будет создан, так как класс не аннотирован @WritableClass с FileType=CSV";

    public ClassNotForWriteException(String fileName) {
        super(String.format(DEFAULT_MESSAGE, fileName));
    }
}
