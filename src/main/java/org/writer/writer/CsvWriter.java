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

public class CsvWriter implements Writable {
    @Override
    public void writeToFile(List<?> data, String fileName) {
        try {
            String dataString = getDataString(data, fileName);
            saveDataStringToFile(dataString, fileName);
            System.out.println(dataString);
        } catch (ClassNotForWriteException e) {
            System.err.println(e.getMessage());
        }
    }

    private String getDataString(List<?> data, String fileName) throws ClassNotForWriteException {
        if (data == null || data.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Object objForFields = data.get(0);
        Class<?> clazz = objForFields.getClass();

        WritableClass annotation = clazz.getAnnotation(WritableClass.class);
        if (annotation == null || !Arrays.asList(annotation.fileType()).contains(FileType.CSV)) {
            throw new ClassNotForWriteException(fileName);
        }

        Field[] fields = clazz.getDeclaredFields();

        // Формируем первую строку — названия полей
        generateTableColumns(fields, sb);

        // Формируем строки со значениями полей
        generateColumnData(data, fields, sb);
        return sb.toString();
    }

    private void saveDataStringToFile(String dataString, String fileName) {
        if (fileName == null || fileName.isBlank()) {
            System.err.println("Предупреждение: имя файла пустое или null. Файл не будет записан");
            return;
        }
        Path outputDir = Paths.get("output");
        try {
            Files.createDirectories(outputDir);
            Path outputFile = outputDir.resolve(fileName + ".csv");
            Files.writeString(outputFile, dataString, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    private static void generateColumnData(List<?> data, Field[] fields, StringBuilder sb) {
        for (Object obj : data) {
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                try {
                    Object value = fields[i].get(obj);
                    if (value == null) {
                        sb.append("");
                    } else if (value instanceof Collection<?> collection) {
                        String formattedCollection = collection.stream()
                                .map(element -> element != null ? element.toString() : "")
                                .collect(Collectors.joining(","));
                        sb.append("\"").append(formattedCollection).append("\"");
                    } else if (value.getClass().isArray()) {
                        int length = Array.getLength(value);
                        List<String> elements = new ArrayList<>();
                        for (int j = 0; j < length; j++) {
                            Object element = Array.get(value, j);
                            elements.add(element != null ? element.toString() : "");
                        }
                        String formattedArray = String.join(",", elements);
                        sb.append("\"").append(formattedArray).append("\"");
                    } else if (value instanceof Map<?, ?> map) {
                        String formattedMap = map.entrySet().stream()
                                .map(entry -> entry.getKey() + "=" + (entry.getValue() != null ? entry.getValue().toString() : ""))
                                .collect(Collectors.joining(","));
                        sb.append("\"").append(formattedMap).append("\"");
                    } else {
                        sb.append(value);
                    }
                } catch (IllegalAccessException e) {
                    sb.append("Невозможно прочитать поле");
                }
                if (i < fields.length - 1) {
                    sb.append(";");
                }
            }
            sb.append("\n");
        }
    }

    private static void generateTableColumns(Field[] fields, StringBuilder sb) {
        for (int i = 0; i < fields.length; i++) {
            String formattedName = splitCamelCaseAndCapitalize(fields[i].getName());
            sb.append(formattedName);
            if (i < fields.length - 1) {
                sb.append(";");
            }
        }
        sb.append("\n");
    }

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
