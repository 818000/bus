/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2022 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.core.io.watchers;

import org.aoju.bus.core.lang.Assert;
import org.aoju.bus.core.toolkit.ThreadKit;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Set;

/**
 * 延迟观察者
 * 使用此观察者通过定义一定的延迟时间,解决{@link WatchService}多个modify的问题
 * 在监听目录或文件时,如果这个文件有修改操作,会多次触发modify方法
 * 此类通过维护一个Set将短时间内相同文件多次modify的事件合并处理触发,从而避免以上问题
 *
 * @author Kimi Liu
 * @version 6.3.3
 * @since JDK 1.8+
 */
public class DelayWatcher implements Watcher {

    /**
     * Path集合 此集合用于去重在指定delay内多次触发的文件Path
     */
    private final Set<Path> eventSet = new HashSet<>();
    /**
     * 实际处理
     */
    private final Watcher watcher;
    /**
     * 延迟,单位毫秒
     */
    private final long delay;

    /**
     * 构造
     *
     * @param watcher 实际处理触发事件的监视器{@link Watcher},不可以是{@link DelayWatcher}
     * @param delay   延迟时间,单位毫秒
     */
    public DelayWatcher(Watcher watcher, long delay) {
        Assert.notNull(watcher);
        if (watcher instanceof DelayWatcher) {
            throw new IllegalArgumentException("Watcher must not be a DelayWatcher");
        }
        this.watcher = watcher;
        this.delay = delay;
    }

    @Override
    public void onModify(WatchEvent<?> event, Path currentPath) {
        if (this.delay < 1) {
            this.watcher.onModify(event, currentPath);
        } else {
            onDelayModify(event, currentPath);
        }
    }

    @Override
    public void onCreate(WatchEvent<?> event, Path currentPath) {
        watcher.onCreate(event, currentPath);
    }

    @Override
    public void onDelete(WatchEvent<?> event, Path currentPath) {
        watcher.onDelete(event, currentPath);
    }

    @Override
    public void onOverflow(WatchEvent<?> event, Path currentPath) {
        watcher.onOverflow(event, currentPath);
    }

    /**
     * 触发延迟修改
     *
     * @param event       事件
     * @param currentPath 事件发生的当前Path路径
     */
    private void onDelayModify(WatchEvent<?> event, Path currentPath) {
        Path eventPath = Paths.get(currentPath.toString(), event.context().toString());
        if (eventSet.contains(eventPath)) {
            //此事件已经被触发过,后续事件忽略,等待统一处理
            return;
        }

        //事件第一次触发,此时标记事件,并启动处理线程延迟处理,处理结束后会删除标记
        eventSet.add(eventPath);
        startHandleModifyThread(event, currentPath);
    }

    /**
     * 开启处理线程
     *
     * @param event       事件
     * @param currentPath 事件发生的当前Path路径
     */
    private void startHandleModifyThread(final WatchEvent<?> event, final Path currentPath) {
        ThreadKit.execute(() -> {
            ThreadKit.sleep(delay);
            eventSet.remove(Paths.get(currentPath.toString(), event.context().toString()));
            watcher.onModify(event, currentPath);
        });
    }

}
