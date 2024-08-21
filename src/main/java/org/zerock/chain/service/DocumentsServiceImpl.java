package org.zerock.chain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.chain.model.Documents;
import org.zerock.chain.dto.DocumentsDTO;
import org.zerock.chain.repository.DocumentsRepository;
import org.zerock.chain.repository.EmployeesRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class DocumentsServiceImpl implements DocumentsService<DocumentsDTO> {

    private final DocumentsRepository documentsRepository;
    private final EmployeesRepository employeesRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService; // 파일 저장 서비스를 주입

    @Override
    public DocumentsDTO getDocumentById(int docNo) {
        Documents document = documentsRepository.findById(docNo).orElseThrow(() -> new RuntimeException("Document not found"));
        return modelMapper.map(document, DocumentsDTO.class);
    }

    @Override
    public List<DocumentsDTO> getSentDocuments(Integer senderEmpNo) {
        // 보낸 문서 목록을 조회하여 DTO로 변환
        List<Documents> documents = documentsRepository.findSentDocuments(senderEmpNo);

        return documents.stream()
                .map(doc -> {
                    DocumentsDTO dto = modelMapper.map(doc, DocumentsDTO.class);

                    // senderName을 employees 테이블에서 조회하여 설정
                    String senderName = employeesRepository.findFullNameByEmpNo(doc.getSenderEmpNo());
                    dto.setSenderName(senderName);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentsDTO> getReceivedDocuments(Integer receiverEmpNo) {
        // 받은 문서 목록을 조회하여 DTO로 변환
        List<Documents> documents = documentsRepository.findReceivedDocuments(receiverEmpNo);
        return documents.stream()
                .map(doc -> modelMapper.map(doc, DocumentsDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentsDTO> getDraftDocuments() {
        // 임시 문서 목록을 조회하여 DTO로 변환
        List<Documents> documents = documentsRepository.findDraftDocuments();
        return documents.stream()
                .map(doc -> {
                    DocumentsDTO dto = modelMapper.map(doc, DocumentsDTO.class);

                    // senderName을 employees 테이블에서 조회하여 설정
                    String senderName = employeesRepository.findFullNameByEmpNo(doc.getSenderEmpNo());
                    dto.setSenderName(senderName);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public int saveDocument(DocumentsDTO documentsDTO) {
        try {
            // 파일이 있는 경우 파일 저장 처리
            String filePath = null;
            if (documentsDTO.getFile() != null && !documentsDTO.getFile().isEmpty()) {
                filePath = fileService.saveFile(documentsDTO.getFile());  // 파일을 저장하고 경로를 반환
                documentsDTO.setFilePath(filePath);  // DTO에 파일 경로 설정
            }

            // Documents 엔티티를 먼저 생성하고 저장
            Documents documents = Documents.builder()
                    .reqDate(LocalDate.now())
                    .senderEmpNo(1)
                    .receiverEmpNo(2)
                    .docStatus(documentsDTO.getDocStatus())  // 요청된 상태를 사용
                    .category(documentsDTO.getCategory())    // 클라이언트가 보낸 카테고리 설정
                    .docTitle(documentsDTO.getDocTitle())    // 문서 제목을 설정
                    .docBody(documentsDTO.getDocBody())      // 내용 입력값을 저장
                    .approvalLine(documentsDTO.getApprovalLine())     // 결재자 정보를 저장
                    .timeStampHtml(documentsDTO.getTimeStampHtml())   // 타임스탬프를 저장
                    .approverNoHtml(documentsDTO.getApproverNoHtml()) // 결재자 순번을 저장
                    .filePath(filePath) // 저장된 파일 경로 설정
                    .build();

            Documents savedDocument = documentsRepository.save(documents);
            log.info("Saved document: {}", savedDocument);

            return savedDocument.getDocNo();
        } catch (Exception e) {
            // 예외 발생 시 에러 로그 출력
            log.error("Error occurred while saving document and form", e);
            // 서비스 계층에서 예외를 던지지 않고 적절한 기본값을 반환
            return -1;
        }
    }

    @Override
    public void updateDocument(DocumentsDTO documentsDTO) throws Exception {

        // 기존 문서를 데이터베이스에서 찾음
        Optional<Documents> optionalDocument = documentsRepository.findById(documentsDTO.getDocNo());
        if (!optionalDocument.isPresent()) {
            throw new Exception("Document not found");
        }

        Documents document = optionalDocument.get();

        // 파일이 있는 경우 파일 저장 처리
        if (documentsDTO.getFile() != null && !documentsDTO.getFile().isEmpty()) {
            String filePath = fileService.saveFile(documentsDTO.getFile());  // 파일을 저장하고 경로를 반환
            documentsDTO.setFilePath(filePath);  // DTO에 파일 경로 설정
            document.setFilePath(filePath);  // 엔티티의 파일 경로를 업데이트
        }

        // 문서의 다른 필드들 업데이트
        document.setDocTitle(documentsDTO.getDocTitle());
        document.setDocBody(documentsDTO.getDocBody());
        document.setApprovalLine(documentsDTO.getApprovalLine());
        document.setTimeStampHtml(documentsDTO.getTimeStampHtml());
        document.setApproverNoHtml(documentsDTO.getApproverNoHtml());
        document.setDocStatus(documentsDTO.getDocStatus());
        document.setCategory(documentsDTO.getCategory());

        // 문서 업데이트
        Documents savedDocument = documentsRepository.save(document);
        log.info("Updated document: {}", savedDocument);
    }

    @Override
    public void deleteDocument(int docNo) throws Exception {
        // 문서를 데이터베이스에서 찾음
        Optional<Documents> optionalDocument = documentsRepository.findById(docNo);
        if (!optionalDocument.isPresent()) {
            throw new Exception("Document not found");
        }

        // 문서 삭제
        documentsRepository.deleteById(docNo);
    }
}
