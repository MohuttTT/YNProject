package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emp_rank")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmpRank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rank_no")
    private Long rankNo;

    @Column(name = "rank_name")
    private String rankName;
}
