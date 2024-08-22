package com.mendix.test.repository;

import com.mendix.test.entity.Backups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackupRepository extends JpaRepository<Backups, Long> {
    Backups findBackupsByIdAndClient_Id(Long backupId,Long clientId);
    List<Backups> findBackupsByClient_Id(Long clientId);
    List<Backups> findBackupsByStatus(String status);
}
