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
@TableName("reply")
public class ReplyEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private int replyId;
	/**
	 * 
	 */
	private String content;
	/**
	 * 
	 */
	private String createTime;
	/**
	 * 
	 */
	private Integer sessionId;
	/**
	 * 
	 */
	private String userId;

}
