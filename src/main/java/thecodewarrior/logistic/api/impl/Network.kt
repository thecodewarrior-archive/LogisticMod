package thecodewarrior.logistic.api.impl

import thecodewarrior.logistic.api.IDrone
import thecodewarrior.logistic.api.INetwork
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class Network(private val _id: Int) : INetwork {
    private val _drones = LinkedList<IDrone?>()
    private val _nodes = mutableListOf<UUID>()

    override fun getId() = _id
    override fun getNodes() = _nodes
    override fun getDrones() = _drones

}
