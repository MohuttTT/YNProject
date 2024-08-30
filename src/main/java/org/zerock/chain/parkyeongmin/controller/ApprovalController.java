package org.zerock.chain.parkyeongmin.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.parkyeongmin.dto.*;
import org.zerock.chain.parkyeongmin.model.Approval;
import org.zerock.chain.parkyeongmin.model.Documents;
import org.zerock.chain.parkyeongmin.repository.ApprovalRepository;
import org.zerock.chain.parkyeongmin.repository.DocumentsRepository;
import org.zerock.chain.parkyeongmin.repository.EmployeesRepository;
import org.zerock.chain.parkyeongmin.service.ApprovalService;
import org.zerock.chain.parkyeongmin.service.DocumentsService;
import org.zerock.chain.parkyeongmin.service.FormService;
import org.zerock.chain.parkyeongmin.service.UserService;
import org.zerock.chain.pse.service.NotificationService;
import org.zerock.chain.pse.service.NotificationServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/approval")
@Log4j2
public class ApprovalController {

    private final DocumentsService documentsService;
    private final FormService formService;  // FormService 주입
    private final UserService userService;
    private final ApprovalService approvalService;

    @Autowired
    private ApprovalRepository approvalRepository;
    @Autowired
    private EmployeesRepository employeesRepository;
    @Autowired
    private DocumentsRepository documentsRepository;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    public ApprovalController(DocumentsService documentsService,
                              FormService formService,
                              UserService userService,
                              ApprovalService approvalService) {
        this.documentsService = documentsService;
        this.formService = formService;
        this.userService = userService;
        this.approvalService = approvalService;
    }

    @GetMapping("/main")  // 보낸 문서함 페이지로 이동
    public String approvalMain(Model model) {
        // 로그인한 사용자의 정보를 userService의 getLoggedInUserEmpNo메서드로 가져오기
        Long loggedInEmpNo = userService.getLoggedInUserEmpNo();;
        List<SentDocumentsDTO> sentDocuments = documentsService.getSentDocuments(loggedInEmpNo);

        // 가상 번호 부여
        int virtualNo = sentDocuments.size();
        for (DocumentsDTO document : sentDocuments) {
            document.setVirtualNo(virtualNo--);  // 가상의 번호를 설정
            log.info("sentDocument: {}, sentVirtualNo: {}", document.getDocNo(), document.getVirtualNo());
        }

        model.addAttribute("sentDocuments", sentDocuments);
        return "approval/main";
    }

    @GetMapping("/receive")  // 받은 문서함 페이지로 이동
    public String approvalReceive(Model model) {
        // 로그인한 사용자의 정보를 userService의 getLoggedInUserEmpNo메서드로 가져오기
        Long loggedInEmpNo = userService.getLoggedInUserEmpNo();

        // 로그인한 사용자가 결재자 또는 참조자인 문서를 docNo 기준으로 최신순 조회
        List<Approval> approvals = approvalRepository.findByEmployeeOrRefEmployee(loggedInEmpNo);

        // Approval 엔티티에서 DocumentsDTO로 변환
        List<DocumentsDTO> receivedDocuments = approvals.stream()
                .map(approval -> convertToDocumentsDTO(approval.getDocuments()))  // Approval 엔티티에서 Documents 엔티티로 접근
                .collect(Collectors.toList());

        // 가상 번호 부여 (최신순 정렬에 따라 부여)
        int virtualNo = receivedDocuments.size();
        for (DocumentsDTO document : receivedDocuments) {
            document.setVirtualNo(virtualNo--);  // 가상의 번호를 설정
            log.info("receiveDocument: {}, receiveVirtualNo: {}", document.getDocNo(), document.getVirtualNo());
        }

        model.addAttribute("receivedDocuments", receivedDocuments);

        return "approval/receive";
    }

    @GetMapping("/draft")  // 임시 문서함 페이지로 이동
    public String approvalDraft(Model model) {
        // 로그인한 사용자의 정보를 userService의 getLoggedInUserEmpNo메서드로 가져오기
        Long loggedInEmpNo = userService.getLoggedInUserEmpNo();
        List<DraftDocumentsDTO> draftDocuments = documentsService.getDraftDocuments(loggedInEmpNo);

        // 가상 번호 부여
        int virtualNo = draftDocuments.size();
        for (DocumentsDTO document : draftDocuments) {
            document.setVirtualNo(virtualNo--);  // 가상의 번호를 설정
            log.info("draftDocument: {}, draftVirtualNo: {}", document.getDocNo(), document.getVirtualNo());
        }

        model.addAttribute("draftDocuments", draftDocuments);
        return "approval/draft";
    }

    @GetMapping("/process")
    public String approvalProcess(Model model) {
        return "approval/process";
    }

    // 여기서 부터 document 관련 메서드 입니다!!
    @PostMapping("/create-document")
    public ResponseEntity<Map<String, Object>> createDocument(@ModelAttribute DocumentsDTO documentsDTO) {
        Map<String, Object> response = new HashMap<>();
        // 요청 데이터 출력
        log.info("DocumentsDTO: {}", documentsDTO);

        try {
            // 문서 저장 후 문서 번호 반환
            int docNo = documentsService.saveDocument(documentsDTO);
            log.info("Saved document with docNo: {}", docNo);

            // 반환된 docNo를 DocumentsDTO에 설정
            documentsDTO.setDocNo(docNo);

            // 결재 요청 처리
            if (!"임시저장".equals(documentsDTO.getDocStatus())) {
                approvalService.requestApproval(documentsDTO);
                log.info("Approval process completed for docNo: {}", docNo);
            } else {
                log.info("Document saved as draft. Approval process skipped for docNo: {}", docNo);
            }

            response.put("docNo", docNo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error while saving document: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/draftRead/{docNo}")
    public String draftReadDocument(@PathVariable("docNo") int docNo,
                               Model model) {

        // 문서 정보 조회
        DocumentsDTO document = documentsService.getDocumentById(docNo);

        // 모델에 문서 데이터를 추가
        model.addAttribute("document", document);

        // 'draftRead.html' 뷰를 반환
        return "/approval/draftRead";
    }

    @GetMapping("/read/{docNo}")
    public String readDocument(@PathVariable("docNo") int docNo,
                               Model model) {

        // 문서 정보 조회
        DocumentsDTO document = documentsService.getDocumentById(docNo);

        // 모델에 문서 데이터를 추가
        model.addAttribute("document", document);

        // 'read.html' 뷰를 반환
        return "/approval/read";
    }

    @GetMapping("/adminRequest/{docNo}")
    public String adminRequestDocument(@PathVariable("docNo") int docNo,
                                       Model model) {
        // 로그인한 사용자의 정보를 가져옵니다.
        EmployeeDTO loggedInUser = userService.getLoggedInUserDetails();

        // 문서 정보 조회
        DocumentsDTO document = documentsService.getDocumentById(docNo);

        // 모델에 사용자 정보를 추가
        model.addAttribute("loggedInUser", loggedInUser);
        // 모델에 문서 데이터를 추가
        model.addAttribute("document", document);

        // 'adminRequest.html' 뷰를 반환
        return "/approval/adminRequest";
    }

    @GetMapping("/rejectionRead/{docNo}")
    public String approvalRejectionDocument(@PathVariable("docNo") int docNo,
                                            @RequestParam("source") String source,
                                            Model model) {
        log.info("Source: {}", source);

        // 문서 정보 조회
        DocumentsDTO document = documentsService.getDocumentById(docNo);

        // 모델에 문서 데이터를 추가
        model.addAttribute("document", document);
        // 출처 페이지 정보 추가
        model.addAttribute("source", source);

        // 'rejectionRead.html' 뷰를 반환
        return "/approval/rejectionRead";
    }

    @GetMapping("/getForm/{category}")
    public ResponseEntity<FormDTO> getFormByCategory(@PathVariable String category) {
        log.info("Fetching form HTML for category: {}", category);

        FormDTO formDTO = formService.getFormByCategory(category);

        if (formDTO != null) {
            return ResponseEntity.ok(formDTO);
        } else {
            log.error("Form not found for category: {}", category);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/getDocumentData/{docNo}")
    public ResponseEntity<Map<String, Object>> getDocumentData(@PathVariable("docNo") int docNo) {
        // 로그인한 사용자의 정보를 userService의 getLoggedInUserEmpNo 메서드로 가져오기
        Long loggedInEmpNo = userService.getLoggedInUserEmpNo();

        // 문서 정보 및 결재 순서 조회
        DocumentsDTO document = documentsService.getDocumentWithApprovalOrder(docNo, loggedInEmpNo);

        // 첫 번째 결재자가 승인했는지 여부 확인
        boolean isFirstApprovalApproved = approvalService.isFirstApprovalApproved(docNo);

        // 현재 결재순서인 자만 결재 승인, 결재 반려 버튼이 보이게하는 변수
        boolean isCurrentApprover = approvalService.isCurrentApprover(docNo, loggedInEmpNo);

        // 사용자가 해당 문서의 결재자인지 확인
        boolean isDocumentApprover = approvalService.isDocumentApprover(docNo, loggedInEmpNo);

        // 반려 사유 조회
        String rejectionReason = documentsService.getRejectionReason(docNo);

        // 반환할 데이터를 맵에 추가
        Map<String, Object> response = new HashMap<>();
        response.put("document", document);
        response.put("isFirstApprovalApproved", isFirstApprovalApproved);
        response.put("isCurrentApprover", isCurrentApprover);
        response.put("rejectionReason", rejectionReason);  // 반려 사유 추가
        response.put("isDocumentApprover", isDocumentApprover);          // 결재자인지 확인

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllEmployees")
    public ResponseEntity<Map<String, Object>> getAllEmployees() {
        try {
            List<EmployeeDTO> employees = userService.getAllEmployees();
            EmployeeDTO loggedInUser = userService.getLoggedInUserDetails();

            // 반환될 데이터를 맵에 추가
            Map<String, Object> response = new HashMap<>();
            response.put("employees", employees);
            response.put("loggedInUser", loggedInUser);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error occurred in getAllEmployees: ", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/getEmployees")
    public ResponseEntity<EmployeeDTO> getLoggedInUserDetails() {
        // 로그인한 사용자의 정보를 가져옵니다
        EmployeeDTO loggedInUser = userService.getLoggedInUserDetails();

        return ResponseEntity.ok(loggedInUser);
    }

    // 임시저장,철회 관련 메서드
    @PostMapping("/update-document")
    public ResponseEntity<String> updateDocument(@ModelAttribute DocumentsDTO documentsDTO) {
        try {
            // 문서의 senderName을 documents 테이블에서 가져와 DTO에 설정
            Documents document = documentsRepository.findById(documentsDTO.getDocNo())
                    .orElseThrow(() -> new RuntimeException("Document not found"));
            documentsDTO.setSenderName(document.getSenderName());

            // 만약 철회 플래그가 true라면, 알림을 보냄
            if ("true".equals(documentsDTO.getWithdraw())) {
                notificationService.createApprovalNotification( documentsDTO.getDocNo(),
                        documentsDTO.getDocTitle(),
                        documentsDTO.getSenderName(),
                        "임시저장",
                        documentsDTO.getWithdraw());
            }

            // 문서 업데이트
            documentsService.updateDocument(documentsDTO);

            return ResponseEntity.ok("Document updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update document");
        }
    }

    // 임시문서를 다시 결재요청할 때 쓰는 메서드
    @PostMapping("/submit-approval")
    public ResponseEntity<String> submitApproval(@ModelAttribute DocumentsDTO documentsDTO) {
        try {
            // 결재자 정보를 삭제하지 않고 문서 업데이트 (상태 변경)
            documentsService.updateDocumentWithoutDeletingApprovals(documentsDTO);
            log.info("제발제발제발");
            // 결재 요청 처리
            approvalService.requestApproval(documentsDTO);
            log.info("Approval process completed2 for docNo: {}", documentsDTO.getDocNo());

            return ResponseEntity.ok("Approval submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to submit approval");
        }
    }

    @DeleteMapping("/delete-document/{docNo}")
    public ResponseEntity<String> deleteDocument(@PathVariable int docNo) {
        try {
            documentsService.deleteDocument(docNo);
            return ResponseEntity.ok("Document deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete document");
        }
    }

    private DocumentsDTO convertToDocumentsDTO(Documents document) {
        DocumentsDTO documentsDTO = new DocumentsDTO();

        // Documents 엔티티의 필드를 DocumentsDTO에 복사
        documentsDTO.setDocNo(document.getDocNo());
        documentsDTO.setDocTitle(document.getDocTitle());
        documentsDTO.setDocBody(document.getDocBody());
        documentsDTO.setSenderName(document.getSenderName()); // 예시로 sender의 이름을 가져옴
        documentsDTO.setReqDate(document.getReqDate());
        documentsDTO.setCategory(document.getCategory());
        documentsDTO.setDocStatus(document.getDocStatus());
        // 추가적인 필드가 있으면 여기에 추가

        return documentsDTO;
    }

    // 결재 승인버튼을 누르면 timeStamp가 업데이트되는 메서드입니다!!
    @PostMapping("/approve/{docNo}")
    public String approveDocument(@PathVariable("docNo") int docNo, @RequestParam("timeStampHtml") String timeStampHtml) {
        // 로그인한 사용자의 정보를 userService의 getLoggedInUserEmpNo 메서드로 가져오기
        Long loggedInEmpNo = userService.getLoggedInUserEmpNo();

        // 결재 승인 처리 (문서 상태 변경)
        approvalService.approveDocument(docNo, loggedInEmpNo);

        documentsService.updateTimeStampHtml(docNo, timeStampHtml);

        // 승인 후 받은 문서함 페이지로 리다이렉트
        return "redirect:/approval/receive";
    }

    // 결재 반려를 처리 하는 컨트롤러 메서드
    @PostMapping("/rejectDocument")
    public ResponseEntity<String> rejectDocument(@RequestParam("docNo") int docNo,
                                                 @RequestParam("loggedInEmpNo") Long empNo,
                                                 @RequestParam("rejectMessage") String rejectMessage) {
        // 결재자 이름 조회
        String employeeName = employeesRepository.findFullNameByEmpNo(empNo);

        if (employeeName == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }

        // 문서 반려 처리
        approvalService.rejectDocument(docNo, empNo, rejectMessage);

        return ResponseEntity.ok("Document rejected successfully");
    }

    // 보낸 문서 상태별 문서 건수 조회 메서드
    @GetMapping("/statusCounts")
    @ResponseBody
    public DocumentStatusCountDTO getCountByStatus() {
        // 로그인한 사용자의 정보를 userService의 getLoggedInUserEmpNo 메서드로 가져오기
        Long loggedInEmpNo = userService.getLoggedInUserEmpNo();

        DocumentStatusCountDTO counts = documentsService.getDocumentStatusCountsForUser(loggedInEmpNo);
        log.info("Returning counts: " + counts);
        return counts;
    }

    // 결재자가 받은 문서의 대기,승인,반려 문서 건수 조회 메서드
    @GetMapping("/approvalStatusCounts")
    @ResponseBody
    public Map<String, Integer> getCountByApprovalStatus() {
        // 로그인한 사용자의 정보를 userService의 getLoggedInUserEmpNo 메서드로 가져오기
        Long loggedInEmpNo = userService.getLoggedInUserEmpNo();
        int pendingCount = approvalService.countPendingApprovals(loggedInEmpNo);
        int approvedCount = approvalService.countApprovedApprovals(loggedInEmpNo);
        int rejectedCount = approvalService.countRejectedDocumentsForApprover(loggedInEmpNo);
        int referencesCount = approvalService.countReferencesDocumentsForUser(loggedInEmpNo);

        Map<String, Integer> counts = new HashMap<>();
        counts.put("pendingCount", pendingCount);
        counts.put("approvedCount", approvedCount);
        counts.put("rejectedCount", rejectedCount);
        counts.put("referencesCount", referencesCount);
        log.info("Returning counts: " + counts);
        return counts;
    }

    // 보낸문서함의 결재 상태별 목록 조회
    @GetMapping("/documentsByStatusPage")
    @ResponseBody
    public List<DocumentsDTO> getDocumentsByStatus(@RequestParam("docStatus") String docStatus) {
        // 로그인한 사용자의 정보를 userService의 getLoggedInUserEmpNo 메서드로 가져오기
        Long loggedInEmpNo = userService.getLoggedInUserEmpNo();

        log.info("Fetching documents for empNo: " + loggedInEmpNo + " with status: " + docStatus);
        List<DocumentsDTO> documents = documentsService.getDocumentsByStatus(loggedInEmpNo, docStatus);
        log.info("Returning documents: " + documents);
        return documents;
    }

    // 받은 문서함의 대기 중인 문서 필터링
    @GetMapping("/pendingDocuments")
    @ResponseBody
    public List<DocumentsDTO> getPendingDocuments() {
        // 로그인한 사용자의 정보를 userService의 getLoggedInUserEmpNo 메서드로 가져오기
        Long loggedInEmpNo = userService.getLoggedInUserEmpNo();

        return documentsService.getPendingDocumentsForUser(loggedInEmpNo);
    }

    // 받은 문서함의 승인된 문서 필터링
    @GetMapping("/approvedDocuments")
    @ResponseBody
    public List<DocumentsDTO> getApprovedDocuments() {
        // 로그인한 사용자의 정보를 userService의 getLoggedInUserEmpNo 메서드로 가져오기
        Long loggedInEmpNo = userService.getLoggedInUserEmpNo();

        return documentsService.getApprovedDocumentsForUser(loggedInEmpNo);
    }

    // 받은 문서함의 반려된 문서 필터링
    @GetMapping("/rejectedDocuments")
    @ResponseBody
    public List<DocumentsDTO> getRejectedDocuments() {
        // 로그인한 사용자의 정보를 userService의 getLoggedInUserEmpNo 메서드로 가져오기
        Long loggedInEmpNo = userService.getLoggedInUserEmpNo();

        return documentsService.getRejectedDocumentsForUser(loggedInEmpNo);
    }

    // 받은 문서함의 참조된 문서 필터링
    @GetMapping("/referencesDocuments")
    @ResponseBody
    public List<DocumentsDTO> getReferencesDocuments() {
        // 로그인한 사용자의 정보를 userService의 getLoggedInUserEmpNo 메서드로 가져오기
        Long loggedInEmpNo = userService.getLoggedInUserEmpNo();

        return documentsService.getReferencesDocumentsForUser(loggedInEmpNo);
    }

    // approvalJson 프론트엔드로 반환하는 메서드
    @GetMapping("/get-approver-json/{docNo}")
    public ResponseEntity<Map<String, String>> getApproversJson(@PathVariable int docNo) {
        Optional<Documents> document = documentsRepository.findById(docNo);
        if (document.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("approversJson", document.get().getApproversJson());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}