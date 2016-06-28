# Abuse Report Format (ARF) Message Generator
This application is used to generate Abuse Report Format (ARF) messages to test an email system response. You can provide the application a variety of parameters to customize how it is used.
  ```bash
usage: ARF
 -a,--abuse                    Set a specific abuse address. The default
                               is the abuse@XXX.XXX.
 -d,--debug                    Turn on debugging code.
 -f,--file <email.msg>         (REQUIRED) Raw email message used to send
                               ARF response.
 -p,--password <password>      password used for SMTP authentication
 -P,--port <25>                Port number of SMTP server
 -s,--smtpServer <localhost>   SMTP server name or IP address. The default
                               is localhost.
 -u,--user <username>          username for SMTP authentication
 ```
The required email message should be the raw email with all of the headers included.

## Requirements
* Java Mail API 1.5.5
* Sun DSN API 1.5.5

```
        <dependency>
            <groupId>com.sun.mail</groupId>
            <artifactId>javax.mail</artifactId>
            <version>1.5.5</version>
            <type>jar</type>
        </dependency>
        <dependency>
            <groupId>com.sun.mail</groupId>
            <artifactId>dsn</artifactId>
            <version>1.5.5</version>
        </dependency>
```
