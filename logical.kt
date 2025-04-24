class AdSkipService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == TYPE_WINDOW_STATE_CHANGED) {
            val rootNode = rootInActiveWindow ?: return
            detectSkipButton(rootNode)
        }
    }

    private fun detectSkipButton(node: AccessibilityNodeInfo) {
        // 通过 ID 匹配
        val skipButtons = node.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/skip")
        
        // 通过文本匹配（支持正则表达式）
        val textButtons = node.findAccessibilityNodeInfosByText(Regex("跳过|跳过广告|立即体验"))

        // 组合策略
        val targetNodes = skipButtons + textButtons
        targetNodes.firstOrNull()?.let {
            it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d("AdSkip", "广告已跳过")
        }
    }
}
// 节流处理（防止高频触发）
var lastSkipTime = 0L
fun throttleCheck(): Boolean {
    return System.currentTimeMillis() - lastSkipTime > 2000
}

// 异步线程处理
private val handler = Handler(Looper.getMainLooper())

fun asyncDetectAd() {
    handler.postDelayed({
        // 延迟 500ms 确保广告加载完成
        detectSkipButton(rootNode)
    }, 500)
}
