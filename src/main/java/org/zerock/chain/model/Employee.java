package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")
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
}