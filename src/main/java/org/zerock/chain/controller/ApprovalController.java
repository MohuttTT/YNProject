package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.chain.model.Documents;
import org.zerock.chain.dto.*;
import org.zerock.chain.model.Form;
import org.zerock.chain.service.DocumentsService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/approval")
@Log4j2
public class ApprovalController {

    private final DocumentsService documentsService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public ApprovalController(DocumentsService documentsService) {
        this.documentsService = documentsService;
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
    public String approvalAdminRequest() {
        return "approval/adminRequest";
    }

    @GetMapping("/completedRead")
    public String approvalCompletedRead() {
        return "approval/completedRead";
    }

    @GetMapping("/draftRead")
    public String approvalDraftRead() {
        return "approval/draftRead";
    }

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
    public String approvalRead() {
        return "approval/read";
    }

    @GetMapping("/rejectionRead")
    public String approvalRejectionRead() {
        return "approval/rejectionRead";
    }

    @GetMapping("/generalApproval")
    public String approvalGeneralApproval() {
        return "approval/generalApproval";
    }

    @GetMapping("/expense")
    public String approvalExpense() {
        return "approval/expense";
    }

    @GetMapping("/overTime")
    public String approvalOverTime() {
        return "approval/overTime";
    }

    // 여기서 부터 document 관련 메서드 입니다!!
    @PostMapping("/create-document")
    public ResponseEntity<Map<String, Integer>> createDocument(@RequestBody RequestDTO requestDTO) {
        // 저장된 문서의 번호 반환
        Map<String, Integer> response = new HashMap<>();
        try {
            // 요청 데이터 출력
            log.info("RequestDTO: {}", requestDTO);

            // 문서와 양식 저장 후 문서 번호 반환
            int docNo = documentsService.saveDocument(requestDTO);
            if (docNo == -1) {
                throw new RuntimeException("Document save failed in service");
            }
            log.info("Generated docNo: {}", docNo); // docNo를 로그로 출력

            response.put("docNo", docNo);
            log.info("Response: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외 발생 시 에러 로그 출력
            log.error("Error occurred while creating document", e);
            // 예외 발생 시 클라이언트에게 적절한 오류 응답 반환
            response.put("error", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /*@PostMapping("/submit-form")
    public String submitForm(@RequestParam("docNo") int docNo,
                             @RequestParam("docTitle") String docTitle,
                             @RequestParam("docStatus") String docStatus,
                             @RequestParam("formHtml") String formHtml,
                             RedirectAttributes redirectAttributes) {

        // 문서 번호로 기존 문서를 조회
        DocumentsDTO documentDTO = documentsService.getDocumentById(docNo);

        // 사용자가 입력한 제목을 설정
        documentDTO.setDocTitle(docTitle);
        documentDTO.setDocStatus(docStatus);

        Documents document = modelMapper.map(documentDTO, Documents.class);

        // 변경 사항을 데이터베이스에 저장
        documentsService.saveDocument(document);

        // 리다이렉트와 함께 메시지 추가
        redirectAttributes.addFlashAttribute("message", "문서가 성공적으로 제출되었습니다.");

        return "redirect:/approval/main"; // main.html로 리다이렉트
    }*/

    @GetMapping("/read/{docNo}")
    public String readDocument(@PathVariable("docNo") int docNo,
                               @RequestParam("category") String category,
                               Model model) {
        // 문서 번호로 문서 조회
        DocumentsDTO document = documentsService.getDocumentById(docNo);

        // 모델에 문서 데이터를 추가
        model.addAttribute("document", document);
        model.addAttribute("category", category);

        // 'read.html' 뷰를 반환
        return "/approval/read";
    }

}