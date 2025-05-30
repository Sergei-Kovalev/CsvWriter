package org.writer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.writer.annotation.FileType;
import org.writer.annotation.WritableClass;

import java.util.List;

@WritableClass(fileType = FileType.CSV)
@Data
@Builder
@AllArgsConstructor
public class Student {

    private String name;

    private List<String> score;
}