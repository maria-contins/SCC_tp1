package scc.data;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.*;
import redis.clients.jedis.JedisPoolConfig;

public class CacheLayer {
        private static final String RedisHostname = System.getenv("REDIS_URL");

        private static final String RedisKey = System.getenv("REDIS_KEY");


        private static final boolean CACHE_ON = System.getenv("CACHE_ON").equals("1");

        public enum CacheType {
            HOUSE, COOKIE, HOUSES_DISCOUNTED, HOUSES_LOCATION, HOUSE_USER, USER, QUESTION_LIST, RENTALS, HOUSES
        }

    public static final String HOUSE_CACHE = "house:";
    public static final String COOKIE_CACHE = "cookie:";
    public static final String RENTALS_CACHE = "rentalsList:";
    public static final String USER_CACHE = "user:";
    public static final String QUESTION_LIST_CACHE = "questionList:";
    public static final String HOUSE_DISCOUNTED_LIST_CACHE = "houseDiscountedList:";
    public static final String HOUSE_LOCATION_LIST_CACHE = "houseLocationList:";
    public static final String HOUSE_USER_LIST_CACHE = "houseUserList:";
    public static final String HOUSES_CACHE = "housesList:";

        private static JedisPool instance;

        private ObjectMapper mapper = new ObjectMapper();



    public CacheLayer() {

    }

    public synchronized static JedisPool getCachePool() {
            if( instance != null)
                return instance;
            final JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(128);
            poolConfig.setMaxIdle(128);
            poolConfig.setMinIdle(16);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);
            poolConfig.setNumTestsPerEvictionRun(3);
            poolConfig.setBlockWhenExhausted(true);
            instance = new JedisPool(poolConfig, RedisHostname, 6380, 1000, RedisKey, true);
            return instance;

        }


    private String getCacheKeyPrefix(CacheType ct) {
        return switch (ct) {
            case HOUSE -> HOUSE_CACHE;
            case COOKIE -> COOKIE_CACHE;
            case USER -> USER_CACHE;
            case QUESTION_LIST -> QUESTION_LIST_CACHE;
            case HOUSES_DISCOUNTED -> HOUSE_DISCOUNTED_LIST_CACHE;
            case HOUSES_LOCATION -> HOUSE_LOCATION_LIST_CACHE;
            case HOUSE_USER -> HOUSE_USER_LIST_CACHE;
            case RENTALS -> RENTALS_CACHE;
            case HOUSES -> HOUSES_CACHE;
            default -> "";
        };
    }


        public <T> void addCache(CacheType ct, String key, T value) {
            if (!(value instanceof Session) && !CACHE_ON) {
                return;
            }
            try (Jedis jedis = getCachePool().getResource()) {
                jedis.set(getCacheKeyPrefix(ct) + key, mapper.writeValueAsString(value));
                if(ct == CacheType.COOKIE){
                    jedis.expire(getCacheKeyPrefix(ct) + key, 3600);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public <T> T readCache(CacheType ct, String key, Class<T> type) {
            if (type != Session.class && !CACHE_ON) {
                return null;
            }
            try (Jedis jedis = getCachePool().getResource()) {
                String value = jedis.get(getCacheKeyPrefix(ct) + key);
                if (value == null) {
                    return null;
                }
                return mapper.readValue(value, type);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        /**public  String readCache(CacheType ct, String key) {

            try (Jedis jedis = getCachePool().getResource()) {
                String value = jedis.get(getCacheKeyPrefix(ct) + key);
                if (value == null) {
                    return "null";
                }
                return value;
            } catch (Exception e) {
                return e.getMessage();
            }
        }*/

    public void removeCache(CacheType ct, String key) {
        if (!CACHE_ON) {
            return;
        }
        try (Jedis jedis = getCachePool().getResource()) {
            jedis.del(getCacheKeyPrefix(ct) + key);
        }
    }





}





