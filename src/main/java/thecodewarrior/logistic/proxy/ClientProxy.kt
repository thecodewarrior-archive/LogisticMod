package thecodewarrior.logistic.proxy

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import thecodewarrior.logistic.render.RenderNetworkLines

/**
 * Created by TheCodeWarrior
 */
class ClientProxy : CommonProxy() {

    override fun pre(e: FMLPreInitializationEvent) {
        super.pre(e)

        RenderNetworkLines
    }
}
