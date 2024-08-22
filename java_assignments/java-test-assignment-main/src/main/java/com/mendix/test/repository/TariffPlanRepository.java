package com.mendix.test.repository;

import com.mendix.test.entity.TariffPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffPlanRepository extends JpaRepository<TariffPlan, Long> {
    TariffPlan findByName(String name);
}
