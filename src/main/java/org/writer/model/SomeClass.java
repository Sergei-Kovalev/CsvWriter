package org.writer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Класс который специально не помечен аннотацией, для проверки, что файл не будет создаваться и записываться
 */
@Data
@Builder
@AllArgsConstructor
public class SomeClass {
    private String name;
    private int age;
}
