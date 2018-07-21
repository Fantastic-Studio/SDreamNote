package org.swdc.note.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swdc.note.entity.ClipsType;

/**
 * 记录类型的数据操作类
 */
@Repository
public interface ClipsTypeRepository extends JpaRepository<ClipsType, Long> {

    ClipsType findByName(String name);

}
