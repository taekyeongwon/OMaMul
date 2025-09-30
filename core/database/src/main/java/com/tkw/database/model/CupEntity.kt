package com.tkw.database.model

import io.realm.kotlin.types.EmbeddedRealmObject
import org.mongodb.kbson.ObjectId

class CupEntity: EmbeddedRealmObject {
    var cupId: String = ObjectId.invoke().toHexString()
    var cupName: String = ""
    var cupAmount: Int = 0
    var cupUnit: String = "ML" // UnitType enum의 name을 저장 (ML, L, FL_OZ)

    companion object {
        const val DEFAULT_CUP_LIST_ID = "default_cup_list_id"
    }
}