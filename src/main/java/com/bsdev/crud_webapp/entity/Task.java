package com.bsdev.crud_webapp.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "user_id")
    private long userId;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false)
    private TaskStatus status = TaskStatus.NEW;
}
