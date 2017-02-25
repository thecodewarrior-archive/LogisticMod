package thecodewarrior.logistic.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thecodewarrior.logistic.logistics.NodeType;
import thecodewarrior.logistic.logistics.nodes.Node;

/**
 * Created by TheCodeWarrior
 */
public interface LogisticWorld {
	/**
	 * Add a node bound to the specified position if there is no node already there
	 * @param pos the block to bind the node to
	 * @param offset the offset from that block's position (clamped to within [±3, ±3, ±3])
	 * @param type the type of this node
	 * @return true if the node was added, false if an error occurred
	 */
	boolean addNode(@NotNull BlockPos pos, @NotNull Vec3d offset, @NotNull NodeType type);
		
	/**
	 * Remove the node bound to the position from the world, if it exists
	 * @param pos the block
	 * @return true if the node was removed, false if an error occurred
	 */
	boolean removeNode(@NotNull BlockPos pos);
	
	/**
	 * Get the node bound to the specified position
	 * @param pos the block
	 * @return the node if it exists, otherwise null
	 */
	@Nullable Node getNode(@NotNull BlockPos pos);
}
