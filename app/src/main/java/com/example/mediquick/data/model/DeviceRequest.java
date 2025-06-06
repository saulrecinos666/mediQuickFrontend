package com.example.mediquick.data.model;

public class DeviceRequest {
    private String userId;
    private String userDevicesToken;
    private String userDevicesType;

    public DeviceRequest(String userId, String userDevicesToken, String userDevicesType) {
        this.userId = userId;
        this.userDevicesToken = userDevicesToken;
        this.userDevicesType = userDevicesType;
    }

}
