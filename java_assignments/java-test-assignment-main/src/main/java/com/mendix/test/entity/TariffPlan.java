package com.mendix.test.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Entity
@Table(name = "tariff_plan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tariff_sequence_generator")
    @SequenceGenerator(name = "tariff_sequence_generator", sequenceName = "tariff_plan_id_seq",allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(mappedBy = "tariffPlan", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Client> clients;

}
