package ru.kostin.rpbd.alpinstclub.util;

public interface Constant {
    String BACK = "Назад";
    String ADD = "Добавить";
    String CHANGE = "Изменить";
    String DELETE = "Удалить";
    String NAME = "Название";
    String SEARCH = "Поиск";
    String SAVE = "Сохранить";
    String CLEAR = "X";
    String NEW = "Новое";
    String CANCELED = "Отменено";
    String SUCCESS = "Успешно завершено";
    String FAIL = "Неудачно завершено";
    String FIO = "ФИО";
    String LEVEL = "Уровень";
    String MOUNTAIN = "Вершина";
    String MIN_LEVEL = "Минимальный уровень";
    String STATUS = "Статус";
    String USERNAME = "Имя пользователя";
    String PERSON_LIMIT = "Лимит участников";
    String ROUTE = "Маршрут";
    String START_DATE = "Дата начала";
    String END_DATE = "Дата завершения";
    String HEIGHT = "Высота";
    String LAT = "Широта";
    String LON = "Долгота";
    String NO_ELEMENT = "Не выбран элемент";
    String NOT_ALLOWED_CHANGE_LEVEL = "Нельзя отредактировать свой уровень";
    String NEWBIE = "Новичок", SKILLED = "Опытный", LEAD = "Лидер";
    String NOT_ALLOWED_DELETE_YOURSELF = "Нельзя удалить свою учетную запись";
    String ALREADY_EXISTS = "Такая запись уже существует";
    String MOUNTAIN_ERROR = "Название не должно быть длиннее 255 символов.\n " +
            "Высота,широта,долгота - действительные положительные числа";
    String HEIGHT_ERROR = "Высота должна быть действительным положительным числом";
    String CLIMBING_ERROR = "Дата начала не может быть после даты завершения.\n" +
            "Количество участников - положительное число от 2 до 10 и не превышает лимит";

}
