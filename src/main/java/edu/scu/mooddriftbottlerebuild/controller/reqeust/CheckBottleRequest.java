package edu.scu.mooddriftbottlerebuild.controller.reqeust;

import lombok.Data;

@Data
public class CheckBottleRequest {

    private int bottleId;

    private int check;

    private String userId;

    private String replyStr;

}
