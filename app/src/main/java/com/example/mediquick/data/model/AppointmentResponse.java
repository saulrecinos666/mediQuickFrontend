package com.example.mediquick.data.model;

import java.util.List;

public class AppointmentResponse {
    private boolean success;
    private int code;
    private String message;
    private List<Appointment> data;
    private String timestamp;

    // Getters y setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<Appointment> getData() { return data; }
    public void setData(List<Appointment> data) { this.data = data; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
