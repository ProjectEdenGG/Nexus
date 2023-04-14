package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.models.skincache.SkinCache;
import gg.projecteden.nexus.models.skincache.SkinCacheService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Timer;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@NoArgsConstructor
@Permission(Group.ADMIN)
public class SkinCacheCommand extends CustomCommand implements Listener {
	private final SkinCacheService service = new SkinCacheService();

	public SkinCacheCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Description("Get the cached head of a player")
	void getHead(@Optional("self") SkinCache cache) {
		PlayerUtils.giveItem(player(), cache.getHead());
		send(PREFIX + "Gave head of " + cache.getNickname());
	}

	@Async
	@Description("Refresh a player's cached skin")
	void update(@Optional("self") SkinCache cache) {
		cache.update();
		PlayerUtils.giveItem(player(), cache.getHead());
		send(PREFIX + "Updated and gave head of " + cache.getNickname());
	}

	@Async
	@Description("Update the cached skins of all online players")
	void updateAll() {
		send(updateAll(new SkinCacheService().getOnline()));
	}

	@Async
	@Description("Cache all skins that appear in the shops menu")
	void cacheShopHeads() {
		List<SkinCache> caches = new ShopService().getAll().stream()
			.filter(shop -> !shop.getProducts().isEmpty())
			.map(SkinCache::of)
			.collect(Collectors.toList());

		send(updateAll(caches));
	}

	@Async
	@Description("View the last time a player changed their skin")
	void getLastChange(@Optional("self") SkinCache cache) {
		send(PREFIX + "Skin last changed " + Timespan.of(cache.getLastChanged()).format() + " ago");
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

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		SkinCache.of(event.getPlayer()).update();
	}

}
