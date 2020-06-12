package com.lsy.Service;

import com.lsy.enums.MsgActionEnum;
import com.lsy.enums.MsgSignFlagEnum;
import com.lsy.enums.SearchFriendsStatusEnum;
import com.lsy.mapper.*;
import com.lsy.netty.pojo.ChatMsg;
import com.lsy.netty.pojo.DataContent;
import com.lsy.netty.utils.UserChannelRel;
import com.lsy.pojo.FriendsRequest;
import com.lsy.pojo.MyFriends;
import com.lsy.pojo.Users;
import com.lsy.pojo.vo.FriendRequestVO;
import com.lsy.pojo.vo.MyFriendsVO;
import com.lsy.utils.*;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper userMapper;
    @Autowired
    private UsersMapperCustom usersMapperCustom;
    @Autowired
    private MyFriendsMapper myFriendsMapper;
    @Autowired
    private FriendsRequestMapper friendsRequestMapper;
    @Autowired
    private ChatMsgMapper chatMsgMapper;

    @Autowired
    private Sid sid;
    @Autowired
    private QRCodeUtils qRCodeUtils;
    @Autowired
    private FastDFSClient fastDFSClient;


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean QueryUserIsExist(String username) {
        Users user = new Users();
        user.setUsername(username);
        Users result = userMapper.selectOne(user);
        return result != null ? true : false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Users Regist(Users user) throws Exception {
        user.setId(sid.nextShort());
        user.setUsername(user.getUsername());
        user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
        user.setFaceImage("");
        user.setFaceImageBig("");
        user.setNickname(user.getUsername());
        //生成二维码
        String qrcodepath = "D://ehcache//user" + user.getId() + "qrcode.png";
        qRCodeUtils.createQRCode(qrcodepath, "muxin_qrcode:" + user.getUsername());
        //文件
        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrcodepath);
        //上传
        String qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);

        user.setQrcode(qrCodeUrl);
        userMapper.insert(user);
        return user;

    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users Login(String username, String password) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);
        Users userResult = userMapper.selectOneByExample(userExample);
        return userResult;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Users UpDateUserInfo(Users users) {
        userMapper.updateByPrimaryKeySelective(users);
        return queryUserById(users.getId());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Integer preconditionSearchFriends(String MyUserId, String friendUserName) {
        //搜索的用户如果不存在，返回:无此用户
        System.out.println("前置条件 - 1通过");
        Users user = QueryUserInfoByUsername(friendUserName);
        if (user == null) {
            return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
        }
        //搜索账号是你自己，返回:不能添加自己
        System.out.println("前置条件 - 2通过");
        if (user.getId().equals(MyUserId)) {
            return SearchFriendsStatusEnum.NOT_YOURSELF.status;
        }
        //搜索的朋友已经是你的好友，返回:该用户已经是你的好友
        System.out.println("前置条件 - 3通过");
        Example mf = new Example(MyFriends.class);
        Example.Criteria criteria = mf.createCriteria();
        criteria.andEqualTo("myUserId", MyUserId);
        criteria.andEqualTo("myFriendUserId", user.getId());
        MyFriends myFriendsResult = myFriendsMapper.selectOneByExample(mf);
        if (myFriendsResult != null) {
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
        }
        return SearchFriendsStatusEnum.SUCCESS.status;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserById(String userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users QueryUserInfoByUsername(String UserName) {
        Example users = new Example(Users.class);
        Example.Criteria criteria = users.createCriteria();
        criteria.andEqualTo("username", UserName);
        return userMapper.selectOneByExample(users);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void SendFriendRequest(String MyUserId, String FriendUsername) {
        //查询朋友信息
        Users friend = QueryUserInfoByUsername(FriendUsername);
        // 1. 查询发送好友请求记录表
        Example example = new Example(FriendsRequest.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sendUserId", MyUserId);
        criteria.andEqualTo("acceptUserId", friend.getId());
        FriendsRequest friendsRequests = friendsRequestMapper.selectOneByExample(example);

        if (friendsRequests == null) {
            // 2. 如果不是你的好友，并且好友记录没有添加，则新增好友请求记录
            FriendsRequest request = new FriendsRequest();
            request.setId(sid.nextShort());
            request.setSendUserId(MyUserId);
            request.setAcceptUserId(friend.getId());
            request.setRequestDateTime(new Date());
            friendsRequestMapper.insert(request);
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId) {
        return usersMapperCustom.queryFriendRequestList(acceptUserId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteFriendRequest(String sendUserId, String acceptUserId) {
        Example example = new Example(FriendsRequest.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("acceptUserId", acceptUserId);
        criteria.andEqualTo("sendUserId", sendUserId);
        friendsRequestMapper.deleteByExample(example);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void passFriendRequest(String sendUserId, String acceptUserId) {
        //保存
        saveFriends(sendUserId, acceptUserId);
        saveFriends(acceptUserId, sendUserId);
        //删除
        deleteFriendRequest(sendUserId, acceptUserId);
        //推送
        Channel sendChannel = UserChannelRel.get(sendUserId);
        if (sendChannel != null) {
            // 使用websocket主动推送消息到请求发起者，更新他的通讯录列表为最新
            DataContent dataContent = new DataContent();
            dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);

            sendChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContent)));
        }

    }

    //互相保存UserID
    private void saveFriends(String sendUserId, String acceptUserId) {
        MyFriends myFriends = new MyFriends();
        String recordId = sid.nextShort();
        myFriends.setId(recordId);
        myFriends.setMyFriendUserId(acceptUserId);
        myFriends.setMyUserId(sendUserId);
        myFriendsMapper.insert(myFriends);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<MyFriendsVO> queryMyFriends(String acceptUserId) {
        List<MyFriendsVO> myFirends = usersMapperCustom.queryMyFriends(acceptUserId);
        return myFirends;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String saveMsg(ChatMsg chatMsg) {
        com.lsy.pojo.ChatMsg msgDB = new com.lsy.pojo.ChatMsg();
        String msgId = sid.nextShort();
        msgDB.setId(msgId);
        msgDB.setAcceptUserId(chatMsg.getReceiverId());
        msgDB.setSendUserId(chatMsg.getSenderId());
        msgDB.setCreateTime(new Date());
        msgDB.setSignFlag(MsgSignFlagEnum.unsign.type);
        msgDB.setMsg(chatMsg.getMsg());

        chatMsgMapper.insert(msgDB);
        return msgId;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateMsgSigned(List<String> msgIdList) {
        usersMapperCustom.batchUpdateMsgSigned(msgIdList);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<com.lsy.pojo.ChatMsg> getUnReadMsgList(String acceptUserId) {
        Example chatExample = new Example(com.lsy.pojo.ChatMsg.class);
        Example.Criteria criteria = chatExample.createCriteria();
        criteria.andEqualTo("signFlag", 0);
        criteria.andEqualTo("acceptUserId", acceptUserId);

        List<com.lsy.pojo.ChatMsg> result = chatMsgMapper.selectByExample(chatExample);

        return result;
    }

}
