package com.example.weather;

import com.example.weather.controller.WeatherController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DemoWeatherApiApplicationTests {

	@Autowired
	private WeatherController weatherController;

	@Test
	void contextLoads() {
		assertThat(weatherController).isNotNull();
	}

}
