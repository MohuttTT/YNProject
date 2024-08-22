package org.zerock.chain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class TodoRequestDTO {

    private Long todoNo;
    private String todoContent;
    private String todoCategory;
    private boolean todoStatus;
    private boolean todoFavorite;
    private LocalDate todoCreatedDate;


    }

