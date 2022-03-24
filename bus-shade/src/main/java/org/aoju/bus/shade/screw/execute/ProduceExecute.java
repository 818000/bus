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
package org.aoju.bus.shade.screw.execute;

import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.logger.Logger;
import org.aoju.bus.shade.screw.Config;
import org.aoju.bus.shade.screw.engine.EngineFactory;
import org.aoju.bus.shade.screw.engine.TemplateEngine;
import org.aoju.bus.shade.screw.metadata.DataSchema;
import org.aoju.bus.shade.screw.process.DataModelProcess;

/**
 * 文档生成
 *
 * @author Kimi Liu
 * @version 6.5.0
 * @since Java 17+
 */
public class ProduceExecute extends AbstractExecute {

    public ProduceExecute(Config config) {
        super(config);
    }

    @Override
    public void execute() {
        try {
            long start = System.currentTimeMillis();
            //处理数据
            DataSchema dataModel = new DataModelProcess(config).process();
            //产生文档
            TemplateEngine produce = new EngineFactory(config.getEngineConfig()).newInstance();
            produce.produce(dataModel, getDocName(dataModel.getDatabase()));
            Logger.debug("database document generation complete time consuming:{}ms",
                    System.currentTimeMillis() - start);
        } catch (Exception e) {
            throw new InstrumentException(e);
        }
    }

}
