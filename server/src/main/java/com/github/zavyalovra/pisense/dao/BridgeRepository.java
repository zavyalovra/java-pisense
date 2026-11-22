package com.github.zavyalovra.pisense.dao;

import com.github.zavyalovra.pisense.model.Bridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BridgeRepository extends JpaRepository<Bridge, Long> {
}
