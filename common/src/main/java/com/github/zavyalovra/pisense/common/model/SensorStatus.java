package com.github.zavyalovra.pisense.common.model;

/**
 * Статус датчика
 * DISCOVERED                 найден при сканировании, но ещё не настроен
 * ACTIVE                     разрешён для работы и опроса
 * TEMPORARILY_UNAVAILABLE    временно не отвечает
 * DISABLED                   отключён администратором
 * RETIRED                    выведен из эксплуатации
 */
public enum SensorStatus {
    DISCOVERED,
    ACTIVE,
    TEMPORARILY_UNAVAILABLE,
    DISABLED,
    RETIRED
}
