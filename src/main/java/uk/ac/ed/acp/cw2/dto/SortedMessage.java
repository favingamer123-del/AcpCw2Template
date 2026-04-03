package uk.ac.ed.acp.cw2.dto;

import com.google.gson.annotations.SerializedName;

public class SortedMessage {

    @SerializedName("Id")
    private int id;

    @SerializedName("Payload")
    private String payload;

    public SortedMessage() {
    }

    public SortedMessage(int id, String payload) {
        this.id = id;
        this.payload = payload;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}