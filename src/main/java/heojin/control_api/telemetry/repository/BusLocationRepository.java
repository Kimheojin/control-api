package heojin.control_api.telemetry.repository;

import heojin.control_api.telemetry.entity.BusLocation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {

	Optional<BusLocation> findFirstByBusIdOrderByRecordedAtDesc(Long busId);
}
