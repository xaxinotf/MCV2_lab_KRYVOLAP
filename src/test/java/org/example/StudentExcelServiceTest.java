package org.example;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StudentExcelServiceTest {
    private StudentExcelService studentExcelService;
    private final String testFilePath = "testStudents.xlsx";
    private final Student testStudent = new Student("1", "John", "Doe", "CS");

    @BeforeClass
    public void setupClass() {
        File file = new File(testFilePath);
        if (file.exists()) {
            file.delete();
        }
    }

    @BeforeMethod
    public void setupMethod() {
        studentExcelService = new StudentExcelService(testFilePath);
    }
    @AfterMethod
    public void tearDownClass() {
        File file = new File(testFilePath);
        if (file.exists()) {
            file.delete();
        }
    }
    @Test(groups = "addTests")
    public void addStudentTest() throws IOException {
        studentExcelService.addStudent(testStudent);
        File file = new File(testFilePath);

        Assert.assertTrue(file.exists(), "File should exist after adding a student.");

        try (FileInputStream fis = new FileInputStream(file)) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(1);

            Assert.assertNotNull(row, "Row for added student should not be null.");
            Assert.assertEquals(row.getCell(0).getStringCellValue(), testStudent.getStudentId(), "Student ID should match.");
            Assert.assertEquals(row.getCell(1).getStringCellValue(), testStudent.getFirstName(), "First name should match.");
            Assert.assertTrue(row.getCell(3).getStringCellValue().contains("CS"), "Major should contain 'CS'.");
        }
    }

    @Test(expectedExceptions = FileNotFoundException.class, groups = "removeTests")
    public void removeStudentNotFoundTest() throws IOException {
        studentExcelService.removeStudent("non-existent-id");
    }

    @Test(groups = "removeTests")
    public void removeStudentTest() throws IOException {
        studentExcelService.addStudent(testStudent);
        studentExcelService.removeStudent(testStudent.getStudentId());
        File file = new File(testFilePath);

        try (FileInputStream fis = new FileInputStream(file)) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            Assert.assertFalse(sheet.getRow(1) != null && sheet.getRow(1).getCell(0).getStringCellValue().equals(testStudent.getStudentId()), "Student should be removed.");
        }
    }
    @DataProvider(name = "studentData")
    public Object[][] provideStudentData() {
        return new Object[][] {
                { new Student("1", "John", "Doe", "CS") },
                { new Student("2", "Jane", "Roe", "Math") }
        };
    }

    @Test(dataProvider = "studentData", groups = "addTests")
    public void addStudentParameterizedTest(Student student) throws IOException {
        studentExcelService.addStudent(student);
        File file = new File(testFilePath);

        Assert.assertTrue(file.exists(), "File should exist after adding a student.");

        try (FileInputStream fis = new FileInputStream(file)) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            boolean studentFound = false;
            for (Row row : sheet) {
                if (row.getCell(0).getStringCellValue().equals(student.getStudentId())) {
                    studentFound = true;
                    Assert.assertEquals(row.getCell(1).getStringCellValue(), student.getFirstName(), "First name should match.");
                    Assert.assertEquals(row.getCell(2).getStringCellValue(), student.getLastName(), "Last name should match.");
                    Assert.assertEquals(row.getCell(3).getStringCellValue(), student.getMajor(), "Major should match.");
                    break;
                }
            }

            Assert.assertTrue(studentFound, "Student should be found in the sheet.");
        }
    }
}
