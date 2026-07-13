package com.example

import java.util.regex.Pattern

object LatexToUnicodeConverter {

    private val GREEK_MAP = mapOf(
        "alpha" to "α", "beta" to "β", "gamma" to "γ", "delta" to "δ",
        "epsilon" to "ε", "zeta" to "ζ", "eta" to "η", "theta" to "θ",
        "iota" to "ι", "kappa" to "κ", "lambda" to "λ", "mu" to "μ",
        "nu" to "ν", "xi" to "ξ", "pi" to "π", "rho" to "ρ",
        "sigma" to "σ", "tau" to "τ", "upsilon" to "υ", "phi" to "φ",
        "chi" to "χ", "psi" to "ψ", "omega" to "ω",
        "Gamma" to "Γ", "Delta" to "Δ", "Theta" to "Θ", "Lambda" to "Λ",
        "Xi" to "Ξ", "Pi" to "Π", "Sigma" to "Σ", "Upsilon" to "Υ",
        "Phi" to "Φ", "Psi" to "Ψ", "Omega" to "Ω"
    )

    private val OPERATOR_MAP = mapOf(
        "int" to "∫", "iint" to "∬", "iiint" to "∭", "oint" to "∮",
        "sum" to "∑", "prod" to "∏", "infty" to "∞", "partial" to "∂",
        "nabla" to "∇", "hbar" to "ℏ", "pm" to "±", "neq" to "≠",
        "approx" to "≈", "leq" to "≤", "geq" to "≥", "cdot" to "·",
        "times" to "×", "div" to "÷", "to" to "→", "rightarrow" to "→",
        "leftarrow" to "←", "Rightarrow" to "⇒", "Leftarrow" to "⇐",
        "Leftrightarrow" to "⇔", "leftrightarrow" to "↔"
    )

    private val SUBSCRIPT_MAP = mapOf(
        '0' to "₀", '1' to "₁", '2' to "₂", '3' to "₃", '4' to "₄",
        '5' to "₅", '6' to "₆", '7' to "₇", '8' to "₈", '9' to "₉",
        '+' to "₊", '-' to "₋", '=' to "₌", '(' to "₍", ')' to "₎",
        'a' to "ₐ", 'e' to "ₑ", 'h' to "ₕ", 'i' to "ᵢ", 'j' to "ⱼ",
        'k' to "ₖ", 'l' to "ₗ", 'm' to "ₘ", 'n' to "ₙ", 'o' to "ₒ",
        'p' to "ₚ", 'r' to "ᵣ", 's' to "ₛ", 't' to "ₛ", 'u' to "ᵤ",
        'v' to "ᵥ", 'x' to "ₓ", 'y' to "ᵧ",
        'A' to "ₐ", 'E' to "ₑ"
    )

    private val SUPERSCRIPT_MAP = mapOf(
        '0' to "⁰", '1' to "¹", '2' to "²", '3' to "³", '4' to "⁴",
        '5' to "⁵", '6' to "⁶", '7' to "⁷", '8' to "⁸", '9' to "⁹",
        '+' to "⁺", '-' to "⁻", '=' to "⁼", '(' to "⁽", ')' to "⁾",
        'n' to "ⁿ", 'i' to "ⁱ",
        'a' to "ᵃ", 'b' to "ᵇ", 'c' to "ᶜ", 'd' to "ᵈ", 'e' to "ᵉ",
        'f' to "𝒻", 'g' to "ᵍ", 'h' to "ʰ", 'i' to "ⁱ", 'j' to "ʲ",
        'k' to "ᵏ", 'l' to "ˡ", 'm' to "ᵐ", 'n' to "ⁿ", 'o' to "ᵒ",
        'p' to "ᵖ", 'r' to "ʳ", 's' to "ˢ", 't' to "ᵗ", 'u' to "ᵘ",
        'v' to "ᵛ", 'w' to "ʷ", 'x' to "ˣ", 'y' to "ʸ", 'z' to "ᶻ",
        'A' to "ᴬ", 'B' to "ᴮ", 'D' to "ᴰ", 'E' to "ᴱ", 'G' to "ᴳ",
        'H' to "ᴴ", 'I' to "ᴵ", 'J' to "ᴶ", 'K' to "𝖪", 'L' to "ᴸ",
        'M' to "ᴹ", 'N' to "ᴺ", 'O' to "ᴼ", 'P' to "ᴾ", 'R' to "ᴿ",
        'T' to "ᵀ", 'U' to "ᵁ", 'V' to "ⱽ", 'W' to "ᵂ"
    )

    // Helper to find matching brace index
    private fun findMatchingBrace(text: String, openBraceIdx: Int): Int {
        var balance = 1
        for (i in (openBraceIdx + 1) until text.length) {
            if (text[i] == '{') balance++
            else if (text[i] == '}') {
                balance--
                if (balance == 0) return i
            }
        }
        return -1
    }

    // Helper to convert string to subscripts
    private fun toSubscript(input: String): String {
        val sb = StringBuilder()
        for (char in input) {
            sb.append(SUBSCRIPT_MAP[char] ?: char.toString())
        }
        return sb.toString()
    }

    // Helper to convert string to superscripts
    private fun toSuperscript(input: String): String {
        val sb = StringBuilder()
        for (char in input) {
            sb.append(SUPERSCRIPT_MAP[char] ?: char.toString())
        }
        return sb.toString()
    }

    fun convert(latex: String): String {
        if (latex.isBlank()) return ""

        var result = latex

        // 1. Remove unnecessary spaces around LaTeX commands to make parsing simpler,
        // but keep normal word spacing.
        result = result.replace(Regex("\\s*\\\\\\s*"), "\\\\")

        // 2. Handle fractions: \frac{num}{den} -> (num) ∕ (den)
        while (true) {
            val fracIdx = result.indexOf("\\frac{")
            if (fracIdx == -1) break
            
            val numOpenIdx = fracIdx + 5
            val numCloseIdx = findMatchingBrace(result, numOpenIdx)
            if (numCloseIdx == -1) break
            
            val numContent = result.substring(numOpenIdx + 1, numCloseIdx)
            
            // Now look for denominator
            val nextOpenIdx = numCloseIdx + 1
            if (nextOpenIdx < result.length && result[nextOpenIdx] == '{') {
                val denCloseIdx = findMatchingBrace(result, nextOpenIdx)
                if (denCloseIdx != -1) {
                    val denContent = result.substring(nextOpenIdx + 1, denCloseIdx)
                    
                    // Format num/den elegantly:
                    val formattedFraction = if (numContent.length == 1 && denContent.length == 1) {
                        "$numContent ∕ $denContent"
                    } else {
                        "($numContent) ∕ ($denContent)"
                    }
                    result = result.replaceRange(fracIdx, denCloseIdx + 1, formattedFraction)
                    continue
                }
            }
            break
        }

        // 3. Handle square root: \sqrt{arg} -> √(arg)
        while (true) {
            val sqrtIdx = result.indexOf("\\sqrt{")
            if (sqrtIdx == -1) break
            
            val openIdx = sqrtIdx + 5
            val closeIdx = findMatchingBrace(result, openIdx)
            if (closeIdx != -1) {
                val arg = result.substring(openIdx + 1, closeIdx)
                val formattedSqrt = "√($arg)"
                result = result.replaceRange(sqrtIdx, closeIdx + 1, formattedSqrt)
                continue
            }
            break
        }
        // Simplify plain square root if written without braces like \sqrt x
        result = result.replace(Regex("\\\\sqrt\\s*([a-zA-Z0-9])"), "√$1")

        // 4. Handle accents: \hat{x} -> x̂, \bar{x} -> x̄, \vec{x} -> x⃗, \dot{x} -> ẋ, \ddot{x} -> ẍ, \tilde{x} -> x̃
        val accents = listOf(
            "hat" to "\u0302",
            "bar" to "\u0304",
            "vec" to "\u20D7",
            "dot" to "\u0307",
            "ddot" to "\u0308",
            "tilde" to "\u0303",
            "acute" to "\u0301"
        )
        for ((cmd, combining) in accents) {
            while (true) {
                val idx = result.indexOf("\\$cmd{")
                if (idx == -1) break
                val openIdx = idx + cmd.length + 1
                val closeIdx = findMatchingBrace(result, openIdx)
                if (closeIdx != -1) {
                    val content = result.substring(openIdx + 1, closeIdx)
                    // Apply combining mark to each char inside (mostly single letter)
                    val applied = content.map { "$it$combining" }.joinToString("")
                    result = result.replaceRange(idx, closeIdx + 1, applied)
                    continue
                }
                break
            }
            // Non-braced fallback: \hat x -> x̂
            result = result.replace(Regex("\\\\$cmd\\s*([a-zA-Z])"), "$1$combining")
        }

        // 5. Replace other operators and symbols (\int, \sum, \alpha, \theta, etc.)
        for ((cmd, unicode) in GREEK_MAP) {
            result = result.replace(Regex("\\\\$cmd\\b"), unicode)
        }
        for ((cmd, unicode) in OPERATOR_MAP) {
            result = result.replace(Regex("\\\\$cmd\\b"), unicode)
        }

        // 6. Handle Superscripts and Subscripts
        // Superscripts with curly braces: ^{content}
        while (true) {
            val idx = result.indexOf("^{")
            if (idx == -1) break
            val closeIdx = findMatchingBrace(result, idx + 1)
            if (closeIdx != -1) {
                val content = result.substring(idx + 2, closeIdx)
                val superContent = toSuperscript(content)
                result = result.replaceRange(idx, closeIdx + 1, superContent)
                continue
            }
            break
        }
        // Subscripts with curly braces: _{content}
        while (true) {
            val idx = result.indexOf("_{")
            if (idx == -1) break
            val closeIdx = findMatchingBrace(result, idx + 1)
            if (closeIdx != -1) {
                val content = result.substring(idx + 2, closeIdx)
                val subContent = toSubscript(content)
                result = result.replaceRange(idx, closeIdx + 1, subContent)
                continue
            }
            break
        }

        // Superscripts with single characters: ^c
        val superPattern = Pattern.compile("\\^([a-zA-Z0-9+\\-=()])")
        val superMatcher = superPattern.matcher(result)
        val superSb = StringBuffer()
        while (superMatcher.find()) {
            val raw = superMatcher.group(1) ?: ""
            superMatcher.appendReplacement(superSb, toSuperscript(raw))
        }
        superMatcher.appendTail(superSb)
        result = superSb.toString()

        // Subscripts with single characters: _c
        val subPattern = Pattern.compile("_([a-zA-Z0-9+\\-=()])")
        val subMatcher = subPattern.matcher(result)
        val subSb = StringBuffer()
        while (subMatcher.find()) {
            val raw = subMatcher.group(1) ?: ""
            subMatcher.appendReplacement(subSb, toSubscript(raw))
        }
        subMatcher.appendTail(subSb)
        result = subSb.toString()

        // 7. Protect standard multi-letter keywords and trigonometric functions so they are NOT italicized.
        // We replace them with placeholders, perform variable italicization, and restore them.
        val protectedWords = listOf(
            "sin", "cos", "tan", "csc", "sec", "cot", "sinh", "cosh", "tanh",
            "arcsin", "arccos", "arctan", "log", "ln", "lim", "det", "max", "min", "exp",
            "dx", "dy", "dz", "dt", "df"
        )
        val placeholders = protectedWords.mapIndexed { idx, word ->
            word to "__MATH_WORD_${idx}__"
        }

        var protectedResult = result
        for ((word, placeholder) in placeholders) {
            // Match whole word with boundaries
            protectedResult = protectedResult.replace(Regex("\\b$word\\b"), placeholder)
        }

        // 8. Convert remaining individual variables to Mathematical Italic.
        // In mathematical equations, English letters used as variables are italicized.
        // Standard Greek letters and combining accents are left as is.
        val styledSb = java.lang.StringBuilder()
        var i = 0
        while (i < protectedResult.length) {
            val c = protectedResult[i]
            
            // If it's a letter (a-z, A-Z), let's check if it's a variable (e.g. not part of a placeholder)
            if (c in 'a'..'z' || c in 'A'..'Z') {
                // Read full alphabetic word
                var end = i
                while (end < protectedResult.length && protectedResult[end] in 'a'..'z' || (end < protectedResult.length && protectedResult[end] in 'A'..'Z')) {
                    end++
                }
                val word = protectedResult.substring(i, end)
                if (word.startsWith("__MATH_WORD_")) {
                    // It's a protected function placeholder, keep it unchanged
                    styledSb.append(word)
                } else {
                    // Convert each variable character to math italic
                    for (char in word) {
                        styledSb.append(MathFontConverter.convertChar(char, MathStyle.MATH_ITALIC))
                    }
                }
                i = end
            } else {
                styledSb.append(c)
                i++
            }
        }
        result = styledSb.toString()

        // 9. Restore protected words with beautiful upright styling
        for ((word, placeholder) in placeholders) {
            // Keep trigonometric functions and d-differentials elegant
            val styledWord = when (word) {
                "dx" -> "𝑑𝓍"
                "dy" -> "𝑑𝓎"
                "dz" -> "𝑑𝓏"
                "dt" -> "𝑑𝒕"
                "df" -> "𝑑𝒻"
                else -> word // plain upright style
            }
            result = result.replace(placeholder, styledWord)
        }

        // 10. Clean up remaining LaTeX characters like braces or backslashes
        result = result.replace("{", "").replace("}", "").replace("\\", "")

        return result.trim()
    }
}
