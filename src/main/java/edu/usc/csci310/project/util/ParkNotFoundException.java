package edu.usc.csci310.project.util;

public class ParkNotFoundException extends RuntimeException {
    public ParkNotFoundException(String message) {
        super(message);
    }
}
