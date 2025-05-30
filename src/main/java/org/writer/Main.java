package org.writer;

import org.writer.model.Months;
import org.writer.model.Person;
import org.writer.model.SomeClass;
import org.writer.model.Student;
import org.writer.writer.CsvWriter;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Writable writable = new CsvWriter();

        Person person = new Person("Misha", "Ovchinnikov", 1, Months.APRIL, 2022);
        Person person2 = new Person("Olga", null, 22, Months.APRIL, 2020);
        Person person3 = new Person("Petia", "Normanov", 11, Months.APRIL, 2018);
        List<Person> data = List.of(person, person2, person3);
        writable.writeToFile(data, "Persons");

        Student student = new Student("Vasia", List.of("22", "11", "48"));
        Student student2 = new Student("Petia", List.of("11", "12", "56"));
        List<Student> data2 = List.of(student, student2);
        writable.writeToFile(data2, "Students");

        SomeClass someClass = new SomeClass("Name1", 22);
        SomeClass someClass2 = new SomeClass("Name2", 21);
        List<SomeClass> data3 = List.of(someClass, someClass2);
        writable.writeToFile(data3, "NotCreated");
    }
}