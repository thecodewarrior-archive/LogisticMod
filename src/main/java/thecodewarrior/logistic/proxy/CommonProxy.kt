package thecodewarrior.logistic.proxy

import com.teamwizardry.librarianlib.common.util.ifCap
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.ChunkWatchEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import thecodewarrior.logistic.api.LogisticAPI
import thecodewarrior.logistic.block.ModBlocks
import thecodewarrior.logistic.items.ModItems
import thecodewarrior.logistic.logistics.WorldCapLogistic

/**
 * Created by TheCodeWarrior
 */
open class CommonProxy {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    open fun pre(e: FMLPreInitializationEvent) {
        ModBlocks
        ModItems

        WorldCapLogistic.init()
    }

    open fun init(e: FMLInitializationEvent) {}

    open fun post(e: FMLPostInitializationEvent) {}


    @SubscribeEvent
    fun worldTick(e: TickEvent.WorldTickEvent) {
        val world = e.world
        if(world is WorldServer) {
            world.ifCap(WorldCapLogistic.cap, null) {
                it.tick(world)
            }
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

    @SubscribeEvent
    fun enterLoadDistance(e: ChunkWatchEvent.Watch) {
        (LogisticAPI.forWorld(e.player.world) as? WorldCapLogistic)?.sendChunkToPlayer(e.chunk, e.player)
    }

    fun syncWorldToPlayer(player: EntityPlayer, world: World) {
        if(player !is EntityPlayerMP)
            return
        world.ifCap(WorldCapLogistic.cap, null) { cap ->

        }
    }
}
