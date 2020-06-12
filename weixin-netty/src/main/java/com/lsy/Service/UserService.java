package com.lsy.Service;

import com.lsy.netty.pojo.ChatMsg;
import com.lsy.pojo.Users;
import com.lsy.pojo.vo.FriendRequestVO;
import com.lsy.pojo.vo.MyFriendsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface UserService {
    //判断用户名是否存在
    public boolean QueryUserIsExist(String username);

    //注册
    public Users Regist(Users user) throws Exception;

    //登录
    public Users Login(String username, String password);

    //用户修改
    public Users UpDateUserInfo(Users users);

    //查询前置
    public Integer preconditionSearchFriends(String MyUserId, String friendUserName);

    //根据用户名查询用户对象
    public Users QueryUserInfoByUsername(String UserName);

    //添加好友请求记录
    public void SendFriendRequest(String MyUserId, String FriendUsername);

    //查询好友请求列表
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    //删除请求记录
    public void deleteFriendRequest(String sendUserId, String acceptUserId);

    //通过好友请求
    public void passFriendRequest(String sendUserId, String acceptUserId);

    //查询好友列表
    public List<MyFriendsVO> queryMyFriends(String acceptUserId);

    //==================netty==================

    //保存聊天消息到数据库
    public String saveMsg(ChatMsg chatMsg);

    //批量签收消息
    public void updateMsgSigned(List<String> msgIdList);

    //获取未签收消息列表
    public List<com.lsy.pojo.ChatMsg> getUnReadMsgList(String acceptUserId);
}
