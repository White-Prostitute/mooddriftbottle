package edu.scu.mooddriftbottlerebuild.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.scu.mooddriftbottlerebuild.entity.BottleEntity;
import edu.scu.mooddriftbottlerebuild.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-05-20 00:09:36
 */
public interface BottleService extends IService<BottleEntity> {

    PageUtils queryPage(Map<String, Object> params);

    BottleEntity getOnBottleChecked(String openid, Integer mode);

    BottleEntity getOneBottleUnChecked();

    void checkBottle(int bottle_id, int check, String replyStr);

    void addBottle(BottleEntity bottle, String token) throws Exception;

    List<BottleEntity> getMyBottleList(String openid);

    void removeBottle(int bottle_id, String openid);
}

