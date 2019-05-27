package cdu.edu.chao.domain;

public class TrainingReport {

    private String trainingDate;
    private Double accurateRate;

    public TrainingReport(String trainingDate, Double accurateRate) {
        this.trainingDate = trainingDate;
        this.accurateRate = accurateRate;
    }

    public String getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(String trainingDate) {
        this.trainingDate = trainingDate;
    }

    public Double getAccurateRate() {
        return accurateRate;
    }

    public void setAccurateRate(Double accurateRate) {
        this.accurateRate = accurateRate;
    }
}
