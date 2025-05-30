package org.writer.writer;

import org.writer.Writable;
import org.writer.annotation.FileType;
import org.writer.annotation.WritableClass;
import org.writer.exception.ClassNotForWriteException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс {@code CsvWriter} реализует интерфейс {@link Writable} и предоставляет функциональность
 * для записи коллекции объектов в CSV-файл.
 * <p>
 * Запись происходит только для классов, помеченных аннотацией {@link WritableClass} с поддержкой типа файла {@link FileType#CSV}.
 * В случае отсутствия такой аннотации или неподдерживаемого типа файла выбрасывается исключение {@link ClassNotForWriteException}.
 * </p>
 * <p>
 * Формат CSV включает заголовок с названиями полей (с преобразованием camelCase в читаемый вид)
 * и строки с данными, где значения коллекций, массивов и карт форматируются в кавычках с разделением запятыми.
 * </p>
 */
public class CsvWriter implements Writable {

    public static final String EMPTY_FILE_NAME = "Предупреждение: имя файла пустое или null. Файл не будет записан";
    public static final String FILE_DIR = "output";
    public static final String FILE_TYPE = ".csv";
    public static final String EMPTY_ELEMENT = "";
    public static final String SURROUND_FORMATTED_ELEMENTS = "\"";
    public static final String DELIMITER_FOR_ELEMENTS_IN_FIELD = ",";
    public static final String DELIMITER_BETWEEN_KEY_VALUE = "=";
    public static final String DELIMITER_BETWEEN_LINES = ";";

    /**
     * Записывает переданные данные в CSV-файл с указанным именем.
     * <p>
     * Если класс объектов в списке не помечен аннотацией {@link WritableClass} с поддержкой {@link FileType#CSV},
     * будет выведено сообщение об ошибке.
     * </p>
     *
     * @param data     список объектов для записи
     * @param fileName имя файла (без расширения), в который будет записан CSV
     */
    @Override
    public void writeToFile(List<?> data, String fileName) {
        try {
            String dataString = getDataString(data, fileName);
            saveDataStringToFile(dataString, fileName);
        } catch (ClassNotForWriteException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Формирует строковое представление данных в формате CSV.
     * @param data     список объектов для преобразования
     * @param fileName имя файла
     * @return строка с содержимым CSV
     * @throws ClassNotForWriteException если класс объектов не поддерживает запись в CSV
     */
    private String getDataString(List<?> data, String fileName) throws ClassNotForWriteException {
        if (data == null || data.isEmpty()) {
            return EMPTY_ELEMENT;
        }
        StringBuilder sb = new StringBuilder();
        Object objForFields = data.get(0);
        Class<?> clazz = objForFields.getClass();

        WritableClass annotation = clazz.getAnnotation(WritableClass.class);
        if (annotation == null || !Arrays.asList(annotation.fileType()).contains(FileType.CSV)) {
            throw new ClassNotForWriteException(fileName);
        }

        Field[] fields = clazz.getDeclaredFields();

        generateTableColumns(fields, sb);
        generateColumnData(data, fields, sb);

        return sb.toString();
    }

    /**
     * Сохраняет строковое представление данных в CSV-файл в папке "output".
     * <p>
     * Если имя файла пустое или {@code null}, выводит предупреждение и не записывает файл.
     * В случае ошибок ввода-вывода выводит сообщение об ошибке.
     * </p>
     *
     * @param dataString содержимое CSV-файла
     * @param fileName   имя файла (без расширения)
     */
    private void saveDataStringToFile(String dataString, String fileName) {
        if (fileName == null || fileName.isBlank()) {
            System.err.println(EMPTY_FILE_NAME);
            return;
        }
        Path outputDir = Paths.get(FILE_DIR);
        try {
            Files.createDirectories(outputDir);
            Path outputFile = outputDir.resolve(fileName + FILE_TYPE);
            Files.writeString(outputFile, dataString, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    /**
     * Формирует строки с данными объектов, разделёнными точкой с запятой.
     * <p>
     * Поддерживает обработку значений полей следующих типов:
     * <ul>
     *     <li>Коллекции — преобразуются в строку с элементами, разделёнными запятыми и обёрнутыми в кавычки</li>
     *     <li>Массивы — аналогично коллекциям</li>
     *     <li>Мапы — преобразуются в строку пар ключ=значение, разделённых запятыми и обёрнутых в кавычки</li>
     *     <li>Прочие типы — вызывается метод {@code toString()}</li>
     * </ul>
     * В случае ошибки доступа к полю вставляется сообщение "Невозможно прочитать поле".
     * </p>
     *
     * @param data   список объектов для записи
     * @param fields массив полей класса объектов
     * @param sb     {@link StringBuilder} для формирования результата
     */
    private static void generateColumnData(List<?> data, Field[] fields, StringBuilder sb) {
        for (Object obj : data) {
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                try {
                    Object value = fields[i].get(obj);
                    if (value == null) {
                        sb.append(EMPTY_ELEMENT);
                    } else if (value instanceof Collection<?> collection) {
                        String formattedCollection = collection.stream()
                                .map(element -> element != null ? element.toString() : EMPTY_ELEMENT)
                                .collect(Collectors.joining(DELIMITER_FOR_ELEMENTS_IN_FIELD));
                        sb.append(SURROUND_FORMATTED_ELEMENTS).append(formattedCollection).append(SURROUND_FORMATTED_ELEMENTS);
                    } else if (value.getClass().isArray()) {
                        int length = Array.getLength(value);
                        List<String> elements = new ArrayList<>();
                        for (int j = 0; j < length; j++) {
                            Object element = Array.get(value, j);
                            elements.add(element != null ? element.toString() : EMPTY_ELEMENT);
                        }
                        String formattedArray = String.join(DELIMITER_FOR_ELEMENTS_IN_FIELD, elements);
                        sb.append(SURROUND_FORMATTED_ELEMENTS).append(formattedArray).append(SURROUND_FORMATTED_ELEMENTS);
                    } else if (value instanceof Map<?, ?> map) {
                        String formattedMap = map.entrySet().stream()
                                .map(entry -> entry.getKey() + DELIMITER_BETWEEN_KEY_VALUE +
                                        (entry.getValue() != null ? entry.getValue().toString() : EMPTY_ELEMENT))
                                .collect(Collectors.joining(DELIMITER_FOR_ELEMENTS_IN_FIELD));
                        sb.append(SURROUND_FORMATTED_ELEMENTS).append(formattedMap).append(SURROUND_FORMATTED_ELEMENTS);
                    } else {
                        sb.append(value);
                    }
                } catch (IllegalAccessException e) {
                    sb.append("Невозможно прочитать поле");
                }
                if (i < fields.length - 1) {
                    sb.append(DELIMITER_BETWEEN_LINES);
                }
            }
            sb.append("\n");
        }
    }

    /**
     * Формирует строку заголовка CSV с названиями полей, разделёнными точкой с запятой.
     * <p>
     * Имена полей преобразуются из camelCase в читаемый формат с пробелами и заглавными буквами.
     * </p>
     *
     * @param fields массив полей класса
     * @param sb     {@link StringBuilder} для формирования результата
     */
    private static void generateTableColumns(Field[] fields, StringBuilder sb) {
        for (int i = 0; i < fields.length; i++) {
            String formattedName = splitCamelCaseAndCapitalize(fields[i].getName());
            sb.append(formattedName);
            if (i < fields.length - 1) {
                sb.append(DELIMITER_BETWEEN_LINES);
            }
        }
        sb.append("\n");
    }

    /**
     * Преобразует строку из camelCase в формат с пробелами и заглавными буквами.
     * <p>
     * Например, {@code "firstName"} преобразуется в {@code "First Name"}.
     * </p>
     *
     * @param input исходная строка в camelCase
     * @return преобразованная строка с пробелами и заглавными буквами
     */
    private static String splitCamelCaseAndCapitalize(String input) {
        String[] words = input.split("(?=[A-Z])");
        for (int i = 0; i < words.length; i++) {
            if (words[i].isEmpty()) continue;
            words[i] = words[i].substring(0, 1).toUpperCase() +
                    words[i].substring(1).toLowerCase();
        }
        return String.join(" ", words);
    }
}
