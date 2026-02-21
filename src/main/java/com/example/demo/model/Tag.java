package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tags", indexes = {
        @Index(name = "idx_tags_slug", columnList = "slug", unique = true)
})
@Getter
@Setter
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String slug;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false)
    private Boolean isActive = true;
}
