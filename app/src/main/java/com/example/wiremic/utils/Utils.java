package com.example.wiremic.utils;

import android.os.Build;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static boolean CheckGenericType(Object obj, Class inner, Class... types)
    {
        for(Type c : obj.getClass().getGenericInterfaces())
        {
            Type innerType = ((ParameterizedType) c).getRawType();
            if(innerType != inner)
            {
                continue;
            }

            Type[] p = ((ParameterizedType) c).getActualTypeArguments();
            if(p.length != types.length)
            {
                continue;
            }
            for(int i = 0; i < p.length;i++){
                if(p[i] != types[i])
                {
                    continue;
                }
            }
            return true;
        }
        return false;
    }

    public static <T> List<T> filterObjectTypes(List<?> objects, Class innerclass, Class typeclass)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return objects.stream()
                    .filter(x-> Utils.CheckGenericType(x,innerclass,typeclass))
                    .map(x->(T)x)
                    .collect(Collectors.toList());
        }
        return null;
    }
}
