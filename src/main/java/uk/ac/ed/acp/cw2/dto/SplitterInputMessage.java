package uk.ac.ed.acp.cw2.dto;

import com.google.gson.annotations.SerializedName;

public class SplitterInputMessage {

    @SerializedName("Id")
    private int id;

    @SerializedName("Value")
    private double value;

    @SerializedName("AdditionalData")
    private String additionalData;

    public SplitterInputMessage() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }


    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }
}