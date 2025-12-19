package s.zhul.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.nio.file.Files;
import java.nio.file.Path;

public class MusicBot extends TelegramLongPollingBot {

    private final String BOT_USERNAME = "musicLotto";
    private final String BOT_TOKEN = "8495688303:AAFqt792uv7OhxsHyDG1FukK83QQ0LevKIc";
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
                    sendMessage(chatId, "Привет! Введите номер трека (например, 1, 2, 3), чтобы воспроизвести соответствующий файл из папки music.");
                    break;
                default:
                    sendMessage(chatId, "Введите число — номер трека (например, 1, 2, 3).");
                    break;
            }
        }
    }

    private void sendTrackByNumber(long chatId, int trackNumber) {
        try {
            // Формируем путь: ./music/{trackNumber}.mp3
            Path trackPath = Path.of(MUSIC_DIR, trackNumber + ".mp3");

            // Проверяем, существует ли файл
            if (!Files.exists(trackPath)) {
                sendMessage(chatId, "Трек с номером " + trackNumber + " не найден. Убедитесь, что файл " + trackNumber + ".mp3 существует в папке music.");
                return;
            }

            // Отправляем аудио
            SendAudio audioRequest = new SendAudio();
            audioRequest.setChatId(chatId);
            audioRequest.setAudio(new InputFile(trackPath.toFile()));

            execute(audioRequest);

        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки аудио: " + e.getMessage());
            sendMessage(chatId, "Не удалось отправить трек. Ошибка: " + e.getMessage());
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
}
