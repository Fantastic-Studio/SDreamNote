package org.swdc.note.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swdc.note.entity.ClipsArtle;

import java.util.List;

/**
 * 记录的数据操作接口
 */
@Repository
public interface ClipsArtleRepository extends JpaRepository<ClipsArtle, Long> {
}
