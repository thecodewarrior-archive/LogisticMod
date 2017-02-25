package thecodewarrior.logistic.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thecodewarrior.logistic.LogisticLog;
import thecodewarrior.logistic.logistics.NodeType;
import thecodewarrior.logistic.logistics.WorldCapLogistic;
import thecodewarrior.logistic.logistics.nodes.Node;

/**
 * Created by TheCodeWarrior
 */
public class LogisticAPI {
	
	private static long lastAlertTime = 0L;
	public static int alertInterval = 60 * 1000; // the number of milliseconds between "no world cap" errors
	
	/**
	 * Gets the API instance for {@param world}
	 * @param world the world
	 * @return The world's API implementation, or {@link #DUMMY_WORLD} if there is no implementation for that world
	 */
	@NotNull
	public static LogisticWorld forWorld(@NotNull World world) {
		WorldCapLogistic cap = world.getCapability(WorldCapLogistic.getCap(), null);
		if(cap == null) {
			long currentTime = System.currentTimeMillis();
			if(lastAlertTime + alertInterval <= currentTime) {
				lastAlertTime = currentTime;
				LogisticLog.INSTANCE.error(new RuntimeException(), "No logistic implementation for world %s", world);
			}
			return DUMMY_WORLD;
		} else {
			return cap;
		}
	}
	
	
	/*==================================================================================================================
					DUMMY WORLD
	==================================================================================================================*/
	
	public static final LogisticWorld DUMMY_WORLD = new LogisticWorld() {
		
		@Override
		public boolean addNode(@NotNull BlockPos pos, @NotNull Vec3d offset, @NotNull NodeType type) { return false; }
		
		@Override
		public boolean removeNode(@NotNull BlockPos pos) { return false; }
		
		@Nullable
		@Override
		public Node getNode(@NotNull BlockPos pos) { return null; }
	};
}
