package heojin.control_api.telemetry.repository;

import heojin.control_api.telemetry.entity.BusEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusEventRepository extends JpaRepository<BusEvent, Long> {
}
