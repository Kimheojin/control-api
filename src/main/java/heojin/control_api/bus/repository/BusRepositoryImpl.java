package heojin.control_api.bus.repository;

import static heojin.control_api.bus.entity.QBus.bus;
import static heojin.control_api.route.entity.QRoute.route;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import heojin.control_api.bus.dto.BusStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class BusRepositoryImpl implements BusRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<BusListRow> search(BusSearchCondition condition, Pageable pageable) {
		BooleanBuilder where = createWhere(condition);

		List<Tuple> tuples = queryFactory
				.select(bus.id, bus.busNumber, bus.routeId, route.name, bus.currentSpeedKph, bus.lastCommunicatedAt)
				.from(bus)
				.leftJoin(route).on(route.id.eq(bus.routeId))
				.where(where)
				.orderBy(bus.id.asc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		Long total = queryFactory
				.select(bus.count())
				.from(bus)
				.leftJoin(route).on(route.id.eq(bus.routeId))
				.where(where)
				.fetchOne();

		List<BusListRow> content = tuples.stream()
				.map(tuple -> new BusListRow(
						tuple.get(bus.id),
						tuple.get(bus.busNumber),
						tuple.get(bus.routeId),
						tuple.get(route.name),
						tuple.get(bus.currentSpeedKph),
						tuple.get(bus.lastCommunicatedAt)))
				.toList();

		return new PageImpl<>(content, pageable, total == null ? 0 : total);
	}

	private BooleanBuilder createWhere(BusSearchCondition condition) {
		BooleanBuilder builder = new BooleanBuilder();

		if (condition.status() == BusStatus.ONLINE) {
			builder.and(bus.lastCommunicatedAt.goe(condition.onlineCutoff()));
		}
		if (condition.status() == BusStatus.OFFLINE) {
			builder.and(bus.lastCommunicatedAt.isNull()
					.or(bus.lastCommunicatedAt.lt(condition.onlineCutoff())));
		}
		if (condition.routeId() != null) {
			builder.and(bus.routeId.eq(condition.routeId()));
		}
		if (condition.keyword() != null && !condition.keyword().isBlank()) {
			String keyword = condition.keyword().trim();
			builder.and(bus.busNumber.containsIgnoreCase(keyword)
					.or(route.name.containsIgnoreCase(keyword)));
		}

		return builder;
	}
}
