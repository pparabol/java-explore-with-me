package ru.practicum.explorewithme.common.dto.event;

public enum StateAction {
    // for user requests
    SEND_TO_REVIEW,
    CANCEL_REVIEW,

    // for admin requests
    PUBLISH_EVENT,
    REJECT_EVENT
}
