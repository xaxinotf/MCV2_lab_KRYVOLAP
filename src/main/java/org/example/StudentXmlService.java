package org.example;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class StudentXmlService {
    private final String filePath;

    public StudentXmlService(String filePath) {
        this.filePath = filePath;
    }

    public void addStudent(Student student) throws IOException, JDOMException {
        Document doc;
        Element root;
        File file = new File(filePath);

        if (file.exists() && file.length() > 0) {
            doc = new SAXBuilder().build(file);
            root = doc.getRootElement();
        } else {
            doc = new Document();
            root = new Element("Students");
            doc.setRootElement(root);
        }

        Element studentElement = new Element("Student");
        studentElement.setAttribute("StudentId", student.getStudentId());
        studentElement.addContent(new Element("FirstName").setText(student.getFirstName()));
        studentElement.addContent(new Element("LastName").setText(student.getLastName()));
        studentElement.addContent(new Element("Major").setText(student.getMajor()));

        root.addContent(studentElement);

        try (java.io.OutputStream out = Files.newOutputStream(file.toPath())) {
            new XMLOutputter(Format.getPrettyFormat()).output(doc, out);
        }
    }

    public void removeStudent(String studentId) throws IOException, JDOMException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found.");
        }

        Document doc = new SAXBuilder().build(file);
        Element root = doc.getRootElement();

        List<Element> students = root.getChildren("Student");
        boolean found = false;
        for (Element student : students) {
            if (student.getAttributeValue("StudentId").equals(studentId)) {
                student.detach();
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Student with ID " + studentId + " not found.");
        }

        try (java.io.OutputStream out = Files.newOutputStream(file.toPath())) {
            new XMLOutputter(Format.getPrettyFormat()).output(doc, out);
        }
    }
}