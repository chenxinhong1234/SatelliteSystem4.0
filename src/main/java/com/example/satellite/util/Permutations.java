package com.example.satellite.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Description: https://graphics.stanford.edu/~seander/bithacks.html#NextBitPermutation
 * Created by Gaoxinwen on 2016/9/9.
 */
public class Permutations {

    /**
     * 指定开机台数和总台数，得到所有可能的机组组合（同一机组类型）
     * @param on 开机台数
     * @param amount 总台数
     * @return String类型的List，方便后面不同机组类型之间进行合并
     */
    public static List<String> permutate(int on, int amount) {
        List<String> list = new ArrayList<>();

        // 处理0的情况
        if (on == 0) {
            list.add(convertToString(on, amount));
            return list;
        }

        int initial = (1 << on) - 1;
        int blockMask = (1 << amount) - 1;

        int v = initial;

        while (v >= initial) {
            list.add(convertToString(v, amount));
            int t = (v | (v - 1)) + 1;
            int w = t | ((((t & -t) / (v & -v)) >> 1) - 1);
            v = w & blockMask;
        }

        // 颠倒一下，更符合实际情况（先1110，再1101，1011，0111）
        Collections.reverse(list);

        return list;
    }

    /**
     * 从String类型变换为int型数组
     * @param s 类似（"1111"）的String
     * @return [1, 1, 1, 1]
     */
    public static int[] convertToIntArray(String s) {

        int[] result = new int[s.length()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (s.charAt(i) == '1') ? 1 : 0;
        }
        return result;
    }

    /**
     * 十进制转二进制，位数不足的补0
     * @param num 十进制数
     * @param digits 位数
     * @return 如（5， 4）应该变为0101
     */
    public static String convertToString(int num, int digits) {
        return String.format("%" + digits + "s", Integer.toBinaryString(num)).replace(' ', '0');
    }

}
