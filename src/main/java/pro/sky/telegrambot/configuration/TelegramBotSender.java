package pro.sky.telegrambot.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.telegrambot.model.entity.User;
import pro.sky.telegrambot.model.state.State;
import pro.sky.telegrambot.model.state.StateButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.replace;

@Service
public class TelegramBotSender extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBot.class);
    @Value("${telegram.bot.name}")
    private String botName;

    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public String getBotUsername() {
        return botName; //botConfig.getBotName();
    }

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${file_info.uri}")
    private String fileInfoUri;

    @Value("${file_storage.uri}")
    private String fileStorageUri;

    @Override
    public String getBotToken() {
        return token;//botConfig.getBotToken();
    }

    public String getFileInfoUri() {
        return fileInfoUri;
    }

    public String getFileStorageUri() {
        return fileStorageUri;
    }

//    @Override
//    public void onUpdateReceived(Update update) {
//    }

    //бот будет проверять этот текст в состояниях тестового ввода. Поэтому public
    public final String RETURN_BUTTON_FOR_TEXT_INPUT = "Назад к кнопкам";
    //Для состояний ожидания ввода текста создадим заранее клавиатуру. Получилось одной строкой
    private final List<KeyboardRow> KEYBOARD_FOR_TEXT_INPUT =
            Collections.singletonList(new KeyboardRow(
                    Collections.singletonList(new KeyboardButton(
                            RETURN_BUTTON_FOR_TEXT_INPUT))));

    /**
     * Отправляет сообщение указанному чату с заданным текстовым сообщением.
     *
     * @param chatId              Идентификатор чата, куда нужно отправить сообщение.
     * @param textToSend          Текст сообщения, который следует отправить.
     * @param replyKeyboardMarkup если не null, то содержит клавиатуру
     * @param replyToMessageId    если не 0, то содержит id предыдущего сообщения,
     *                            в ответ на которое надо отправить заданное
     */
    public void sendMessage(long chatId, String textToSend,
                            ReplyKeyboardMarkup replyKeyboardMarkup, int replyToMessageId)
            throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        //при посылке подчеркивания возникает ошибка
        //[400] Bad Request: can't parse entities: Can't find end of the entity starting at byte offset - место подчеркивания
        //поэтому заменяю подчеркивания на тире
        sendMessage.setText(replace(textToSend, "_", "-"));
        if (replyKeyboardMarkup != null) {
            sendMessage.enableMarkdown(true);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        if (replyToMessageId != 0) sendMessage.setReplyToMessageId(replyToMessageId);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.error("Error occurred by sending message '"
                    + textToSend + "' to chat " + chatId + " : " + e.getMessage());
            throw e; //пробрасываем в вызывающие методы, чтобы они прервали свою работу
        }
    }

    /**
     * Метод отправляет сообщение пользователю,
     * учитывая его состояние кнопок (чтобы не стереть их, а повторить) .<br>
     *
     * @param user             пользователь, которому надо отправить соообщение.
     * @param text             текст сообщения
     * @param replyToMessageId идентификатор предыдущего сообщения от пользователя,
     *                         на которое надо отправить ответ
     * @throws TelegramApiException выбрасывается, если отправка не состоялась.
     */
    //replyToMessageId если не 0, то боту уйдет сообщение в виде - с включенным в ответ вопросом
    public void sendMessageToUser(User user, String text, int replyToMessageId)
            throws TelegramApiException {
        //задача - послать юзеру текст, но снабдить его кнопками в соответствии с состоянием
        //этом метод вызываем откуда угодно и любой момент общения с ботом,
        //например, после получения ответа от волонтераthrows TelegramApiException
        //если текст в параметре пустой, то используется текст состояния из State
        State state = user.getState();
        if (text == null) {
            text = state.getText();
        }

        //т.е. можно посылать все что угодно и сколько угодно раз с помощью SendMessage,
        //но завершать переписку надо обязательно через sendMessageToUser(user, null, 0)
        //чтобы появились правильные кнопки и обозначилось текущее состояние.
        //Сообщений без кнопок мы посылать не планируем. Какие-нибудь кнопки всегда должны быть.
        //Поэтому надо позаботиться, чтобы в таблицах не оказалось состояний не текстового ввода и без кнопок.

        List<KeyboardRow> keyboard;
        if (state.isTextInput()) {
            keyboard = KEYBOARD_FOR_TEXT_INPUT; //прикрепим только кнопку Назад к кнопкам
        } else {
            //если не текстовый ввод и не список приютов,
            //то должны быть кнопки в таблице state_button. Делаем спец клавиатуру
            //для использования переменных в лямбде они должны быть final
            final List<KeyboardRow> customKeyboard = new ArrayList<>();
            final List<StateButton> buttons = state.getButtons();
            if (buttons.isEmpty()) {
                LOGGER.error("State " + state.getId() + " has no button");
            }
            buttons.stream()
                    .filter(button -> button.getShelterId() == null
                            || button.getShelterId().equals(user.getShelterId()))
                    .mapToInt(StateButton::getRow)
                    .distinct()
                    .forEachOrdered(row -> {
                        KeyboardRow keyboardRow = new KeyboardRow();
                        buttons.stream()
                                .filter(button -> button.getRow() == row
                                        && (button.getShelterId() == null
                                        || button.getShelterId().equals(user.getShelterId())))
                                .sorted(Comparator.comparingInt(StateButton::getCol))
                                .forEach(button -> keyboardRow.add(button.getCaption()));
                        customKeyboard.add(keyboardRow);
                    });
            keyboard = customKeyboard;
        }

        //Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        //устанавливаем список keyboard нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
        //бросает TelegramApiException
        sendMessage(user.getId(), text, replyKeyboardMarkup, replyToMessageId);
    }
}
