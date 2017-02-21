package thecodewarrior.logistic.api;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Created by TheCodeWarrior
 */
public interface IDrone {
	/**
	 * Get the current order being fulfilled, or null if idle
	 * @return The current order being fulfilled, or null if idle
	 */
	@Nullable IOrder getCurrentOrder();
	
	/**
	 * Get the amount of the current order that is being fulfilled by this drone
	 * @return The quantity being fulfilled
	 */
	int getAmountFulfilling();
	
	/**
	 * Get the stack being carried
	 * @return The stack currently being carried
	 */
	@Nullable ItemStack getCarrying();
	
	/**
	 * Set the stack being carried
	 * @param carrying The stack to carry
	 */
	void setCarrying(@Nullable ItemStack carrying);
	
	/**
	 * Get the current charge level
	 * @return The amount of FE stored
	 */
	int getPower();
	
	/**
	 * Get the max charge level for the drone
	 * @return The maximum number FE the internal battery can store
	 */
	int getMaxPower();
	
	/**
	 * Get the id of the network this drone is in.
	 * @return The network ID
	 */
	int getNetworkID();
}
