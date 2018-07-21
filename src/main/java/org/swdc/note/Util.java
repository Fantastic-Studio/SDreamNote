package org.swdc.note;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 通用工具类
 */
public class Util {

    /**
     * 更新对象的数据
     *
     * @param obj    源对象
     * @param target 目标对象
     * @param <T>    目标对象的类型
     * @return 更新后的目标对象
     * @throws Exception 反射的时候的异常
     */
    public static <T> T updateObjects(Object obj, T target) throws Exception {
        List<Field> fields = new ArrayList<>();
        Class clazz = obj.getClass();
        while (clazz.getSuperclass() != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        for (Field field : fields) {
            // 如果目标不含有源对象的某个字段
            if (!isPropertyExists(target.getClass(), field.getName())) {
                continue;
            }
            PropertyDescriptor pdsDst = new PropertyDescriptor(field.getName(), target.getClass());
            PropertyDescriptor pdsSrc = new PropertyDescriptor(field.getName(), obj.getClass());
            Object srcVal = pdsSrc.getReadMethod().invoke(obj);
            Object dstVal = pdsDst.getReadMethod().invoke(target);
            // 跳过相同的，空的
            if (srcVal == null || srcVal.equals("") || srcVal.equals(dstVal)) {
                continue;
            }
            // 更新目标对象的数据
            pdsDst.getWriteMethod().invoke(target, dstVal);
        }
        return target;
    }

    /**
     * 判断属性是否存在
     *
     * @param clazz 类
     * @param name  属性名
     * @return 是否存在
     */
    public static boolean isPropertyExists(Class clazz, String name) {
        try {
            PropertyDescriptor pds = new PropertyDescriptor(name, clazz);
            if (pds.getReadMethod() != null && pds.getWriteMethod() != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
