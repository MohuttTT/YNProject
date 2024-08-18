package org.zerock.chain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitRequest {
    private String docTitle;
    // form 뭐시기 들어가야함
    private String file; // 양식 선택값을 위한 필드
}
