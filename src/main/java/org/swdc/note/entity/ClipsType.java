package org.swdc.note.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 摘录的分类
 */
@Entity
@Data
public class ClipsType {

    /**
     * 类型名
     */
    private String name;

    /**
     * 类型id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 类型拥有的记录
     */
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClipsArtle> artles;

}
