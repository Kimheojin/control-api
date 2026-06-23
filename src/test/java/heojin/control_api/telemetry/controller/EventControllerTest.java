package heojin.control_api.telemetry.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import heojin.control_api.global.exception.GlobalExceptionHandler;
import heojin.control_api.telemetry.dto.RecentEventsResponse;
import heojin.control_api.telemetry.entity.BusEventType;
import heojin.control_api.telemetry.service.BusEventService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

	@Mock
	private BusEventService busEventService;

	@Test
	void getRecentEventsReturnsEvents() throws Exception {
		MockMvc mockMvc = mockMvc();
		when(busEventService.getRecentEvents(eq(BusEventType.SUDDEN_BRAKE), eq(20)))
				.thenReturn(new RecentEventsResponse(List.of(new RecentEventsResponse.EventResponse(
						501L,
						new RecentEventsResponse.EventBusResponse(1L, "서울74사1234", "271번"),
						BusEventType.SUDDEN_BRAKE,
						Instant.parse("2026-06-23T01:18:12Z"),
						new RecentEventsResponse.EventLocationResponse(
								new BigDecimal("37.5658"),
								new BigDecimal("126.9772")),
						"급정거 감지"))));

		mockMvc.perform(get("/v1/events").param("type", "SUDDEN_BRAKE"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.events[0].bus.busNumber").value("서울74사1234"))
				.andExpect(jsonPath("$.events[0].type").value("SUDDEN_BRAKE"));
	}

	@Test
	void getRecentEventsReturnsInvalidRequestWhenTypeIsInvalid() throws Exception {
		MockMvc mockMvc = mockMvc();

		mockMvc.perform(get("/v1/events").param("type", "UNKNOWN"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
	}

	@Test
	void getRecentEventsReturnsInvalidRequestWhenLimitExceedsMax() throws Exception {
		MockMvc mockMvc = mockMvc();

		mockMvc.perform(get("/v1/events").param("limit", "101"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
	}

	private MockMvc mockMvc() {
		return MockMvcBuilders
				.standaloneSetup(new EventController(busEventService))
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}
}
