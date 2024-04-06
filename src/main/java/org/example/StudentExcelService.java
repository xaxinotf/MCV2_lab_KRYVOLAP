package org.example;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class StudentExcelService {

    private final String filePath;

    public StudentExcelService(String filePath) {
        this.filePath = filePath;
    }

    public void addStudent(Student student) throws IOException {
        Workbook workbook;
        Sheet sheet;

        File file = new File(filePath);
        if (file.exists()) {
            workbook = new XSSFWorkbook(new FileInputStream(file));
            sheet = workbook.getSheetAt(0);
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Students");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("StudentId");
            header.createCell(1).setCellValue("FirstName");
            header.createCell(2).setCellValue("LastName");
            header.createCell(3).setCellValue("Major");
        }

        int lastRow = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(lastRow);
        row.createCell(0).setCellValue(student.getStudentId());
        row.createCell(1).setCellValue(student.getFirstName());
        row.createCell(2).setCellValue(student.getLastName());
        row.createCell(3).setCellValue(student.getMajor());

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            workbook.write(out);
        }
    }

    public void removeStudent(String studentId) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("The data file was not found: " + filePath);
        }

        Workbook workbook = new XSSFWorkbook(new FileInputStream(file));
        Sheet sheet = workbook.getSheetAt(0);

        boolean found = false;
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getCell(0).getStringCellValue().equals(studentId)) {
                sheet.removeRow(row);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Student with ID " + studentId + " not found.");
        }

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            workbook.write(out);
        }
    }
}
