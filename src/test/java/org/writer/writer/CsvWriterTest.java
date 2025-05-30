package org.writer.writer;

import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.writer.model.Months;
import org.writer.model.Person;
import org.writer.model.SomeClass;
import org.writer.model.Student;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class CsvWriterTest {

    private final Faker faker = new Faker();
    private String fileName;
    private File file;

    @BeforeEach
    void setUp() {
        fileName = "Test";
        file = new File(String.format("output/%s.csv", fileName));
    }

    @Test
    void writeToFile_withPersonClass() throws IOException {
        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            persons.add(new Person(
                    faker.name().firstName(),
                    faker.name().lastName(),
                    faker.number().numberBetween(1, 20),
                    Months.values()[faker.number().numberBetween(0, 12)],
                    faker.number().numberBetween(2000, 2025)
            ));
        }

        new CsvWriter().writeToFile(persons, fileName);

        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

        assertFalse(lines.isEmpty());
        assertTrue(lines.get(0).contains("First Name"));
    }

    @Test
    void writeToFile_withStudentClass() throws IOException {
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            students.add(
                    new Student(faker.name().firstName(), List.of(
                            String.valueOf(faker.number().numberBetween(1, 50)),
                            String.valueOf(faker.number().numberBetween(1, 50))))
                        );
        }

        new CsvWriter().writeToFile(students, fileName);

        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

        assertFalse(lines.isEmpty());
        assertTrue(lines.get(0).contains("Name"));
    }

    @Test
    void writeToFile_withSomeClass() {
        List<SomeClass> some = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            some.add(new SomeClass(faker.name().firstName(), faker.number().numberBetween(1, 10)));
        }

        new CsvWriter().writeToFile(some, fileName);

        assertFalse(file.exists());
    }

    @Test
    void writeToFile_whenDataEmpty() throws IOException {
        List<Person> persons = new ArrayList<>();

        new CsvWriter().writeToFile(persons, fileName);

        assertTrue(file.exists());
        assertEquals(0, file.length());

        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

        assertTrue(lines.isEmpty());
    }

    @Test
    void writeToFile_whenFileNameEmpty() {
        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            persons.add(new Person(
                    faker.name().firstName(),
                    faker.name().lastName(),
                    faker.number().numberBetween(1, 20),
                    Months.values()[faker.number().numberBetween(0, 12)],
                    faker.number().numberBetween(2000, 2025)
            ));
        }

        new CsvWriter().writeToFile(persons, "");

        assertFalse(file.exists());
    }

    @AfterEach
    void tearDown() {
        file.delete();
    }
}