package info.noconfuse.modules.cache.support;

import info.noconfuse.modules.cache.MemcachedCache;
import net.rubyeye.xmemcached.MemcachedClient;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Simple cache manager working against a Memcached service.
 *
 * @author Zheng Zhipeng
 */
public class MemcachedCacheManager extends AbstractCacheManager {

    private Set<String> cacheNames;

    private MemcachedClient memcachedClient;

    @Override
    protected Collection<? extends Cache> loadCaches() {
        Assert.notNull(cacheNames, "cacheNames must be assigned");
        Assert.notEmpty(cacheNames, "cacheNames must be assigned");
        Assert.notNull(memcachedClient, "memcachedClient must be assigned");

        Set<MemcachedCache> cacheSet = new LinkedHashSet<>(cacheNames.size());
        for (String cacheName : cacheNames) {
            cacheSet.add(new MemcachedCache(cacheName, memcachedClient));
        }
        return cacheSet;
    }

    public void setCacheNames(Set<String> cacheNames) {
        this.cacheNames = cacheNames;
    }

    public void setMemcachedClient(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }
}
