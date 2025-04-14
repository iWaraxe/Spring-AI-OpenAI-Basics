package com.coherentsolutions.springaiopenaibasics.model;

/**
 * Represents a request to get the capital of a state or country.
 * Used as the input for the capital endpoint.
 */
public record GetCapitalRequest(String stateOrCountry) {
}