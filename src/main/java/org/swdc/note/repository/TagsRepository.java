package org.swdc.note.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swdc.note.entity.GlobalType;
import org.swdc.note.entity.Tags;

import java.util.List;

/**
 * 标签的数据操作类
 */
@Repository
public interface TagsRepository extends JpaRepository<Tags, Long> {

    @Query("FROM Tags WHERE name = :name AND globalType = :tp")
    Tags findByNameAndType(@Param("name") String name, @Param("tp") GlobalType type);

    @Query("FROM Tags WHERE globalType = :tp")
    List<Tags> findByType(@Param("tp") GlobalType type);

}
