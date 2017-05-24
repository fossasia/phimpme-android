package com.xinlan.imageeditlibrary.editimage.utils;

import java.util.List;

/**
 * Created by panyi on 17/3/30.
 */

public class ListUtil {
    public static boolean isEmpty(List list) {
        if (list == null)
            return true;

        return list.size() == 0;
    }

}//end class
