package mainpackage.domain;


public enum StatusTask {
    NEW("Новая"),
    IN_WORK("В работе"),
    REVIEW("Ревью"),
    BACKLOG("Обратная связь"),
    TESTING("В тестировании"),
    CLOSED("Закрыта"),
    ;

    private String status;

    StatusTask(String status) {
        this.status = status;
    }

}
