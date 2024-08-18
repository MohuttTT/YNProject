package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.chain.model.Employee;

@Repository
public interface EmployeesRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT CONCAT(e.lastName, e.firstName) FROM Employee e WHERE e.empNo = :empNo")
    String findFullNameByEmpNo(@Param("empNo") Integer empNo);
}
