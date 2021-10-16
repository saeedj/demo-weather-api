package com.example.weather.service.external;import com.example.weather.dto.WeatherExternalResponseDTO;import com.example.weather.exception.ApiError;import com.example.weather.exception.DownstreamException;import com.example.weather.model.WeatherRequest;import com.squareup.okhttp.mockwebserver.MockResponse;import com.squareup.okhttp.mockwebserver.MockWebServer;import com.squareup.okhttp.mockwebserver.RecordedRequest;import org.junit.jupiter.api.BeforeEach;import org.junit.jupiter.api.Test;import org.junit.jupiter.api.extension.ExtendWith;import org.mockito.junit.jupiter.MockitoExtension;import org.springframework.http.HttpHeaders;import org.springframework.http.HttpStatus;import org.springframework.http.MediaType;import org.springframework.web.util.UriTemplate;import java.io.IOException;import static com.example.weather.util.TestUtils.asJsonString;import static org.hamcrest.CoreMatchers.is;import static org.hamcrest.MatcherAssert.assertThat;import static org.junit.jupiter.api.Assertions.assertThrows;@ExtendWith(MockitoExtension.class)class WeatherExternalServiceTest {    private static final String FAKE_PATH = "/PATH?q={city},{country}";    private MockWebServer mockWebServer;    private WeatherExternalService weatherExternalService;    @BeforeEach    public void setupMockWebServer() throws IOException {        mockWebServer = new MockWebServer();        mockWebServer.play();        weatherExternalService = new WeatherExternalService();        weatherExternalService.setUrl(mockWebServer.getUrl(FAKE_PATH).toString());        weatherExternalService.setTimeout(10L);    }    @Test    public void getFromExternalAPI_whenGetRequestSubmitted_serverReceivesGetRequestAsExpected() throws Exception {        mockWebServer.enqueue(                new MockResponse().setResponseCode(200)                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)                        .setBody("{\"error_code\": null, \"error_message\": null}")        );        WeatherRequest weatherRequest = WeatherRequest.builder().city("city").country("country").build();        weatherExternalService.getFromExternalAPI(weatherRequest);        RecordedRequest request = mockWebServer.takeRequest();        String expectedMethod = "GET";        assertThat(request.getMethod(), is(expectedMethod));        String expectedRequestPath = new UriTemplate(FAKE_PATH).expand(weatherRequest.getCity(), weatherRequest.getCountry()).toString();        assertThat(request.getPath(), is(expectedRequestPath));    }    @Test    public void getFromExternalAPI_whenServerReturns200_succeeds() throws Exception {        WeatherExternalResponseDTO expectedServerResponse = WeatherExternalResponseDTO.builder().responseId("111").build();        mockWebServer.enqueue(                new MockResponse().setResponseCode(200)                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)                        .setBody(asJsonString(expectedServerResponse))        );        WeatherRequest weatherRequest = WeatherRequest.builder().city("city").country("country").build();        WeatherExternalResponseDTO result = weatherExternalService.getFromExternalAPI(weatherRequest);        assertThat(result.getResponseId(), is(expectedServerResponse.getResponseId()));    }    @Test    public void getFromExternalAPI_whenServerReturnsError_throwsDownstreamApiException() throws Exception {        ApiError expectedServerError = ApiError.builder().errorId(HttpStatus.BAD_REQUEST.toString()).httpStatus(HttpStatus.BAD_REQUEST).build();        mockWebServer.enqueue(                new MockResponse().setResponseCode(expectedServerError.getHttpStatus().value())                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)                        .setBody(asJsonString(expectedServerError))        );        WeatherRequest weatherRequest = WeatherRequest.builder().city("city").country("country").build();        assertThrows(DownstreamException.class, () ->                weatherExternalService.getFromExternalAPI(weatherRequest)        );    }}