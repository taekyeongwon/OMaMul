package com.tkw.database.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class SettingEntity: RealmObject {
    @PrimaryKey
    var id: Int = 0
    var intake: Int = 2000
    var unit: Int = 0   // legacy
    var unitString: String = "ml"   // "ml", "fl oz", "cup"
}