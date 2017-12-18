package ru.kostin.rpbd.alpinstclub.util;

import ru.kostin.rpbd.alpinstclub.persistence.model.ClimbingStatus;
import ru.kostin.rpbd.alpinstclub.persistence.model.PersonLevel;

import static ru.kostin.rpbd.alpinstclub.util.Constant.*;

public class Util {

    public static String getLevel(String level) {
        String result = "";
        switch (PersonLevel.valueOf(level)) {
            case NEWBIE:
                result = Constant.NEWBIE;
                break;
            case SKILLED:
                result = Constant.SKILLED;
                break;
            case LEAD:
                result = Constant.LEAD;
                break;
        }
        return result;
    }

    public static String getPersonLevelName(String level) {
        String result = "";
        switch (level) {
            case NEWBIE:
                result = PersonLevel.NEWBIE.name();
                break;
            case SKILLED:
                result = PersonLevel.SKILLED.name();
                break;
            case LEAD:
                result = PersonLevel.LEAD.name();
                break;
        }
        return result;
    }

    public static String getClimbingStatusName(String status) {
        String result = "";
        switch (status) {
            case NEW:
                result = ClimbingStatus.NEW.name();
                break;
            case FAIL:
                result = ClimbingStatus.FAIL.name();
                break;
            case CANCELED:
                result = ClimbingStatus.CANCELED.name();
                break;
            case SUCCESS:
                result = ClimbingStatus.SUCCESS.name();
                break;
        }
        return result;
    }

    public static String getStatus(String status) {
        String result = "";
        switch (ClimbingStatus.valueOf(status)) {
            case NEW:
                result = Constant.NEW;
                break;
            case FAIL:
                result = Constant.FAIL;
                break;
            case CANCELED:
                result = Constant.CANCELED;
                break;
            case SUCCESS:
                result = Constant.SUCCESS;
                break;

        }
        return result;
    }
}
