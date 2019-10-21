package com.yordan.finance.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "subcategories")
public class Subcategory {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int categoryId;
    private String name;

    @Ignore
    public Subcategory(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public Subcategory(){}

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
