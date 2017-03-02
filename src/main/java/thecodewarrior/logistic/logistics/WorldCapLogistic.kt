package thecodewarrior.logistic.logistics

import com.teamwizardry.librarianlib.common.base.capability.CapabilityMod
import com.teamwizardry.librarianlib.common.base.capability.ICapabilityObjectProvider
import com.teamwizardry.librarianlib.common.network.PacketHandler
import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.toRl
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import thecodewarrior.logistic.api.LogisticWorld
import thecodewarrior.logistic.logistics.nodes.Node
import thecodewarrior.logistic.logistics.nodes.NodeManager
import thecodewarrior.logistic.logistics.sync.ChangeTracker
import thecodewarrior.logistic.packets.PacketNodes

/**
 * Created by TheCodeWarrior
 */
class WorldCapLogistic(val world: World) : CapabilityMod("logistic:logisticWorld".toRl()), LogisticWorld {
    companion object {
        @JvmStatic
        @CapabilityInject(WorldCapLogistic::class)
        lateinit var cap: Capability<WorldCapLogistic>
            private set

        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        @SubscribeEvent
        fun attach(e: AttachCapabilitiesEvent<World>) {
            if(!e.`object`.isRemote) { // don't attach on client side
                WorldCapLogistic(e.`object`).attach(e)
            }
        }

        fun init() {
            register(WorldCapLogistic::class.java, ICapabilityObjectProvider {
                cap
            })
            MinecraftForge.EVENT_BUS.register(WorldCapLogistic::class.java)
        }
    }

    /*==================================================================================================================
                    API
    ==================================================================================================================*/

    override fun getNode(pos: BlockPos): Node? = nodeManager.getNode(pos)
    override fun addNode(pos: BlockPos, offset: Vec3d, logistic: NodeLogisticData?): Boolean = nodeManager.addNode(pos, offset, logistic)
    override fun removeNode(pos: BlockPos): Boolean = nodeManager.removeNode(pos)

    /*==================================================================================================================
                    PRIVATE API
    ==================================================================================================================*/

    fun  sendChunkToPlayer(chunk: ChunkPos, player: EntityPlayerMP) {
        val nodes = nodeManager.getNodesInChunk(chunk)
        PacketHandler.NETWORK.sendTo(PacketNodes(nodes, listOf()), player)
    }

    /*==================================================================================================================
                    IMPLEMENTATION
    ==================================================================================================================*/

    @Save
    private val nodeManager = NodeManager(this)

    internal val changeTracker = ChangeTracker(this)

    override fun writeCustomNBT(nbtTagCompound: NBTTagCompound) {
        super.writeCustomNBT(nbtTagCompound)
    }

    override fun readCustomNBT(nbtTagCompound: NBTTagCompound) {
        super.readCustomNBT(nbtTagCompound)
    }

    fun tick(world: WorldServer) {
        changeTracker.sync(world)
    }

}

