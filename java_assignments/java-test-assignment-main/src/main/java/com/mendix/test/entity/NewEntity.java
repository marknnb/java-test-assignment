package com.mendix.test.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "new_entity")
public class NewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tariff_sequence_generator")
    @SequenceGenerator(name = "tariff_sequence_generator")
    @Column(name = "id", nullable = false)
    private Long id;

    @PostLoad
    public void postLoad() {

    }
}