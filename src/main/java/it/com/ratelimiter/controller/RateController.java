package it.com.ratelimiter.controller;

import it.com.ratelimiter.annotation.RedisRateLimiter;
import it.com.ratelimiter.util.ServerResponseUtil;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/rate")
public class RateController {

    private static final Logger logger = LogManager.getLogger("bussniesslog");

    /**
     * 指定时间段内，限制次数
     * @return
     */
    @RequestMapping("/redisRateLimiter")
    @RedisRateLimiter(count = 3, time = 1)
    public Object redisRateLimiter() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getRemoteAddr();
        logger.info("访问接口redisRateLimiter, ip={}, time={}", ip, new Date());
        return ServerResponseUtil.success();
    }
}
