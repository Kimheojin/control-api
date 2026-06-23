package heojin.control_api.telemetry.repository;

import heojin.control_api.telemetry.entity.BusLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {
}
