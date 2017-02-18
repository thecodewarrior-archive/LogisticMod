package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.generics

import com.teamwizardry.librarianlib.common.util.forEach
import com.teamwizardry.librarianlib.common.util.handles.MethodHandleHelper
import com.teamwizardry.librarianlib.common.util.safeCast
import com.teamwizardry.librarianlib.common.util.saving.FieldTypeGeneric
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.Targets
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagIntArray
import net.minecraft.nbt.NBTTagList
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleGraph
import org.jgrapht.graph.SimpleWeightedGraph


/**
 * Created by TheCodeWarrior
 */
object SerializeGraphs {
    // check for map interface and 0 arg constructor
    init {
        SerializerRegistry.register("jgrapht:generator.graph", Serializer(SimpleWeightedGraph::class.java))

        SerializerRegistry["jgrapht:generator.graph"]?.register(Targets.NBT, { type ->
            type as FieldTypeGeneric
            val vertParam = type.generic(0)!!
            val edgeParam = type.generic(1)!!
            val vertSerializer = SerializerRegistry.lazyImpl(Targets.NBT, vertParam)
            if(edgeParam.clazz != DefaultWeightedEdge::class.java)
                throw IllegalArgumentException("Can only serialize DefaultEdge edges")

            val constructorMH = MethodHandleHelper.wrapperForConstructor<SimpleWeightedGraph<Any, DefaultWeightedEdge>>(type.clazz, Class::class.java)

            Targets.NBT.impl<SimpleWeightedGraph<Any, DefaultWeightedEdge>>({ nbt, existing, syncing ->

                val compound = nbt.safeCast<NBTTagCompound>()
                val vertList = compound.getTag("vertices").safeCast<NBTTagList>()
                val fromList = compound.getTag("edgesFrom").safeCast<NBTTagIntArray>().intArray
                val toList = compound.getTag("edgesTo").safeCast<NBTTagIntArray>().intArray
                val weightList = compound.getTag("edgesWeights").safeCast<NBTTagIntArray>().intArray

                val graph = existing ?: constructorMH(arrayOf(DefaultWeightedEdge::class.java))

                graph.removeAllVertices(graph.vertexSet().toList())

                val verts = mutableListOf<Any>()
                vertList.forEach<NBTBase> {
                    verts.add(vertSerializer().read(it, null, syncing))
                }
                verts.map { graph.addVertex(it) }

                for(i in fromList.indices) {
                    if(fromList[i] != -1 && toList[i] != -1) {
                        graph.addEdge(verts[fromList[i]], verts[toList[i]])
                        graph.setEdgeWeight(graph.getEdge(verts[fromList[i]], verts[toList[i]]), weightList[i]/10000.0)
                    }
                }

                graph
            }, { value, syncing ->
                val vertList = NBTTagList()
                val fromList = mutableListOf<Int>()
                val toList = mutableListOf<Int>()
                val weightList = mutableListOf<Int>()

                for(v in value.vertexSet()) {
                    vertList.appendTag(vertSerializer().write(v, syncing))
                }

                var i = 0
                val verts = value.vertexSet().associate { it to i++ }

                for(e in value.edgeSet()) {
                    fromList.add(verts[value.getEdgeSource(e)] ?: -1)
                    toList.add(verts[value.getEdgeTarget(e)] ?: -1)
                    weightList.add((value.getEdgeWeight(e)*10000).toInt())
                }

                val compound = NBTTagCompound()

                compound.setTag("vertices", vertList)
                compound.setTag("edgesFrom", NBTTagIntArray(fromList.toIntArray()))
                compound.setTag("edgesTo", NBTTagIntArray(toList.toIntArray()))
                compound.setTag("edgesWeights", NBTTagIntArray(weightList.toIntArray()))

                compound
            })
        })

        SerializerRegistry["jgrapht:generator.graph"]?.register(Targets.BYTES, { type ->
            type as FieldTypeGeneric
            val vertParam = type.generic(0)!!
            val edgeParam = type.generic(1)!!
            val vertSerializer = SerializerRegistry.lazyImpl(Targets.BYTES, vertParam)
            if(edgeParam.clazz != DefaultEdge::class.java)
                throw IllegalArgumentException("Can only serialize DefaultEdge edges")

            val constructorMH = MethodHandleHelper.wrapperForConstructor<SimpleGraph<Any, DefaultEdge>>(type.clazz, Class::class.java)

            Targets.BYTES.impl<SimpleGraph<Any,DefaultEdge>>({ buf, existing, syncing ->

                val verts = (0 until buf.readInt()).map { vertSerializer().read(buf, null, syncing) }
                val fromList = (0 until buf.readInt()).map { buf.readInt() }
                val toList = (0 until buf.readInt()).map { buf.readInt() }

                val graph = existing ?: constructorMH(arrayOf(DefaultEdge::class.java))

                graph.removeAllVertices(graph.vertexSet().toList())

                verts.map { graph.addVertex(it) }

                for(i in fromList.indices) {
                    if(fromList[i] != -1 && toList[i] != -1)
                        graph.addEdge(verts[fromList[i]], verts[toList[i]])
                }

                graph
            }, { buf, value, syncing ->
                var i = 0
                val verts = value.vertexSet().associate { it to i++ }

                buf.writeInt(verts.size)
                verts.forEach { vertSerializer().write(buf, it, syncing) }

                val fromList = mutableListOf<Int>()
                val toList = mutableListOf<Int>()

                for(e in value.edgeSet()) {
                    fromList.add(verts[value.getEdgeSource(e)] ?: -1)
                    toList.add(verts[value.getEdgeTarget(e)] ?: -1)
                }

                buf.writeInt(fromList.size)
                fromList.forEach { buf.writeInt(it) }
                buf.writeInt(toList.size)
                toList.forEach { buf.writeInt(it) }
            })
        })
    }
}
