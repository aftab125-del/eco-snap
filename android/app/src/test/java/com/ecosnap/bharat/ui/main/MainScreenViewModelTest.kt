package com.ecosnap.bharat.ui.main

import com.ecosnap.bharat.data.AppLanguage
import com.ecosnap.bharat.data.AppMode
import com.ecosnap.bharat.data.DataRepository
import com.ecosnap.bharat.data.ImpactMetrics
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Test

class DataRepositoryTest {
    @Test
    fun testInitialAppModeIsFamily() {
        val repository = DataRepository()
        assertEquals(AppMode.FAMILY, repository.appMode.value)
    }

    @Test
    fun testToggleAppMode() {
        val repository = DataRepository()
        repository.toggleAppMode()
        assertEquals(AppMode.SOCIETY, repository.appMode.value)
        repository.toggleAppMode()
        assertEquals(AppMode.FAMILY, repository.appMode.value)
    }

    @Test
    fun testInitialLanguageIsHindiAndCanChange() {
        val repository = DataRepository()
        assertEquals(AppLanguage.HINDI, repository.appLanguage.value)

        repository.setAppLanguage(AppLanguage.ENGLISH)
        assertEquals(AppLanguage.ENGLISH, repository.appLanguage.value)

        repository.toggleAppLanguage()
        assertEquals(AppLanguage.HINDI, repository.appLanguage.value)
    }

    @Test
    fun testGetSolutionsForPlasticBottle() {
        val repository = DataRepository()
        val solutions = repository.getSolutionsForItem("preset_bottle")
        assertNotNull(solutions.jugaad)
        assertNotNull(solutions.energyResource)
        assertNotNull(solutions.scrapDisposal)
        assertEquals("Self-Watering Planter", solutions.jugaad.titleEnglish)
    }

    @Test
    fun testLogSolutionAdoptedIncreasesSavings() {
        val repository = DataRepository()
        val initialSavings = repository.userStats.value.totalSavedInr
        repository.logSolutionAdopted(ImpactMetrics(40, 0.08), scrapValueInr = 12)
        assertEquals(initialSavings + 12, repository.userStats.value.totalSavedInr)
    }
}
