package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "form_fields")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "field_no")
    private Integer fieldNo;

    @Column(name = "form_no")
    private Integer formNo;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "field_type")
    private String fieldType;

    @Column(name = "field_options")
    private String fieldOptions;

    @Column(name = "category")
    private String category;

}
