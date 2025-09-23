package com.tkw.domain.model

data class Settings(
    val intake: Int = 2000,
    val unit: Int = 0,   // 0: ml, L 1: fl.oz (legacy)
    val unitString: String = "ml"   // "ml", "fl oz", "cup" (새로운 방식)
)