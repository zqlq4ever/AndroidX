package com.luqian.androidx.widget.ecgview

import java.util.regex.Pattern

object StringToAscii {

    @JvmStatic
    fun extractNumbers(args: String): ArrayList<String> {
        val data = ArrayList<String>()
        val p = Pattern.compile("\\d{3,}")
        val m = p.matcher(args)
        while (m.find()) {
            data.add(m.group())
        }
        return data
    }

}
