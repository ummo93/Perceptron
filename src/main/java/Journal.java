import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/*
 * Пример использования:
 * Journal.open("./logs.txt");
 * Journal.log("Привет!");
 * Journal.log("Привет!");
 * Journal.log("Привет!");
 * Journal.close();
 * */
class Journal {

    static String pathToFileJournal;
    private static FileWriter fileHandler;
    private static FileWriter errorHandler;

    static void open(String path) throws IOException {
        if(!path.split("\\.")[1].equals("html")) {
            throw new IOException("Journal file must be .html format");
        }
        pathToFileJournal = path;
        try {
            fileHandler = new FileWriter(path, false);
            fileHandler.write("<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\" /><title>Нейронная сеть</title></head><body>");
            String pathToErrorLog = "err" + path.replace(".html", ".txt");
            errorHandler = new FileWriter(pathToErrorLog, false);
            errorHandler.write("Errors: \n");
        } catch(IOException e) {
            System.out.println("Ошибка, данные не будут записаны");
        }
    }

    static void logln(String stroke) {
        try {
            fileHandler.append(stroke);
            fileHandler.append("\r\n");
        } catch(IOException e) {
            System.out.println("Ошибка log, данные не будут записаны");
        }
            
    }

    static void log(String stroke) {
        try {
            fileHandler.append(stroke);
        } catch(IOException e) {
            System.out.println("Ошибка log, данные не будут записаны");
        }
    }

    static void close() {
        try {
            fileHandler.write("</body></html>");
            fileHandler.flush();
            fileHandler.close();
            errorHandler.close();
        } catch(IOException e) {
            System.out.println("Ошибка close, данные не будут записаны");
        }
    }

    static void except(String strokeException) {
        try {
            String date = new Date().toString();
            errorHandler.append("Time: ["+ date +"] - " + strokeException);
        } catch(IOException e) {
            System.out.println("Ошибка, данные не будут записаны в логи");
        }
    }
}