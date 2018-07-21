package org.swdc.note.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 摘录的具体内容
 */
@Entity
@Data
public class ClipsContent {

    /**
     * 内容的Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 内容
     */
    @Column(columnDefinition = "text")
    private String content;

}
