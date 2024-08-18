package org.zerock.chain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {

   /* private Integer docNo;            // 문서 번호
    private Integer senderEmpNo;      // 보낸 사원 번호
    private Integer receiverEmpNo;    // 받은 사원 번호
    private String docTitle;          // 문서 제목
    private String docStatus;         // 문서 상태
    private LocalDate reqDate;        // 요청일
    private LocalDate reReqDate;      // 재요청일
    private LocalDate draftDate;      // 임시저장일*/
    private String category;          // 양식 종류
    private String formHtml;          // 양식의 HTML 구조
//    private String formData;          // 양식의 입력 데이터 JSON
}
