package com.dryver.Models;

/**
 * Enum for denoting all possible states while in driver's view when searching for requests
 */

public enum ActivityDryverMainState {
    ALL,
    PENDING,
    ACTIVE,
    GEOLOCATION,
    KEYWORD,
    RATE,
    COST
}
