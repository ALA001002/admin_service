package com.bigo.project.bigo.marketsituation.domain;

import java.util.List;

public class Deep  {
    private Long lastUpdateId;
    private List<DeepBids> bidsList;
    private List<DeepAsks> asksList;

    public Long getLastUpdateId() {
        return lastUpdateId;
    }

    public void setLastUpdateId(Long lastUpdateId) {
        this.lastUpdateId = lastUpdateId;
    }

    public List<DeepBids> getBidsList() {
        return bidsList;
    }

    public void setBidsList(List<DeepBids> bidsList) {
        this.bidsList = bidsList;
    }

    public List<DeepAsks> getAsksList() {
        return asksList;
    }

    public void setAsksList(List<DeepAsks> asksList) {
        this.asksList = asksList;
    }
}
