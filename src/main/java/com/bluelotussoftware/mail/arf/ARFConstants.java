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

/**
 * 
 * feedback-report = *( feedback-type / user-agent / version )
                     opt-fields-once
                     *( opt-fields-many )
                     *( ext-field )

   feedback-type = "Feedback-Type:" [CFWS] token [CFWS] CRLF
       ; the "token" must be a registered feedback type as
       ; described elsewhere in this document

   user-agent = "User-Agent:" [CFWS] product *( CFWS product )
                [CFWS] CRLF

   version = "Version:" [CFWS] %x31-39 *DIGIT [CFWS] CRLF
       ; as described above

   opt-fields-once = [ arrival-date ]
                     [ incidents ]
                     [ original-envelope-id ]
                     [ original-mail-from ]
                     [ reporting-mta ]
                     [ source-ip ]
   
   arrival-date = "Arrival-Date:" [CFWS] date-time CRLF

   incidents = "Incidents:" [CFWS] 1*DIGIT [CFWS] CRLF
             ; must be a 32-bit unsigned integer

   original-envelope-id = "Original-Envelope-Id:" [CFWS]
                          envelope-id [CFWS] CRLF

   original-mail-from = "Original-Mail-From:" [CFWS]
                        reverse-path [CFWS] CRLF

   reporting-mta = "Reporting-MTA:" [CFWS] mta-name-type [CFWS] ";"
                   [CFWS] mta-name [CFWS] CRLF

   source-ip = "Source-IP:" [CFWS]
               ( IPv4-address-literal /
                 IPv6-address-literal ) [CFWS] CRLF

   opt-fields-many = [ authres-header ]
                     [ original-rcpt-to ]
                     [ reported-domain ]
                     [ reported-uri ]

   original-rcpt-to = "Original-Rcpt-To:" [CFWS]
                      forward-path [CFWS] CRLF

   reported-domain = "Reported-Domain:" [CFWS]
                     domain [CFWS] CRLF

   reported-uri = "Reported-URI:" [CFWS] URI [CFWS] CRLF

   ext-field = field-name ":" unstructured               
                   
 * @author John Yeary <jyeary@bluelotussoftware.com>
 * @version 1.0
 */
public interface ARFConstants {
    // Required as per RFC-5965 Section 3.5
    String FEEDBACK_TYPE_HEADER = "Feedback-Type";
    String USER_AGENT_HEADER = "User-Agent";
    String VERSION_HEADER = "Version";

    // Optional Fields appearing only once 
    String ARRIVAL_DATE_HEADER = "Arrival-Date";
    String INCIDENTS_HEADER = "Incidents";
    String ORIGINAL_ENVELOPE_ID_HEADER = "Original-Envelope-Id";
    String ORIGINAL_MAIL_FROM_HEADER = "Original-Mail-From";
    String REPORTING_MTA_HEADER = "Reporting-MTA";
    String SOURCE_IP_HEADER = "Source-IP";

    // Optional Fields that may appear many times.
    String ORIGINAL_RECEIPT_TO_HEADER = "Original-Rcpt-To";
    String REPORTED_DOMAIN_HEADER = "Reported-Domain";
    String REPORTED_URI = "Reported-URI";
    
}
