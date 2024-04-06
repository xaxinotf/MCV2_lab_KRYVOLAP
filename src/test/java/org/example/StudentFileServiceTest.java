package org.example;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class StudentFileServiceTest {

    private StudentFileService studentFileService;
    private Path tempFile;

    @BeforeClass
    public void setUpClass() throws IOException {
        tempFile = Files.createTempFile("test_students", ".txt");
    }

    @BeforeMethod
    public void setUp() throws IOException {
        studentFileService = new StudentFileService(tempFile.toString());
        Files.write(tempFile, new byte[0]);
    }
    @AfterMethod
    public void tearDown() throws IOException {
        Files.write(tempFile, new byte[0]);
    }

    @Test
    public void testAddStudent() throws IOException {
        Student student = new Student("1", "John", "Doe", "Computer Science");
        studentFileService.addStudent(student);
        List<Student> students = studentFileService.getAllStudents();
        Assert.assertTrue(students.contains(student));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testAddStudentNull() throws IOException {
        studentFileService.addStudent(null);
    }

    @Test
    public void testRemoveStudent() throws IOException {
        Student student = new Student("2", "Jane", "Doe", "Mathematics");
        studentFileService.addStudent(student);
        studentFileService.removeStudent("2");
        List<Student> students = studentFileService.getAllStudents();
        Assert.assertFalse(students.contains(student));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRemoveNonExistentStudentShouldThrow() throws IOException {
        studentFileService.removeStudent("999");
    }

    @Test
    public void testGetAllStudentsSize() throws IOException {
        Student student = new Student("3", "Mike", "Smith", "Physics");
        studentFileService.addStudent(student);
        List<Student> students = studentFileService.getAllStudents();
        Assert.assertEquals(students.size(), 1);
    }

    @Test
    public void testGetAllStudentsNotEmpty() throws IOException {
        Student student = new Student("4", "Emily", "Jones", "Biology");
        studentFileService.addStudent(student);
        List<Student> students = studentFileService.getAllStudents();
        Assert.assertFalse(students.isEmpty());
    }
    @DataProvider(name = "studentDataProvider")
    public static Object[][] studentDataProvider() {
        return new Object[][]{
                {new Student("1", "John", "Doe", "Computer Science")},
                {new Student("2", "Jane", "Doe", "Mathematics")}
        };
    }

    @Test(dataProvider = "studentDataProvider")
    public void testAddStudent(Student student) throws IOException {
        studentFileService.addStudent(student);
        List<Student> students = studentFileService.getAllStudents();
        Assert.assertTrue(students.contains(student));
    }
    @Test
    public void testGetAllStudentsWhenEmpty() throws IOException {
        List<Student> students = studentFileService.getAllStudents();
        Assert.assertNotNull(students);
    }
}
