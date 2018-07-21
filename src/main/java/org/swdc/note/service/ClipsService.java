package org.swdc.note.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swdc.note.entity.*;
import org.swdc.note.repository.ClipsArtleRepository;
import org.swdc.note.repository.ClipsContentRepository;
import org.swdc.note.repository.ClipsTypeRepository;
import org.swdc.note.repository.TagsRepository;
import org.swdc.note.ui.common.TreeNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 摘录内容服务
 */
@Service
public class ClipsService {

    @Autowired
    private ClipsTypeRepository typeRepository;

    @Autowired
    private ClipsArtleRepository artleRepository;

    @Autowired
    private ClipsContentRepository contentRepository;

    @Autowired
    private TagsRepository tagsRepository;

    /**
     * 获取类型树
     *
     * @return
     */
    @Transactional
    public DefaultMutableTreeNode getClipTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
        List<ClipsType> types = typeRepository.findAll();
        // 读取摘录的分类
        types.stream().map(item -> new TreeNode<>(item, "name")).forEach(item -> {
            ClipsType type = item.getUserObject();
            List<ClipsArtle> artles = type.getArtles();
            // 按照分类下的标签对内容进行组织,用HashSet去重
            HashSet<Tags> tagsSet = new HashSet<>();
            // 读取所有记录的tag，存放到HashSet，这就没有重复了
            artles.stream()
                    .map(arts -> arts.getTags())
                    .forEach(tgs -> tagsSet.add(tgs));
            // 创建节点，加到tree上面
            tagsSet.stream().map(tag -> new TreeNode(tag, "name"))
                    .forEach(tagNode -> item.add(tagNode));
            root.add(item);
        });
        return root;
    }

    /**
     * 获取类型列表
     *
     * @return
     */
    public List<String> getTypeList() {
        List<ClipsType> types = typeRepository.findAll();
        return types.stream().map(item -> item.getName()).collect(Collectors.toList());
    }

    /**
     * 获得标签列表
     *
     * @return
     */
    public List<String> getClipsTags() {
        return tagsRepository.findByType(GlobalType.CLIPS)
                .stream().map(item -> item.getName()).collect(Collectors.toList());
    }

    /**
     * 读取记录列表
     *
     * @param type
     * @param tag
     * @return
     */
    @Transactional
    public List<ClipsArtle> getArtlesOf(ClipsType type, Tags tag) {
        type = typeRepository.getOne(type.getId());
        final Tags tagsFin = tagsRepository.getOne(tag.getId());
        return type.getArtles().stream()
                .filter(item -> item.getTags().equals(tagsFin))
                .collect(Collectors.toList());
    }

    /**
     * 加载具体内容
     *
     * @param id
     * @return
     */
    @Transactional
    public ClipsContent loadContent(Long id) {
        ClipsArtle artle = artleRepository.getOne(id);
        ClipsContent content = artle.getContent();
        return content;
    }

    /**
     * 修改内容
     *
     * @param tagName    标签名
     * @param typeName   类型
     * @param xdocString 内容
     * @param title      标题
     * @param id         id
     */
    @Transactional
    public void modifyContent(String tagName, String typeName, String xdocString, String title, Long id) {
        Tags tag = resolveTags(tagName, true);
        ClipsType type = resolveType(typeName, true);
        ClipsArtle artle = loadClipArtle(id);
        ClipsContent content = loadContent(id);
        content.setContent(xdocString);
        artle.setTitle(title);
        artle.setTags(tag);
        artle.setType(type);
        artle.setContent(content);
        contentRepository.save(content);
        artleRepository.save(artle);
    }

    /**
     * 读取摘录的数据
     *
     * @param id 摘录的id
     * @return
     */
    @Transactional
    public ClipsArtle loadClipArtle(Long id) {
        ClipsArtle artle = artleRepository.getOne(id);
        artle.getTitle();
        artle.getContent();
        artle.getTags();
        artle.getType();
        return artleRepository.getOne(id);
    }

    /**
     * 读取标签
     * @param tagName 标签名
     * @param create 如果不存在是否创建
     * @return 标签对象
     */
    public Tags resolveTags(String tagName,boolean create){
        Tags tag = tagsRepository.findByNameAndType(tagName, GlobalType.CLIPS);
        if (tag == null&&create) {
            tag = new Tags();
            tag.setName(tagName);
            tag.setGlobalType(GlobalType.CLIPS);
            tag = tagsRepository.save(tag);
        }
        return tag;
    }

    /**
     * 读取摘录类型
     *
     * @param typeName 类型名
     * @param create   没有的时候是否创建
     * @return 类型对象
     */
    public ClipsType resolveType(String typeName,boolean create){
        ClipsType type = typeRepository.findByName(typeName);
        if (type == null&&create) {
            type = new ClipsType();
            type.setName(typeName);
            type.setArtles(new ArrayList<>());
            type = typeRepository.save(type);
        }
        return type;
    }

    /**
     * 存储内容
     */
    public void saveContent(String tagName, String typeName, String xdocString, String title) {
        // 读取目标的标签，没有就创建
        Tags tag = resolveTags(tagName, true);
        // 读取类型，没有就创建
        ClipsType type = resolveType(typeName,true);
        //创建内容实例
        ClipsArtle artle = new ClipsArtle();
        artle.setType(type);
        artle.setCreateDate(new Date());
        artle.setTags(tag);
        artle.setTitle(title);
        ClipsContent content = new ClipsContent();
        content.setContent(xdocString);
        artle.setContent(content);
        artleRepository.save(artle);
        contentRepository.save(content);
    }

}
