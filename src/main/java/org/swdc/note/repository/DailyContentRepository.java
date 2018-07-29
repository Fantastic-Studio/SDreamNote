package org.swdc.note.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swdc.note.entity.DailyContent;

/**
 * 日记内容的操作对象
 */
@Repository
public interface DailyContentRepository extends JpaRepository<DailyContent, Long> {
}
