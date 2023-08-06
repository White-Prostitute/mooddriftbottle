package edu.scu.mooddriftbottlerebuild.entity;

import lombok.Data;

@Data
public class WeiChatResponse {

    private String openid;
    private boolean isRegistered;
    private boolean isForbidden;

}
