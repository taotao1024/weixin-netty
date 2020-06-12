package com.lsy.pojo.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 返回给后端的数据
 */
@Data
@ToString
public class UsersBO {
    @ApiModelProperty(name = "用户ID")
    private String userId;
    @ApiModelProperty(name = "用户头像")
    private String faceData;
    @ApiModelProperty(name = "昵称")
    private String nickname;
}
