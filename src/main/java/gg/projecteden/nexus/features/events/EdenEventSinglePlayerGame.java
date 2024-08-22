package gg.projecteden.nexus.features.events;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeavingRegionEvent;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Depends(EdenEvent.class)
public abstract class EdenEventSinglePlayerGame extends Feature implements Listener {
	@Getter
	protected boolean playing = false;
	@Getter
	protected Player gamer;
	protected boolean init = false;

	protected int updateTaskId = -1;
	protected int updateIntervalTicks = 4;
	@Getter
	public long gameTicks = 0;

	@Override
	public void onStart() {
		super.onStart();
		Nexus.registerListener(this);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public EdenEventGameConfig getConfig() {
		return getClass().getAnnotation(EdenEventGameConfig.class);
	}

	public String getPrefix() {
		return StringUtils.getPrefix(getConfig().prefix());
	}

	@NonNull
	public String getWorldName() {
		return getConfig().world();
	}

	public World getWorld() {
		return Bukkit.getWorld(getWorldName());
	}

	public WorldGuardUtils worldguard() {
		return new WorldGuardUtils(getWorld());
	}

	public WorldEditUtils worldedit() {
		return new WorldEditUtils(getWorld());
	}

	public String getPlayRegion() {
		return getConfig().playRegion();
	}

	public long getMaxGameTicks() {
		return TickTime.MINUTE.x(5);
	}

	public void init() {
		init = true;
	}

	public void reset() {
		cancelUpdateTask();
		gameTicks = 0;
		gamer = null;
		playing = false;
	}

	protected boolean startChecks(Player player) {
		if (Nexus.isMaintenanceQueued()) {
			send("&cServer maintenance is queued, try again later");
			return false;
		}

		if (playing) {
			send("&cThe game is already being played");
			return false;
		}

		return true;
	}

	protected void preStart() {

	}

	public void start(Player player) {
		if (!init) {
			init();
		}

		if (!startChecks(player))
			return;

		gamer = player;
		gameTicks = 0;
		preStart();

		_start(player);
	}

	private void _start(Player player) {
		playing = true;
		gamer = player;

		updateTaskId = Tasks.repeat(0, updateIntervalTicks, () -> {
			update();
			gameTicks += updateIntervalTicks;
		});
	}

	protected void update() {
		if (!playing) {
			cancelUpdateTask();
			return;
		}

		if (gameTicks >= getMaxGameTicks()) {
			end();
			return;
		}
	}

	public void end() {
		reset();
	}

	public void cancelUpdateTask() {
		if (updateTaskId == -1)
			return;

		Tasks.cancel(updateTaskId);
	}

	@EventHandler
	public void on(PlayerLeavingRegionEvent event) {
		if (!shouldHandle(event.getPlayer()))
			return;

		if (!event.getRegion().getId().equalsIgnoreCase(getPlayRegion()))
			return;

		event.setCancelled(true);
		sendCooldown("&cYou can't leave while playing the game", "event_game_sp_leaving");
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (!shouldHandle(player)) return;

		reset();
	}

	private boolean shouldHandle(Player player) {
		if (!playing)
			return false;

		return gamer.getUniqueId().equals(player.getUniqueId());
	}

	public void sendCooldown(String message, String key) {
		if (!new CooldownService().check(gamer.getPlayer().getUniqueId(), key, TickTime.SECOND.get()))
			return;

		send(message);
	}

	public void line() {
		PlayerUtils.send(gamer, "");
	}

	public void send(String message) {
		PlayerUtils.send(gamer, getPrefix() + message);
	}

	public void send(Player player, String message) {
		PlayerUtils.send(player, getPrefix() + message);
	}

	public void give(ItemBuilder item) {
		give(item.build());
	}

	public void give(ItemStack item) {
		PlayerUtils.giveItem(gamer, item);
	}

	public void give(List<ItemStack> items) {
		PlayerUtils.giveItems(gamer, items);
	}

	public Location location(double x, double y, double z) {
		return location(x, y, z, 0, 0);
	}

	public Location location(double x, double y, double z, float yaw, float pitch) {
		return new Location(getWorld(), x, y, z, yaw, pitch);
	}

	public Location getBaseLocation() {
		return worldedit().getBlocks(worldguard().getProtectedRegion(getPlayRegion())).getFirst().getLocation();
	}
}
