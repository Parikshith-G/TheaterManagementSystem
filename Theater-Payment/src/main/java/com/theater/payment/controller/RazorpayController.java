package com.theater.payment.controller;

import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/pg")
public class RazorpayController {

	private static final Logger logger = LoggerFactory.getLogger(RazorpayController.class);

	private RazorpayClient client;

	@Value("${razorpay.secretid}")
	private String apiKey;

	@Value("${razorpay.secretkey}")
	private String secret;

	@PostMapping("/createOrder")
	public OrderResponse createOrder(@RequestBody OrderRequest orderRequest) {
		OrderResponse response = new OrderResponse();
		try {

			client = new RazorpayClient(apiKey, secret);

			Order order = createRazorPayOrder(orderRequest.getAmount());

			String orderId = (String) order.get("id");

			response.setRazorpayOrderId(orderId);
			response.setApplicationFee("" + orderRequest.getAmount());

			response.setSecretKey("hirXp7bWfzBn4DceklzQVINV");
			response.setSecretId("rzp_test_TljrVVQCuQ0BV0");
			response.setPgName("razor1");

			logger.info("Razorpay order created successfully. Order ID: {}", orderId);
			return response;
		} catch (RazorpayException e) {
			logger.error("Error creating Razorpay order: {}", e.getMessage(), e);
			e.printStackTrace();
		}

		return response;

	}

	private Order createRazorPayOrder(BigInteger amount) throws RazorpayException {

		JSONObject options = new JSONObject();
		options.put("amount", amount.multiply(BigInteger.valueOf(100L)));
		options.put("currency", "INR");
		options.put("receipt", "txn_123456");
		options.put("payment_capture", 1); // You can enable this if you want to do Auto Capture.
		return client.orders.create(options);
	}
}
