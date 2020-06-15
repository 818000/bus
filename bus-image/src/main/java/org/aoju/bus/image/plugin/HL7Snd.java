/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
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
 ********************************************************************************/
package org.aoju.bus.image.plugin;

import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.toolkit.IoKit;
import org.aoju.bus.core.toolkit.StreamKit;
import org.aoju.bus.image.Device;
import org.aoju.bus.image.metric.Connection;
import org.aoju.bus.image.metric.internal.hl7.MLLPConnection;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * @author Kimi Liu
 * @version 5.9.9
 * @since JDK 1.8+
 */
public class HL7Snd extends Device {

    private final Connection conn = new Connection();
    private final Connection remote = new Connection();

    private Socket sock;
    private MLLPConnection mllp;

    public HL7Snd() {
        super("hl7snd");
        addConnection(conn);
    }

    public void open() throws IOException, InstrumentException, GeneralSecurityException {
        sock = conn.connect(remote);
        sock.setSoTimeout(conn.getResponseTimeout());
        mllp = new MLLPConnection(sock);
    }

    public void close() {
        conn.close(sock);
    }

    public void sendFiles(List<String> pathnames) throws IOException {
        for (String pathname : pathnames)
            if (pathname.equals(Symbol.HYPHEN))
                send(readFromStdIn());
            else {
                Path path = Paths.get(pathname);
                if (Files.isDirectory(path)) {
                    Files.walkFileTree(path, new HL7Send());
                } else
                    send(readFromFile(path));
            }
    }

    private void send(byte[] data) throws IOException {
        mllp.writeMessage(data);
        if (mllp.readMessage() == null)
            throw new IOException("Connection closed by receiver");
    }

    private byte[] readFromStdIn() throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(FileDescriptor.in);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            StreamKit.copy(in, buf);
            return buf.toByteArray();
        } finally {
            IoKit.close(in);
        }
    }

    private byte[] readFromFile(Path path) throws IOException {
        FileInputStream in = null;
        try {
            File f = path.toFile();
            in = new FileInputStream(f);
            byte[] b = new byte[(int) f.length()];
            StreamKit.readFully(in, b, 0, b.length);
            return b;
        } finally {
            IoKit.close(in);
        }
    }

    class HL7Send extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            send(readFromFile(file));
            return FileVisitResult.CONTINUE;
        }
    }

}
