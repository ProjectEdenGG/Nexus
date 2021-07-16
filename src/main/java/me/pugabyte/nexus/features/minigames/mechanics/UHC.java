package me.pugabyte.nexus.features.minigames.mechanics;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import eden.utils.TimeUtils;
import eden.utils.TimeUtils.Timespan;
import eden.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.lexikiq.event.block.BlockDropResourcesEvent;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.sabotage.MinigamerDisplayTimerEvent;
import me.pugabyte.nexus.features.minigames.models.matchdata.UHCMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessVanillaMechanic;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.AdventureUtils;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.MaterialUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.title.Title;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class UHC extends TeamlessVanillaMechanic {
	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.GOLDEN_APPLE);
	}

	@Override
	public @NotNull String getName() {
		return "Ultra Hardcore";
	}

	@Override
	public @NotNull String getDescription() {
		return "Be the last person alive as you fight other players to death and escape the world border, all without regenerating health";
	}

	@EqualsAndHashCode
	@AllArgsConstructor
	private static final class WorldBorderWrapper {
		private final @NotNull Duration delay;
		private final @NotNull Duration shrink;
		private final @NotNull Duration warning;
		private final int diameter;

		public final String getDiameterString() {
			return diameter + "x" + diameter;
		}
	}

	private final int worldDiameter = 3001; // TODO: custom variable for world border center radius
	private final String worldName = "uhc";
	private static final List<WorldBorderWrapper> WORLD_BORDER_DATA = List.of(
		new WorldBorderWrapper(Duration.ofMinutes(40), Duration.ofMinutes(17), Duration.ofMinutes(3), 49),
		new WorldBorderWrapper(Duration.ofMinutes(58), Duration.ofMinutes(2), Duration.ofSeconds(10), 9)
	);

	private void announce(Audience to, ComponentLike message) {
		Component msg = message.asComponent();
		Title title = Title.title(Component.empty(), msg, AdventureUtils.BASIC_TIMES);
		to.sendMessage(JsonBuilder.fromPrefix("UHC", msg));
		to.showTitle(title);
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		final Match match = event.getMatch();
		match.<UHCMatchData>getMatchData().setStartTime(LocalDateTime.now());
		WORLD_BORDER_DATA.forEach(worldBorder -> {
			match.getTasks().wait(TimeUtils.Time.SECOND.x(worldBorder.delay.minus(worldBorder.warning).getSeconds()),
				() -> announce(match, new JsonBuilder("&cThe border will begin shrinking in &e" + worldBorder.warning.toMinutes() + " minutes")));
			match.getTasks().wait(TimeUtils.Time.SECOND.x(worldBorder.delay.getSeconds()), () -> {
				getWorld().getWorldBorder().setSize(worldBorder.diameter, worldBorder.shrink.getSeconds());
				getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
				announce(match, new JsonBuilder("&cThe border is now shrinking to &e" + worldBorder.getDiameterString()));
			});
		});
	}

	@Override
	public void onDisplayTimer(MinigamerDisplayTimerEvent event) {
		super.onDisplayTimer(event);
		String wbText = null; // world border text
		PlayerInventory inv = event.getMinigamer().getPlayer().getInventory();
		if (inv.getItemInMainHand().getType() == Material.COMPASS || inv.getItemInOffHand().getType() == Material.COMPASS) {
			UUID uuid = event.getMinigamer().getUniqueId();
			Location loc = event.getMinigamer().getPlayer().getLocation();
			Utils.MinMaxResult<Minigamer> result = Utils.getMin(event.getMatch().getAliveMinigamers().stream().filter(minigamer -> !minigamer.getUniqueId().equals(uuid)).collect(Collectors.toList()),
				minigamer -> minigamer.getPlayer().getLocation().distance(loc));
			wbText = "&3The nearest player is &e" + result.getObject().getNickname() + "&3 (&6" + result.getInteger() + "m&3)";
		} else {
			LocalDateTime start = event.getMatch().<UHCMatchData>getMatchData().getStartTime();
			LocalDateTime now = LocalDateTime.now();
			for (WorldBorderWrapper worldBorder : WORLD_BORDER_DATA) {
				LocalDateTime borderStart = start.plus(worldBorder.delay);
				if (now.isBefore(borderStart)) {
					wbText = "World Border shrinks in &e" + Timespan.of(now, borderStart).format();
					break;
				} else if (now.isBefore(borderStart.plus(worldBorder.shrink))) {
					break; // don't display "shrinks in" message if a border is shrinking
				}
			}
			if (wbText == null) {
				int size = (int) Math.ceil(getWorld().getWorldBorder().getSize());
				wbText = "&cWorld Border is currently &6" + size + "x" + size;
			}
		}
		event.setContents(new JsonBuilder(event.getContents())
			.next(" | Y: &e" + (int) Math.floor(event.getMinigamer().getPlayer().getLocation().getY()))
			.next(" | ").next(wbText));
	}

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		if (event.getAttacker() != null)
			event.getAttacker().scored();
		event.getMinigamer().getMatch().<UHCMatchData>getMatchData().died(event.getMinigamer());
		dropItems(event.getMinigamer());
		super.onDeath(event);
	}

	@EventHandler
	public void onBlockDropResources(BlockDropResourcesEvent event) {
		// always drop apples on leaf break (mining/decay)
		if (!event.getBlock().getWorld().getName().equals(worldName)) return;
		event.getResources().removeIf(itemStack -> itemStack.getType() == Material.APPLE); // remove existing apples
		if (MaterialTag.LEAVES.isTagged(event.getBlock().getType()) && RandomUtils.getRandom().nextInt(200) == 0)
			event.getResources().add(new ItemStack(Material.APPLE));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onItemDrop(BlockDropItemEvent event) {
		if (!event.getPlayer().getWorld().getName().startsWith(worldName))
			return;
		World world = event.getPlayer().getWorld();

		for (Item item : event.getItems()) {
			ItemStack stack = item.getItemStack();
			ItemStack ingot = MaterialUtils.oreToIngot(world, stack.getType());
			if (ingot == null)
				continue;

			stack.setType(ingot.getType());
			item.setItemStack(stack);
			break;
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onRegainHealth(EntityRegainHealthEvent event) {
		if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED
		&& event.getEntity() instanceof HumanEntity entity
		&& entity.getWorld().getName().equals(worldName))
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onExhaustion(EntityExhaustionEvent event) {
		if (event.getEntity().getWorld().getName().equals(worldName) && event.getExhaustionReason() == EntityExhaustionEvent.ExhaustionReason.REGEN)
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onExperience(PlayerPickupExperienceEvent event) {
		if (event.getPlayer().getWorld().getName().equals(worldName))
			event.getExperienceOrb().setExperience(event.getExperienceOrb().getExperience() * 2);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlaceBlock(BlockPlaceEvent event) {
		// TODO: replace this with damaging the player if they go too high (after 5-10 seconds)
		if (event.getPlayer().getWorld().getName().equals(worldName) && event.getBlock().getY() > 150) {
			event.setCancelled(true);
			ActionBarUtils.sendActionBar(event, "&cYou have reached the height limit!");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityKill(EntityDeathEvent event) {
		if (!event.getEntity().getWorld().getName().equals(worldName))
			return;
		World world = event.getEntity().getWorld();

		for (ItemStack item : event.getDrops()) {
			ItemStack cooked = MaterialUtils.rawToCooked(world, item.getType());
			if (cooked == null) continue;
			item.setType(cooked.getType());
		}
	}

}
