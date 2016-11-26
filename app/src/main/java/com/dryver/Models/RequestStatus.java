package com.dryver.Models;

/**
 * Holds the status of a request
 */

public enum RequestStatus {
    CANCELLED,
    NO_DRIVERS,
    DRIVERS_AVAILABLE,
    DRIVER_CHOSEN,
    PAYMENT_AUTHORIZED,
    PAYMENT_ACCEPTED
}
