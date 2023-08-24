package edu.scu.mooddriftbottlerebuild.controller;

import edu.scu.mooddriftbottlerebuild.controller.response.StatisticResponse;
import edu.scu.mooddriftbottlerebuild.service.impl.MonitorService;
import edu.scu.mooddriftbottlerebuild.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    MonitorService monitorService;

    @GetMapping("/statistic")
    public R getStatisticalData(){
        StatisticResponse statisticData = monitorService.getStatisticData();
        return R.ok().put("data", statisticData);
    }

}
