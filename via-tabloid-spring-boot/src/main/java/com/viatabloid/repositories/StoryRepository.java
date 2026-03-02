package com.viatabloid.repositories;


import com.viatabloid.entities.StoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoryRepository extends JpaRepository<StoryEntity, Integer>
{
    // list all stories by department id,
    // however it's cause n_+1 problem, so we need to use @EntityGraph in StoryEntity
    @Query("""
        SELECT s FROM StoryEntity s
        LEFT JOIN FETCH s.department d
        WHERE d.id = :departmentId
       """)
    List<StoryEntity> findAllByDepartmentId(@Param("departmentId") int departmentId);
}
