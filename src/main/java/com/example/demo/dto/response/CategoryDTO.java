package com.example.demo.dto.response;

public class CategoryDTO {

    private Integer id;
    private String name;

    public CategoryDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
