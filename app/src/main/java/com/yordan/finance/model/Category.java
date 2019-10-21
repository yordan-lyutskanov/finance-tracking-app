package com.yordan.finance.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.HashSet;
import java.util.Set;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey
    private int id;
    private String name;

    @Ignore
    private Set<Subcategory> subcategories;

    public Category() {
        this.subcategories = new HashSet<>();
    }

    @Ignore
    public Category(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addCategory(Subcategory subcategory){
        subcategories.add(subcategory);
    }

    public void removeSubcategory(Subcategory subcategory){
        subcategories.remove(subcategory);
    }

    public Set<Subcategory> getSubcategories() {
        return subcategories;
    }
}
