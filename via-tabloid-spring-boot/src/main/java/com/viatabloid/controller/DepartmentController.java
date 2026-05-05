package com.viatabloid.controller;

import com.viatabloid.dto.DepartmentDto;
import com.viatabloid.entities.DepartmentEntity;
import com.viatabloid.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/departments")
public class DepartmentController
{
    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentController(DepartmentRepository departmentRepository)
    {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping()
    public ResponseEntity<?> getAllDepartments()
    {
        return ResponseEntity.ok(departmentRepository.findAll());
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<?> getDepartmentById(@PathVariable int departmentId)
    {
        var department = departmentRepository.findById(departmentId);
        if (department.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(department.get());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createDepartment(@RequestBody DepartmentDto dto)
    {
        var saved = departmentRepository.save(new DepartmentEntity(dto.name()));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<?> deleteDepartment(@PathVariable int departmentId)
    {
        if (!departmentRepository.existsById(departmentId))
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Department not found with id: " + departmentId);
        departmentRepository.deleteById(departmentId);
        return ResponseEntity.noContent().build();
    }
}
