package com.luqian.androidx.widget.ecgview

import java.util.ArrayList
import java.util.regex.Pattern

object StringToAscii {

    private fun toHexUtil(n: Int): String {
        return when (n) {
            10 -> "A"
            11 -> "B"
            12 -> "C"
            13 -> "D"
            14 -> "E"
            15 -> "F"
            else -> n.toString()
        }
    }

    @JvmStatic
    fun toHex(n: Int): String {
        val sb = StringBuilder()
        if (n / 16 == 0) {
            return toHexUtil(n)
        } else {
            val t = toHex(n / 16)
            val nn = n % 16
            sb.append(t).append(toHexUtil(nn))
        }
        return sb.toString()
    }

    @JvmStatic
    fun parseAscii(str: String): String {
        val sb = StringBuilder()
        val bs = str.toByteArray()
        for (b in bs) {
            sb.append(toHex(b.toInt()))
        }
        return sb.toString()
    }

    @JvmStatic
    fun test(args: String): ArrayList<String> {
        val data = ArrayList<String>()
        // 这个 3 是指连续数字的最少个数
        val p = Pattern.compile("\\d{3,}")
        val m = p.matcher(args)
        while (m.find()) {
            data.add(m.group())
        }
        return data
    }
}
