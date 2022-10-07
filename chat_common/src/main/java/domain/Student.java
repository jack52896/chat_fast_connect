package domain;

import lombok.Data;

/**
 * @author linyu.dai
 * @project chat_fast_connect
 * @description
 * @creat 2022/10/7 15:46:20
 */
@Data
public class Student {

    private String name;

    private Integer classId;

    private User user;
}
