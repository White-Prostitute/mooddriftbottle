package edu.scu.mooddriftbottlerebuild.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * 
 * 
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-05-20 00:09:36
 */
@Data
@TableName("session")
public class SessionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 会话id
	 */
	@TableId
	private int sessionId;
	/**
	 * 该会话的第一句话
	 */
	private String firstSentence;
	/**
	 * 创建时间
	 */
	private String sessionCreateTime;
	/**
	 * 审核情况
	 */
	private int checked;
	/**
	 * 所属的瓶子id
	 */
	private Integer bottleId;
	/**
	 * 逻辑删除位
	 */
	@TableLogic(delval = "1")
	private Integer logic;
	/**
	 * 用户Id
	 */
	private String UserId;
}
