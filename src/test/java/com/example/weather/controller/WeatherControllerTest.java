package com.example.weather.controller;import com.example.weather.exception.DataNotFoundException;import com.example.weather.exception.RateLimitException;import com.example.weather.model.WeatherRequest;import com.example.weather.model.WeatherResponse;import com.example.weather.service.ApiUsageService;import com.example.weather.service.internal.WeatherInternalService;import org.junit.jupiter.api.Test;import org.mockito.Mockito;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;import org.springframework.boot.test.mock.mockito.MockBean;import org.springframework.http.MediaType;import org.springframework.test.web.servlet.MockMvc;import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;import static com.example.weather.controller.WeatherController.DEFAULT_API_KEY;import static com.example.weather.util.TestUtils.ANY_STRING;import static com.example.weather.util.TestUtils.asJsonString;import static org.apache.commons.lang3.StringUtils.EMPTY;import static org.mockito.Mockito.*;import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;@WebMvcTest(WeatherController.class)class WeatherControllerTest {    private static final String API_KEY = "API_KEY";    private static final String URL_WITH_PATH_VARIABLE = "/live/data/{apiKey}";    private static final String URL_NO_PATH_VARIABLE = "/live/data/";    @Autowired    private MockMvc mockMvc;    @MockBean    private WeatherInternalService weatherInternalService;    @MockBean    private ApiUsageService apiUsageService;    @Test    public void fetchLiveData_whenInputDataIsNullOrEmpty_returnsBadRequest() throws Exception {        mockMvc.perform(MockMvcRequestBuilders.post(URL_WITH_PATH_VARIABLE, API_KEY)                .contentType(MediaType.APPLICATION_JSON)                .content(asJsonString(WeatherRequest.builder().city(EMPTY).country(EMPTY).build())))                .andExpect(status().isBadRequest());    }    @Test    public void fetchLiveData_whenApiRateLimitExceeded_returnsTooManyRequests() throws Exception {        Mockito.doThrow(RateLimitException.class).when(apiUsageService).checkRateLimit(API_KEY);        WeatherRequest weatherRequest = WeatherRequest.builder().city("city").country("country").build();        mockMvc.perform(MockMvcRequestBuilders.post(URL_WITH_PATH_VARIABLE, API_KEY)                .contentType(MediaType.APPLICATION_JSON)                .content(asJsonString(weatherRequest)))                .andExpect(status().isTooManyRequests());        verify(apiUsageService, times(1)).checkRateLimit(API_KEY);    }    @Test    public void fetchLiveData_whenInputDataIsCorrectAndApiKeyPassed_succeeds() throws Exception {        doNothing().when(apiUsageService).checkRateLimit(API_KEY);        WeatherRequest weatherRequest = WeatherRequest.builder().city(ANY_STRING).country(ANY_STRING).build();        WeatherResponse weatherResponse = WeatherResponse.builder().description(ANY_STRING).build();        Mockito.doReturn(weatherResponse).when(weatherInternalService).getLiveData(weatherRequest);        mockMvc.perform(MockMvcRequestBuilders.post(URL_WITH_PATH_VARIABLE, API_KEY)                .contentType(MediaType.APPLICATION_JSON)                .content(asJsonString(weatherRequest)))                .andExpect(status().is2xxSuccessful())                .andExpect(jsonPath("$.description").value(ANY_STRING));        verify(apiUsageService, times(1)).checkRateLimit(API_KEY);        verify(weatherInternalService, times(1)).getLiveData(weatherRequest);    }    @Test    public void fetchLiveData_whenInputDataIsCorrectNoApiKeyPassed_returnsSuccessWithDefaultApiKey() throws Exception {        doNothing().when(apiUsageService).checkRateLimit(DEFAULT_API_KEY);        WeatherRequest weatherRequest = WeatherRequest.builder().city(ANY_STRING).country(ANY_STRING).build();        WeatherResponse weatherResponse = WeatherResponse.builder().description(ANY_STRING).build();        Mockito.doReturn(weatherResponse).when(weatherInternalService).getLiveData(weatherRequest);        mockMvc.perform(MockMvcRequestBuilders.post(URL_NO_PATH_VARIABLE)                .contentType(MediaType.APPLICATION_JSON)                .content(asJsonString(weatherRequest)))                .andExpect(status().is2xxSuccessful())                .andExpect(jsonPath("$.description").value(ANY_STRING));        verify(apiUsageService, times(1)).checkRateLimit(DEFAULT_API_KEY);        verify(weatherInternalService, times(1)).getLiveData(weatherRequest);    }    @Test    public void fetchLiveData_whenNoDataFound_returnsNotFound() throws Exception {        doNothing().when(apiUsageService).checkRateLimit(API_KEY);        WeatherRequest weatherRequest = WeatherRequest.builder().city(ANY_STRING).country(ANY_STRING).build();        Mockito.doThrow(DataNotFoundException.class).when(weatherInternalService).getLiveData(weatherRequest);        mockMvc.perform(MockMvcRequestBuilders.post(URL_WITH_PATH_VARIABLE, API_KEY)                .contentType(MediaType.APPLICATION_JSON)                .content(asJsonString(weatherRequest)))                .andExpect(status().isNotFound());        verify(apiUsageService, times(1)).checkRateLimit(API_KEY);        verify(weatherInternalService, times(1)).getLiveData(weatherRequest);    }}