local key = KEYS[1]     -- 限流key的名字，这里使用 接口名 + IP 地址的组合
local limit = tonumber(KEYS[2])   -- 可访问的次数，tonumber() 是 Lua 的内置函数，用来将输入转为数值
local time = tonumber(KEYS[3])    -- 时间段
local current = redis.call('GET', key)
if current == false then		-- key 不存在
   redis.call('SET', key, 1)	-- 设置 key 并设置过期时间
   redis.call('EXPIRE', key, time)
   return '1'
else
   local num_current = tonumber(current)
   if num_current + 1 > limit then	-- 超过限制
       return '0'
   else
       redis.call('INCRBY', key, 1)	-- 次数+1
       return '1'
   end
end