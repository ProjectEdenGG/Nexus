package me.pugabyte.nexus.features.events.y2021.bearfair21.quests;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.Collector;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent.Content;
import me.pugabyte.nexus.models.bearfair21.ClientsideContentService;
import me.pugabyte.nexus.utils.PacketUtils;
import me.pugabyte.nexus.utils.Tasks;
import net.minecraft.server.v1_16_R3.EntityItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ClientsideContentManager implements Listener {
	private static final ClientsideContentService contentService = new ClientsideContentService();
	private static final BearFair21UserService userService = new BearFair21UserService();
	private static final HashMap<Player, List<EntityItemFrame>> playerItemFrames = new HashMap<>();

	public ClientsideContentManager() {
		Nexus.registerListener(this);
		playerBlockTask();
	}

	public static void startup() {
		for (Player player : BearFair21.getPlayers())
			sendSpawnItemFrames(player);
	}

	public static void shutdown() {
		for (Player player : playerItemFrames.keySet()) {
			List<EntityItemFrame> itemFrames = playerItemFrames.get(player);
			for (EntityItemFrame itemFrame : itemFrames) {
				PacketUtils.killItemFrame(player, itemFrame);
			}
		}
		playerItemFrames.clear();
	}

	private void playerBlockTask() {
		Tasks.repeat(0, Time.TICK.x(10), () -> {
			Set<Player> players = BearFair21.getPlayers();
			for (Content content : contentService.getList()) {
				if (content.isItemFrame()) continue;

				for (Player player : players) {
					if (!isNear(player, content)) continue;
					if (!canSee(player, content)) continue;

					player.sendBlockChange(content.getLocation(), content.getMaterial().createBlockData());
				}
			}
		});
	}

	private static boolean isNear(Player player, Content content) {
		return content.getLocation().distance(player.getLocation()) < 75;
	}

	private static boolean canSee(Player player, Content content) {
		BearFair21User user = userService.get(player);
		return user.getClientsideLocations().contains(content.getLocation());
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		check(event.getPlayer());
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		check(event.getPlayer());
	}

	private void check(Player player) {
		Tasks.wait(1, () -> {
			if (!BearFair21.isAtBearFair(player))
				return;

			if (BearFair21.getPlayers().size() == 1)
				Collector.spawn();

			sendSpawnItemFrames(player);
		});
	}

	public static void sendSpawnItemFrames(Player player) {
		sendSpawnItemFrames(player, contentService.getList());
	}

	public static void sendSpawnItemFrames(Player player, List<Content> contentList) {
		for (Content content : contentList) {
			if (!content.isItemFrame()) continue;
			if (!canSee(player, content)) continue;

			EntityItemFrame itemFrame = PacketUtils.spawnItemFrame(player, content.getLocation(), content.getBlockFace(),
					content.getItemStack(), content.getRotation(), false, true);

			if (!playerItemFrames.containsKey(player))
				playerItemFrames.put(player, new ArrayList<>());

			playerItemFrames.get(player).add(itemFrame);
		}
	}
}
