package com.lsy.mapper;

import java.util.List;

import com.lsy.pojo.Users;
import com.lsy.pojo.vo.FriendRequestVO;
import com.lsy.pojo.vo.MyFriendsVO;
import com.lsy.utils.MyMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersMapperCustom extends MyMapper<Users> {

    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    public List<MyFriendsVO> queryMyFriends(String userId);

    public void batchUpdateMsgSigned(List<String> msgIdList);

}