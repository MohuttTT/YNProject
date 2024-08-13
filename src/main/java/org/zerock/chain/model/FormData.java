package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "form_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormData {

    @EmbeddedId
    private FormDataNo formDataNo;

    @Column(name = "field_value")
    private String fieldValue;

}
