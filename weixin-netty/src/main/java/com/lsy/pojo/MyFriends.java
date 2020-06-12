package com.lsy.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@ToString
@Table(name = "my_friends")
public class MyFriends {
    @Id
    @ApiModelProperty("主键")
    private String id;

    @Column(name = "my_user_id")
    @ApiModelProperty("我的id")
    private String myUserId;

    @Column(name = "my_friend_user_id")
    @ApiModelProperty("朋友id")
    private String myFriendUserId;

}