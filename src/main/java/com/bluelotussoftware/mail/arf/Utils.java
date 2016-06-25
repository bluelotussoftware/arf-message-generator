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

import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Header;
import javax.mail.internet.InternetHeaders;

/**
 * Mail Utilities
 *
 * @author John Yeary <jyeary@bluelotussoftware.com>
 * @version 1.0
 */
public class Utils {

    /**
     * Reads {@link InternetHeaders} evaluating <code>Received:</code> lines to
     * determine the source IP address.
     *
     * @param internetHeaders The headers to examine.
     * @return the IP address of the last {@code Received:} header, or
     * {@code null} if it can not be determined.
     */
    public static String getSourceIPAddress(final InternetHeaders internetHeaders) {
        Enumeration<Header> headers = (Enumeration<Header>) internetHeaders.getAllHeaders();
        Pattern IP_ADDRESS_PATTERN = Pattern.compile("\\[(.*?)\\]");
        String sourceIP = null;
        while (headers.hasMoreElements()) {
            Header h = (Header) headers.nextElement();
            if ("Received".equals(h.getName())) {
                Matcher m = IP_ADDRESS_PATTERN.matcher(h.getValue());
                while (m.find()) {
                    sourceIP = m.group(1);
                }
            }
        }

        return sourceIP;
    }

    /**
     * Reads the {@link InternetHeaders} looking for the <code>Subject:</code>
     * and returns it with <code>FW:</code> prepended.
     *
     * @param internetHeaders The headers to examine.
     * @return a forwarded subject line.
     */
    public static String getForwardedSubject(final InternetHeaders internetHeaders) {
        String[] subjects = internetHeaders.getHeader("Subject");
        StringBuilder sb = new StringBuilder();
        sb.append("FW: ");

        for (String s : subjects) {
            sb.append(s);
        }

        return sb.toString();
    }
}
