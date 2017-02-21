package thecodewarrior.logistic.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Created by TheCodeWarrior
 */
public interface INetwork {
	int getId();
	@NotNull List<UUID> getNodes();
	@NotNull List<IDrone> getDrones();
}
