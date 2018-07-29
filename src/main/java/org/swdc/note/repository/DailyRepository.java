package org.swdc.note.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swdc.note.entity.DailyArtle;

import java.util.Date;
import java.util.List;

/**
 * 日记记录的操作对象
 */
@Repository
public interface DailyRepository extends JpaRepository<DailyArtle, Long> {

    List<DailyArtle> findByDateCreated(Date dateCreated);

}
