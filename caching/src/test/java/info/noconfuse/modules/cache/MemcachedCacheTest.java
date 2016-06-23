package info.noconfuse.modules.cache;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * Created by zzp on 6/23/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context-cache.xml"})
public class MemcachedCacheTest {

    @Autowired
    private MemcachedClientBuilder clientBuilder;

    private MemcachedClient memcachedClient;
    private MemcachedCache memcachedCache;
    private String key;

    @Before
    public void setUp() throws IOException {
        memcachedClient = clientBuilder.build();
        memcachedCache = new MemcachedCache("test", memcachedClient);
        key = Long.toString(System.currentTimeMillis());
    }

    @After
    public void tearDown() throws Exception {
        memcachedClient.flushAll();
    }

    @Test
    public void testPut() throws Exception {
        memcachedCache.put(key, this.getClass().getName());
        String target = memcachedClient.get("test_" + key);
        Assert.assertEquals(MemcachedCacheTest.class.getName(), target);
    }

    @Test
    public void testPutIfAbsent() throws Exception {
        Cache.ValueWrapper vw = memcachedCache.putIfAbsent(key, this.getClass().getName());
        Assert.assertNull(vw);

        vw = memcachedCache.putIfAbsent(key, this.getClass().getName() + "_new");
        Assert.assertEquals(MemcachedCacheTest.class.getName(), vw.get());
        String target = memcachedClient.get("test_" + key);
        Assert.assertNotEquals(MemcachedCacheTest.class.getName() + "_new", target);

        memcachedCache.putIfAbsent(key + "_new", this.getClass().getName() + "_new");
        target = memcachedClient.get("test_" + key + "_new");
        Assert.assertEquals(MemcachedCacheTest.class.getName() + "_new", target);
    }

    @Test
    public void testGet() throws Exception {
        memcachedClient.set("test_" + key, 0, this.getClass().getName());
        Cache.ValueWrapper vw = memcachedCache.get(key);
        Assert.assertEquals(MemcachedCacheTest.class.getName(), vw.get());
    }

    @Test
    public void testGetWithType() throws Exception {
        memcachedClient.set("test_" + key, 0, this.getClass().getName());
        String value = memcachedCache.get(key, String.class);
        Assert.assertEquals(MemcachedCacheTest.class.getName(), value);
    }

    @Test
    public void testEvict() throws Exception {
        memcachedClient.set("test_" + key, 0, this.getClass().getName());
        memcachedCache.evict(key);
        String target = memcachedClient.get("test_" + key);
        Assert.assertNull(target);
    }

    @Test
    public void testClear() throws Exception {
        memcachedClient.set("test_" + key, 0, this.getClass().getName());
        memcachedClient.set("test_" + key + "_new", 0, this.getClass().getName());
        memcachedCache.clear();
        String target = memcachedClient.get("test_" + key);
        Assert.assertNull(target);
        target = memcachedClient.get("test_" + key + "_new");
        Assert.assertNull(target);
    }
}
