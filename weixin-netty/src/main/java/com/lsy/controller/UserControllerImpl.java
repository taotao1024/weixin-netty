package com.lsy.controller;

import com.lsy.Service.UserService;
import com.lsy.enums.OperatorFriendRequestTypeEnum;
import com.lsy.enums.SearchFriendsStatusEnum;
import com.lsy.pojo.Users;
import com.lsy.pojo.bo.UsersBO;
import com.lsy.pojo.vo.MyFriendsVO;
import com.lsy.pojo.vo.UsersVO;
import com.lsy.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "u")
public class UserControllerImpl implements UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FastDFSClient fastDFSClient;


    @Override
    @PostMapping("/registOrLogin")
    public JSONResult RegistOrLogin(@RequestBody Users user) throws Exception {
        System.out.println("登陆或注册");

        Users userResult = null;
        UsersVO usersVO = new UsersVO();

        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank((user.getPassword()))) {
            return JSONResult.errorMsg("用户名或密码不能为空");
        }
        //判断用户是否存在
        boolean result = userService.QueryUserIsExist(user.getUsername());

        if (result) {
            //登录
            userResult = userService.Login(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
            if (userResult == null) {
                return JSONResult.errorMsg("用户名或者密码错误");
            }
        } else {
            //注册
            userResult = userService.Regist(user);
            if (userResult == null) {
                return JSONResult.errorMsg("请稍后重试");
            }
        }
        BeanUtils.copyProperties(userResult, usersVO);
        return JSONResult.ok(usersVO);
    }

    @Override
    @PostMapping("/uploadFaceBase64")
    public JSONResult UploadFaceBase64(@RequestBody UsersBO userBO) throws Exception {
        System.out.println("正在上传图片");
        UsersVO usersVO = new UsersVO();

        //获取字符串,转换为文件对象
        String base64Data = userBO.getFaceData();
        String userFacePath = "D://ehcache//cache" + userBO.getUserId() + "userFace64.png";
        FileUtils.base64ToFile(userFacePath, base64Data);
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        //FDFS
        String url = fastDFSClient.uploadBase64(faceFile);
        System.out.println(url);

        //分隔
        String thump = "_80x80.";
        String split[] = url.split("\\.");
        String thumpImgUrl = split[0] + thump + split[1];

        Users user = new Users();
        user.setId(userBO.getUserId());
        user.setFaceImageBig(thumpImgUrl);
        user.setFaceImage(url);

        userService.UpDateUserInfo(user);
        BeanUtils.copyProperties(user, usersVO);

        return JSONResult.ok(usersVO);
    }

    @Override
    @PostMapping("/setNickname")
    public JSONResult SetNickname(@RequestBody UsersBO usersBO) throws Exception {
        System.out.println("修改昵称");
        Users users = new Users();
        users.setId(usersBO.getUserId());
        users.setNickname(usersBO.getNickname());
        Users result = userService.UpDateUserInfo(users);
        return JSONResult.ok(result);
    }

    @Override
    @PostMapping("/search")
    public JSONResult SearchUser(String MyUserId, String FriendUserName) throws Exception {
        System.out.println("搜索好友");
        if (StringUtils.isBlank(MyUserId) || StringUtils.isBlank(FriendUserName)) {
            return JSONResult.errorMsg("好友不存在");
        }

        // 前置条件 - 1. 搜索的用户如果不存在，返回:无此用户
        // 前置条件 - 2. 搜索账号是你自己，返回:不能添加自己
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回:该用户已经是你的好友
        Integer status = userService.preconditionSearchFriends(MyUserId, FriendUserName);

        if (status == SearchFriendsStatusEnum.SUCCESS.status) {
            Users user = userService.QueryUserInfoByUsername(FriendUserName);
            UsersVO userVO = new UsersVO();
            BeanUtils.copyProperties(user, userVO);
            return JSONResult.ok(userVO);
        } else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return JSONResult.errorMsg(errorMsg);
        }
    }

    @Override
    @PostMapping("/addFriendRequest")
    public JSONResult AddFriendRequest(String MyUserId, String FriendUsername) throws Exception {
        System.out.println("请求添加");
        if (StringUtils.isBlank(MyUserId) || StringUtils.isBlank(FriendUsername)) {
            return JSONResult.errorMsg("好友不存在");
        }

        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer status = userService.preconditionSearchFriends(MyUserId, FriendUsername);

        if (status == SearchFriendsStatusEnum.SUCCESS.status) {
            System.out.println("添加好友");
            userService.SendFriendRequest(MyUserId, FriendUsername);
        } else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return JSONResult.errorMsg(errorMsg);
        }
        return JSONResult.ok();
    }

    @Override
    @PostMapping("/queryFriendRequests")
    public JSONResult QueryFriendRequests(String userId) throws Exception {
        System.out.println("查询好友请求");
        if (StringUtils.isBlank(userId)) {
            //请求列表为空
            return JSONResult.errorMsg("");
        }
        return JSONResult.ok(userService.queryFriendRequestList(userId));
    }

    @Override
    @PostMapping("/operFriendRequest")
    public JSONResult OperFriendRequest(String acceptUserId, String sendUserId, Integer operType) throws Exception {
        System.out.println("处理好友请求");
        if (StringUtils.isBlank(acceptUserId) || StringUtils.isBlank(sendUserId) || operType == null) {
            return JSONResult.errorMsg("");
        }

        // 1. 如果operType 没有对应的枚举值，则直接抛出空错误信息
        if (StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))) {
            return JSONResult.errorMsg("");
        }

        // 2. 判断如果忽略好友请求，则直接删除好友请求的数据库表记录
        if (operType == OperatorFriendRequestTypeEnum.IGNORE.type) {
            userService.deleteFriendRequest(sendUserId, acceptUserId);
        }

        // 3. 判断如果是通过好友请求，则互相增加好友记录到数据库对应的表，然后删除好友请求的数据库表记录
        if (operType == OperatorFriendRequestTypeEnum.PASS.type) {
            userService.passFriendRequest(sendUserId, acceptUserId);
        }

        // 4. 数据库查询好友列表
        List<MyFriendsVO> myFirends = userService.queryMyFriends(acceptUserId);
        return JSONResult.ok(myFirends);
    }

    @Override
    @PostMapping("/myFriends")
    public JSONResult MyFriends(String userId) {
        System.out.println("查找我的通讯录");
        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("");
        }
        List<MyFriendsVO> myFirends = userService.queryMyFriends(userId);

        return JSONResult.ok(myFirends);
    }


    @Override
    @PostMapping("/getUnReadMsgList")
    public JSONResult GetUnReadMsgList(String acceptUserId) {
        System.out.println("获取未读的信息");
        if (StringUtils.isBlank(acceptUserId)) {
            return JSONResult.errorMsg("");
        }
        // 查询列表
        List<com.lsy.pojo.ChatMsg> unreadMsgList = userService.getUnReadMsgList(acceptUserId);

        return JSONResult.ok(unreadMsgList);
    }
}
