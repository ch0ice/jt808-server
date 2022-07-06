package cn.com.onlinetool.jt808.util;

public class BitUtil {
    /**
     * 判断n的第i位
     *
     * @param n int32
     * @param i 取值范围0~31
     */
    public static boolean isTrue(int n, int i) {
        return get(n, i) > 0;
    }

    /**
     * 读取n的第i位
     *
     * @param n int32
     * @param i 取值范围0~31
     */
    public static int get(int n, int i) {
        return (1 << i) & n;
    }

    /**
     * 设置n的第i位为1
     *
     * @param n int32
     * @param i 取值范围0~31
     */
    public static int set1(int n, int i) {
        return (1 << i) | n;
    }

    /**
     * 设置n的第i位为0
     *
     * @param n int32
     * @param i 取值范围0~31
     */
    public static int set0(int n, int i) {
        return get(n, i) ^ n;
    }

    /**
     * 写入bool到n的第i位
     *
     * @param n int32
     * @param i 取值范围0~31
     */
    public static int set(int n, int i, boolean bool) {
        return bool ? set1(n, i) : set0(n, i);
    }
}

