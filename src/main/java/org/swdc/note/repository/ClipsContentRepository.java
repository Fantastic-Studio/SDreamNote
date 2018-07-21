package org.swdc.note.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swdc.note.entity.ClipsContent;

/**
 * 记录的具体内容的数据操作类
 */
@Repository
public interface ClipsContentRepository extends JpaRepository<ClipsContent, Long> {
}
