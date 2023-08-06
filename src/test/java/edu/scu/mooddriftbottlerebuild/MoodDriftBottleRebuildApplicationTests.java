package edu.scu.mooddriftbottlerebuild;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.scu.mooddriftbottlerebuild.dao.BottleDao;
import edu.scu.mooddriftbottlerebuild.entity.BottleEntity;
import edu.scu.mooddriftbottlerebuild.service.BottleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

@SpringBootTest
class MoodDriftBottleRebuildApplicationTests {

    @Autowired
    BottleService service;

    @Resource
    RedisTemplate<String, String> template;

    @Test
    void contextLoads() {
        service.removeBottle(1, "dwada");
    }

}
