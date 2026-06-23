package heojin.control_api.telemetry.repository;

import heojin.control_api.telemetry.entity.BusEventType;
import java.util.List;

public interface BusEventRepositoryCustom {

	List<BusEventRow> findRecentByBusId(Long busId, int limit);

	List<BusEventRow> findRecentEvents(BusEventType type, int limit);
}
