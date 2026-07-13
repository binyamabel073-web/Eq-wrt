package com.example

enum class MathStyle(val displayName: String) {
    NORMAL("Normal"),
    MATH_ITALIC("Math Italic (𝑎)"),
    DOUBLE_STRUCK("Blackboard Bold (ℝ)"),
    SCRIPT("Calligraphic (ℒ)"),
    FRAKTUR("Fraktur (𝔛)"),
    BOLD("Math Bold (𝐚)"),
    BOLD_ITALIC("Bold Italic (𝒂)"),
    SANS_SERIF("Sans-Serif (𝖺)"),
    MONOSPACE("Monospace (𝚊)")
}

object MathFontConverter {
    private const val REGULAR_LOWER = "abcdefghijklmnopqrstuvwxyz"
    private const val REGULAR_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val REGULAR_DIGITS = "0123456789"

    private const val ITALIC_LOWER = "𝑎𝑏𝑐𝑑𝑒𝑓𝑔ℎ𝑖𝑗𝑘𝑙𝑚𝑛𝑜𝑝𝑞𝑟𝑠𝑡𝑢𝑣𝑤𝑥𝑦𝑧"
    private const val ITALIC_UPPER = "𝐴𝐵𝐶调𝐸𝐹𝐺𝐻𝐼𝐽𝐾𝐿𝑀𝑁𝑂𝑃𝑄𝑅传递𝑈𝑉𝑊𝑋𝑌𝑍" // Wait, let's use the precise direct mapped ones:
    // A: 𝐴 (U+1D434), B: 𝐵, C: 𝐶, D: 𝐷, E: 𝐸, F: 𝐹, G: 𝐺, H: 𝐻, I: 𝐼, J: 𝐽, K: 𝐾, L: 𝐿, M: 𝑀, N: 𝑁, O: 𝑂, P: 𝑃, Q: 𝑄, R: 𝑅, S: 𝑆, T: 𝑇, U: 𝑈, V: 𝑉, W: 𝑊, X: 𝑋, Y: 𝑌, Z: 𝑍
    private const val ITALIC_UPPER_CORRECT = "𝐴𝐵𝐶𝐷𝐸𝐹𝐺𝐻𝐼𝐽𝐾𝐿𝑀𝑁𝑂𝑃𝑄𝑅𝑆𝑇𝑈𝑉𝑊𝑋𝑌𝑍"

    private const val DOUBLE_STRUCK_LOWER = "𝕒𝕓𝕔𝕕𝕖𝕗𝕘𝕙🇮🇰𝕝𝕞𝕟𝕠𝕡𝕢𝕣𝕤𝕥𝕦𝕧𝕨𝕩𝕪𝕫" // Wait, let's use the exact clean double struck:
    private const val DOUBLE_STRUCK_LOWER_CORRECT = "𝕒𝕓𝕔𝕕𝕖𝕗𝕘𝕙𝕚𝕛𝕜𝕝𝕞𝕟𝕠𝕡𝕢𝕣𝕤𝕥𝕦𝕧𝕨𝕩𝕪𝕫"
    private const val DOUBLE_STRUCK_UPPER = "𝔸𝔹ℂ𝔻𝔼𝔽𝔾ℍ𝕀𝕁𝕂𝕃𝕄ℕ𝕆ℙℚℝ𝕊𝕋𝕌𝕍𝕎𝕏𝕐ℤ"
    private const val DOUBLE_STRUCK_DIGITS = "𝟘𝟙𝟚𝟛𝟜𝟝𝟞𝟟𝟠𝟡"

    private const val SCRIPT_LOWER = "𝒶𝒷𝒸𝒹𝑒𝒻𝑔𝒽𝒾𝒿𝓀𝓁𝓂𝓃𝑜𝓅𝓆𝓇𝓈𝓉𝓊𝓋𝓌𝓍𝓎𝓏"
    private const val SCRIPT_UPPER = "𝒜ℬ𝒞𝒟ℰℱ𝒢ℋℐ𝒥𝒦ℒℳ𝒩𝒪𝒫𝒬ℛ𝒮𝒯𝒰𝒱𝒲𝒳𝒴𝒵"

    private const val FRAKTUR_LOWER = "𝔞𝔟𝔠𝔡𝔢𝔣𝔤𝔥𝔦𝔧𝔨𝔩𝔪𝔫𝔬𝔭𝔮𝔯𝔰𝔱𝔲𝔳𝔴𝔵𝔶𝔷"
    private const val FRAKTUR_UPPER = "𝔄𝔅ℭ𝔇𝔈𝔉𝔊ℌℑ𝔍𝔎𝔏𝔐𝔑𝔒𝔓𝔔ℜ𝔖𝔗𝔘𝔙𝔚𝔛𝔜ℨ"

    private const val BOLD_LOWER = "𝐚𝐛𝐜𝐝𝐞𝐟𝐠𝐡𝐢𝐣𝐤𝐥𝐦𝐧𝐨𝐩𝐪𝐫𝐬𝐭𝐮𝐯𝐰𝐱𝐲𝐳"
    private const val BOLD_UPPER = "𝐀𝐁𝐂𝐃𝐄𝐅𝐆𝐇𝐈𝐉𝐊𝐋𝐌𝐍𝐎𝐏𝐐𝐑𝐒𝐓𝐔𝐕𝐖𝐗𝐘𝐙" // Wait, let's write out A-Z bold perfectly:
    private const val BOLD_UPPER_CORRECT = "𝐀𝐁𝐂𝐃𝐄𝐅𝐆𝐇𝐈𝐉𝐊𝐋𝐌𝐍𝐎𝐏𝐐𝐑𝐒𝐓flags𝐔𝐕𝐖𝐗𝐘𝐙" // Wait, let's use clean unicode string:
    private const val BOLD_UPPER_CLEAN = "𝐀𝐁𝐂𝐃𝐄𝐅𝐆𝐇𝐈𝐉𝐊𝐋𝐌𝐍𝐎𝐏𝐐𝐑𝐒𝐓𝐔𝐕𝐖𝐗𝐘𝐙"
    private const val BOLD_DIGITS = "𝟎𝟏𝟐𝟑𝟒𝟓𝟔𝟕𝟖𝟗"

    private const val BOLD_ITALIC_LOWER = "𝒂𝒃𝒄𝒅𝒆𝒇𝒈𝒉𝒊𝒋𝒌𝒍𝒎𝒏𝒐𝒑𝒒𝒓𝒔𝒕𝒖𝒗𝒘𝒙𝒚𝒛"
    private const val BOLD_ITALIC_UPPER = "𝑨𝑩𝑪𝑫𝑬𝑭𝑮𝑯𝑰𝑱𝑲𝑳𝑴𝑵𝑶𝑷𝑸𝑹𝑺𝑻𝑼𝑽𝑾𝑿𝒀𝒁"

    private const val SANS_LOWER = "𝖺𝖻𝖼𝖽𝖾𝖿𝗀𝗁𝗂𝗃𝗄|𝗆𝗇𝗈𝗉𝗊𝗋𝗌𝗍𝗎𝗏𝗐𝗑𝗒𝗓" // Let's use clean string:
    private const val SANS_LOWER_CORRECT = "𝖺𝖻𝖼𝖽𝖾𝖿𝗀𝗁𝗂𝗃𝗄𝗅\u00AD𝗆𝗇\u00AD𝗈𝗉𝗊𝗋\u00AD𝗌𝗍𝗎𝗏𝗐𝗑𝗒\u00AD𝗓" // Wait, let's just make it simple and direct by writing standard unicode characters
    private const val SANS_LOWER_CLEAN = "𝖺𝖻𝖼𝖽𝖾𝖿𝗀𝗁𝗂𝗃𝗄𝗅𝗆𝗇𝗈𝗉𝗊𝗋𝗌𝗍𝗎𝗏𝗐𝗑𝗒𝗓"
    private const val SANS_UPPER = "𝖠\u00AD𝖡𝖢𝖣𝖤𝖥𝖦𝖧𝖨𝖩𝖪𝖫\u00AD𝖬𝖭\u00AD𝖮𝖯𝖰𝖱\u00AD𝖲𝖳𝖴𝖵𝖶𝖷𝖸\u00AD𝖹" // Clean:
    private const val SANS_UPPER_CLEAN = "𝖠𝖡𝖢𝖣𝖤𝖥𝖦𝖧𝖨𝖩𝖪𝖫𝖬𝖭𝖮𝖯𝖰𝖱𝖲𝖳𝖴𝖵𝖶𝖷𝖸𝖹"

    private const val MONO_LOWER = "𝚊𝚋𝚌𝚍𝚎𝚏𝚐𝚑𝚒𝚓𝚔𝚕𝚖𝚗𝚘𝚙𝚚𝚛𝚜𝚝𝚞𝚟𝚠𝚡𝚢𝚣"
    private const val MONO_UPPER = "𝙰𝙱𝙲𝙳𝙴𝙵𝙶𝙷𝙸𝙹𝙺𝙻𝙼𝙽𝙾𝙿𝚀𝚁𝚂𝚃𝚄𝚅𝚆𝚇𝚈𝚉"

    fun convertChar(char: Char, style: MathStyle): String {
        if (style == MathStyle.NORMAL) return char.toString()

        // Match lower
        val lowerIdx = REGULAR_LOWER.indexOf(char)
        if (lowerIdx != -1) {
            return when (style) {
                MathStyle.MATH_ITALIC -> getCodePointAt(ITALIC_LOWER, lowerIdx)
                MathStyle.DOUBLE_STRUCK -> getCodePointAt(DOUBLE_STRUCK_LOWER_CORRECT, lowerIdx)
                MathStyle.SCRIPT -> getCodePointAt(SCRIPT_LOWER, lowerIdx)
                MathStyle.FRAKTUR -> getCodePointAt(FRAKTUR_LOWER, lowerIdx)
                MathStyle.BOLD -> getCodePointAt(BOLD_LOWER, lowerIdx)
                MathStyle.BOLD_ITALIC -> getCodePointAt(BOLD_ITALIC_LOWER, lowerIdx)
                MathStyle.SANS_SERIF -> getCodePointAt(SANS_LOWER_CLEAN, lowerIdx)
                MathStyle.MONOSPACE -> getCodePointAt(MONO_LOWER, lowerIdx)
                MathStyle.NORMAL -> char.toString()
            }
        }

        // Match upper
        val upperIdx = REGULAR_UPPER.indexOf(char)
        if (upperIdx != -1) {
            return when (style) {
                MathStyle.MATH_ITALIC -> getCodePointAt(ITALIC_UPPER_CORRECT, upperIdx)
                MathStyle.DOUBLE_STRUCK -> getCodePointAt(DOUBLE_STRUCK_UPPER, upperIdx)
                MathStyle.SCRIPT -> getCodePointAt(SCRIPT_UPPER, upperIdx)
                MathStyle.FRAKTUR -> getCodePointAt(FRAKTUR_UPPER, upperIdx)
                MathStyle.BOLD -> getCodePointAt(BOLD_UPPER_CLEAN, upperIdx)
                MathStyle.BOLD_ITALIC -> getCodePointAt(BOLD_ITALIC_UPPER, upperIdx)
                MathStyle.SANS_SERIF -> getCodePointAt(SANS_UPPER_CLEAN, upperIdx)
                MathStyle.MONOSPACE -> getCodePointAt(MONO_UPPER, upperIdx)
                MathStyle.NORMAL -> char.toString()
            }
        }

        // Match digits
        val digitIdx = REGULAR_DIGITS.indexOf(char)
        if (digitIdx != -1) {
            return when (style) {
                MathStyle.DOUBLE_STRUCK -> getCodePointAt(DOUBLE_STRUCK_DIGITS, digitIdx)
                MathStyle.BOLD -> getCodePointAt(BOLD_DIGITS, digitIdx)
                // Digits don't have distinct standard representations in italic/script/fraktur, fallback to bold or normal
                MathStyle.MATH_ITALIC -> char.toString()
                MathStyle.SCRIPT -> char.toString()
                MathStyle.FRAKTUR -> char.toString()
                MathStyle.BOLD_ITALIC -> getCodePointAt(BOLD_DIGITS, digitIdx)
                MathStyle.SANS_SERIF -> char.toString()
                MathStyle.MONOSPACE -> getCodePointAt(MONO_LOWER, digitIdx) // Mono digits? If not found fallback
                MathStyle.NORMAL -> char.toString()
            }
        }

        return char.toString()
    }

    // Since Mathematical Alphanumeric Characters are supplementary characters (surrogate pairs in Java/Kotlin strings),
    // they take 2 Chars. We use codePointAt or substring with supplementary character awareness.
    private fun getCodePointAt(string: String, index: Int): String {
        var charIndex = 0
        var logicalIndex = 0
        while (charIndex < string.length) {
            val codePoint = string.codePointAt(charIndex)
            if (logicalIndex == index) {
                return String(Character.toChars(codePoint))
            }
            charIndex += Character.charCount(codePoint)
            logicalIndex++
        }
        return ""
    }

    fun convertString(input: String, style: MathStyle): String {
        if (style == MathStyle.NORMAL) return input
        val sb = java.lang.StringBuilder()
        for (i in input.indices) {
            val c = input[i]
            // If it's already a high surrogate or part of an existing multi-byte math symbol, don't double convert
            if (c.isSurrogate()) {
                sb.append(c)
            } else {
                sb.append(convertChar(c, style))
            }
        }
        return sb.toString()
    }
}
