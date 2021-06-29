package me.pugabyte.nexus.features.minigames.mechanics;

import eden.utils.TimeUtils;
import eden.utils.TimeUtils.Timespan;
import lombok.Getter;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.matchdata.UHCMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessVanillaMechanic;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.AdventureUtils;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.MaterialUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDate;
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

	private final int worldRadius = 7000;
	private final String worldName = "uhc";

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		final Match match = event.getMatch();
		match.getTasks().wait(TimeUtils.Time.MINUTE.x(40), () -> {
			getWorld().getWorldBorder().setSize(70, Duration.ofMinutes(10).toSeconds());
			Component msg = JsonBuilder.fromPrefix("UHC").next("The border is now shrinking to &e70x70&r!").build();
			Title title = Title.title(Component.empty(), msg, AdventureUtils.BASIC_TIMES);
			match.sendMessage(msg);
			match.showTitle(title);
		});
		match.getTasks().wait(TimeUtils.Time.MINUTE.x(55), () -> {
			getWorld().getWorldBorder().setSize(6, Duration.ofMinutes(5).toSeconds());
			Component msg = JsonBuilder.fromPrefix("UHC").next("The border is now shrinking to &e6x6&r!").build();
			Title title = Title.title(Component.empty(), msg, AdventureUtils.BASIC_TIMES);
			match.sendMessage(msg);
			match.showTitle(title);
		});
	}

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		if (event.getAttacker() != null)
			event.getAttacker().scored();
		event.getMinigamer().getMatch().<UHCMatchData>getMatchData().died(event.getMinigamer());
		dropItems(event.getMinigamer());
		super.onDeath(event);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onItemDrop(BlockDropItemEvent event) {
		if (!event.getPlayer().getWorld().getName().equals(worldName)) return;
		for (Item item : event.getItems()) {
			ItemStack stack = item.getItemStack();
			Material mat = stack.getType();

			// apples from all trees - part 1
			if (mat == Material.APPLE) return;

			// ore auto-smelter
			Material ingot = MaterialUtils.oreToIngot(stack.getType());
			if (ingot == null) continue;
			stack.setType(ingot);
			item.setItemStack(stack);
			return;
		}
		// apples from all trees - part 2
		if (MaterialTag.LEAVES.isTagged(event.getBlockState().getType()) && RandomUtils.randomInt(1, 200) == 1)
			event.getItems().add(event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE)));
	}

	// TODO: fast leaf decay + apples from leaf decay

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onRegainHealth(EntityRegainHealthEvent event) {
		if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED
		&& event.getEntity() instanceof HumanEntity entity
		&& entity.getWorld().getName().equals(worldName))
			event.setCancelled(true);
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
		if (!event.getEntity().getWorld().getName().equals(worldName)) return;
		for (ItemStack item : event.getDrops()) {
			Material cooked = MaterialUtils.rawToCooked(item.getType());
			if (cooked == null) continue;
			item.setType(cooked);
			return;
		}
	}

	@Override
	@SuppressWarnings("StringConcatenationInLoop")
	public void onEnd(@NotNull MatchEndEvent event) {
		final Match match = event.getMatch();
		if (match.isStarted())
			try {
				if (!LocalDate.now().isEqual(LocalDate.of(2021, 6, 29)))
					return;

				final String nl = System.lineSeparator();
				UHCMatchData matchData = match.getMatchData();
				String message = "**UHC** " + TimeUtils.shortDateTimeFormat(matchData.getStartTime()) + nl;

				message += "Alive: " + match.getAliveMinigamers().stream().map(Minigamer::getNickname).collect(Collectors.joining(", ")) + nl + nl;

				message += "Scores: ```";
				for (Minigamer minigamer : match.getAllMinigamers())
					message += minigamer.getNickname() + ": " + minigamer.getScore() + nl;
				message += "```" + nl;

				message += "Time Alive: ```";
				for (Minigamer minigamer : match.getAllMinigamers())
					if (matchData.getTimeAlive().containsKey(minigamer.getUniqueId()))
						message += minigamer.getNickname() + ": " + Timespan.of(matchData.getTimeAlive().get(minigamer.getUniqueId())).format() + nl;
				message += "```";

				System.out.println(message);
				Discord.adminLog(message);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		super.onEnd(event);
	}

}
