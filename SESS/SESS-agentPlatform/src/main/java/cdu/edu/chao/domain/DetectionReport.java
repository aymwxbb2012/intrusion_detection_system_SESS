package cdu.edu.chao.domain;

import java.sql.Timestamp;

public class DetectionReport {

    private String detectionDate;
    private int attackNumber;
    private int totalDataNumber;


    public DetectionReport(String detectionDate, int attackNumber, int totalDataNumber) {
        this.detectionDate = detectionDate;
        this.attackNumber = attackNumber;
        this.totalDataNumber = totalDataNumber;

    }

    public String getDetectionDate() {
        return detectionDate;
    }

    public void setDetectionDate(String detectionDate) {
        this.detectionDate = detectionDate;
    }

    public int getAttackNumber() {
        return attackNumber;
    }

    public void setAttackNumber(int attackNumber) {
        this.attackNumber = attackNumber;
    }

    public int getTotalDataNumber() {
        return totalDataNumber;
    }

    public void setTotalDataNumber(int totalDataNumber) {
        this.totalDataNumber = totalDataNumber;
    }


}
