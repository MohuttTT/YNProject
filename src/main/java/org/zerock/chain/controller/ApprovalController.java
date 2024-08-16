package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.chain.model.Documents;
import org.zerock.chain.dto.*;
import org.zerock.chain.service.DocumentsService;
import org.zerock.chain.service.FormDataService;
import org.zerock.chain.service.FormFieldsService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/approval")
@Log4j2
public class ApprovalController {

    private final DocumentsService documentsService;
    private final FormDataService formDataService;
    private final FormFieldsService formFieldsService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public ApprovalController(DocumentsService documentsService, FormDataService formDataService, FormFieldsService formFieldsService) {
        this.documentsService = documentsService;
        this.formDataService = formDataService;
        this.formFieldsService = formFieldsService;
    }

    @GetMapping("/main")                                         // 보낸 문서함 페이지로 이동
    public String approvalMain(@RequestParam(value = "senderEmpNo", defaultValue = "1") Integer senderEmpNo, Model model) {
        // 보낸 문서함에 대한 문서 목록을 조회
        List<SentDocumentsDTO> sentDocuments = documentsService.getSentDocuments(senderEmpNo);
        model.addAttribute("sentDocuments", sentDocuments);
        return "approval/main";
    }

    @GetMapping("/receive")                                      // 받은 문서함 페이지로 이동
    public String approvalReceive(@RequestParam(value = "receiverEmpNo", defaultValue = "1") Integer receiverEmpNo, Model model) {
        // 받은 문서함에 대한 문서 목록을 조회
        List<ReceiveDocumentsDTO> receivedDocuments = documentsService.getReceivedDocuments(receiverEmpNo);
        log.info(receivedDocuments);
        model.addAttribute("receivedDocuments", receivedDocuments);
        return "approval/receive";
    }

    @GetMapping("/draft")                                        // 임시 문서함 페이지로 이동
    public String approvalDraft(Model model) {
        // 임시 문서함에 대한 문서 목록을 조회
        List<DraftDocumentsDTO> draftDocuments = documentsService.getDraftDocuments();
        log.info(draftDocuments);
        model.addAttribute("draftDocuments", draftDocuments);
        return "approval/draft";
    }

    @GetMapping("/adminRequest")
    public String approvalAdminRequest() { return "approval/adminRequest"; }

    @GetMapping("/completedRead")
    public String approvalCompletedRead() { return "approval/completedRead"; }

    @GetMapping("/draftRead")
    public String approvalDraftRead() { return "approval/draftRead"; }

    @GetMapping("/process")
    public String approvalProcess(@RequestParam("docNo") int docNo,
                                  @RequestParam("category") String category,
                                  Model model) {
        // 필요에 따라 docNo와 category를 모델에 추가
        model.addAttribute("docNo", docNo);
        model.addAttribute("category", category);

        // 추가적으로 필요하면 문서 정보를 조회하여 모델에 추가
        DocumentsDTO document = documentsService.getDocumentById(docNo);
        model.addAttribute("document", document);
        return "approval/process";
    }

    @GetMapping("/read")
    public String approvalRead() { return "approval/read"; }

    @GetMapping("/rejectionRead")
    public String approvalRejectionRead() { return "approval/rejectionRead"; }

    @GetMapping("/generalApproval")
    public String approvalGeneralApproval() { return "approval/generalApproval"; }

    @GetMapping("/expense")
    public String approvalExpense() { return "approval/expense"; }

    @GetMapping("/overTime")
    public String approvalOverTime() { return "approval/overTime"; }

    // 여기서 부터 document 관련 메서드 입니다!!
    @GetMapping("/read/{docNo}")
    public String readDocument(@PathVariable("docNo") int docNo,
                               @RequestParam("category") String category,
                               Model model) {
        // 문서 번호로 문서 조회
        DocumentsDTO documentDTO = documentsService.getDocumentById(docNo);

        // 모델에 문서 데이터를 추가
        model.addAttribute("document", documentDTO);
        model.addAttribute("category", category);

        // 'read.html' 뷰를 반환
        return "/approval/read";
    }

    @PostMapping("/submit-form")
    public String submitForm(@RequestParam("docNo") int docNo,
                             @RequestParam("docTitle") String docTitle,
                             @RequestParam("docStatus") String docStatus,
                             RedirectAttributes redirectAttributes) {

        // 문서 번호로 기존 문서를 조회
        DocumentsDTO documentDTO = documentsService.getDocumentById(docNo);

        // 사용자가 입력한 제목을 설정
        documentDTO.setDocTitle(docTitle);
        documentDTO.setDocStatus(docStatus);

        Documents document = modelMapper.map(documentDTO, Documents.class);

        // 변경 사항을 데이터베이스에 저장
        documentsService.saveDocument(document, null, null);

        // 리다이렉트와 함께 메시지 추가
        redirectAttributes.addFlashAttribute("message", "문서가 성공적으로 제출되었습니다.");

        return "redirect:/approval/main"; // main.html로 리다이렉트
    }

    @PostMapping("/create-document")
    public ResponseEntity<Map<String, Integer>> createDocument(@RequestBody Documents documents) {
        // 문서 엔티티 생성 및 카테고리 설정
        documents.setReqDate(LocalDate.now()); // 현재 날짜를 저장
        documents.setSenderEmpNo(1); // 임시로 고정된 사원 번호 설정
        documents.setReceiverEmpNo(2); // 임시로 고정된 수신자 번호 설정
        documents.setDocStatus("요청"); // 임시로 무조건 등록하면 상태가 요청으로 나오게 설정

        // 문서 저장 후 문서 번호 반환
        int savedDocument = documentsService.saveDocument(documents, null, null); // 문서 저장

        // 저장된 문서의 번호 반환
        Map<String, Integer> response = new HashMap<>();
        response.put("docNo", savedDocument);
        return ResponseEntity.ok(response);
    }

    // category에 기반하여 템플릿 이름을 결정하는 로직
    private String getTemplateNameByCategory(String category) {
        switch (category) {
            case "일반기안서":
                return "generalApproval.html";   // 일반 기안
            case "지출결의서":
                return "expense.html";           // 지출결의서
            case "연장근무신청서":
                return "overTime.html";          // 연장근무 신청서
            default:
                return "default";  // 기본 템플릿
        }
    }
}
