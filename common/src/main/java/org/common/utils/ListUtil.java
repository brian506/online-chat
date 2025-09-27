package org.common.utils;

import java.util.List;

public class ListUtil {
    public static <T> List<T> getOrElseThrowList(List<T> list, String message){
        if (list == null || list.isEmpty()) {
            throw new RuntimeException(message);
        }
        return list;
    }
}
