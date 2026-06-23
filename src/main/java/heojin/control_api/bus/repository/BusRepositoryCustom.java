package heojin.control_api.bus.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BusRepositoryCustom {

	Page<BusListRow> search(BusSearchCondition condition, Pageable pageable);
}
