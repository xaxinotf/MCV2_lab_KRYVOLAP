package org.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudentFileService {
    private final String filePath;

    public StudentFileService(String filePath) {
        this.filePath = filePath;
    }

    public void addStudent(Student student) throws IOException {
        Path path = Paths.get(filePath);
        Files.write(path, (student.toString() + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public void removeStudent(String studentId) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("The data file was not found: " + filePath);
        }

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<String> updatedLines = lines.stream()
                .filter(line -> !line.startsWith(studentId + ","))
                .collect(Collectors.toList());

        if (lines.size() == updatedLines.size()) {
            throw new IllegalArgumentException("Student with ID " + studentId + " not found.");
        }

        Files.write(path, updatedLines, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public List<Student> getAllStudents() throws IOException {
        Path path = Paths.get(filePath);
        List<Student> students = new ArrayList<>();
        if (Files.exists(path)) {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    students.add(new Student(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        }
        return students;
    }
}
