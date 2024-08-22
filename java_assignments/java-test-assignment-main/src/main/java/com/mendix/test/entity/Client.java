package com.mendix.test.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "client")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_sequence_generator")
    @SequenceGenerator(name = "client_sequence_generator", sequenceName = "client_id_seq",allocationSize = 1)
    private Long id;

    @Column(name = "credentials", nullable = false, length = 255)
    private String credentials;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_plan_id", referencedColumnName = "id")
    private TariffPlan tariffPlan;
}
