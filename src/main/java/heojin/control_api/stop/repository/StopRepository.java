package heojin.control_api.stop.repository;

import heojin.control_api.stop.entity.Stop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StopRepository extends JpaRepository<Stop, Long> {
}
