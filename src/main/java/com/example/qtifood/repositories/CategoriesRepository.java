package com.example.qtifood.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.qtifood.entities.Categories;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    
}
