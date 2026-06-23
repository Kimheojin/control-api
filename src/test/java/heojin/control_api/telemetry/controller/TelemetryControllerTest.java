package heojin.control_api.telemetry.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import heojin.control_api.global.exception.BusinessException;
import heojin.control_api.global.exception.ErrorCode;
import heojin.control_api.global.exception.GlobalExceptionHandler;
import heojin.control_api.telemetry.dto.TelemetryRequest;
import heojin.control_api.telemetry.dto.TelemetryResponse;
import heojin.control_api.telemetry.entity.BusEventType;
import heojin.control_api.telemetry.service.TelemetryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class TelemetryControllerTest {

	@Mock
	private TelemetryService telemetryService;

	@Captor
	private ArgumentCaptor<TelemetryRequest> requestCaptor;

	@Test
	void receiveTelemetryReturnsAck() throws Exception {
		MockMvc mockMvc = mockMvc();
		when(telemetryService.receiveTelemetry(any(TelemetryRequest.class)))
				.thenReturn(new TelemetryResponse(1L, true));

		mockMvc.perform(post("/v1/internal/telemetry")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "busId": 1,
								  "latitude": 37.5665,
								  "longitude": 126.9780,
								  "speedKph": 42,
								  "recordedAt": "2026-06-23T01:20:00Z",
								  "event": {
								    "type": "SUDDEN_BRAKE",
								    "description": "급정거 감지"
								  }
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.busId").value(1))
				.andExpect(jsonPath("$.received").value(true));

		verify(telemetryService).receiveTelemetry(requestCaptor.capture());
		TelemetryRequest request = requestCaptor.getValue();
		org.assertj.core.api.Assertions.assertThat(request.event().type()).isEqualTo(BusEventType.SUDDEN_BRAKE);
		org.assertj.core.api.Assertions.assertThat(request.event().description()).isEqualTo("급정거 감지");
	}

	@Test
	void receiveTelemetryReturnsInvalidRequestWhenRequiredFieldIsMissing() throws Exception {
		MockMvc mockMvc = mockMvc();

		mockMvc.perform(post("/v1/internal/telemetry")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "busId": 1,
								  "latitude": 37.5665,
								  "longitude": 126.9780,
								  "recordedAt": "2026-06-23T01:20:00Z"
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
	}

	@Test
	void receiveTelemetryReturnsInvalidRequestWhenValueIsInvalid() throws Exception {
		MockMvc mockMvc = mockMvc();

		mockMvc.perform(post("/v1/internal/telemetry")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "busId": 1,
								  "latitude": 91,
								  "longitude": 126.9780,
								  "speedKph": 42,
								  "recordedAt": "2026-06-23T01:20:00Z"
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
	}

	@Test
	void receiveTelemetryReturnsBusNotFound() throws Exception {
		MockMvc mockMvc = mockMvc();
		when(telemetryService.receiveTelemetry(any(TelemetryRequest.class)))
				.thenThrow(new BusinessException(ErrorCode.BUS_NOT_FOUND));

		mockMvc.perform(post("/v1/internal/telemetry")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "busId": 999,
								  "latitude": 37.5665,
								  "longitude": 126.9780,
								  "speedKph": 42,
								  "recordedAt": "2026-06-23T01:20:00Z"
								}
								"""))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("BUS_NOT_FOUND"));
	}

	private MockMvc mockMvc() {
		return MockMvcBuilders
				.standaloneSetup(new TelemetryController(telemetryService))
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}
}
