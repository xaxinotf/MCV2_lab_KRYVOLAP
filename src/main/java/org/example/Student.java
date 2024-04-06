package org.example;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Student {

    private String studentId;
    private String firstName;
    private String lastName;
    private String major;

    @Override
    public String toString() {
        return studentId + "," + firstName + "," + lastName + "," + major;
    }
}
