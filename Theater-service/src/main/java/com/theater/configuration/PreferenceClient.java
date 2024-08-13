package com.theater.configuration;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "preference-service",url = Constants.url+"preferences")
public interface PreferenceClient {
}