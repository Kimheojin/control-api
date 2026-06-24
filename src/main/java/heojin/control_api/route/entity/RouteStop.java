package heojin.control_api.route.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
	name = "route_stops",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_route_stops_route_order", columnNames = {"route_id", "stop_order"}),
		@UniqueConstraint(name = "uk_route_stops_route_stop", columnNames = {"route_id", "stop_id"})
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RouteStop {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "route_id", nullable = false)
	private Long routeId;

	@Column(name = "stop_id", nullable = false)
	private Long stopId;

	@Column(name = "stop_order", nullable = false)
	private Integer stopOrder;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	public static RouteStop create(Long routeId, Long stopId, Integer stopOrder, LocalDateTime now) {
		RouteStop routeStop = new RouteStop();
		routeStop.routeId = routeId;
		routeStop.stopId = stopId;
		routeStop.stopOrder = stopOrder;
		routeStop.createdAt = now;
		routeStop.updatedAt = now;
		return routeStop;
	}
}
