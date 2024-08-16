package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.chain.model.Documents;

import java.util.List;

@Repository
public interface DocumentsRepository extends JpaRepository<Documents, Integer> {

    @Query("SELECT d FROM Documents d WHERE d.senderEmpNo = :senderEmpNo ORDER BY d.docNo DESC")
    List<Documents> findSentDocuments(@Param("senderEmpNo") Integer senderEmpNo);

    @Query("SELECT d FROM Documents d WHERE d.receiverEmpNo = :receiverEmpNo")
    List<Documents> findReceivedDocuments(@Param("receiverEmpNo") Integer receiverEmpNo);

    @Query("SELECT d FROM Documents d WHERE d.docStatus = '임시저장'")
    List<Documents> findDraftDocuments();

    @Query("SELECT d.formNo FROM Documents d WHERE d.docNo = :docNo")
    Integer findFormNoByDocNo(@Param("docNo") Integer docNo);

    @Query("SELECT d.category FROM Documents d WHERE d.docNo = :docNo")
    String findCategoryByDocNo(@Param("docNo") int docNo);
}
