package org.zerock.chain.service;

import org.zerock.chain.dto.DocumentsDTO;

import java.util.List;

public interface DocumentsService <T extends DocumentsDTO> {

    List<T> getSentDocuments(Integer senderEmpNo);  // 보낸 문서 목록 조회
    List<T> getReceivedDocuments(Integer receiverEmpNo);  // 받은 문서 목록 조회
    List<T> getDraftDocuments();  // 임시 문서 목록 조회
    // 필요한 메서드 정의 기능

    // 사용자가 문서에 입력한 모든 정보를 반환하는 메소드 추가
    int saveDocument(DocumentsDTO documentsDTO);
    // 사용자가 임시저장한 문서에서 정보를 업데이트하는 메소드 추가
    void updateDocument(DocumentsDTO documentsDTO) throws Exception;
    // 사용자가 임시저장한 문서 삭제하는 메소드 추가
    void deleteDocument(int docNo) throws Exception;

    T getDocumentById(int docNo);
}
