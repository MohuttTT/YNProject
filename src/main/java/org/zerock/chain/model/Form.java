package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "form")
public class Form {

    @Id
    @Column(name = "doc_no")
    private Integer docNo;

    private String category;  // 양식의 카테고리

    @Lob
    @Column(name = "form_html", columnDefinition = "longtext")
    private String formHtml;  // 양식의 HTML을 저장

    /*@Lob
    @Column(name = "form_data")
    private String formData;  // 사용자가 입력한 데이터를 JSON 형식으로 저장*/

    @OneToOne
    @MapsId // docNo가 Documents의 기본 키를 참조
    @JoinColumn(name = "doc_no", nullable = false) // 외래 키 설정
    private Documents documents;  // Documents와의 관계 설정

    @Override
    public String toString() {
        return "Form{" +
                "docNo=" + docNo +
                ", category='" + category + '\'' +
                ", formHtml='" + formHtml + '\'' +
                ", documents=" + (documents != null ? documents.getDocNo() : null) +  // 참조하는 Documents의 docNo만 출력
                '}';
    }
}
