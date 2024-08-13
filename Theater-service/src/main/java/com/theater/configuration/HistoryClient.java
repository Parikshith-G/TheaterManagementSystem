package com.theater.configuration;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "history-client",url = Constants.url+"history")
public interface HistoryClient {

}
