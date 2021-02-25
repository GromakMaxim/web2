package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SettingsReader {
    private final String clientSettingsFilePath = "settings_file.txt";

    private String[] splitString(String fileString) {
        return fileString
                .replace(" ", "")//очистить пробелы, переносы строки и пр
                .replace("\r", "")
                .replace("\n", "")
                .trim()
                .split(";");//дробим файл по разделителям
    }

    private String readSettingsFileIntoString() {
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fin = new FileInputStream(clientSettingsFilePath)) {
            int i;
            while ((i = fin.read()) != -1) {//читаем все символы
                sb.append((char) i);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return sb.toString();
    }


    //достаём заданный параметр из файла с настройками
    private String readParameterByOption(String parameter) {
        String[] fileLines = splitString(readSettingsFileIntoString());
        for (String line : fileLines) {
            String settingsOption = line.split(":")[0];//слева "опция", справа - её аргумент
            if (settingsOption.equalsIgnoreCase(parameter)) {//ищем определённую опцию
                return line.split(":")[1];//если нашли, вытаскиваем её аргумент(справа от двоеточия)
            }
        }
        return "";
    }

    public boolean checkValidPath(String path) {
        String[] settingsArguments = readParameterByOption("valid_paths").split(",");//достаём содержащийся массив
        for (String argument : settingsArguments) {
            if (argument.replace("\t", "").equalsIgnoreCase(path)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkValidFileExtension(String path) {
        String[] settingsArguments = readParameterByOption("valid_file_extension").split(",");//достаём содержащийся массив
        for (String argument : settingsArguments) {
            if (path.contains(argument.replace("\t", ""))) {
                return true;
            }
        }
        return false;
    }


    //прочитать порт сервера
    public String getServerPortParameterFromSettingsFile() {
        String server_port = "server_port";
        checkSettingsFileExistence();
        return readParameterByOption(server_port);
    }

    //прочитать кол-во threads
    public String getThreadsNumberFromSettingsFile() {
        String threads = "threads";
        checkSettingsFileExistence();
        return readParameterByOption(threads);
    }

    //проверить наличие файла с настройкам в заданной директории
    private void checkSettingsFileExistence() {
        File settingsFile = new File(clientSettingsFilePath);
        if (!settingsFile.exists()) {
            System.err.println("Can't find settings file");
        }
    }
}
