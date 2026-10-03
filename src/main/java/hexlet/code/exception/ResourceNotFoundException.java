package hexlet.code.exception;

/** Запрошенной записи нет — ответ 404. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
