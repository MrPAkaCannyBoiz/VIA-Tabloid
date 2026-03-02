package com.viatabloid.repositories;

import com.viatabloid.entities.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Integer>
{
}
