/**
 * ClassName: package-info
 * Package: cn.xuguowen.juc.happens_before
 * Description:“happens-before” 规则是并发编程中重要的概念，用于定义在多线程环境下，操作之间的顺序关系，确保正确的同步行为。
 * Java 内存模型 (JMM) 使用 "happens-before" 规则来规定内存可见性和操作顺序，确保多线程程序在不同处理器和内存系统下能正确执行。
 * Java 内存模型（Java Memory Model, JMM）中的这些规则主要适用于实例变量（成员变量）和静态变量。这是因为局部变量是线程私有的，不存在可见性和一致性的问题。
 * happens-before 规定了对共享变量的写操作对其它线程的读操作可见，它是可见性与有序性的一套规则总结，抛开 happens-before 规则，JMM 并不能保证一个线程对共享变量的写，其它线程对该共享变量的读可见
 *
 * @Author 徐国文
 * @Create 2024/6/27 11:58
 * @Version 1.0
 */
package cn.xuguowen.juc.happens_before;