package s.zhul.startApp;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import s.zhul.bot.MusicBot;

public class Start {

    public static void main(String[] args) {
        try {
            // Создаём объект бота
            TelegramLongPollingBot bot = new MusicBot();

            // Создаём API и регистрируем бота
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);

            // Вебхук не нужно удалять вручную — он и так не установлен при Long Polling
            // bot.deleteWebhook() — НЕ НУЖЕН

            System.out.println("Бот запущен и готов к работе!");

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
