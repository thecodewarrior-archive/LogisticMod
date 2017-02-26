package thecodewarrior.logistic.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thecodewarrior.logistic.logistics.NodeLogisticData;
import thecodewarrior.logistic.logistics.nodes.Node;

/**
 * Created by TheCodeWarrior
 */
public interface LogisticWorld {
	/**
	 * Add a node bound to the specified position if there is no node already there
	 * @param pos the block to bind the node to
	 * @param offset the offset from that block's position (clamped to within [±3, ±3, ±3])
	 * @param logistic the logistics data, or null for a node that doesn't interact with logistics
	 * @return true if the node was added, false if an error occurred
	 */
	boolean addNode(@NotNull BlockPos pos, @NotNull Vec3d offset, @Nullable NodeLogisticData logistic);
		
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
