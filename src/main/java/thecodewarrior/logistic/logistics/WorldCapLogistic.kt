package thecodewarrior.logistic.logistics

import com.teamwizardry.librarianlib.common.base.capability.CapabilityMod
import com.teamwizardry.librarianlib.common.base.capability.ICapabilityObjectProvider
import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.toRl
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import thecodewarrior.logistic.api.LogisticWorld
import thecodewarrior.logistic.logistics.nodes.Node
import thecodewarrior.logistic.logistics.nodes.NodeManager

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
    override fun addNode(pos: BlockPos, offset: Vec3d, type: NodeType): Boolean = nodeManager.addNode(pos, offset, type)
    override fun removeNode(pos: BlockPos): Boolean = nodeManager.removeNode(pos)

    /*==================================================================================================================
                    IMPLEMENTATION
    ==================================================================================================================*/
    @Save
    private val nodeManager = NodeManager(this)

//    private val netsByChunk = ChunkIndexer() // nets by chunk (chunk -> net id)
//
//    private val nets = IDStore<Network>() // nets (net id -> net)
//
//    fun getNodesForPos(pos: BlockPos): List<AStarNode> {
//        val chunk = ChunkPos(pos)
//        return netsByChunk[chunk]
//    }

    override fun writeCustomNBT(nbtTagCompound: NBTTagCompound) {
        super.writeCustomNBT(nbtTagCompound)
    }

    override fun readCustomNBT(nbtTagCompound: NBTTagCompound) {
        super.readCustomNBT(nbtTagCompound)
    }
}

