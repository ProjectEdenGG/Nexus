package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.models.skincache.SkinCache;
import me.pugabyte.nexus.models.skincache.SkinCacheService;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Timer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Permission("group.admin")
public class SkinCacheCommand extends CustomCommand {
	private SkinCacheService service = new SkinCacheService();

	public SkinCacheCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("getHead [player]")
	void getHead(@Arg("self") SkinCache cache) {
		PlayerUtils.giveItem(player(), cache.getHead());
		send(PREFIX + "Gave head of " + cache.getNickname());
	}

	@Async
	@Path("update [player]")
	void update(@Arg("self") SkinCache cache) {
		cache.update();
		PlayerUtils.giveItem(player(), cache.getHead());
		send(PREFIX + "Updated and gave head of " + cache.getNickname());
	}

	@Async
	@Path("updateAll")
	void updateAll() {
		send(updateOnline());
	}

	@Async
	@Path("cacheShopHeads")
	void cacheShopHeads() {
		List<SkinCache> caches = new ShopService().getAll().stream()
				.filter(shop -> !shop.getProducts().isEmpty())
				.map(SkinCache::of)
				.collect(Collectors.toList());

		send(updateAll(caches));
	}

	@Async
	@Path("deleteAll")
	void deleteAll() {
		service.deleteAll();
		send(PREFIX + "Cleared database");
	}

	private static String updateOnline() {
		return updateAll(new SkinCacheService().getOnline());
	}

	@NotNull
	private static String updateAll(List<SkinCache> online) {
		AtomicInteger updated = new AtomicInteger();

		Timer timer = new Timer("updateAllHeads", () -> {
			for (SkinCache skinCache : online)
				if (skinCache.update())
					updated.incrementAndGet();
		});

		return StringUtils.getPrefix("SkinCache") + "Checked " + online.size() + " skins, found " + updated + " updates, took " + timer.getDuration() + "ms";
	}

	static {
		Nexus.getCron().schedule("0 */6 * * *", () -> Tasks.async(() -> Nexus.log(updateOnline())));
	}

}
