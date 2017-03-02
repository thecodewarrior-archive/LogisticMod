package thecodewarrior.logistic.proxy

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import thecodewarrior.logistic.client.ClientNetInfoTracker
import thecodewarrior.logistic.client.NodeNetRenderer

/**
 * Created by TheCodeWarrior
 */
class ClientProxy : CommonProxy() {

    override fun pre(e: FMLPreInitializationEvent) {
        super.pre(e)

        ClientNetInfoTracker
        NodeNetRenderer
//        RenderNetworkLines
//        RenderNetworkDrones
    }
}
