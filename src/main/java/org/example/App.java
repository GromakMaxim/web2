package org.example;

public class App {
    /*запускается на http://localhost:25999/index.html

    * чтение параметров можно посмотреть из боковых панелек "найти" и "рассылка"
    *
    * settings_file.txt - файл настроек
    */

    public static void main(String[] args) throws Throwable {
        new Server().start();
    }
}