package gg.projecteden.nexus.features.homes;

import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class HomesFeature extends Feature {
	public final static String PREFIX = StringUtils.getPrefix("Homes");
	public final static int maxHomes = 100;

	@Override
	public void onStart() {
		new HomeListener();
	}

	@Getter
	private static final List<Home> deleted = new ArrayList<>();

	public static void deleteFromWorld(String world, Runnable callback) {
		HomeService service = new HomeService();
		Tasks.async(() -> {
			deleted.clear();
			List<HomeOwner> all = service.getAll();
			all.forEach(homeOwner -> {
				homeOwner.getHomes().stream().filter(home -> home.getLocation() == null
						|| home.getLocation().getWorld() == null
						|| home.getLocation().getWorld().getName().equals(world)
				).forEach(deleted::add);

				deleted.forEach(homeOwner::delete);
				// MongoDB no longer recognizes the homes after serialization so it can't merge the deletions
				// Easy workaround is to delete the entire homeowner and re-save it
				service.deleteSync(homeOwner);
				service.saveSync(homeOwner);
			});

			if (callback != null)
				callback.run();
		});
	}

}
