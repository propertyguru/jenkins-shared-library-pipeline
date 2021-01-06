package org.pg.common

import java.text.SimpleDateFormat

@Singleton
class Log {
    private static def _context
    private static ArrayList<String> levels = ["debug", "info", "error"]
    private static String dateFormat = "dd.MM.yyyy|HH:mm:ss.SSS"
    private static String logLevel = "info"

    static def setup() {
        _context = Context.get()
        if (_context.LOGLEVEL == "true") {
            logLevel = "debug"
        }
    }

    static def level() {
        return logLevel
    }

    static def debug(def message){
        logMessage("debug", message)
    }

    static def info(def message) {
        logMessage("info", message)
    }

    static def error(message){
        logMessage("error", message)
    }

    private static logMessage(String level, def message){
        if(levels.indexOf(level) >= levels.indexOf(logLevel)){
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat)
            String date = formatter.format(new Date())
            _context.println "${date} [${level.toUpperCase()}] ${message}"
        }
    }
}
