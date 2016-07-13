package info.noconfuse.modules.cache.support;

import info.noconfuse.modules.cache.MemcachedCache;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.util.Assert;

import java.io.IOException;
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

    private Integer expireSeconds;

    @Override
    protected Collection<? extends Cache> loadCaches() {
        Assert.notNull(cacheNames, "cacheNames must be assigned");
        Assert.notEmpty(cacheNames, "cacheNames must be assigned");
        Assert.notNull(memcachedClient, "memcachedClient must be assigned");

        Set<MemcachedCache> cacheSet = new LinkedHashSet<>(cacheNames.size());
        if (expireSeconds != null) {
            for (String cacheName : cacheNames) {
                cacheSet.add(new MemcachedCache(cacheName, memcachedClient, expireSeconds));
            }
        } else {
            for (String cacheName : cacheNames) {
                cacheSet.add(new MemcachedCache(cacheName, memcachedClient));
            }
        }
        return cacheSet;
    }

    public void setCacheNames(Set<String> cacheNames) {
        this.cacheNames = cacheNames;
    }

    public void setMemcachedClient(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

    public void setMemcachedClientBuilder(MemcachedClientBuilder clientBuilder) throws IOException {
        this.memcachedClient = clientBuilder.build();
    }

    public void setExpireSeconds(Integer expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
}
