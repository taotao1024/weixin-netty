package com.lsy.pojo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import javax.persistence.*;

@ToString
@Data
@Table(name = "chat_msg")
public class ChatMsg {
    @Id
    @ApiModelProperty("主键")
    private String id;

    @Column(name = "send_user_id")
    @ApiModelProperty("发送者id")
    private String sendUserId;

    @Column(name = "accept_user_id")
    @ApiModelProperty("接受者id")
    private String acceptUserId;

    @ApiModelProperty("消息")
    private String msg;

    @ApiModelProperty("状态")
    @Column(name = "sign_flag")
    private Integer signFlag;

    @ApiModelProperty("创建时间")
    @Column(name = "create_time")
    private Date createTime;

}