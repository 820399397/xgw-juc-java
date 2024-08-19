package cn.xuguowen.juc.principle.application;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: User
 * Package: cn.xuguowen.juc.principle.application
 * Description:
 *
 * @Author 徐国文
 * @Create 2024/8/12 16:59
 * @Version 1.0
 */
@Data
public class User {
    private Long id;
    private String userId;
    private String userNickname;
    private String userHead;
    private String userPassword;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
