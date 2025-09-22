package com.tkw.navigation

// Compose Navigation으로 전환되어 더 이상 사용되지 않음
// 향후 필요 시 재활용 가능하도록 주석 처리
/*
import android.content.Context

sealed class DeepLinkDestination(val addressRes: Int) {
    object Home: DeepLinkDestination(R.string.home_deeplink)
    object Cup: DeepLinkDestination(R.string.cup_manage_deeplink)
    object Alarm: DeepLinkDestination(R.string.alarm_deeplink)
}

fun DeepLinkDestination.getDeepLink(context: Context) = context.getString(this.addressRes)
*/