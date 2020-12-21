package org.pg.common

import java.text.SimpleDateFormat

@Singleton
class Log {
    private static def context
    private static String logLevel = "info"
    private static ArrayList<String> levels = ["debug", "info", "error"]
    private static String dateFormat = "dd.MM.yyyy|HH:mm:ss.SSS"

    static def setup(context) {
        this.context = context
    }

    static def info(String message) {
        logMessage(logLevel, message)
    }

    private static logMessage(String level, message){
        if(levels.indexOf(level) >= levels.indexOf(logLevel)){
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat)
            String date = formatter.format(new Date())
            println "${date} [${level.toUpperCase()}] ${message}"
        }
    }
}
