package org.swdc.note.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 记录的标签
 */
@Data
@Entity
public class Tags {

    /**
     * 标签名
     */
    private String name;

    /**
     * 标签id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 整体分类
     */
    @Enumerated(EnumType.STRING)
    private GlobalType globalType;

}
