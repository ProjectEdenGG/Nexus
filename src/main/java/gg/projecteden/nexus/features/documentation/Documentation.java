package gg.projecteden.nexus.features.documentation;

import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Tasks;

public class Documentation extends Feature {

	@Override
	public void onStart() {
		Tasks.async(DocumentCommands::new);
	}

}
