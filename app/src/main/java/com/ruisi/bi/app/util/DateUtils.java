package com.ruisi.bi.app.util;

import java.util.Date;
import java.util.TimeZone;

/**
 * Created by berly on 2016/9/5.
 */
public class DateUtils {
    public static Date changeTimeZone(Date date, TimeZone oldZone, TimeZone newZone) {
        Date dateTmp = null;
        if (date != null) {
            int timeOffset = oldZone.getRawOffset() - newZone.getRawOffset();
            dateTmp = new Date(date.getTime() + timeOffset);
        }
        return dateTmp;
    }
}
