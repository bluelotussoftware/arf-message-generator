/*
 * Copyright 2016 John Yeary <jyeary@bluelotussoftware.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bluelotussoftware.mail.arf;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;

/**
 * A {@link DataContentHandler} implementation for
 * <code>message/feedback-report</code>.
 *
 * @author John Yeary <jyeary@bluelotussoftware.com>
 * @version 1.0
 */
public class message_feedback_report implements DataContentHandler {

    private static final ActivationDataFlavor ACTIVATION_DATA_FLAVOR
            = new ActivationDataFlavor(AbuseFormatReport.class,
                    "message/feedback-report", "Feedback Report");

    /**
     * {@inheritDoc}
     * <p>
     * This returns the {@link ActivationDataFlavor} associated with
     * <code>message/feedback-report</code>.
     * </p>
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{ACTIVATION_DATA_FLAVOR};
    }

    /**
     * {@inheritDoc}
     * <p>
     * This returns a {@link AbuseFormatReport} object from the provided
     * {@link DataSource}.
     * </p>
     */
    @Override
    public Object getContent(DataSource ds) throws IOException {
        try {
            return new AbuseFormatReport(ds.getInputStream());
        } catch (MessagingException me) {
            throw new IOException(
                    "Exception creating AbuseReportFormat in "
                    + "message/feedback-report DataContentHandler: "
                    + me.toString());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method delegates the writing of the object to the object itself by
     * calling {@link AbuseFormatReport#writeTo(java.io.OutputStream)}.
     * </p>
     */
    @Override
    public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
        if (obj instanceof AbuseFormatReport) {
            AbuseFormatReport arf = (AbuseFormatReport) obj;
            arf.writeTo(os);
        } else {
            throw new IOException("unsupported object.");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation calls
     * {@link message_feedback_report#getContent(javax.activation.DataSource)}
     * and returns the result if the {@link DataFlavor} is
     * <code>"message/feedback-report"</code>
     * </p>
     */
    @Override
    public Object getTransferData(DataFlavor df, DataSource ds) throws UnsupportedFlavorException, IOException {
        if (ACTIVATION_DATA_FLAVOR.equals(df)) {
            return getContent(ds);
        } else {
            return null;
        }
    }

}
