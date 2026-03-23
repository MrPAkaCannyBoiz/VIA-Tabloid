package com.viatabloid.controller;

import com.viatabloid.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(("api/departments"))
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
        var departments = departmentRepository.findAll();
        return ResponseEntity.ok(departments);
    }

    // get with id
    @GetMapping("/{departmentId}")
    public ResponseEntity<?> getDepartmentById(@PathVariable int departmentId)
    {
        var department = departmentRepository.findById(departmentId);
        if (department.isEmpty())
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(department.get());
    }

    @PostMapping(path = "/{name}",consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> createDepartment(@PathVariable String name)
    {
        var department = new com.viatabloid.entities.DepartmentEntity(name);
        var savedDepartment = departmentRepository.save(department);
        return ResponseEntity.ok(savedDepartment);
    }

}
