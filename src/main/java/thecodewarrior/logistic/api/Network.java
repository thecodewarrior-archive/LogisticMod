package thecodewarrior.logistic.api;

import com.teamwizardry.librarianlib.common.util.saving.Savable;
import com.teamwizardry.librarianlib.common.util.saving.Save;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by TheCodeWarrior
 */
@Savable
public class Network {
	
	public Network() {
		
	}
	
	public int id = -1;
	
	@Save
	public ArrayList<UUID> nodes = new ArrayList<>();
}
