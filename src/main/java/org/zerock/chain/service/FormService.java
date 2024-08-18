package org.zerock.chain.service;

import org.zerock.chain.dto.FormDTO;

import java.util.Optional;

public interface FormService {
    FormDTO saveForm(FormDTO formDto);  // DTO를 사용하여 Form 저장
    Optional<FormDTO> getFormByDocNo(Integer docNo);  // DTO를 반환
}
