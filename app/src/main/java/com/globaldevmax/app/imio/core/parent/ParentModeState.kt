package com.globaldevmax.app.imio.core.parent

data class ParentModeState(
    val isActive: Boolean = false,
    val allowedMinutes: String = "",
    val endsAtMillis: Long = 0L,
    val sleepDialogVisible: Boolean = false,
    val recentMinutes: List<String> = emptyList()
)
