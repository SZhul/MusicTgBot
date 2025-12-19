package s.zhul.botLogic;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class MusicBot extends TelegramLongPollingBot {

    private String BOT_USERNAME;
    private String BOT_TOKEN;

    public MusicBot() {
        loadProperties();
    }

    private static final String MUSIC_DIR = "./music";


    /*
    Этот метод:
    Принимает все обновления от Telegram
    Проверяет, является ли обновление текстовым сообщением
    Если да — передаёт его в handleCommand для дальнейшей обработки
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleCommand(update);
        }
    }

    /*
    Этот метод Определяет, что прислал пользователь — команда или номер трека

    Если номер — отправляет соответствующий аудиофайл
    Если /start — даёт приветствие
    Если что-то непонятное — просит ввести число
    Это логика маршрутизации действий бота.
     */

    private void handleCommand(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        // Проверяем, является ли сообщение числом
        if (messageText.matches("\\d+")) {
            int trackNumber = Integer.parseInt(messageText);
            sendTrackByNumber(chatId, trackNumber);
        } else {
            // Обработка команд
            switch (messageText.toLowerCase()) {
                case "/start":
                    sendMessage(chatId, "Привет! Введите номер трека (например, 1, 2, 3, но не 4!!), чтобы воспроизвести соответствующий файл из папки music.");
                    break;
                default:
                    sendMessage(chatId, "Введите число — номер трека (например, 1, 2, 3).");
                    break;
            }
        }
    }

    /*
    Рабочий метод для локального запуска
     */
//    private void sendTrackByNumber(long chatId, int trackNumber) {
//        try {
//            String resourcePath = ".src/main/resources/music/" + trackNumber + ".mp3";
//            var resource = getClass().getClassLoader().getResource(resourcePath);
//
//            if (resource == null) {
//                sendMessage(chatId, "Трек с номером " + trackNumber + " не найден.");
//                return;
//            }
//
//            // Копируем в временный файл — Telegram API требует File
//            Path tempFile = Files.createTempFile("track_", ".mp3");
//            Files.copy(resource.openStream(), tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
//
//            SendAudio audioRequest = new SendAudio();
//            audioRequest.setChatId(chatId);
//            audioRequest.setAudio(new InputFile(tempFile.toFile()));
//            execute(audioRequest);
//
//            // Удалим временный файл после отправки
//            tempFile.toFile().deleteOnExit();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            sendMessage(chatId, "Ошибка при отправке трека.");
//        }
//    }

    /*
    Метод для сборки в railway
     */
    private void sendTrackByNumber(long chatId, int trackNumber) {
        try {
            String resourcePath = "music/" + trackNumber + ".mp3";
            var resource = getClass().getClassLoader().getResource(resourcePath);

            if (resource == null) {
                sendMessage(chatId, "Трек с номером " + trackNumber + " не найден.");
                return;
            }

            // Копируем в временный файл — Telegram API требует File
            Path tempFile = Files.createTempFile("track_", ".mp3");
            Files.copy(resource.openStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            SendAudio audioRequest = new SendAudio();
            audioRequest.setChatId(chatId);
            audioRequest.setAudio(new InputFile(tempFile.toFile()));

            execute(audioRequest);

            // Удаляем временный файл после отправки
            tempFile.toFile().deleteOnExit();

        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(chatId, "Ошибка при отправке трека.");
        }
    }

    private void sendMessage(long chatId, String text) {
        try {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build();
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

//    /*
//    Это метод для загрузки настроек из файла application.properties
//     */
//    private void loadProperties() {
//        Properties prop = new Properties();
//        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
//            if (input == null) {
//                throw new RuntimeException("Файл application.properties не найден в resources");
//            }
//            prop.load(input);
//            BOT_USERNAME = prop.getProperty("BOT_USERNAME");
//            BOT_TOKEN = prop.getProperty("BOT_TOKEN");
//        } catch (IOException e) {
//            throw new RuntimeException("Ошибка при загрузке настроек", e);
//        }
//    }

    /*
     Это метод для загрузки настроек из переменных окружения для бесплатного хостинга
     */
    private void loadProperties() {
        BOT_USERNAME = System.getenv("BOT_USERNAME");
        BOT_TOKEN = System.getenv("BOT_TOKEN");

        if (BOT_USERNAME == null || BOT_TOKEN == null) {
            throw new RuntimeException("BOT_USERNAME and BOT_TOKEN must be set as environment variables!");
        }
    }

}

