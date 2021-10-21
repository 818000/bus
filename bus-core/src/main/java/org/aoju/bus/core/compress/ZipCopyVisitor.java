/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org and other contributors.                      *
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
package org.aoju.bus.core.compress;

import org.aoju.bus.core.toolkit.StringKit;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Zip文件拷贝的FileVisitor实现，zip中追加文件，此类非线程安全
 * 此类在遍历源目录并复制过程中会自动创建目标目录中不存在的上级目录
 *
 * @author Kimi Liu
 * @version 6.3.0
 * @since JDK 1.8+
 */
public class ZipCopyVisitor extends SimpleFileVisitor<Path> {

    /**
     * 源Path，或基准路径，用于计算被拷贝文件的相对路径
     */
    private final Path source;
    private final FileSystem fileSystem;
    private final CopyOption[] copyOptions;

    /**
     * 构造
     *
     * @param source      源Path，或基准路径，用于计算被拷贝文件的相对路径
     * @param fileSystem  目标Zip文件
     * @param copyOptions 拷贝选项，如跳过已存在等
     */
    public ZipCopyVisitor(Path source, FileSystem fileSystem, CopyOption... copyOptions) {
        this.source = source;
        this.fileSystem = fileSystem;
        this.copyOptions = copyOptions;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        final Path targetDir = resolveTarget(dir);
        if (StringKit.isNotEmpty(targetDir.toString())) {
            try {
                Files.copy(dir, targetDir, copyOptions);
            } catch (FileAlreadyExistsException e) {
                if (false == Files.isDirectory(targetDir)) {
                    throw e;
                }
            }
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.copy(file, resolveTarget(file), copyOptions);
        return FileVisitResult.CONTINUE;
    }

    /**
     * 根据源文件或目录路径，拼接生成目标的文件或目录路径
     * 原理是首先截取源路径，得到相对路径，再和目标路径拼接
     *
     * <p>
     * 如：源路径是 /opt/test/，需要拷贝的文件是 /opt/test/a/a.txt，得到相对路径 a/a.txt
     * 目标路径是/home/，则得到最终目标路径是 /home/a/a.txt
     * </p>
     *
     * @param file 需要拷贝的文件或目录Path
     * @return 目标Path
     */
    private Path resolveTarget(Path file) {
        return fileSystem.getPath(source.relativize(file).toString());
    }

}
