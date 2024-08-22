package com.mendix.test.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "backups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Backups {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "backup_sequence_generator")
    @SequenceGenerator(name = "backup_sequence_generator", sequenceName = "backups_id_seq",allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @Column(name = "start_date", nullable = false)
    private Long startDate;

    @Column(name = "end_date")
    private Long endDate;

    @Column(name = "database_size")
    private Long databaseSize;

    @Column(name = "backup_time")
    private Long backupTime;

    @Column(name = "status", length = 255)
    private String status;
}
