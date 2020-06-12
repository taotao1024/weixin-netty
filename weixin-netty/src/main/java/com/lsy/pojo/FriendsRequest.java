package com.lsy.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import javax.persistence.*;

@Data
@ToString
@Table(name = "friends_request")
public class FriendsRequest {
    @Id
    @ApiModelProperty("主键")
    private String id;

    @Column(name = "send_user_id")
    @ApiModelProperty("发出请求的id")
    private String sendUserId;

    @Column(name = "accept_user_id")
    @ApiModelProperty("接受方的id")
    private String acceptUserId;

    @Column(name = "request_date_time")
    @ApiModelProperty("请求时间")
    private Date requestDateTime;

}