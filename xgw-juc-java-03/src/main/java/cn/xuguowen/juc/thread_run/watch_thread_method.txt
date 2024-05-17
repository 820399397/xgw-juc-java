1、windows操作系统
    ① 任务管理器可以查看进程和线程数，也可以用来杀死进程
    ② tasklist 查看进程 例如：tasklist | findstr java / jps 查看具体的java程序进程名称
    ③ taskkill 杀死进程 例如：taskkill /F /PID <进程号>  其中 /F 是强制终止进程的选项  /PID 用于指定要终止的进程的ID（即进程号）

2、linux操作系统
    ① ps -ef | grep java 查看java进程
    ② kill -9 <进程号> 杀死进程
    ③ top -H -p <进程号> 查看某个进程（PID）的所有线程，找到线程号，使用kill -9 <线程号> 杀死线程
    ④ ps -fT -p <PID> 查看某个进程（PID）的所有线程

3、Java的一些工具
    ① jps 查看所有 Java 进程
    ② jstack <PID> 查看某个 Java 进程（PID）的线程堆栈信息，只能看到执行这个命令那一瞬间的堆栈信息
    ③ jconsole 查看某个 Java 进程（PID）的线程堆栈信息，可以动态的查看线程堆栈信息，并且可以动态的结束某个线程(图形化界面)
      jconsole 远程监控配置，需要以如下方式运行你的 java 类
      java -Djava.rmi.server.hostname=192.168.56.100 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=12345 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false VisitThreadRun
