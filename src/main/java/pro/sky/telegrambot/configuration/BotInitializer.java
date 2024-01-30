package pro.sky.telegrambot.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import org.telegram.telegrambots.meta.generics.TelegramBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;

@Component
public class BotInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotInitializer.class);

    private final TelegramBot bot;

    public BotInitializer(TelegramBot bot) {
        this.bot = bot;
    }
    /**
     * Метод инициализации приложения, который реагирует на событие ContextRefreshedEvent.
     * При вызове данного метода, он создает экземпляр TelegramBotsApi, затем регистрирует бота с этим API.
     */
    //было @EventListener({ContextRefreshedEvent.class})
    @PostConstruct
    public void init() {
        try {
            // Создание экземпляра TelegramBotsApi с использованием DefaultBotSession.
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            // Регистрация бота с TelegramBotsApi.
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            LOGGER.error("Error of creation or registration of bot occurred: " + e.getMessage());
        }
    }
}