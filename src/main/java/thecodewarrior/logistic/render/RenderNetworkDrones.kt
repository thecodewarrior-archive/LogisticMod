package thecodewarrior.logistic.render

import com.teamwizardry.librarianlib.client.core.color
import com.teamwizardry.librarianlib.client.core.pos
import com.teamwizardry.librarianlib.client.event.CustomWorldRenderEvent
import com.teamwizardry.librarianlib.common.util.plus
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import thecodewarrior.logistic.logistics.ClientLogisticTracker
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
object RenderNetworkDrones {

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun render(e: CustomWorldRenderEvent) {
        val radiusXZ = 0.15;
        val radiusY = 0.05;

        val tess = Tessellator.getInstance()
        val vb = tess.buffer

        GlStateManager.depthMask(false)

        GlStateManager.depthFunc(GL11.GL_GREATER)

        vb.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR)
        ClientLogisticTracker.drones.forEach {
            vb.pos(it.value.currentPos(e.partialTicks)).color(Color(128, 0, 0, 255)).endVertex()
        }
        tess.draw()

        GlStateManager.depthMask(true)
        GlStateManager.depthFunc(GL11.GL_LEQUAL)
        GlStateManager.disableCull()

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
        ClientLogisticTracker.drones.forEach {
            val p = it.value.currentPos(e.partialTicks)

            vb.pos(p + vec(-radiusXZ, -radiusY, 0)).color(Color.RED).endVertex() // normal along Z
            vb.pos(p + vec(-radiusXZ,  radiusY, 0)).color(Color.RED).endVertex()
            vb.pos(p + vec( radiusXZ,  radiusY, 0)).color(Color.RED).endVertex()
            vb.pos(p + vec( radiusXZ, -radiusY, 0)).color(Color.RED).endVertex()

            vb.pos(p + vec(0, -radiusY, -radiusXZ)).color(Color.RED).endVertex() // normal along X
            vb.pos(p + vec(0,  radiusY, -radiusXZ)).color(Color.RED).endVertex()
            vb.pos(p + vec(0,  radiusY,  radiusXZ)).color(Color.RED).endVertex()
            vb.pos(p + vec(0, -radiusY,  radiusXZ)).color(Color.RED).endVertex()

            vb.pos(p + vec(-radiusXZ, 0, -radiusXZ)).color(Color.RED).endVertex() // normal along Y
            vb.pos(p + vec( radiusXZ, 0, -radiusXZ)).color(Color.RED).endVertex()
            vb.pos(p + vec( radiusXZ, 0,  radiusXZ)).color(Color.RED).endVertex()
            vb.pos(p + vec(-radiusXZ, 0,  radiusXZ)).color(Color.RED).endVertex()
        }
        tess.draw()

        GlStateManager.enableCull()
    }
}
