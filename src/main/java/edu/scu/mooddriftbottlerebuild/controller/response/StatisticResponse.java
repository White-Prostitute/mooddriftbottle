package edu.scu.mooddriftbottlerebuild.controller.response;

import lombok.Data;

@Data
public class StatisticResponse {

    private int totalBottleNum;

    private int uncheckBottleNum;

    private int totalSessionNum;

    private int uncheckSessionNum;

    private int totalReplyNum;

    private int totalUserNum;
}
