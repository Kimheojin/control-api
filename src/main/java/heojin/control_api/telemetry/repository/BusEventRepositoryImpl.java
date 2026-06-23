package heojin.control_api.telemetry.repository;

import static heojin.control_api.bus.entity.QBus.bus;
import static heojin.control_api.route.entity.QRoute.route;
import static heojin.control_api.telemetry.entity.QBusEvent.busEvent;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import heojin.control_api.telemetry.entity.BusEventType;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BusEventRepositoryImpl implements BusEventRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<BusEventRow> findRecentByBusId(Long busId, int limit) {
		return selectRows(new BooleanBuilder(busEvent.busId.eq(busId)), limit);
	}

	@Override
	public List<BusEventRow> findRecentEvents(BusEventType type, int limit) {
		BooleanBuilder where = new BooleanBuilder();
		if (type != null) {
			where.and(busEvent.type.eq(type));
		}
		return selectRows(where, limit);
	}

	private List<BusEventRow> selectRows(BooleanBuilder where, int limit) {
		List<Tuple> tuples = queryFactory
				.select(
						busEvent.id,
						busEvent.busId,
						bus.busNumber,
						route.name,
						busEvent.type,
						busEvent.latitude,
						busEvent.longitude,
						busEvent.occurredAt,
						busEvent.description)
				.from(busEvent)
				.leftJoin(bus).on(bus.id.eq(busEvent.busId))
				.leftJoin(route).on(route.id.eq(bus.routeId))
				.where(where)
				.orderBy(busEvent.occurredAt.desc(), busEvent.id.desc())
				.limit(limit)
				.fetch();

		return tuples.stream()
				.map(tuple -> new BusEventRow(
						tuple.get(busEvent.id),
						tuple.get(busEvent.busId),
						tuple.get(bus.busNumber),
						tuple.get(route.name),
						tuple.get(busEvent.type),
						tuple.get(busEvent.latitude),
						tuple.get(busEvent.longitude),
						tuple.get(busEvent.occurredAt),
						tuple.get(busEvent.description)))
				.toList();
	}
}
