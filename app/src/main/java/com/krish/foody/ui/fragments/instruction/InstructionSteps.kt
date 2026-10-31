package com.krish.foody.ui.fragments.instruction

import com.krish.foody.models.AnalyzedInstruction

/** One line of the native instructions list: either a section title or a numbered step. */
data class InstructionLine(val section: String?, val number: Int, val text: String)

object InstructionSteps {

    /**
     * Flattens Spoonacular's analyzedInstructions into display lines. Sections without a name are
     * merged; steps are renumbered so the list always reads 1, 2, 3... even across sections.
     */
    fun from(instructions: List<AnalyzedInstruction?>?): List<InstructionLine> {
        val lines = mutableListOf<InstructionLine>()
        var n = 0
        instructions.orEmpty().filterNotNull().forEach { block ->
            var section = block.name?.trim()?.takeIf { it.isNotEmpty() }
            block.steps.orEmpty().filterNotNull().forEach { step ->
                val text = step.step?.trim().orEmpty()
                if (text.isNotEmpty()) {
                    lines += InstructionLine(section, ++n, text)
                    section = null
                }
            }
        }
        return lines
    }
}
