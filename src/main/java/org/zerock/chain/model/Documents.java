package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "documents")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Documents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_no")
    private Integer docNo;

    @Column(name = "sender_emp_no")
    private Integer senderEmpNo;

    @Column(name = "receiver_emp_no")
    private Integer receiverEmpNo;

    @Column(name = "doc_title", nullable = false)
    private String docTitle;

    @Column(name = "doc_status")
    private String docStatus;

    @Column(name = "req_date")
    private LocalDate reqDate;

    @Column(name = "re_req_date")
    private LocalDate reReqDate;

    @Column(name = "draft_date")
    private LocalDate draftDate;

    @Column(name = "category")
    private String category;

    @Column(name = "doc_body", columnDefinition = "longtext")
    private String docBody;  // 문서 내용

    @Column(name = "approval_line", columnDefinition = "longtext")
    private String approvalLine;  // 결재선 Div 태그 HTML 부분

    @Column(name = "time_stamp_html", columnDefinition = "longtext")
    private String timeStampHtml;  // 타임스탬프 Div 태그 HTML 부분

    @Column(name = "approver_no_html", columnDefinition = "longtext")
    private String approverNoHtml;  // 결재자 순서 번호 Div 태그 HTML 부분

    @Column(name = "file_path")
    private String filePath;  // 파일 경로

    @Override
    public String toString() {
        return "DocumentsEntity{" +
                "docNo=" + docNo +
                ", docTitle='" + docTitle + '\'' +
                ", docStatus='" + docStatus + '\'' +
                ", reqDate=" + reqDate +
                ", senderEmpNo=" + senderEmpNo +
                ", receiverEmpNo=" + receiverEmpNo +
                ", category='" + category + '\'' +
                ", docBody='" + docBody + '\'' +
                ", approvalLine='" + approvalLine + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
