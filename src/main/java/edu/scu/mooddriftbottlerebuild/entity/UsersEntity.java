package edu.scu.mooddriftbottlerebuild.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-05-20 00:09:36
 */
@Data
@TableName("users")
public class UsersEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private String openId;
	/**
	 * 
	 */
	private String nickName;
	/**
	 * 
	 */
	private Integer salvageNum;
	/**
	 * 
	 */
	private Integer violation;

}
