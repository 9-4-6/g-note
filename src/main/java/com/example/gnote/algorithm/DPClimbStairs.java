package com.example.gnote.algorithm;

/**
 * @author guozhong
 * @date 2026/1/28
 * @description 动态规划
 *
 *
 * 假设你正在爬楼梯，需要 n 阶才能到楼顶。每次你可以爬 1 阶或 2 阶，问有多少种不同的方法爬到楼顶？
 *
 * 爬 5 阶楼梯的 8 种方法：
 * 1+1+1+1+1
 * 1+1+1+2
 * 1+1+2+1
 * 1+2+1+1
 * 2+1+1+1
 * 1+2+2
 * 2+1+2
 * 2+2+1
 */
public class DPClimbStairs {

    public static void main(String[] args) {
        // 测试：爬5阶楼梯
        int n = 5;
        int result = climbStairs(n);
        System.out.println("\n最终结果：爬" + n + "阶楼梯共有 " + result + " 种方法");
    }

    /**
     * 基础版DP实现（数组存储所有状态）
     * 优点：逻辑直观，易理解；缺点：占用O(n)空间
     *  1  1
     *  2  2
     *  3  3
     *  4  5
     *  5  8
     *
     */
    public static int climbStairs(int n) {
        // 步骤1：处理边界条件（核心：先把最基础的子问题答案确定）
        if (n == 1) {
            System.out.println("边界：1阶楼梯只有1种方法");
            return 1;
        }
        if (n == 2) {
            System.out.println("边界：2阶楼梯有2种方法（1+1 或 2）");
            return 2;
        }

        // 步骤2：定义状态数组 —— dp[i] 表示「爬到第i阶楼梯的方法数」
        int[] dp = new int[n + 1];

        // 步骤3：初始化边界状态（基础子问题的答案）
        dp[1] = 1;
        dp[2] = 2;
        System.out.println("初始化：dp[1]=" + dp[1] + "，dp[2]=" + dp[2]);

        // 步骤4：状态转移（核心：用小问题的答案推大问题）
        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2]; // 转移方程：到i阶 = 到i-1阶走1步 + 到i-2阶走2步
            System.out.println("递推：dp[" + i + "] = dp[" + (i-1) + "] + dp[" + (i-2) + "] = " + dp[i-1] + " + " + dp[i-2] + " = " + dp[i]);
        }

        return dp[n];
    }
}
