package com.example.gnote.algorithm;

import java.util.Arrays;

/**
 * @author guozhong
 * @date 2026/1/27
 *
 * 算法名称	快速排序（Quick Sort）
 * 为什么用	1. 性能优：平均时间复杂度 O (n log n)，是工业界最常用的排序算法（JDK Arrays.sort () 底层对基本类型用快排）；
 *          2. 空间省：原地排序（仅递归栈 O (log n)），比归并排序（需 O (n) 额外空间）更省内存；
 *          3. 适配场景：大规模无序数组排序，如后台数据批量排序、搜索前的预处理。
 * 思考逻辑
 *          面对 “数组排序” 需求，首先排除 O (n²) 的冒泡 / 插入（数据量大时性能差）；
 *          归并排序虽稳定但需额外空间，而快排 “分治 + 原地分区” 更契合 “高性能、低内存” 的工业场景；
 * 核心思考：选一个基准值，把数组分成 “小于基准” 和 “大于基准” 两部分，递归处理子数组 —— 用 “分治” 拆解问题，用 “分区” 实现原地排序。
 */
public class QuickSort {

    public static void main(String[] args) {
        int[] arr = {5, 2, 9, 3, 7, 6, 1, 8, 4};
        quickSort(arr, 0, arr.length - 1);
        // [1,2,3,4,5,6,7,8,9]
        System.out.println(Arrays.toString(arr));
    }

    public static void quickSort(int[] arr, int low, int high) {
        if (low >= high) {
            return;
        }
        int pivotIdx = partition(arr, low, high);
        quickSort(arr, low, pivotIdx - 1);
        quickSort(arr, pivotIdx + 1, high);
    }

    private static int partition(int[] arr, int low, int high) {
        // 选尾元素做基准（也可随机选基准优化）
        int pivot = arr[high];
        // 小于基准的区域指针
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                // 交换元素，扩大小于基准的区域
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        // 基准元素归位
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }
}
