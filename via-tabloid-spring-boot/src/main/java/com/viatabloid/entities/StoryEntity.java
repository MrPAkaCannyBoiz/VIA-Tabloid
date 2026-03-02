package com.viatabloid.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "stories")
public class StoryEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String title;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

    @Column
    private LocalDateTime createdAt;

    public StoryEntity()
    {
    }

    public StoryEntity(String title, String description, DepartmentEntity department)
    {
        this.title = title;
        this.description = description;
        this.department = department;
        this.createdAt = LocalDateTime.now();
    }

    public int getId()
    {
        return id;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public DepartmentEntity getDepartment()
    {
        return department;
    }

    public void setDepartment(DepartmentEntity department)
    {
        this.department = department;
    }

    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt)
    {
        this.createdAt = createdAt;
    }
}
