package org.example;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
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

public class StudentXmlServiceTest {
    private StudentXmlService studentXmlService;
    private Path tempFile;

    @BeforeClass
    public void setUpClass() throws IOException {
        tempFile = Files.createTempFile("test_students", ".xml");
    }

    @BeforeMethod
    public void setUp() throws IOException {
        studentXmlService = new StudentXmlService(tempFile.toString());
    }

    @AfterMethod
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void testAddStudent() throws IOException, JDOMException {
        Student student = new Student("1", "John", "Doe", "Computer Science");
        studentXmlService.addStudent(student);

        boolean contains = Files.readAllLines(tempFile).contains("John");
        Assert.assertFalse(contains);
    }

    @Test
    public void testRemoveStudent() throws IOException, JDOMException {
        Student student = new Student("2", "Jane", "Doe", "Mathematics");
        studentXmlService.addStudent(student);
        studentXmlService.removeStudent("2");

        boolean contains = Files.readAllLines(tempFile).contains("<Student>");
        Assert.assertFalse(contains);
    }

    @Test(expectedExceptions = IOException.class)
    public void testRemoveNonExistentStudent() throws IOException, JDOMException {
        studentXmlService.removeStudent("999");
    }

    @Test(expectedExceptions = IOException.class)
    public void testFileNotFound() throws IOException, JDOMException {
        studentXmlService = new StudentXmlService("non_existent_file.xml");
        studentXmlService.removeStudent("1");
    }
    @Test
    public void testRemoveStudentNotNull() throws IOException, JDOMException {
        Student student = new Student("2", "Jane", "Doe", "Mathematics");
        studentXmlService.addStudent(student);
        studentXmlService.removeStudent("2");

        String fileContent = Files.readAllLines(tempFile)
                .toString();
        Assert.assertNotNull(fileContent);
    }
    @Test
    public void testAddStudentEqualsTo() throws IOException, JDOMException {
        Student student = new Student("1", "John", "Doe", "Computer Science");
        studentXmlService.addStudent(student);

        Document doc = new SAXBuilder().build(tempFile.toFile());
        Element rootElement = doc.getRootElement();
        List<Element> studentElements = rootElement.getChildren("Student");

        Element studentElement = studentElements.get(0);
        Assert.assertEquals(studentElement.getAttributeValue("StudentId"), "1");
        Assert.assertEquals(studentElement.getChildText("FirstName"), "John");
        Assert.assertEquals(studentElement.getChildText("LastName"), "Doe");
        Assert.assertEquals(studentElement.getChildText("Major"), "Computer Science");
    }

    @DataProvider(name = "studentDataProvider")
    public Object[][] studentDataProvider() {
        return new Object[][]{
                {"1", "John", "Doe", "Computer Science"},
                {"2", "Jane", "Doe", "Mathematics"},
                {"3", "Emily", "Jones", "Biology"}
        };
    }

    @Test(dataProvider = "studentDataProvider")
    public void testAddStudent(String studentId, String firstName, String lastName, String major) throws IOException, JDOMException {
        Student student = new Student(studentId, firstName, lastName, major);
        studentXmlService.addStudent(student);

        Document doc = new SAXBuilder().build(tempFile.toFile());
        Element rootElement = doc.getRootElement();
        List<Element> studentElements = rootElement.getChildren("Student");

        boolean found = studentElements.stream().anyMatch(element ->
                element.getAttributeValue("StudentId").equals(studentId) &&
                        element.getChildText("FirstName").equals(firstName) &&
                        element.getChildText("LastName").equals(lastName) &&
                        element.getChildText("Major").equals(major));

        Assert.assertTrue(found);
    }
}
