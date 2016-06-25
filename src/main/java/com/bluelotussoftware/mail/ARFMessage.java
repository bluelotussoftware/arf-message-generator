/*
 * Copyright 2016 Blue Lotus Software, LLC.
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
package com.bluelotussoftware.mail;

import com.bluelotussoftware.mail.arf.AbuseFormatReport;
import com.sun.mail.dsn.MultipartReport;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author John Yeary <jyeary@bluelotussoftware.com>
 * @version 1.0
 */
public class ARFMessage {

    private final Session session;
    private final String from;
    private final String to;
    private final String subject;
    private final MimeMessage originalMessage;
    /**
     * "Source-IP" contains an IPv4 or IPv6 address of the MTA from which the
     * original message was received. Addresses MUST be formatted as per section
     * 4.1.3 of [SMTP].
     */
    private final String sourceIP;
    /**
     * "Arrival-Date" indicates the date and time at which the original message
     * was received by the Mail Transfer Agent (MTA) of the generating ADMD
     * (Administrative Management Domain). This field MUST be formatted as per
     * section 3.3 of [MAIL]. The date should be in a form such as: Thu, 8 Mar
     * 2005 14:00:00 EDT.
     */
    private final String arrivalDate;

    public ARFMessage(final Session session, final String from, final String to, final String subject,
            final String sourceIP, final String arrivalDate, final MimeMessage originalMessage) {
        this.session = session;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.sourceIP = sourceIP;
        this.arrivalDate = arrivalDate;
        this.originalMessage = originalMessage;

    }

    public MimeMessage generateARF() throws MessagingException {
        MimeMessage mimeMessage = new MimeMessage(session);

        //RFC822 From:
        mimeMessage.setFrom(new InternetAddress(from));

        //RFC822 To:
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        //RFC822 Subject:
        mimeMessage.setSubject(subject);

        MultipartReport report = new MultipartReport();
        // Part I : Human Readable
        report.setText(getHumanReadableMessage());

        // Part II : Abuse Format Report
        AbuseFormatReport afr = new AbuseFormatReport();
        report.setReport(afr);

        // Part III : Original Message
        report.setReturnedMessage(originalMessage);

        mimeMessage.setContent(report);

        return mimeMessage;
    }

    private String getHumanReadableMessage() {
        return "This is an email abuse report for an email message received from IP\n"
                + sourceIP + " on " + arrivalDate + ".  For more information\n"
                + "about this format please see http://www.mipassoc.org/arf/.\n";
    }

}
