package it.com.ratelimiter.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RedisLuaUtil {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger logger = LogManager.getLogger("bussniesslog");

    /**
     * 执行 lua 脚本
     *
     * @param luaFileName lua文件
     * @param keyList 参数
     * @return 执行结果
     */
    public String runLuaScript(String luaFileName, List<String> keyList) {
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/" + luaFileName)));
        redisScript.setResultType(String.class);
        String result = "";
        String args = "none";
        try {
            logger.info("开始执行 lua 脚本 {}", luaFileName);
            result = stringRedisTemplate.execute(redisScript, keyList, args);
        } catch (Exception e) {
            logger.error("执行 lua 脚本 {} 发生异常: {}", luaFileName, e);
        }
        return result;
    }
}
