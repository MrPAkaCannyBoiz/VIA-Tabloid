package com.viatabloid.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "departments")
public class DepartmentEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    public DepartmentEntity() {}

    public DepartmentEntity(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

}
