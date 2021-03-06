package org.common

import java.text.SimpleDateFormat

@Singleton
class Log implements Serializable {
    private static ArrayList<String> levels = ["debug", "info", "error"]
    private static String dateFormat = "dd.MM.yyyy|HH:mm:ss.SSS"
    private static String logLevel = "info"
    private static Map<String, String> ansiCodes = [
            "debug": "\033[33m",
            "info": "\033[32m",
            "error": "\033[31m"
    ]

    static void setup() {
        if (StepExecutor.env('LOGLEVEL') == "true") {
            logLevel = "debug"
        }
    }

    static def level() {
        return logLevel
    }

    static void debug(String message){
        logMessage("debug", message)
    }

    static void info(String message) {
        logMessage("info", message)
    }

    static void error(String message){
        logMessage("error", message)
        StepExecutor.error(message)
    }

    private static String coloredMessage(String level, String message) {
        return "${ansiCodes[level]}${message}\033[0m"
    }

    private static void logMessage(String level, String message){
        if(levels.indexOf(level) >= levels.indexOf(logLevel)){
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat)
            String date = formatter.format(new Date())
            StepExecutor.ansiColor('xterm', {
                String text = coloredMessage(level, "${date} [${level.toUpperCase()}] ${message}")
                StepExecutor.println(text)
            })
        }
    }
}
