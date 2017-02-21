package thecodewarrior.logistic.proxy

import com.teamwizardry.librarianlib.common.network.PacketHandler
import com.teamwizardry.librarianlib.common.util.ifCap
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.generics.SerializeGraphs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import thecodewarrior.logistic.block.ModBlocks
import thecodewarrior.logistic.items.ModItems
import thecodewarrior.logistic.logistics.*

/**
 * Created by TheCodeWarrior
 */
open class CommonProxy {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    open fun pre(e: FMLPreInitializationEvent) {
        SerializeGraphs

        ModBlocks
        ModItems

        CapabilityLogisticWorld.init()
    }

    open fun init(e: FMLInitializationEvent) {}

    open fun post(e: FMLPostInitializationEvent) {}

    @SubscribeEvent
    fun attach(e: AttachCapabilitiesEvent<World>) {
        if(!e.`object`.isRemote) { // don't attach on client side
            CapabilityLogisticWorld(e.`object`).attach(e)
        }
    }

    @SubscribeEvent
    fun loadWorld(e: PlayerEvent.PlayerLoggedInEvent) {
        syncWorldToPlayer(e.player, e.player.world)
    }

    @SubscribeEvent
    fun switchWorld(e: PlayerEvent.PlayerChangedDimensionEvent) {
        syncWorldToPlayer(e.player, DimensionManager.getWorld(e.toDim))
    }

    fun syncWorldToPlayer(player: EntityPlayer, world: World) {
        if(player !is EntityPlayerMP)
            return
        world.ifCap(CapabilityLogisticWorld.cap, null) { cap ->
            PacketHandler.NETWORK.sendTo(PacketAddNodes().apply {
                val arr = cap.nodeSet.toList()

                ids = arr.map { it.uuid }.toTypedArray()
                data = arr.map { BareLogisticNode(it.pos, NodeType.NAV) }.toTypedArray()
            }, player)
            PacketHandler.NETWORK.sendTo(PacketAddEdges().apply {
                val arr = cap.edgeSet.toList()

                idsFrom = arr.map { (cap.graph.getEdgeSource(it) as LogisticNode).uuid }.toTypedArray()
                idsTo = arr.map { (cap.graph.getEdgeTarget(it) as LogisticNode).uuid }.toTypedArray()
            }, player)
        }
    }
}
