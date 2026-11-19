package com.github.zavyalovra.pisense.common.model;

/**
 * Статус батча:
 * ACCEPTED - принят впервые, запись в БД, ответ HTTP code = 201 (CREATED)
 * REJECTED - был принят уже ранее, игнорируется (идемпотентность), ответ HTTP code = 200 (OK)
 */
public enum BatchStatus {
    ACCEPTED,
    REJECTED
}
