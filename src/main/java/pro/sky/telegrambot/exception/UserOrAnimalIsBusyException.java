package pro.sky.telegrambot.exception;

public class UserOrAnimalIsBusyException extends RuntimeException {
    @Override
    public String getMessage() {
        return "User is busy!";
    }
}
