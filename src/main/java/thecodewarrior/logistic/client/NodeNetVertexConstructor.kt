package thecodewarrior.logistic.client

import com.teamwizardry.librarianlib.client.core.color
import com.teamwizardry.librarianlib.client.core.pos
import com.teamwizardry.librarianlib.common.util.plus
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.Vec3d
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
object NodeNetVertexConstructor {
    fun renderToBuffer(buffer: VertexBuffer) {
        checkBufferCompatibility(buffer)

        renderNodesToBuffer(buffer)
    }

    private fun checkBufferCompatibility(buffer: VertexBuffer) {
        if(buffer.vertexFormat != DefaultVertexFormats.POSITION_COLOR)
            throw IllegalArgumentException("The node network must use DefaultVertexFormats.POSITION_COLOR")
    }

    private fun renderNodesToBuffer(buffer: VertexBuffer) {
        ClientNetInfoTracker.nodes.values.forEach {
            drawNode(buffer, it, Color.BLUE)
        }
    }

    /*==================================================================================================================
                    Part render methods
    ==================================================================================================================*/

    fun drawNode(vb: VertexBuffer, pos: Vec3d, color: Color) {
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

    fun drawEdge(vb: VertexBuffer, a: Vec3d, b: Vec3d, color: Color) {
        vb.pos(a).color(color).endVertex()
        vb.pos(b).color(color).endVertex()
    }
}
