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
package com.bluelotussoftware.mail;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

/**
 * Sends a {@link MimeMessage} using the username, password, and smtpServer
 * provided.
 *
 * @author John Yeary <jyeary@bluelotussoftware.com>
 * @version 1.0
 */
public class SMTPSender {

    private String username;
    private String password;
    private String smtpServer;
    /**
     * Default SMTP port.
     */
    private int port = 25;
    private boolean debug;

    /**
     * Constructor used to set up the SMTP service for sending messages.
     *
     * @param username The username associated with the SMTP server if required.
     * This may be {@code null}.
     * @param password The password associated with the SMTP server if required.
     * This may be {@code null}.
     * @param smtpServer The host name, or IP address of the SMTP server.
     */
    public SMTPSender(final String username, final String password, final String smtpServer) {
        this.username = username;
        this.password = password;
        this.smtpServer = smtpServer;
    }

    /**
     * Constructor used to set up the SMTP service for sending messages.
     *
     * @param username The username associated with the SMTP server if required.
     * This may be {@code null}.
     * @param password The password associated with the SMTP server if required.
     * This may be {@code null}.
     * @param smtpServer The host name, or IP address of the SMTP server.
     * @param port The port number if not set to the default;
     */
    public SMTPSender(final String username, final String password, final String smtpServer, final int port) {
        this(username, password, smtpServer);
        this.port = port;
    }

    /**
     * Constructor used to set up the SMTP service for sending messages.
     *
     * @param username The username associated with the SMTP server if required.
     * This may be {@code null}.
     * @param password The password associated with the SMTP server if required.
     * This may be {@code null}.
     * @param smtpServer The host name, or IP address of the SMTP server.
     * @param port The port number if not set to the default.
     * @param debug A boolean flag to enable debug.
     */
    public SMTPSender(final String username, final String password, final String smtpServer, final int port, final boolean debug) {
        this(username, password, smtpServer, port);
        this.debug = debug;
    }

    /**
     * Constructor used to set up the SMTP service for sending messages.
     *
     * @param username The username associated with the SMTP server if required.
     * This may be {@code null}.
     * @param password The password associated with the SMTP server if required.
     * This may be {@code null}.
     * @param smtpServer The host name, or IP address of the SMTP server.
     * @param debug A boolean flag to enable debug.
     */
    public SMTPSender(final String username, final String password, final String smtpServer, final boolean debug) {
        this(username, password, smtpServer);
        this.debug = debug;
    }

    /**
     * A basic implementation of {@link Authenticator}.
     */
    private class SMTPAuthenticator extends Authenticator {

        /**
         * {@inheritDoc}
         */
        @Override
        public PasswordAuthentication getPasswordAuthentication() {

            return new PasswordAuthentication(username, password);
        }
    }

    /**
     * A method to return the {@link Session}.
     *
     * @return a default {@link Session}.
     */
    public Session getSession() {
        Session session;
        Properties properties = System.getProperties();
        // Setup mail server
        properties.setProperty("mail.smtp.host", smtpServer);

        if (debug) {
            properties.setProperty("mail.debug", "true");
        }

        //Set alternate port value if neccessary
        if (port != 25) {
            properties.setProperty("mail.smtp.port", Integer.toString(port));
        }

        // validity check
        if (username != null && password != null) {
            properties.put("mail.smtp.auth", "true");
            Authenticator auth = new SMTPAuthenticator();

            // Get the default Session object.
            session = Session.getDefaultInstance(properties, auth);
        } else {
            session = Session.getDefaultInstance(properties);
        }
        return session;
    }

    /**
     * Send a {@link MimeMessage}.
     *
     * @param message The message to be sent.
     * @return {@code true} if the message was successfully sent, and
     * {@code false} otherwise.
     */
    public boolean send(final MimeMessage message) {
        boolean success = false;

        if (message != null) {
            try {
                Transport.send(message);
                success = true;
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        return success;
    }

}
