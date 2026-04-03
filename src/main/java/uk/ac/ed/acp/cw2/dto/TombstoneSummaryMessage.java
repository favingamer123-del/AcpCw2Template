package uk.ac.ed.acp.cw2.dto;

public class TombstoneSummaryMessage {
    private int totalMessagesWritten;
    private int totalMessagesProcessed;
    private int totalRedisUpdates;
    private double totalValueWritten;
    private double totalAdded;

    public TombstoneSummaryMessage() {
    }

    public TombstoneSummaryMessage(int totalMessagesWritten,
                                   int totalMessagesProcessed,
                                   int totalRedisUpdates,
                                   double totalValueWritten,
                                   double totalAdded) {
        this.totalMessagesWritten = totalMessagesWritten;
        this.totalMessagesProcessed = totalMessagesProcessed;
        this.totalRedisUpdates = totalRedisUpdates;
        this.totalValueWritten = totalValueWritten;
        this.totalAdded = totalAdded;
    }

    public int getTotalMessagesWritten() {
        return totalMessagesWritten;
    }

    public void setTotalMessagesWritten(int totalMessagesWritten) {
        this.totalMessagesWritten = totalMessagesWritten;
    }

    public int getTotalMessagesProcessed() {
        return totalMessagesProcessed;
    }

    public void setTotalMessagesProcessed(int totalMessagesProcessed) {
        this.totalMessagesProcessed = totalMessagesProcessed;
    }

    public int getTotalRedisUpdates() {
        return totalRedisUpdates;
    }

    public void setTotalRedisUpdates(int totalRedisUpdates) {
        this.totalRedisUpdates = totalRedisUpdates;
    }

    public double getTotalValueWritten() {
        return totalValueWritten;
    }

    public void setTotalValueWritten(double totalValueWritten) {
        this.totalValueWritten = totalValueWritten;
    }

    public double getTotalAdded() {
        return totalAdded;
    }

    public void setTotalAdded(double totalAdded) {
        this.totalAdded = totalAdded;
    }
}