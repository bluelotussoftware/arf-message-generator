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

import com.sun.mail.dsn.Report;
import com.sun.mail.util.LineOutputStream;
import com.sun.mail.util.MailLogger;
import com.sun.mail.util.PropUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;

/**
 *
 * @author John Yeary <jyeary@bluelotussoftware.com>
 * @version 1.0
 */
public class AbuseFormatReport extends Report {

    private static MailLogger logger = new MailLogger(
            AbuseFormatReport.class,
            "DEBUG ARF",
            PropUtil.getBooleanSystemProperty("mail.dsn.debug", false),
            System.out);

    public static final String FEEDBACK_TYPE = "Feedback-Type";
    private static final String USER_AGENT = "User-Agent";
    protected FeedbackType feedbackType = FeedbackType.abuse;
    protected InternetHeaders arfHeaders = new InternetHeaders();

    public AbuseFormatReport() {
        super("feedback-report");
        logger.fine("Setting default Feedback-Type: abuse");
    }

    public AbuseFormatReport(final FeedbackType feedbackType) {
        super("feedback-report");
        this.feedbackType = feedbackType;
        logger.fine("Setting Feedback-Type: " + feedbackType.name());
    }

    public AbuseFormatReport(final InputStream is) throws MessagingException {
        super("feedback-report");
        logger.fine("Attempting to load InternetHeaders from InputStream.");
        InternetHeaders temp = new InternetHeaders(is);

        logger.fine("Checking to see if a Feedback-Type header is set already.");
        // Check to see if an Feedback-Type: header is already set. Odd use case.
        String[] result = temp.getHeader(FEEDBACK_TYPE);
        if (result.length > 0) {
            feedbackType = FeedbackType.valueOf(result[0]);
            arfHeaders.setHeader(FEEDBACK_TYPE, feedbackType.name());
        }
    }

    /**
     * Indicates the type of feedback which is set.
     *
     * @return one of the enumerated {@link FeedbackType} objects.
     */
    public FeedbackType getFeedbackType() {
        return feedbackType;
    }

    /**
     * Sets the feedback type to the value provided.
     *
     * @param feedbackType one of the RFC-5965 feedback types.
     */
    public void setFeedbackType(FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    public void writeTo(OutputStream os) throws IOException {
        LineOutputStream lineOutputStream;

        if (os instanceof LineOutputStream) {
            lineOutputStream = (LineOutputStream) os;
        } else {
            lineOutputStream = new LineOutputStream(os);
        }

        arfHeaders.setHeader(FEEDBACK_TYPE, feedbackType.name());
        arfHeaders.setHeader(USER_AGENT, "arf-message-generator/1.0");
        arfHeaders.setHeader("Version", "1");
        writeHeaders(arfHeaders, lineOutputStream);
        lineOutputStream.writeln();
    }

    protected static void writeHeaders(InternetHeaders headers, LineOutputStream lineOutputStream)
            throws IOException {
        Enumeration e = headers.getAllHeaderLines();
        while (e.hasMoreElements()) {
            String element = (String) e.nextElement();
            if (e.hasMoreElements()) {
                lineOutputStream.writeln(element);
            } else {
                lineOutputStream.write(element.getBytes());
            }
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This will return a result similar to the example below with only one
     * changing variable: Feedback-Type:.
     * </p>
     * <pre>
     * Feedback-Type: abuse
     * User-Agent: arf-message-generator/1.0
     * Version: 1
     * </pre>
     */
    @Override
    public String toString() {
        return "Feedback-Type: " + feedbackType.name() + "\n"
                + "User-Agent: arf-message-generator/1.0\n"
                + "Version: 1\n";
    }

}
