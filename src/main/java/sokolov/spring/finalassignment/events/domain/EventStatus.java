package sokolov.spring.finalassignment.events.domain;

public enum EventStatus {
    //- мероприятие ожидает начала. Доступные новые регистрации в этом состоянии.
    // В этом состоянии еще возможно отменить мероприятие (перевести в статус CANCELLED), в остальных - нельзя.
    WAIT_START("мероприятие ожидает начала"),
    // - мероприятие началось. Новые регистрации недоступны на мероприятие.
    STARTED("мероприятие началось"),
    // - мероприятие отменено организатором. Новые регистрации недоступны на мероприятие.
    CANCELLED("мероприятие отменено организатором"),
    // - мероприятие окончилось. Новые регистрации недоступны на мероприятие.
    FINISHED("мероприятие окончилось");

    private final String description;

    EventStatus(String description) {
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
