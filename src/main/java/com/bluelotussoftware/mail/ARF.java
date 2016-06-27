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

import com.bluelotussoftware.mail.arf.Utils;
import static com.bluelotussoftware.mail.arf.Utils.getSourceIPAddress;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.mail.Address;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author John Yeary <jyeary@bluelotussoftware.com>
 * @version 1.0
 */
public class ARF {

    private final Options options;

    public ARF() {
        options = new Options();
        buildOptions();
    }

    public static void main(String[] args) throws MessagingException, IOException {
        String username = null;
        String password = null;
        String smtpServer = "localhost";
        int port = 25;
        boolean debug = false;
        MimeMessage originalMessage = null;
        String fileName = null;
        String abuseEmailAddress = null;

        ARF m = new ARF();

        try {

            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(m.getOptions(), args);

            if (cmd.hasOption('u')) {
                username = cmd.getOptionValue('u');
            }

            if (cmd.hasOption('p')) {
                password = cmd.getOptionValue('p');
            }

            if (cmd.hasOption('s')) {
                smtpServer = cmd.getOptionValue('s');
            }

            if (cmd.hasOption('P')) {
                port = Integer.parseInt(cmd.getOptionValue('P'));
            }

            if (cmd.hasOption("d")) {
                debug = true;
            }

            if (cmd.hasOption('f')) {
                fileName = cmd.getOptionValue('f');
            }

            if (cmd.hasOption('a')) {
                abuseEmailAddress = cmd.getOptionValue('a');
            }

            SMTPSender smtp = new SMTPSender(username, password, smtpServer, port, debug);

            try (InputStream is = new BufferedInputStream(new FileInputStream(fileName))) {

                originalMessage = new MimeMessage(smtp.getSession(), is);

                if (debug) {
                    Enumeration e = originalMessage.getAllHeaderLines();
                    System.out.println("<------------ Original Message -------------->");
                    while (e.hasMoreElements()) {
                        String header = (String) e.nextElement();
                        System.out.println(header);
                    }
                    System.out.println("<-------------------------------------------->");
                }

            } catch (IOException e) {
                e.printStackTrace(System.err);
                System.exit(1);
            }

            try (InputStream is = new BufferedInputStream(new FileInputStream(fileName))) {

                InternetHeaders messageHeaders = new InternetHeaders(is);

                Enumeration e = messageHeaders.getAllHeaders();
                Map<String, Header> headerMap = new HashMap<>();

                while (e.hasMoreElements()) {
                    Header h = (Header) e.nextElement();
                    headerMap.put(h.getName(), h);
                    // System.out.println(h.getName()); 
                }

                System.out.println(String.format("Final IP Address: %s", getSourceIPAddress(messageHeaders)));
                System.out.println(MessageFormat.format("Date: {0}", headerMap.get("Date").getValue()));

                if (abuseEmailAddress == null) {
                    Address[] from = originalMessage.getFrom();
                    String sender = from == null ? null : ((InternetAddress) from[0]).getAddress().split("@")[1];
                    abuseEmailAddress = "abuse@" + sender;
                    System.out.println(MessageFormat.format("Abuse email address: {0}", abuseEmailAddress));
                }

                System.out.println("From: " + originalMessage.getFrom()[0].toString());
                System.out.println("To: " + originalMessage.getAllRecipients()[0].toString());

                ARFMessage arfm = new ARFMessage(smtp.getSession(),
                        originalMessage.getAllRecipients()[0].toString(),
                        abuseEmailAddress,
                        Utils.getForwardedSubject(messageHeaders),
                        Utils.getSourceIPAddress(messageHeaders),
                        headerMap.get("Date").getValue(), originalMessage);
                MimeMessage mm = arfm.generateARF();
                mm.writeTo(System.out);

                System.out.println("Mail Success: " + smtp.send(mm));
            }

        } catch (MissingOptionException e) {
            System.err.println(e.getMessage());
            m.printHelp();
        } catch (ParseException ex) {
            ex.printStackTrace(System.err);
        }

    }

    public Options getOptions() {
        return options;
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ARF", getOptions());
    }

    private void buildOptions() {
        Option username = Option.builder("u").longOpt("user").argName("username")
                .hasArg().desc("username for SMTP authentication").build();
        Option password = Option.builder("p").longOpt("password").argName("password")
                .hasArg().desc("password used for SMTP authentication").build();
        Option smtpServer = Option.builder("s").longOpt("smtpServer").argName("localhost")
                .hasArg().desc("SMTP server name or IP address. The default is localhost.").build();
        Option file = Option.builder("f").longOpt("file").argName("email.msg")
                .hasArg().required().desc("(REQUIRED) Raw email message used to send ARF response.").build();
        Option port = Option.builder("P").longOpt("port").hasArg().argName("25")
                .desc("Port number of SMTP server").build();
        Option debug = Option.builder("d").longOpt("debug").desc("Turn on debugging code.").build();
        Option abuse = Option.builder("a").longOpt("abuse").argName("abuse@example.com")
                .desc("Set a specific abuse address. The default is the abuse@XXX.XXX.").build();

        options.addOption(username);
        options.addOption(password);
        options.addOption(smtpServer);
        options.addOption(file);
        options.addOption(port);
        options.addOption(debug);
        options.addOption(abuse);
    }

}
