package com.example

data class SymbolCategory(
    val name: String,
    val icon: String, // Emoji or simple representation
    val symbols: List<String>
)

data class EquationPreset(
    val title: String,
    val description: String,
    val category: String,
    val unicode: String,
    val latex: String
)

object MathSymbols {
    val categories = listOf(
        SymbolCategory(
            name = "Operators",
            icon = "➕",
            symbols = listOf(
                "+", "−", "×", "÷", "=", "≠", "<", ">", "≤", "≥",
                "±", "∓", "∕", "∗", "∘", "∙", "∝", "≡", "≈", "≅",
                "∼", "≃", "≈", "≝", "∝", "∥", "∦", "⊥", "∟"
            )
        ),
        SymbolCategory(
            name = "Calculus & Analysis",
            icon = "∫",
            symbols = listOf(
                "∫", "∬", "∭", "∮", "∯", "∰", "∂", "∇", "∆", "∞",
                "√", "∛", "∜", "∑", "∏", "∐", "𝝅", "ℯ", "ⅈ", "ℏ",
                "𝜕", "Δ", "δ", "𝑑", "𝛿", "𝜀", "′", "″", "‴"
            )
        ),
        SymbolCategory(
            name = "Greek Lower",
            icon = "α",
            symbols = listOf(
                "α", "β", "γ", "δ", "ε", "ζ", "η", "θ", "ι", "κ",
                "λ", "μ", "ν", "ξ", "ο", "π", "ρ", "σ", "τ", "υ",
                "φ", "χ", "ψ", "ω", "ϑ", "ϕ", "ϖ", "ϱ", "ϵ"
            )
        ),
        SymbolCategory(
            name = "Greek Upper",
            icon = "Ω",
            symbols = listOf(
                "Α", "Β", "Γ", "Δ", "Ε", "Ζ", "Η", "Θ", "Ι", "Κ",
                "Λ", "Μ", "Ν", "Ξ", "Ο", "Π", "Ρ", "Σ", "Τ", "Υ",
                "Φ", "Χ", "Ψ", "Ω"
            )
        ),
        SymbolCategory(
            name = "Logic & Sets",
            icon = "∈",
            symbols = listOf(
                "∈", "∉", "∋", "∌", "⊂", "⊃", "⊆", "⊇", "⊄", "⊅",
                "∪", "∩", "∅", "∖", "℘", "ℵ", "ℶ", "∀", "∃", "∄",
                "∴", "∵", "⇒", "⇔", "¬", "∧", "∨", "⊕", "⊗", "⊢",
                "⊨", "□", "■", "∎", "⊤", "⊥"
            )
        ),
        SymbolCategory(
            name = "Subscripts",
            icon = "ₓ",
            symbols = listOf(
                "₀", "₁", "₂", "₃", "₄", "₅", "₆", "₇", "₈", "₉",
                "₊", "₋", "₌", "₍", "₎", "ₐ", "ₑ", "ₒ", "ₓ", "ᵧ",
                "ₕ", "ₖ", "ₗ", "ₘ", "ₙ", "ₚ", "ₛ", "ₜ"
            )
        ),
        SymbolCategory(
            name = "Superscripts",
            icon = "ˣ",
            symbols = listOf(
                "⁰", "¹", "²", "³", "⁴", "⁵", "⁶", "⁷", "⁸", "⁹",
                "⁺", "⁻", "⁼", "⁽", "⁾", "ⁿ", "ⁱ", "ᵃ", "ᵇ", "ᶜ",
                "ᵈ", "ᵉ", "ᵍ", "ʰ", "ⁱ", "ʲ", "ᵏ", "ˡ", "ᵐ", "ⁿ",
                "ᵒ", "ᵖ", "ʳ", "ˢ", "ᵗ", "ᵘ", "ᵛ", "ʷ", "ˣ", "ʸ", "ᶻ"
            )
        ),
        SymbolCategory(
            name = "Accents",
            icon = "◌̄",
            symbols = listOf(
                "\u0304", // Combining Macron (x̄)
                "\u0302", // Combining Circumflex (x̂)
                "\u20D7", // Combining Right Vector Arrow (x⃗)
                "\u0307", // Combining Dot Above (ẋ)
                "\u0308", // Combining Diaeresis/Double Dot (ẍ)
                "\u0303", // Combining Tilde (x̃)
                "\u0306", // Combining Breve (x̆)
                "\u0301"  // Combining Acute Accent (x́)
            )
        ),
        SymbolCategory(
            name = "Brackets & Arrows",
            icon = "⟨⟩",
            symbols = listOf(
                "(", ")", "[", "]", "{", "}", "⟨", "⟩", "⌈", "⌉",
                "⌊", "⌋", "|", "‖", "←", "→", "↑", "↓", "↔", "↕",
                "↖", "↗", "↘", "↙", "⇐", "⇒", "⇑", "⇓", "⇔"
            )
        )
    )

    val presets = listOf(
        EquationPreset(
            title = "Euler's Formula",
            description = "Establishes the fundamental relationship between trigonometric functions and complex exponential functions.",
            category = "Trigonometry",
            unicode = "ℯ^(ⅈ 𝜽) = cos 𝜽 + ⅈ sin 𝜽",
            latex = "e^{i\\theta} = \\cos\\theta + i\\sin\\theta"
        ),
        EquationPreset(
            title = "Double Angle Identity",
            description = "Expresses trigonometric functions of double angles in terms of single-angle functions.",
            category = "Trigonometry",
            unicode = "sin(𝟚 𝜽) = 𝟚 sin 𝜽 cos 𝜽",
            latex = "\\sin(2\\theta) = 2\\sin\\theta\\cos\\theta"
        ),
        EquationPreset(
            title = "Euler's Identity",
            description = "Considered the most beautiful equation in mathematics, linking five fundamental constants.",
            category = "Analysis",
            unicode = "ℯ^(𝝅 ⅈ) + 𝟙 = 𝟘",
            latex = "e^{i\\pi} + 1 = 0"
        ),
        EquationPreset(
            title = "Schrödinger Equation",
            description = "The fundamental equation of quantum mechanics describing wave function evolution.",
            category = "Physics",
            unicode = "ⅈ ℏ (𝜕𝜓 ∕ 𝜕𝒕) = Ĥ 𝜓",
            latex = "i\\hbar\\frac{\\partial\\psi}{\\partial t} = \\hat{H}\\psi"
        ),
        EquationPreset(
            title = "Quadratic Formula",
            description = "Provides solutions for any quadratic equation of the form ax² + bx + c = 0.",
            category = "Algebra",
            unicode = "𝒙 = (−𝒃 ± √(𝒃² − 𝟜𝒂𝒄)) ∕ 𝟚𝒂",
            latex = "x = \\frac{-b \\pm \\sqrt{b^2 - 4ac}}{2a}"
        ),
        EquationPreset(
            title = "Gaussian Integral",
            description = "The integral of the Gaussian function over the entire real line, central to probability.",
            category = "Calculus",
            unicode = "∫_{−∞}^{∞} ℯ^(−𝓍²) 𝑑𝓍 = √𝝅",
            latex = "\\int_{-\\infty}^{\\infty} e^{-x^2} dx = \\sqrt{\\pi}"
        ),
        EquationPreset(
            title = "Mass-Energy Equivalence",
            description = "Einstein's relativistic equation establishing the equivalence of mass and energy.",
            category = "Physics",
            unicode = "𝑬 = 𝒎 𝒄²",
            latex = "E = m c^2"
        ),
        EquationPreset(
            title = "Fourier Transform",
            description = "Decomposes a function of time into its constituent frequencies.",
            category = "Analysis",
            unicode = "̂𝒻(𝜉) = ∫_{−∞}^{∞} 𝒻(𝓍) ℯ^(−𝟚𝝅 ⅈ 𝓍 𝜉) 𝑑𝓍",
            latex = "\\hat{f}(\\xi) = \\int_{-\\infty}^{\\infty} f(x) e^{-2\\pi i x \\xi} dx"
        ),
        EquationPreset(
            title = "Gaussian Distribution (PDF)",
            description = "The probability density function for a normal distribution.",
            category = "Probability",
            unicode = "𝒻(𝓍) = 𝟙 ∕ (𝜎√(𝟚𝝅)) ℯ^(−(𝓍 − 𝜇)² ∕ 𝟚𝜎²)",
            latex = "f(x) = \\frac{1}{\\sigma\\sqrt{2\\pi}} e^{-\\frac{(x-\\mu)^2}{2\\sigma^2}}"
        ),
        EquationPreset(
            title = "Pythagorean Theorem",
            description = "Relates the lengths of the sides of a right-angled triangle.",
            category = "Geometry",
            unicode = "𝒂² + 𝒃² = 𝒄²",
            latex = "a^2 + b^2 = c^2"
        ),
        EquationPreset(
            title = "Entropy (Boltzmann)",
            description = "Statistical mechanics formula relating thermodynamic entropy to microstates.",
            category = "Physics",
            unicode = "𝑺 = 𝒌_𝑩 𝐥𝐧 𝑾",
            latex = "S = k_B \\ln W"
        )
    )
}
