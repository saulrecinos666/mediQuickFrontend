package com.example.mediquick.data.model;

public class Branch {
    private String branchId;
    private String branchName;
    private String branchFullAddress;
    private String branchDescription;

    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getBranchFullAddress() { return branchFullAddress; }
    public void setBranchFullAddress(String branchFullAddress) { this.branchFullAddress = branchFullAddress; }

    public String getBranchDescription() { return branchDescription; }
    public void setBranchDescription(String branchDescription) { this.branchDescription = branchDescription; }
}
