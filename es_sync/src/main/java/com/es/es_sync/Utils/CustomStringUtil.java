package com.es.es_sync.Utils;

import org.apache.commons.lang3.StringUtils;

public class CustomStringUtil {
    public static boolean isEmptyAndBlank(String text){
        return StringUtils.isEmpty(text) || StringUtils.isBlank(text);
    }

    public static boolean isNotEmptyAndBlank(String text){
        return StringUtils.isNotEmpty(text) && StringUtils.isNotBlank(text);
    }
}
