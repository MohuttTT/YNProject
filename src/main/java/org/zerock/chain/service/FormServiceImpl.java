package org.zerock.chain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.chain.dto.FormDTO;
import org.zerock.chain.model.Form;
import org.zerock.chain.repository.FormRepository;

import java.util.Optional;

@Service
public class FormServiceImpl implements FormService {

    @Autowired
    private FormRepository formRepository;

    @Override
    public FormDTO saveForm(FormDTO formDto) {
        // DTO를 엔티티로 변환
        Form form = new Form();
        form.setDocNo(formDto.getDocNo());
        form.setCategory(formDto.getCategory());
        form.setFormHtml(formDto.getFormHtml());
        /*form.setFormData(formDto.getFormData());*/

        // 엔티티 저장
        Form savedForm = formRepository.save(form);

        // 저장된 엔티티를 다시 DTO로 변환
        FormDTO savedFormDto = new FormDTO();
        savedFormDto.setDocNo(savedForm.getDocNo());
        savedFormDto.setCategory(savedForm.getCategory());
        savedFormDto.setFormHtml(savedForm.getFormHtml());
//        savedFormDto.setFormData(savedForm.getFormData());

        return savedFormDto;  // 저장된 DTO 반환
    }

    @Override
    public Optional<FormDTO> getFormByDocNo(Integer docNo) {
        Optional<Form> formOptional = formRepository.findById(docNo);
        if (formOptional.isPresent()) {
            Form form = formOptional.get();

            // 엔티티를 DTO로 변환
            FormDTO formDto = new FormDTO();
            formDto.setDocNo(form.getDocNo());
            formDto.setCategory(form.getCategory());
            formDto.setFormHtml(form.getFormHtml());
//            formDto.setFormData(form.getFormData());

            return Optional.of(formDto);
        } else {
            return Optional.empty();
        }
    }
}
