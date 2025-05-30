package org.writer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для классов, коллекции которых возможно записать в файл
 * Атрибут {@code fileType} задаёт массив типов файлов {@link FileType},
 * в которые данный класс может быть записан. По умолчанию установлен один тип — {@code CSV}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WritableClass {
    FileType[] fileType() default {FileType.CSV};
}
