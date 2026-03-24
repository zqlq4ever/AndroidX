package com.luqian.androidx.widget.ecgview

object StringToAscii {

    private val numberPattern = Regex("\\d{3,}")

    @JvmStatic
    fun extractNumbers(args: String): ArrayList<String> {
        return numberPattern.findAll(args)
            .map { it.value }
            .toCollection(ArrayList())
    }
}
