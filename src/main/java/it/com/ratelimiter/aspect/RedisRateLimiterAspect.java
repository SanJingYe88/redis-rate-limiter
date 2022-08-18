package it.com.ratelimiter.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.com.ratelimiter.annotation.RedisRateLimiter;
import it.com.ratelimiter.constant.ResponseCode;
import it.com.ratelimiter.util.RedisLuaUtil;
import it.com.ratelimiter.util.ServerResponseUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Aspect
public class RedisRateLimiterAspect {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String RATE_LIMIT_KEY_PREFIX = "req_limit_";

    @Resource
    private RedisLuaUtil redisLuaUtil;

    @Pointcut("@annotation(it.com.ratelimiter.annotation.RedisRateLimiter)")
    private void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object requestLimit(ProceedingJoinPoint joinPoint) {
        try {
            Signature signature = joinPoint.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            //获取目标方法
            Method targetMethod = methodSignature.getMethod();
            String method_name = targetMethod.getName();
            if (targetMethod.isAnnotationPresent(RedisRateLimiter.class)) {
                //获取目标方法的注解
                RedisRateLimiter limit = targetMethod.getAnnotation(RedisRateLimiter.class);
                String key = RATE_LIMIT_KEY_PREFIX.concat(method_name);
                // 判断是否限制 IP
                boolean ipLimit = limit.ipLimit();
                if (ipLimit) {
                    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                    String ip = request.getRemoteAddr();
                    key = key.concat(method_name).concat("_").concat(ip);
                }
                boolean checkResult = checkByRedis(limit, key);
                if (checkResult) {
                    return joinPoint.proceed();
                } else {
                    return objectMapper.writeValueAsString(ServerResponseUtil.error(ResponseCode.ACCESS_LIMIT.getMsg()));
                }
            } else {
                return joinPoint.proceed();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean checkByRedis(RedisRateLimiter limit, String key) {
        List<String> keyList = new ArrayList<>();
        keyList.add(key);
        keyList.add(String.valueOf(limit.count()));
        keyList.add(String.valueOf(limit.time()));
        String res = redisLuaUtil.runLuaScript("redisRateLimiter.lua", keyList);
        return res.equals("1");
    }
}