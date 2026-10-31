package com.krish.foody.ui.fragments.instruction

import com.krish.foody.models.AnalyzedInstruction
import com.krish.foody.models.Step
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InstructionStepsTest {

    private fun step(n: Int, text: String?) = Step(null, null, n, text)

    @Test
    fun flattensSectionsAndRenumbers() {
        val lines = InstructionSteps.from(
            listOf(
                AnalyzedInstruction("", listOf(step(1, "Boil water."), step(2, "  Add pasta. "))),
                AnalyzedInstruction("Sauce", listOf(step(1, "Fry garlic."), step(2, "Add tomatoes.")))
            )
        )
        assertEquals(4, lines.size)
        assertEquals(InstructionLine(null, 1, "Boil water."), lines[0])
        assertEquals("Add pasta.", lines[1].text)
        assertEquals(InstructionLine("Sauce", 3, "Fry garlic."), lines[2])
        assertEquals(InstructionLine(null, 4, "Add tomatoes."), lines[3])
    }

    // Recipes without structured steps fall back to the source page / empty state.
    @Test
    fun missingOrBlankStepsGiveNoLines() {
        assertTrue(InstructionSteps.from(null).isEmpty())
        assertTrue(InstructionSteps.from(listOf(null, AnalyzedInstruction(null, null))).isEmpty())
        assertTrue(InstructionSteps.from(listOf(AnalyzedInstruction("x", listOf(step(1, " "), null)))).isEmpty())
    }
}
