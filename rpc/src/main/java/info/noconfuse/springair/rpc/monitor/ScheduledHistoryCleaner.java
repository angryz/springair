/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Zheng Zhipeng
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package info.noconfuse.springair.rpc.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Scheduled history data cleaning task.
 *
 * @author Zheng Zhipeng
 */
@Component
public class ScheduledHistoryCleaner {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledHistoryCleaner.class);

    protected static final int MAX_HISTORY_LIMIT_PER_SERV = 5;

    private Queue<History> abandonedNodes;

    @Autowired
    private HistoryRepository historyRepository;

    /**
     * Delay 1 hour between each cleanning task.
     */
    @Scheduled(fixedDelay = 5 * 1000)
    public void clean() {
        if (abandonedNodes != null && !abandonedNodes.isEmpty()) {
            long start = System.currentTimeMillis();
            LOG.info("Start history cleaning ...");
            for (int i = 0; i < abandonedNodes.size(); i++) {
                try {
                    historyRepository.remove(abandonedNodes.poll());
                } catch (Exception e) {
                }
            }
            LOG.info("Finish history cleaning, cost {}ms", System.currentTimeMillis() - start);
        }
    }

    /**
     * Add abandoned nodes into queue, use {@code offer()} and ignore failure.
     */
    public void abandon(History history) {
        if (abandonedNodes == null)
            abandonedNodes = new LinkedBlockingDeque<>(50);
        abandonedNodes.offer(history);
    }
}
