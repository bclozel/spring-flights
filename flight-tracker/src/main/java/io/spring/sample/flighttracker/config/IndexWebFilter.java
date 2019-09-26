package io.spring.sample.flighttracker.config;

import java.net.URI;

import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

@Component
public class IndexWebFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		if ("/".equals(exchange.getRequest().getPath().toString())) {
			ServerHttpResponse response = exchange.getResponse();
			response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
			response.getHeaders().setLocation(URI.create("/index.html"));
			return response.setComplete();
		}
		return chain.filter(exchange);
	}
}
