package com.lsy.pojo.vo;

import javax.persistence.Column;
import javax.persistence.Id;

//返回给前端的数据
public class UsersVO {
    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 用户登录名称
     */
    private String username;


    /**
     * 用户头像压缩
     */
    @Column(name = "face_image")
    private String faceImage;

    /**
     * 用户头像原图
     */
    @Column(name = "face_image_big")
    private String faceImageBig;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 二维码
     */
    private String qrcode;


    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取用户登录名称
     *
     * @return username - 用户登录名称
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户登录名称
     *
     * @param username 用户登录名称
     */
    public void setUsername(String username) {
        this.username = username;
    }


    /**
     * 获取用户头像压缩
     *
     * @return face_image - 用户头像压缩
     */
    public String getFaceImage() {
        return faceImage;
    }

    /**
     * 设置用户头像压缩
     *
     * @param faceImage 用户头像压缩
     */
    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }

    /**
     * 获取用户头像原图
     *
     * @return face_image_big - 用户头像原图
     */
    public String getFaceImageBig() {
        return faceImageBig;
    }

    /**
     * 设置用户头像原图
     *
     * @param faceImageBig 用户头像原图
     */
    public void setFaceImageBig(String faceImageBig) {
        this.faceImageBig = faceImageBig;
    }

    /**
     * 获取用户昵称
     *
     * @return nickname - 用户昵称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置用户昵称
     *
     * @param nickname 用户昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取二维码
     *
     * @return qrcode - 二维码
     */
    public String getQrcode() {
        return qrcode;
    }

    /**
     * 设置二维码
     *
     * @param qrcode 二维码
     */
    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

}