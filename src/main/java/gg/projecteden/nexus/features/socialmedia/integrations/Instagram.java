package gg.projecteden.nexus.features.socialmedia.integrations;

import com.github.instagram4j.instagram4j.IGClient;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.Tasks;

public class Instagram {
	public static IGClient instagram;

	public static void connect() {
		Tasks.async(() -> {
			try {
				instagram = IGClient.builder()
					.username("ProjectEdenGG")
					.password(Nexus.getInstance().getConfig().getString("tokens.instagram.password"))
					.login();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	public static IGClient get() {
		return instagram;
	}

}
