package com.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ApiGatewayApplicationTests {

	@InjectMocks
	private ApiGatewayApplication apiGatewayApplication;

	@Test
	void contextLoads() {
	}

    void testLoggerMessages() {
        // Mocking the LoggerFactory
        try (MockedStatic<LoggerFactory> loggerFactoryMockedStatic = mockStatic(LoggerFactory.class);
             MockedStatic<SpringApplication> springApplicationMockedStatic = mockStatic(SpringApplication.class)) {

            Logger mockLogger = mock(Logger.class);
            loggerFactoryMockedStatic.when(() -> LoggerFactory.getLogger(ApiGatewayApplication.class)).thenReturn(mockLogger);

            // When: The application starts
            springApplicationMockedStatic.when(() -> SpringApplication.run(ApiGatewayApplication.class)).thenAnswer(invocation -> {
                ApiGatewayApplication.main(new String[]{});
                return null;
            });

            ApiGatewayApplication.main(new String[]{});

            // Then: Verify that logger messages are printed
            verify(mockLogger, times(1)).info("Starting ApiGatewayApplication...");
            verify(mockLogger, times(1)).info("ApiGatewayApplication started successfully.");
        }
    }

}
