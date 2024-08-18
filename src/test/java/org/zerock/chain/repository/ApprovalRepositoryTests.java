package org.zerock.chain.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.chain.model.*;


import java.time.LocalDate;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Log4j2
public class ApprovalRepositoryTests {

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private EmployeesRepository employeesRepository;

    @Test
    public void setupDraftDocuments() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            Documents document = Documents.builder()
                    .docTitle("결재 문서입니다. 확인 부탁드립니다.")
                    .docStatus("임시저장")
                    .draftDate(LocalDate.now())
                    .senderEmpNo(1) // 사원 번호
                    .category("일반 기안")
                    .build();
            documentsRepository.save(document);
        });
    }

    @Test
    public void setupRequestDocuments() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            Documents document = Documents.builder()
                    .docTitle("결재 문서입니다. 확인 부탁드립니다.")
                    .docStatus("요청")
                    .reqDate(LocalDate.now())
                    .senderEmpNo(1) // 보내는 사원 번호
                    .receiverEmpNo(2) // 받는 사원 번호
                    .category("일반기안")
                    .build();
            documentsRepository.save(document);
        });
    }

    @Test
    public void setupEmployees() {
        Employee employee = Employee.builder()
                .empNo(1L)
                .firstName("나리")
                .lastName("이")
                .hireDate(LocalDate.now().minusYears(1))
                .lastDate(null)
                .phoneNum("010-1234-5678")
                .profileImg("profile1.png")
                .build();
        employeesRepository.save(employee);
    }

    @Test
    public void setupApproval() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            Documents document = documentsRepository.findById(i).orElseThrow();
            Employee employee = employeesRepository.findById((long) i).orElseThrow();

            Approval approval = Approval.builder()
                    .docNo(document)
                    .empNo(employee)
                    .approvalDate(LocalDate.now())
                    .rejectionReason("Sample rejection reason " + i)
                    .build();

            approvalRepository.save(approval);
        });
    }

    @Test
    public void testSave() {
        Documents document = Documents.builder()
                .docTitle("결재 올려드립니다")
                .docStatus("요청")
                .reqDate(LocalDate.now())
                .senderEmpNo(1)
                .receiverEmpNo(2)
                .category("일반기안")
                .build();

        Documents savedDocument = documentsRepository.save(document);
        assertNotNull(savedDocument);
        assertEquals(document.getDocTitle(), savedDocument.getDocTitle());
    }
}