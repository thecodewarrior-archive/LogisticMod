package thecodewarrior.logistic.api;

import com.google.common.collect.Iterables;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import thecodewarrior.logistic.logistics.CapabilityLogisticWorld;
import thecodewarrior.logistic.logistics.LogisticNode;

import java.util.Collection;

/**
 * Created by TheCodeWarrior
 */
public final class LogisticAPI {
	private LogisticAPI() {}
	
	/**
	 * Get the node associated with {@param pos} and return its network.
	 * @param pos
	 * @return The network, or null if there is no node associated with {@param pos}
	 */
	@Nullable
	public static INetwork getNetwork(World world, BlockPos pos) {
		CapabilityLogisticWorld cap = world.getCapability(CapabilityLogisticWorld.getCap(), null);
		if(cap == null)
			return null;
		
		Collection<LogisticNode> nodes = cap.getNodes(pos);
		if(nodes.size() == 0)
			return null;
		
		LogisticNode node = Iterables.get(nodes, 0);
		
		return cap.getNetwork(node.getNetworkID());
	}
	
}
