package org.swdc.note.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 日记的实体记录内容
 */
@Entity
@Data
public class DailyContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artleId")
    private DailyArtle artle;

}
