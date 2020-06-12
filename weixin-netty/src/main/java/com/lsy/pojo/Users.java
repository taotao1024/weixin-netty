package com.lsy.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@ToString
public class Users {
    @Id
    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("用户登录名称")
    private String username;

    @ApiModelProperty("用户登录密码")
    private String password;

    @Column(name = "face_image")
    @ApiModelProperty("用户头像压缩")
    private String faceImage;

    @Column(name = "face_image_big")
    @ApiModelProperty("用户头像原图")
    private String faceImageBig;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("二维码")
    private String qrcode;

    @ApiModelProperty("唯一cid")
    private String cid;

}