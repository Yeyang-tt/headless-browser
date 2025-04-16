package com.yeyang.browser.headless.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
@UtilityClass
public class CommandUtil {

    /**
     * 自动
     *
     * @param command 命令
     */
    public void execAuto(String command) {
        String osName = System.getProperty("os.name");
        log.debug("命令行工具-执行命令-系统名称：{}", osName);
        if (osName.contains("Windows")) {
            execWindow(command);
        } else {
            execLinux(command);
        }
    }

    /**
     * window
     *
     * @param command 命令
     */
    public void execWindow(String command) {
        // Process可以控制该子进程的执行或获取该子进程的信息
        Process process = null;
        try {
            // exec()方法指示Java虚拟机创建一个子进程执行指定的可执行程序，并返回与该子进程对应的Process对象实例。
            process = Runtime.getRuntime().exec(command);
            // 下面两个可以获取输入输出流
            InputStream errorStream = process.getErrorStream();
            InputStream inputStream = process.getInputStream();

            // 等待子进程完成再往下执行，返回值是子线程执行完毕的返回值,返回0表示正常结束
            int exitStatus = process.waitFor();
            // 第二种接受返回值的方法
            // 接收执行完毕的返回值
            int i = process.exitValue();
            log.debug("命令行工具-windows命令：{}：{}", command, i);
            // if (i != 0) {
            // 	windowExec(command);
            // }
        } catch (Exception e) {
            log.error("命令行工具-windows命令异常：", e);
        } finally {
            if (process != null) {
                process.destroy(); // 销毁子进程
            }
        }
    }

    /**
     * linux
     *
     * @param command 命令
     */
    public void execLinux(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            int exitStatus = 0;
            int errorCount = 1;
            // 等待子进程完成再往下执行，返回值是子线程执行完毕的返回值,返回0表示正常结束
            exitStatus = process.waitFor();
            // 第二种接受返回值的方法
            // 接收执行完毕的返回值
            int i = process.exitValue();
            log.debug("命令行工具-linux命令：{}：{}", command, i);
            // if (i != 0) {
            // 	LinuxExec(cmd);
            // }
        } catch (Exception e) {
            log.error("命令行工具-linux命令异常：", e);

        } finally {
            if (process != null) {
                // 销毁子进程
                process.destroy();
            }
        }
    }

}