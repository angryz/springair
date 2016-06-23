package info.noconfuse.modules.cache;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

import java.util.concurrent.TimeoutException;

/**
 * An implementation of {@link org.springframework.cache.Cache} which is based on Memcached.
 * <br/>
 * Use killme2008/xmemcached as client of Memcached.
 *
 * @author Zheng Zhipeng
 */
public class MemcachedCache implements Cache {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemcachedCache.class);

    private String name;

    private MemcachedClient memcachedClient;

    public MemcachedCache(String name, MemcachedClient memcachedClient) {
        Assert.notNull(name, "Cache name must be assigned");
        Assert.notNull(memcachedClient, "MemcachedClient must be assigned");
        this.name = name;
        this.memcachedClient = memcachedClient;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return memcachedClient;
    }

    @Override
    public ValueWrapper get(Object key) {
        Object data = lookup(genKey(key));
        return data == null ? null : new SimpleValueWrapper(data);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return (T) lookup(genKey(key));
    }

    @Override
    public void put(Object key, Object value) {
        // evict if put a null value
        if (value == null) {
            evict(key);
            return;
        }

        save(genKey(key), value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        ValueWrapper old = get(key);
        if (old == null)
            put(key, value);
        return old;
    }

    @Override
    public void evict(Object key) {
        delete(genKey(key));
    }

    @Override
    public void clear() {
        deleteAll();
    }

    /**
     * 通过 xmemcached 客户端访问 memcached 服务并查询缓存的数据
     *
     * @param key memcached 中的 key
     * @return memcached 中对应 key 的 value 数据
     */
    protected Object lookup(String key) {
        Object data = null;
        try {
            data = memcachedClient.get(key);
        } catch (TimeoutException e) {
            LOGGER.error("Timeout when looking up cached data for key:{}", key, e);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted when looking up cached data for key:{}", key, e);
        } catch (MemcachedException e) {
            LOGGER.error("Failed when looking up cached data for key:{}", key, e);
        }
        return data;
    }

    /**
     * 通过 xmemcached 客户端访问 memcached 服务并保存数据
     */
    protected void save(String key, Object value) {
        try {
            memcachedClient.set(key, 0, value);
        } catch (TimeoutException e) {
            LOGGER.error("Timeout when caching data for key:{}", key, e);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted when caching data for key:{}", key, e);
        } catch (MemcachedException e) {
            LOGGER.error("Failed when caching data for key:{}", key, e);
        }
    }

    /**
     * 通过 xmemcached 客户端访问 memcached 服务并删除数据
     */
    protected void delete(String key) {
        try {
            memcachedClient.delete(key);
        } catch (TimeoutException e) {
            LOGGER.error("Timeout when deleting data for key:{}", key, e);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted when deleting data for key:{}", key, e);
        } catch (MemcachedException e) {
            LOGGER.error("Failed when deleting data for key:{}", key, e);
        }
    }

    /**
     * 通过 xmemcached 客户端访问 memcached 服务并清空数据
     */
    protected void deleteAll() {
        try {
            memcachedClient.flushAll();
        } catch (TimeoutException e) {
            LOGGER.error("Timeout when deleting all data", e);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted when deleting all data", e);
        } catch (MemcachedException e) {
            LOGGER.error("Failed when deleting all data", e);
        }
    }

    /**
     * 根据传入的对象生成 memcached 中的 key 值
     */
    protected String genKey(Object o) {
        String s = objectToString(o);
        if (s == null) return null;
        // add prefix to key so that keys won't duplicate easily
        return name + "_" + s;
    }

    /**
     * Memcached 的 key 需要是字符串, 此方法用于将对象转换为字符串.
     */
    protected String objectToString(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof String) {
            return removeBlankSpaces((String) o);
        } else {
            return removeBlankSpaces(o.toString());
        }
    }

    /**
     * remove all blank space in the String
     */
    protected String removeBlankSpaces(String s) {
        return s.replaceAll("\\s+", "");
    }

}
