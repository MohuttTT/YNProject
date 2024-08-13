package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.chain.model.FormFields;

import java.util.List;

@Repository
public interface FormFieldsRepository extends JpaRepository<FormFields, Integer> {

    @Query("SELECT f FROM FormFields f WHERE f.formNo = :formNo")
    List<FormFields> findByFormNo(@Param("formNo") Integer formNo);

    // category를 기준으로 formFields를 조회하는 메소드
    @Query("SELECT f FROM FormFields f WHERE f.category = :category")
    List<FormFields> findByCategory(@Param("category") String category);
}
