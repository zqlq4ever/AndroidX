package com.luqian.androidx.uitls

import android.util.Log
import java.io.DataOutputStream
import java.io.IOException

/**
 * Android 运行 adb shell 命令
 */
object RootCmd {
    /*
     *  使用方法
     *  打开移动网络 shell 命令：adb shell svc data enable
     *  注意添加 \n
     *  val commend = "svc data enable \n"
     *  RootCmd.execRootCmd(commend)
     */
    private const val TAG = "RootCmd"
    private var mHaveRoot = false

    /**
     * 判断机器 Android 是否已经 root，即是否获取 root 权限
     */
    fun haveRoot(): Boolean {
        if (!mHaveRoot) {
            // 通过执行测试命令来检测
            val ret = execRootCmd("echo test")
            if (ret != -1) {
                Log.i(TAG, "have root!")
                mHaveRoot = true
            } else {
                Log.i(TAG, "not root!")
            }
        } else {
            Log.i(TAG, "mHaveRoot = true, have root!")
        }
        return mHaveRoot
    }

    /**
     * 执行命令
     */
    fun execRootCmd(cmd: String): Int {
        var result = -1
        var dos: DataOutputStream? = null
        try {
            val process = Runtime.getRuntime().exec("su")
            dos = DataOutputStream(process.outputStream)
            dos.writeBytes("$cmd\n")
            dos.flush()
            dos.writeBytes("exit\n")
            dos.flush()
            process.waitFor()
            result = process.exitValue()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (dos != null) {
                try {
                    dos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }
}