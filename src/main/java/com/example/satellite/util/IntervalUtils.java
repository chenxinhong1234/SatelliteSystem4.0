package com.example.satellite.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 组合振动区的相关计算
 * Created by Gaoxinwen on 2016/9/8.
 */
public class IntervalUtils {

    /**
     * 合并区间
     * @param intervals 区间List
     * @return 合并好的区间
     */
    public static List<Interval> merge(List<Interval> intervals) {
        // 升序排列
        intervals.sort(Comparator.comparingDouble(Interval::getLower));

        List<Interval> merged = new ArrayList<>();

        for (int i = 0; i < intervals.size(); i++) {
            if (i == 0) {
                merged.add(intervals.get(i));
            } else {
                Interval last = merged.get(merged.size() - 1);
                if (intervals.get(i).getLower() <= last.getUpper()) {
                    last.setUpper(Math.max(last.getUpper(), intervals.get(i).getUpper()));
                } else {
                    merged.add(intervals.get(i));
                }
            }
        }

        return merged;
    }

    /**
     * 区间组合
     *
     * @param i1 第一个区间
     * @param i2 第二个区间
     * @return 组合区间
     */
    public static Interval combine(Interval i1, Interval i2) {
        return new Interval(i1.getLower() + i2.getLower(), i1.getUpper() + i2.getUpper());
    }

    /**
     * d是否在组合区间内
     * @param d double型数
     * @param intervals 组合区间
     * @return true or false
     */
    public static boolean contains(double d, List<Interval> intervals) {
        for (Interval interval : intervals) {
            if (d >= interval.getLower() || d <= interval.getUpper())
                return true;
        }
        return false;
    }


    /**
     * 区间求补集，适用于将振动区转化为可行区，反之亦然
     *
     * @param intervals 区间List
     * @param domain    总区间
     * @return 区间List的补集
     */
    public static List<Interval> complement(List<Interval> intervals, Interval domain) {

        List<Interval> merged = merge(intervals);

        List<Interval> result = new ArrayList<>();

        for (int i = 0; i < merged.size(); i++) {
            if (i == 0) {
                if (domain.getLower() < merged.get(i).getLower()) {
                    result.add(new Interval(domain.getLower(), merged.get(i).getLower()));
                }
                if (merged.size() == 1) {
                    if (domain.getUpper() > merged.get(i).getUpper()) {
                        result.add(new Interval(merged.get(i).getUpper(), domain.getUpper()));
                    }
                }
            } else {
                result.add(new Interval(merged.get(i - 1).getUpper(), merged.get(i).getLower()));
                if (i == merged.size() - 1) {
                    if (domain.getUpper() > merged.get(i).getUpper()) {
                        result.add(new Interval(merged.get(i).getUpper(), domain.getUpper()));
                    }
                }
            }
        }

        return result;
    }

    /**
     * 两台机组求组合振动区
     * @param c1 第一台
     * @param c2 第二台
     * @return 组合成一台大机组
     */
    public static IntervalCustom mergeUnit(IntervalCustom c1, IntervalCustom c2) {
        Interval totalDomain = IntervalUtils.combine(c1.getDomain(), c2.getDomain());

        // 振动区求补集，即求可行区
        List<Interval> complement1 = IntervalUtils.complement(c1.getIntervals(), c1.getDomain());
        List<Interval> complement2 = IntervalUtils.complement(c2.getIntervals(), c2.getDomain());

        // 某一台机组可以空载运行
        Interval interval1 = new Interval(0, 0);
        Interval interval2 = new Interval(0, 0);
        complement1.add(interval1);
        complement2.add(interval2);

        List<Interval> combined = new ArrayList<>();

        for (Interval i1 : complement1) {
            combined.addAll(complement2.stream().map(i2 -> IntervalUtils.combine(i1, i2)).collect(Collectors.toList()));
        }

        List<Interval> merged = IntervalUtils.merge(combined);
        List<Interval> complement = IntervalUtils.complement(merged, totalDomain);

        return new IntervalCustom(complement, totalDomain);

    }

//    public static void main(String[] args) {
//        Interval interval1 = new Interval(0, 0);
//        Interval interval2 = new Interval(28, 45);
//        List<Interval> intervalList1 = new ArrayList<>(Arrays.asList(interval1, interval2));
//        Interval domain1 = new Interval(0, 45);
//        List<Interval> list1 = IntervalUtils.complement(intervalList1, domain1);
//        list1.forEach(System.out::println);
//
//        Interval interval3 = new Interval(0, 0);
//        Interval interval4 = new Interval(90, 120);
//        List<Interval> intervalList2 = new ArrayList<>(Arrays.asList(interval3, interval4));
//        Interval domain2 = new Interval(0, 120);
//        List<Interval> list2 = IntervalUtils.complement(intervalList2, domain2);
//        list2.forEach(System.out::println);
//
//
//        IntervalCustom custom1 = new IntervalCustom(list1, domain1);
//        IntervalCustom custom2 = new IntervalCustom(list2, domain2);
//
//        IntervalCustom sumCustom = IntervalUtils.mergeUnit(custom1, custom2);
//
//        System.out.println(sumCustom);
//
//
//    }
}
