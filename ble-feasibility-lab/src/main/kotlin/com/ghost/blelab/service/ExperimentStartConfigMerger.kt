package com.ghost.blelab.service

import com.ghost.blelab.experiment.ExperimentConfig
import com.ghost.blelab.experiment.TestCondition

/**
 * Pure logic for merging a UI-selected pending [TestCondition] into the
 * [ExperimentConfig] used to start an experiment.
 *
 * Extracted from [ServiceController] so the merge rule is unit-testable
 * without Android framework classes.
 */
object ExperimentStartConfigMerger {

    /**
     * If [pendingCondition] is non-null, return [config] with that condition
     * applied and invoke [onConsumed] exactly once (so the pending condition
     * is cleared). Otherwise return [config] unchanged and do not invoke
     * [onConsumed].
     */
    fun merge(
        config: ExperimentConfig,
        pendingCondition: TestCondition?,
        onConsumed: () -> Unit
    ): ExperimentConfig {
        if (pendingCondition == null) return config
        onConsumed()
        return config.copy(testCondition = pendingCondition)
    }
}
