package thecodewarrior.logistic.api;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * Created by TheCodeWarrior
 */
public interface IOrder {
	/**
	 * Get the block that placed the order
	 * @return The position of the block that placed the order
	 */
	@NotNull BlockPos getDestination();
	
	/**
	 * Get the item request
	 * @return The item request
	 */
//	@NotNull IRequest getRequest();
	
	/**
	 * Get the quantity of the request
	 * @return The amount of th
	 */
	int getQuantityRequested();
	int getQuantityFulfilling();
	void fulfillAmount(int amount);
}
