package org.zerock.chain.imjongha.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "employees")
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_no")
    private Long empNo;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "phone_num", nullable = false, length = 15)
    private String phoneNum;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "addr", length = 255)
    private String addr;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "last_date")
    private LocalDate lastDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dmp_no")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank_no")
    private Rank rank;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeePermission> employeePermissions = new ArrayList<>();

    // 연차관리를 위한 table 연결(영민이 추가)
    @OneToOne(mappedBy = "employee", fetch = FetchType.LAZY)
    private EmployeeLeave employeeLeave;

    // 편의 메서드
    public void addPermission(Permission permission) {
        EmployeePermission employeePermission = new EmployeePermission(this, permission);
        employeePermissions.add(employeePermission);
        permission.getEmployeePermissions().add(employeePermission);
    }

    public void removePermission(Permission permission) {
        EmployeePermission employeePermission = new EmployeePermission(this, permission);
        permission.getEmployeePermissions().remove(employeePermission);
        employeePermissions.remove(employeePermission);
    }

    // fullName 필드를 생성하기 위해 @Transient 사용(영민 추가)
    @Transient
    public String getFullName() {
        return lastName + firstName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(empNo, employee.empNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empNo);
    }
}