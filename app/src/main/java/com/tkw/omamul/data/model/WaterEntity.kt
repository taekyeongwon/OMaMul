package com.tkw.omamul.data.model

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class WaterEntity: EmbeddedRealmObject {
//    @PrimaryKey
//    var id: ObjectId = ObjectId.invoke()
    var amount: Int = 0
    var date: String = ""
}