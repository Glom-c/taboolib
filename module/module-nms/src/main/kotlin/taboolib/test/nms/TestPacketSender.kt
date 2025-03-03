package taboolib.test.nms

import org.tabooproject.reflex.Reflex.Companion.invokeConstructor
import org.tabooproject.reflex.Reflex.Companion.unsafeInstance
import taboolib.common.Inject
import taboolib.common.Test
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.*
import taboolib.platform.util.onlinePlayers

/**
 * TabooLib
 * taboolib.module.nms.test.TestPacketSender
 *
 * @author 坏黑
 * @since 2023/8/5 00:56
 */
@Inject
object TestPacketSender : Test() {

    var testSend = false
    var testReceive = false

    override fun check(): List<Result> {
        val result = arrayListOf<Result>()
        result += sandbox("NMS:getConnections()") { nmsProxy<ConnectionGetter>().getConnections() }
        val player = onlinePlayers.firstOrNull()
        if (player != null) {
            result += sandbox("NMS:getConnection(Player)") { PacketSender.getConnection(player) }
            result += sandbox("NMS:sendPacketBlocking(Player, Any)") {
                try {
                    player.sendPacketBlocking(nmsClass("PacketPlayOutKeepAlive").unsafeInstance())
                } catch (ex: ClassNotFoundException) {
                    // 1.20.2 移除了 PacketPlayOutKeepAlive
                    player.sendPacketBlocking(nmsClass("PacketPlayOutViewDistance").invokeConstructor(8))
                }
            }
            result += sandbox("NMS:sendBundlePacketBlocking(Player, Any)") {
                try {
                    player.sendBundlePacketBlocking(nmsClass("PacketPlayOutKeepAlive").unsafeInstance())
                } catch (ex: ClassNotFoundException) {
                    player.sendBundlePacketBlocking(nmsClass("PacketPlayOutViewDistance").invokeConstructor(8))
                }
            }
            result += if (testSend) Success.of("NMS:PacketSendEvent") else Failure.of("NMS:PacketSendEvent", "NOT_TRIGGERED")
            result += if (testReceive) Success.of("NMS:PacketReceiveEvent") else Failure.of("NMS:PacketReceiveEvent", "NOT_TRIGGERED")
        }
        return result
    }

//    @SubscribeEvent
//    private fun onSend(e: PacketSendEvent) {
//        testSend = true
//    }
//
//    @SubscribeEvent
//    private fun onReceive(e: PacketReceiveEvent) {
//        testReceive = true
//    }
}