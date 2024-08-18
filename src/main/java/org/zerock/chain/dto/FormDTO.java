package org.zerock.chain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDTO {

    private Integer docNo;   // 문서번호
    private String category; // 양식 종류
    private String formHtml; // 각 양식 HTML 구조가 저장 되는 곳
    private String formData; // 각 양식의 입력데이터가 JSON형태로 저장되는 곳

    /*private DocumentsDTO documents; // DocumentsDTO와 연결*/
}
