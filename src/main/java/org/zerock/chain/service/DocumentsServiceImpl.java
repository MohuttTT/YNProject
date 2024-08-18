package org.zerock.chain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.Request;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.chain.dto.RequestDTO;
import org.zerock.chain.model.Documents;
import org.zerock.chain.dto.DocumentsDTO;
import org.zerock.chain.model.Form;
import org.zerock.chain.repository.DocumentsRepository;
import org.zerock.chain.repository.EmployeesRepository;
import org.zerock.chain.repository.FormRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class DocumentsServiceImpl implements DocumentsService<DocumentsDTO> {

    private final DocumentsRepository documentsRepository;
    private final EmployeesRepository employeesRepository;
    private final FormRepository formRepository;
    private final ModelMapper modelMapper;

    @Override
    public DocumentsDTO getDocumentById(int docNo) {
        Documents document = documentsRepository.findById(docNo).orElseThrow(() -> new RuntimeException("Document not found"));
        return modelMapper.map(document, DocumentsDTO.class);
    }

    @Override
    public List<DocumentsDTO> getAllDocuments() {
        List<Documents> documents = documentsRepository.findAll();
        return documents.stream()
                .map(doc -> modelMapper.map(doc, DocumentsDTO.class))
                .toList();
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
                .map(doc -> modelMapper.map(doc, DocumentsDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public String getCategoryByDocNo(int docNo) {
        return documentsRepository.findCategoryByDocNo(docNo);
    }

    @Override
    public int saveDocument(RequestDTO requestDTO) {
        try {
            // Documents 엔티티를 먼저 생성하고 저장
            Documents documents = Documents.builder()
                    .reqDate(LocalDate.now())
                    .senderEmpNo(1)
                    .receiverEmpNo(2)
                    .docStatus("요청")
                    .category(requestDTO.getCategory())
                    .build();

            Documents savedDocument = documentsRepository.save(documents);
            log.info("Saved document: {}", savedDocument);

            // Form 엔티티를 생성하고, Documents와 연결
            Form form = Form.builder()
                    .docNo(savedDocument.getDocNo()) // 저장된 Documents의 docNo 사용
                    .formHtml(requestDTO.getFormHtml())
                    .category(requestDTO.getCategory())
                    .documents(savedDocument) // 단방향 관계 설정
                    .build();
            log.info("Attempting to save form: {}", form);

            Form savedForm = formRepository.save(form); // Form 저장
            log.info("Saved form: {}", savedForm);

            return savedDocument.getDocNo();
        } catch (Exception e) {
            // 예외 발생 시 에러 로그 출력
            log.error("Error occurred while saving document and form", e);
            // 서비스 계층에서 예외를 던지지 않고 적절한 기본값을 반환
            return -1;
        }
    }
}
