package com.tkw.navigation

// Compose Navigation으로 전환되어 더 이상 사용되지 않음
// 향후 필요 시 재활용 가능하도록 주석 처리
/*
import android.content.Context
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions

fun NavController.deepLinkNavigateTo(
    context: Context,
    deepLinkDestination: DeepLinkDestination,
    popupTo: Boolean = false
) {
    val builder = NavOptions.Builder()

    if(popupTo) {
        builder.setPopUpTo(graph.startDestinationId, true)
    }

    navigate(
        buildDeepLink(context, deepLinkDestination),
        builder.build()
    )
}

private fun buildDeepLink(context: Context, destination: DeepLinkDestination) =
    NavDeepLinkRequest.Builder
        .fromUri(destination.getDeepLink(context).toUri())
        .build()
*/