package s.zhul.tgAppLogic;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import s.zhul.botLogic.MusicBot;

public class StartBot {
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
