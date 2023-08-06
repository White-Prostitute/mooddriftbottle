package edu.scu.mooddriftbottlerebuild.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.scu.mooddriftbottlerebuild.utils.PageUtils;
import edu.scu.mooddriftbottlerebuild.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import edu.scu.mooddriftbottlerebuild.entity.BottleEntity;
import edu.scu.mooddriftbottlerebuild.service.BottleService;




/**
 * 
 *
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-05-20 00:09:36
 */
@RestController
@RequestMapping("/bottle")
public class BottleController {
    @Autowired
    private BottleService bottleService;


    /**
     * 获取一个已经完成审核随机的漂流瓶
     */
    @GetMapping("/get_random_bottle_checked")
    R getRandomBottleChecked(String openid, Integer mode){
        if(mode == null)mode = 2;
        return R.ok().put("bottle", bottleService.getOnBottleChecked(openid, mode));
    }

    /**
     * 根据BottleId获取一个瓶子
     */
    @GetMapping("/get_one_bottle")
    R getOneBottleById(int bottle_id){
        return R.ok().put("bottle", bottleService.getById(bottle_id));
    }

    /**
     * 获取一个还未审核的瓶子
     */
    @GetMapping("/get_random_bottle_unchecked")
    R getRandomBottleUnchecked(){
        return R.ok().put("bottle", bottleService.getOneBottleUnChecked());
    }

    /**
     * 完成一个瓶子的审核
     * @param bottle_id 审核瓶子的id
     * @param check 审核结果 1 -> 通过 ; 2 -> 驳回
     * @param replyStr 审核员可以直接回复瓶子
     * @return 成功与否 success / exception
     */
    @PutMapping("/check")
    R checkBottle(int bottle_id, int check, String replyStr){
        try{
            bottleService.checkBottle(bottle_id, check, replyStr);
        }catch (Exception e){
            return R.error().put("exception", e.toString());
        }
        return Objects.requireNonNull(R.ok().put("result", check)).put("bottle_id", bottle_id);
    }

    /**
     * 扔出一个瓶子
     * @param bottle 瓶子封装类
     * @param token 操作token,用于保证幂等性,放在请求头
     * @return 操作成功与否 success / exception
     */
    @PostMapping("/throw_bottle")
    R throwBottle(@RequestBody BottleEntity bottle, String token){
        try{
            bottleService.addBottle(bottle, token);
        }catch (Exception e){
            return R.error().put("exception", e.toString());
        }
        return R.ok();
    }

    /**
     *获取我扔出的所有瓶子
     * @param openid 用户id
     * @return 瓶子列表
     */
    @GetMapping("/get_my_bottle_list")
    R getMyBottleList(String openid){
        List<BottleEntity> list = bottleService.getMyBottleList(openid);
        return R.ok().put("data", list);
    }


    /**
     * 逻辑删除一个瓶子
     * @param bottle_id 瓶子id
     * @param openid 用户id
     * @return 操作成功与否 success / exception
     */
    @DeleteMapping("/remove_bottle")
    R removeBottle(int bottle_id, String openid){
        try{
            bottleService.removeBottle(bottle_id, openid);
        }catch (Exception e){
            return R.error().put("exception", e.toString());
        }
        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = bottleService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{bottleId}")
    public R info(@PathVariable("bottleId") Integer bottleId){
		BottleEntity bottle = bottleService.getById(bottleId);

        return R.ok().put("bottle", bottle);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody BottleEntity bottle){
		bottleService.save(bottle);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody BottleEntity bottle){
		bottleService.updateById(bottle);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] bottleIds){
		bottleService.removeByIds(Arrays.asList(bottleIds));

        return R.ok();
    }

}
