package edu.scu.mooddriftbottlerebuild.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
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
@TableName("bottle")
public class BottleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 瓶子id
	 */
	@TableId
	private int bottleId;
	/**
	 * 用户id
	 */
	private String userId;
	/**
	 * 漂流瓶内容
	 */
	private String content;
	/**
	 * 审核情况 0:未审核; 1:审核通过; 2.审核不通过
	 */
	private int checked;
	/**
	 * 生成时间
	 */
	private Date createTime;
	/**
	 * 漂流瓶类型 -> 好心情 : 坏心情
	 */
	private Integer type;

	/**
	 * 逻辑删除位
	 */
	@TableLogic(delval = "1")
	private int logic;

	/**
	 * 是否有新的评论
	 */
	@TableField(exist = false)
	private boolean hasNewReply;

}
