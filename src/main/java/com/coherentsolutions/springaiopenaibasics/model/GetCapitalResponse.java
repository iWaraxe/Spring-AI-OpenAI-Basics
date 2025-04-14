package com.coherentsolutions.springaiopenaibasics.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record GetCapitalResponse(@JsonPropertyDescription("This is the city name") String answer) {}