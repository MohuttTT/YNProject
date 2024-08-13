package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.chain.model.FormData;
import org.zerock.chain.model.FormDataNo;

import java.util.List;

@Repository
public interface FormDataRepository extends JpaRepository<FormData, FormDataNo> {

    // JPQL 쿼리로 복합키의 docNo 필드에 기반한 검색
    @Query("SELECT f FROM FormData f WHERE f.formDataNo.docNo = :docNo")
    List<FormData> findByDocNo(@Param("docNo") int docNo);
}
