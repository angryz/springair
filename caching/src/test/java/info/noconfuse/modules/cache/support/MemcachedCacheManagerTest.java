package info.noconfuse.modules.cache.support;

import info.noconfuse.modules.cache.MemcachedCache;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context-cache.xml"})
public class MemcachedCacheManagerTest {

    @Autowired
    private MemcachedClientBuilder clientBuilder;

    private MemcachedClient memcachedClient;

    @Autowired
    private MemcachedCacheManager memcachedCacheManager;

    @Before
    public void setUp() throws IOException {
        memcachedClient = clientBuilder.build();
    }

    /**
     * 实例化 MemcachedCacheManager 时没有指定 cacheNames, 应抛出 IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoadCachesWithoutCacheNames() {
        MemcachedCacheManager manager = new MemcachedCacheManager();
        manager.setMemcachedClient(memcachedClient);
        manager.loadCaches();
    }

    /**
     * 实例化 MemcachedCacheManager 时 cacheNames 是空集合, 应抛出 IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoadCachesWithEmptyCacheNames() {
        MemcachedCacheManager manager = new MemcachedCacheManager();
        manager.setCacheNames(Collections.EMPTY_SET);
        manager.setMemcachedClient(memcachedClient);
        manager.loadCaches();
    }

    /**
     * 实例化 MemcachedCacheManager 时没有指定 memcachedClient, 应抛出 IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoadCachesWithoutClient() {
        MemcachedCacheManager manager = new MemcachedCacheManager();
        manager.setCacheNames(Collections.singleton("hello"));
        manager.loadCaches();
    }

    /**
     * MemcachedCacheManager 实例可以创建 Cache 实例
     */
    @Test
    public void testLoadCaches() {
        MemcachedCacheManager manager = new MemcachedCacheManager();
        manager.setCacheNames(Collections.singleton("hello"));
        manager.setMemcachedClient(memcachedClient);
        Set<Cache> caches = (Set<Cache>) manager.loadCaches();
        Assert.assertTrue(caches.size() == 1);
        Cache cache = caches.iterator().next();
        Assert.assertTrue(cache instanceof MemcachedCache);
        Assert.assertTrue(cache.getName().equals("hello"));
    }

    /**
     * 通过 Spring 实例化 MemcachedCacheManager
     */
    @Test
    public void testSpringConfig() {
        Set<Cache> caches = (Set<Cache>) memcachedCacheManager.loadCaches();
        Assert.assertTrue(caches.size() == 2);
        Iterator<Cache> it = caches.iterator();
        while (it.hasNext()) {
            Cache cache = it.next();
            Assert.assertTrue(cache instanceof MemcachedCache);
            Assert.assertTrue(cache.getName().equals("foobar") || cache.getName().equals("hello"));
        }
    }
}
