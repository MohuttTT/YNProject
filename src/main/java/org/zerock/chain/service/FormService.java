package org.zerock.chain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.chain.dto.FormDTO;
import org.zerock.chain.model.Form;
import org.zerock.chain.repository.FormRepository;

import java.util.Optional;

@Service
public interface FormService {
    FormDTO getFormByCategory(String category); // 특정 category로 저장된 form_html을 조회하는 메서드
}
