package org.mythicprojects.cerberusloader.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CerberusLoggerFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        String message = record.getMessage();
        if (message == null || message.isEmpty() || message.trim().isEmpty()) {
            return " \n";
        }
        return String.format("[%s] [%s]: %s\n", record.getLevel().getName(), record.getLoggerName(), message);
    }

}
