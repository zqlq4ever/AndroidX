package com.fubao.baselibrary.base

import java.io.IOException

class BaseException : IOException {

    companion object {
        /**
         * 解析数据失败
         */
        const val PARSE_ERROR = 1001
        const val PARSE_ERROR_MSG = "解析数据失败"

        /**
         * 网络问题
         */
        const val BAD_NETWORK = 1002
        const val BAD_NETWORK_MSG = "网络问题"

        /**
         * 连接错误
         */
        const val CONNECT_ERROR = 1003
        const val CONNECT_ERROR_MSG = "连接错误"

        /**
         * 连接超时
         */
        const val CONNECT_TIMEOUT = 1004
        const val CONNECT_TIMEOUT_MSG = "连接超时"

        /**
         * 未知错误
         */
        const val OTHER = 1005
        const val OTHER_MSG = "未知错误"
    }

    var errorMsg: String? = null
        private set
    var errorCode: Int = 0
        private set

    constructor(errorMsg: String?, cause: Throwable?) : super(errorMsg, cause) {
        this.errorMsg = errorMsg
    }

    constructor(message: String?, cause: Throwable?, errorCode: Int) : super(message, cause) {
        this.errorCode = errorCode
        this.errorMsg = message
    }

    constructor(message: String?, errorCode: Int) : super(message) {
        this.errorCode = errorCode
        this.errorMsg = message
    }
}
