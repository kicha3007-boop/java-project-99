package hexlet.code.exception;

/** Тело запроса ссылается на несуществующую запись (статус, исполнитель, метка) — ответ 400. */
public class InvalidReferenceException extends RuntimeException {
    public InvalidReferenceException(String message) {
        super(message);
    }
}
