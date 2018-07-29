package org.swdc.note.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swdc.note.config.BCrypt;
import org.swdc.note.entity.DailyArtle;
import org.swdc.note.entity.DailyContent;
import org.swdc.note.entity.GlobalType;
import org.swdc.note.entity.Tags;
import org.swdc.note.repository.DailyContentRepository;
import org.swdc.note.repository.DailyRepository;
import org.swdc.note.repository.TagsRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 处理日记数据的服务类
 */
@Service
public class DailyService {

    @Autowired
    private DailyContentRepository dailyContentRepository;

    @Autowired
    private DailyRepository dailyRepository;

    @Autowired
    private TagsRepository tagsRepository;

    @Transactional
    public List<DailyArtle> getArtlesOf(Date date) {
        return Optional.ofNullable(dailyRepository.findByDateCreated(date)).orElse(new ArrayList<>());
    }

    public void saveContent(String tagName, String xdocString, String title, String question, String answer) {
        Tags tags = resolveTag(tagName, true);
        DailyContent content = new DailyContent();
        content.setContent(xdocString);
        DailyArtle artle = new DailyArtle();
        artle.setDateCreated(new Date());
        if (question != null && answer != null) {
            artle.setCheckQuestion(question);
            // 使用BCrypt加密
            artle.setAnswer(BCrypt.hashpw(answer, BCrypt.gensalt()));
        }
        artle.setTags(tags);
        artle.setContent(content);
        artle.setTitle(title);
        artle = dailyRepository.save(artle);
        content.setArtle(artle);
        dailyContentRepository.save(content);
    }

    @Transactional
    public void modifyContent(Long id, String tagName, String xdocString, String title, String question, String answer) {
        DailyArtle artle = dailyRepository.getOne(id);
        DailyContent content = artle.getContent();
        content.setContent(xdocString);
        if (question != null && answer != null) {
            artle.setCheckQuestion(question);
            // 使用BCrypt加密
            artle.setAnswer(BCrypt.hashpw(answer, BCrypt.gensalt()));
        }
        artle.setTitle(title);
        artle.setTags(resolveTag(tagName, true));
        artle.setContent(content);
        dailyRepository.save(artle);
    }

    @Transactional
    public DailyArtle loadContent(Long id) {
        DailyArtle artle = dailyRepository.getOne(id);
        artle.getContent();
        artle.getCheckQuestion();
        artle.getAnswer();
        return artle;
    }

    /**
     * 读取标签
     *
     * @param tagName 标签名
     * @param created 如果不存在是否创建
     * @return 标签对象
     */
    public Tags resolveTag(String tagName, boolean created) {
        Tags tag = tagsRepository.findByNameAndType(tagName, GlobalType.DELAY);
        if (tag == null && created) {
            tag = new Tags();
            tag.setName(tagName);
            tag.setGlobalType(GlobalType.DELAY);
            tag = tagsRepository.save(tag);
        }
        return tag;
    }

    public void deleteArtle(Long id) {
        dailyRepository.deleteById(id);
    }

    /**
     * 获得标签列表
     *
     * @return
     */
    public List<String> getDailyTags() {
        return tagsRepository.findByType(GlobalType.DELAY)
                .stream().map(item -> item.getName()).collect(Collectors.toList());
    }

}
