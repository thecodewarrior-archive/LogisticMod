package thecodewarrior.logistic

import com.teamwizardry.librarianlib.common.base.ModCreativeTab
import com.teamwizardry.librarianlib.common.core.LoggerBase
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import thecodewarrior.logistic.items.ModItems
import thecodewarrior.logistic.proxy.CommonProxy

/**
 * Created by TheCodeWarrior
 */


@Mod(modid = LogisticMod.MODID, version = LogisticMod.VERSION, name = LogisticMod.MODNAME, modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object LogisticMod {

    @Mod.EventHandler
    fun preInit(e: FMLPreInitializationEvent) {
        PROXY.pre(e)
    }

    @Mod.EventHandler
    fun init(e: FMLInitializationEvent) {
        PROXY.init(e)
    }

    @Mod.EventHandler
    fun postInit(e: FMLPostInitializationEvent) {
        PROXY.post(e)
    }


    const val MODID = "logistic"
    const val MODNAME = "Logistic"
    const val VERSION = "0.0.0"
    const val CLIENT = "thecodewarrior.logistic.proxy.ClientProxy"
    const val SERVER = "thecodewarrior.logistic.proxy.CommonProxy"

    @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
    lateinit var PROXY: CommonProxy
        @JvmStatic @JvmName("proxy") get

    val tab: ModCreativeTab = object : ModCreativeTab("tab") {
        override val iconStack: ItemStack
            get() = ItemStack(ModItems.navPoint)

        init {
            registerDefaultTab()
        }
    }
}

object LogisticLog : LoggerBase("Logistic")
