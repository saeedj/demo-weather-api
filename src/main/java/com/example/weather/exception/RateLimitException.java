package com.example.weather.exception;import lombok.AllArgsConstructor;import lombok.Getter;import lombok.Setter;@Getter@Setter@AllArgsConstructorpublic class RateLimitException extends RuntimeException {    private static final long serialVersionUID = -1299162657544343170L;}