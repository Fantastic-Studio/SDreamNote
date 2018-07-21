package org.swdc.note.entity;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;

/**
 * 摘录部分的内容
 */
@Entity
@Data
public class ClipsArtle {

    /**
     * 记录的内容id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 类型
     */
    @ManyToOne
    @JoinColumn(name = "typeId")
    private ClipsType type;

    /**
     * 记录拥有的标签
     */
    @ManyToOne
    private Tags tags;

    /**
     * 收录日期
     */
    private Date createDate;

    /**
     * 具体内容
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "contentId")
    @Fetch(FetchMode.JOIN)
    private ClipsContent content;

}
