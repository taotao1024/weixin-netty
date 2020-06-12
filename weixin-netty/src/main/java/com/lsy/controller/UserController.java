package com.lsy.controller;

import com.lsy.pojo.Users;
import com.lsy.pojo.bo.UsersBO;
import com.lsy.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "用户相关接口", description = "提供了登录注册、修改昵称、添加好友、接收消息等方法")
public interface UserController {

    @ApiOperation("登陆或注册")
    @ApiImplicitParam(name = "user", value = "用户", required = true, paramType = "form", dataType = "Users")
    public JSONResult RegistOrLogin(Users user) throws Exception;

    @ApiOperation("上传图片")
    @ApiImplicitParam(name = "userBO", value = "用户", required = true, paramType = "form", dataType = "UsersBO")
    public JSONResult UploadFaceBase64(UsersBO userBO) throws Exception;

    @ApiOperation("修改昵称")
    @ApiImplicitParam(name = "usersBO", value = "用户", required = true, paramType = "form", dataType = "UsersBO")
    public JSONResult SetNickname(UsersBO usersBO) throws Exception;

    @ApiOperation("搜索好友")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "MyuserId", value = "我的ID", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "FriendUserName", value = "搜索的好友名称", required = true, paramType = "path", dataType = "String")
    })
    public JSONResult SearchUser(String MyuserId, String FriendUserName) throws Exception;

    @ApiOperation("添加好友")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "MyUserId", value = "我的ID", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "FriendUsername", value = "搜索的好友名称", required = true, paramType = "path", dataType = "String")
    })
    public JSONResult AddFriendRequest(String MyUserId, String FriendUsername) throws Exception;

    @ApiOperation("查询好友请求")
    @ApiImplicitParam(name = "userId", value = "好友的ID", required = true, paramType = "path", dataType = "String")
    public JSONResult QueryFriendRequests(String userId) throws Exception;

    @ApiOperation("处理请求")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "acceptUserId", value = "接收方的ID", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "sendUserId", value = "发送的ID", required = true, paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "operType", value = "枚举", required = true, paramType = "path", dataType = "Integer")
    })
    public JSONResult OperFriendRequest(String acceptUserId, String sendUserId, Integer operType) throws Exception;

    @ApiOperation("查找好友列表")
    @ApiImplicitParam(name = "userId", value = "我的ID", required = true, paramType = "path", dataType = "String")
    public JSONResult MyFriends(String userId);

    @ApiOperation("获取未签收的消息列表")
    @ApiImplicitParam(name = "acceptUserId", value = "接收方的ID", required = true, paramType = "path", dataType = "String")
    public JSONResult GetUnReadMsgList(String acceptUserId);
}