package org.writer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.annotation.FileType;
import org.writer.annotation.WritableClass;

@WritableClass(fileType = {FileType.CSV, FileType.TXT})
@Data
@Builder
@AllArgsConstructor
public class Person {

    private String firstName;

    private String lastName;

    private int dayOfBirth;

    private Months monthOfBirth;

    private int yearOfBirth;

}
