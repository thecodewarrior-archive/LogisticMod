package thecodewarrior.logistic.client

import com.teamwizardry.librarianlib.client.core.addCacheArray
import com.teamwizardry.librarianlib.client.core.color
import com.teamwizardry.librarianlib.client.core.createCacheArrayAndReset
import com.teamwizardry.librarianlib.client.core.pos
import com.teamwizardry.librarianlib.client.event.CustomWorldRenderEvent
import com.teamwizardry.librarianlib.common.util.plus
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
object NodeNetRenderer {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    private var savedBuffer = IntArray(0)
    private val buf = VertexBuffer(5000)

    fun rebuild() {
        buf.reset()
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

        NodeNetVertexConstructor.renderToBuffer(buf)

        savedBuffer = buf.createCacheArrayAndReset()
    }

    @SubscribeEvent
    fun drawEvent(e: CustomWorldRenderEvent) {
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
        vb.addCacheArray(savedBuffer)

        tessellator.draw()
    }

    /*==================================================================================================================
                    Part render methods
    ==================================================================================================================*/

    fun drawNode(pos: Vec3d, color: Color) {
        val vb = Tessellator.getInstance().buffer

        val r = 0.25

        val c = pos // center
        val eus = pos + vec( r,  r,  r) // east/up  /south, x/y/z
        val wus = pos + vec(-r,  r,  r) // west/up  /south, x/y/z
        val eds = pos + vec( r, -r,  r) // east/down/south, x/y/z
        val wds = pos + vec(-r, -r,  r) // west/down/south, x/y/z
        val eun = pos + vec( r,  r, -r) // east/up  /north, x/y/z
        val wun = pos + vec(-r,  r, -r) // west/up  /north, x/y/z
        val edn = pos + vec( r, -r, -r) // east/down/north, x/y/z
        val wdn = pos + vec(-r, -r, -r) // west/down/north, x/y/z

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

    fun drawEdge(a: Vec3d, b: Vec3d, color: Color) {
        val vb = Tessellator.getInstance().buffer

        vb.pos(a).color(color).endVertex()
        vb.pos(b).color(color).endVertex()
    }

}
