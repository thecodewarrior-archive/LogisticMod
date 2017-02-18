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
import thecodewarrior.logistic.capability.BareLogisticNode
import thecodewarrior.logistic.capability.ClientLogisticTracker
import thecodewarrior.logistic.capability.EdgeData
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
object RenderNetworkLines {

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun render(e: CustomWorldRenderEvent) {

        val tess = Tessellator.getInstance()
        val vb = tess.buffer

        GlStateManager.depthMask(false)

        GlStateManager.depthFunc(GL11.GL_GREATER)

        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
        ClientLogisticTracker.edges.forEach { drawEdge(it, Color(0, 128, 0, 255)) }
        ClientLogisticTracker.nodes.values.forEach { drawNode(it, Color(0, 0, 128, 255)) }
        tess.draw()


        GlStateManager.depthFunc(GL11.GL_LEQUAL)

        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
        ClientLogisticTracker.edges.forEach { drawEdge(it, Color(0, 255, 0, 255)) }
        ClientLogisticTracker.nodes.values.forEach { drawNode(it, Color(0, 0, 255, 255)) }
        tess.draw()
        GlStateManager.depthMask(true)
    }

    fun drawNode(node: BareLogisticNode, color: Color) {
        val vb = Tessellator.getInstance().buffer

        val r = 0.25

        val c = node.pos // center
        val eus = node.pos + vec( r,  r,  r) // east/up  /south, x/y/z
        val wus = node.pos + vec(-r,  r,  r) // west/up  /south, x/y/z
        val eds = node.pos + vec( r, -r,  r) // east/down/south, x/y/z
        val wds = node.pos + vec(-r, -r,  r) // west/down/south, x/y/z
        val eun = node.pos + vec( r,  r, -r) // east/up  /north, x/y/z
        val wun = node.pos + vec(-r,  r, -r) // west/up  /north, x/y/z
        val edn = node.pos + vec( r, -r, -r) // east/down/north, x/y/z
        val wdn = node.pos + vec(-r, -r, -r) // west/down/north, x/y/z

        //region east -> west lines
        vb.pos(eus).color(color).endVertex()
        vb.pos(wus).color(color).endVertex()

        vb.pos(eds).color(color).endVertex()
        vb.pos(wds).color(color).endVertex()

        vb.pos(eun).color(color).endVertex()
        vb.pos(wun).color(color).endVertex()

        vb.pos(edn).color(color).endVertex()
        vb.pos(wdn).color(color).endVertex()
        //endregion

        //region up -> down lines
        vb.pos(eus).color(color).endVertex()
        vb.pos(eds).color(color).endVertex()

        vb.pos(wus).color(color).endVertex()
        vb.pos(wds).color(color).endVertex()

        vb.pos(eun).color(color).endVertex()
        vb.pos(edn).color(color).endVertex()

        vb.pos(wun).color(color).endVertex()
        vb.pos(wdn).color(color).endVertex()
        //endregion

        //region north -> south lines
        vb.pos(eus).color(color).endVertex()
        vb.pos(eun).color(color).endVertex()

        vb.pos(wus).color(color).endVertex()
        vb.pos(wun).color(color).endVertex()

        vb.pos(eds).color(color).endVertex()
        vb.pos(edn).color(color).endVertex()

        vb.pos(wds).color(color).endVertex()
        vb.pos(wdn).color(color).endVertex()
        //endregion

        //region crossways lines
        vb.pos(eus).color(color).endVertex() // one of the corners
        vb.pos(wdn).color(color).endVertex() // the inverse on all axes

        vb.pos(wus).color(color).endVertex() // cycling through all the permutations of the first two bits handles four
        vb.pos(edn).color(color).endVertex() // of the corners, and the other four are handled by the inverse ends

        vb.pos(eds).color(color).endVertex()
        vb.pos(wun).color(color).endVertex()

        vb.pos(wds).color(color).endVertex()
        vb.pos(eun).color(color).endVertex()
        //endregion
    }

    fun drawEdge(edge: EdgeData, color: Color) {
        val vb = Tessellator.getInstance().buffer

        vb.pos(edge.aPos).color(color).endVertex()
        vb.pos(edge.bPos).color(color).endVertex()
    }
}
