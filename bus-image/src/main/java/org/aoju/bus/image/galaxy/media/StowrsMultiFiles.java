/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
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
package org.aoju.bus.image.galaxy.media;

import org.aoju.bus.image.Builder;
import org.aoju.bus.image.Status;
import org.aoju.bus.image.Tag;
import org.aoju.bus.image.galaxy.data.Attributes;
import org.aoju.bus.image.galaxy.data.Sequence;
import org.aoju.bus.image.galaxy.data.VR;
import org.aoju.bus.image.metric.Progress;
import org.aoju.bus.logger.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Kimi Liu
 * @version 5.8.9
 * @since JDK 1.8+
 */
public class StowrsMultiFiles extends AbstractStowrs {

    public StowrsMultiFiles(String requestURL, String contentType) {
        this(requestURL, contentType, null, null);
    }

    public StowrsMultiFiles(String requestURL, String contentType, String agentName, Map<String, String> headers) {
        super(requestURL, contentType, agentName, headers);
    }

    public Status uploadDicom(List<String> filesOrFolders, boolean recursive) throws IOException {
        HttpURLConnection httpPost = buildConnection();
        Status state = new Status(new Progress());
        String message;
        int nbFile = 0;
        try (DataOutputStream out = new DataOutputStream(httpPost.getOutputStream())) {
            for (String entry : filesOrFolders) {
                File file = new File(entry);
                if (file.isDirectory()) {
                    List<File> fileList = new ArrayList<>();
                    Builder.getAllFilesInDirectory(file, fileList, recursive);
                    for (File f : fileList) {
                        uploadFile(f, out);
                        nbFile++;
                    }
                } else {
                    uploadFile(file, out);
                    nbFile++;
                }
            }
            Attributes error = writeEndMarkers(httpPost, out);
            if (error == null) {
                state.setStatus(org.aoju.bus.image.Status.Success);
                message = "all the files has been tranfered";
            } else {
                message = "one or more files has not been tranfered";
                state.setStatus(org.aoju.bus.image.Status.OneOrMoreFailures);
                Progress p = state.getProgress();
                if (p != null) {
                    Sequence seq = error.getSequence(Tag.FailedSOPSequence);
                    if (seq != null && !seq.isEmpty()) {
                        Attributes cmd = Optional.ofNullable(p.getAttributes()).orElseGet(Attributes::new);
                        cmd.setInt(Tag.Status, VR.US, org.aoju.bus.image.Status.OneOrMoreFailures);
                        cmd.setInt(Tag.NumberOfCompletedSuboperations, VR.US, nbFile);
                        cmd.setInt(Tag.NumberOfFailedSuboperations, VR.US, seq.size());
                        cmd.setInt(Tag.NumberOfWarningSuboperations, VR.US, 0);
                        cmd.setInt(Tag.NumberOfRemainingSuboperations, VR.US, 0);
                        p.setAttributes(cmd);
                        message = seq.stream().map(s -> s.getString(Tag.ReferencedSOPInstanceUID, "Unknown SopUID")
                                + " -> " + s.getString(Tag.FailureReason)).collect(Collectors.joining(","));
                        return Status.build(state, null,
                                new RuntimeException("Failed instances: " + message));
                    }
                }
            }
        } catch (Exception e) {
            state.setStatus(org.aoju.bus.image.Status.UnableToProcess);
            Logger.error("STOWRS: error when posting data", e);
            return Status.build(state, "STOWRS: error when posting data", e);
        } finally {
            removeConnection(httpPost);
        }
        return Status.build(state, message, null);
    }

    private void uploadFile(File file, DataOutputStream out) throws IOException {
        writeContentMarkers(out);

        // write dicom binary file
        Files.copy(file.toPath(), out);
    }

}
