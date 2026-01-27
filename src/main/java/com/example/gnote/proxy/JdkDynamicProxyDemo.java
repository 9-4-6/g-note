package com.example.gnote.proxy;

import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author guozhong
 * @date 2026/1/26
 * @description jdk动态代理
 */
public class JdkDynamicProxyDemo {
    // ------------- 第一步：定义被代理的接口（对应 MyBatis 的 Mapper 接口）-------------
    /**
     * 用户服务接口（目标接口）
     */
    public interface UserService {
        // 根据ID查询用户
        String getUserById(Integer id);
        // 修改用户名
        void updateUserName(Integer id, String newName);
    }
    // ------------- 第二步：编写接口的真实实现类（核心业务逻辑）-------------
    /**
     * UserService 接口的真实实现类（包含实际业务逻辑）
     */
    public static class UserServiceImpl implements UserService {
        @Override
        public String getUserById(Integer id) {
            // 模拟真实的数据库查询逻辑
            try {
                TimeUnit.MILLISECONDS.sleep(50); // 模拟查询耗时
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "用户" + id + "：张三（真实数据库查询结果）";
        }

        @Override
        public void updateUserName(Integer id, String newName) {
            // 模拟真实的数据库更新逻辑
            try {
                TimeUnit.MILLISECONDS.sleep(80); // 模拟更新耗时
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("【真实业务逻辑】执行 SQL：UPDATE user SET name = '" + newName + "' WHERE id = " + id);
        }
    }

    // ------------- 第三步：自定义代理处理器（持有实现类实例，执行真实逻辑）-------------
    /**
     * 代理处理器：增强逻辑 + 调用实现类的真实方法
     */
    static class MyInvocationHandler implements InvocationHandler {
        // 持有接口实现类的实例（核心：代理不替代实现，只是增强）
        private final Object target;

        // 通过构造方法传入真实实现类实例
        public MyInvocationHandler(Object target) {
            this.target = target;
        }

        /**
         * 代理对象调用方法时触发：增强逻辑 + 真实方法执行
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 1. 前置增强：日志记录 + 性能计时开始
            System.out.println("[前置增强-日志] 执行方法：" + method.getName() + "，入参：" + (args == null ? "无" : args[0]));
            long startTime = System.currentTimeMillis();

            // 2. 核心：调用实现类的真实方法（这是代理的核心目的——执行真实逻辑）
            // method.invoke(目标对象, 方法入参)：反射调用实现类的方法
            Object result = method.invoke(target, args);

            // 3. 后置增强：性能计时结束 + 结果记录
            long costTime = System.currentTimeMillis() - startTime;
            System.out.println("[后置增强-性能监控] 方法 " + method.getName() + " 执行耗时：" + costTime + "ms");
            if (result != null) {
                System.out.println("[后置增强-结果记录] 方法返回结果：" + result);
            }
            System.out.println("----------------------------------------");

            // 返回真实方法的执行结果
            return result;
        }
    }


    // ------------- 第三步：生成代理对象并测试 -------------
    public static void main(String[] args) throws Exception {
        // 1. 创建接口的真实实现类实例（核心业务逻辑）
        UserService userServiceReal = new UserServiceImpl();

        // 2. 创建代理处理器，传入真实实现类实例
        InvocationHandler handler = new MyInvocationHandler(userServiceReal);

        // 3. 生成代理对象（代理对象 ≈ 增强版的 UserService 实现类）
        UserService userServiceProxy = (UserService) Proxy.newProxyInstance(
                UserService.class.getClassLoader(),
                new Class[]{UserService.class},
                handler
        );

        // 4. 调用代理对象的方法 → 触发 invoke() → 执行增强逻辑 + 真实实现类逻辑
        String user = userServiceProxy.getUserById(1);
        userServiceProxy.updateUserName(1, "李四");
    }
}
