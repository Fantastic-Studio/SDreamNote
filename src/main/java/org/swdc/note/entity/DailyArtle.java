package org.swdc.note.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 日记的记录类型
 */
@Entity
@Data
public class DailyArtle {

    private String title;

    /**
     * 记录拥有的标签
     */
    @ManyToOne
    private Tags tags;

    @Column(columnDefinition = "date")
    private Date dateCreated;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contentId")
    private DailyContent content;

    /**
     * 身份验证问题
     */
    private String checkQuestion;

    /**
     * 问题答案
     */
    @Column(columnDefinition = "text")
    private String answer;

}
