package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.arenas.KangarooJumpingArena;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.minigames.utils.PowerUpUtils;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public final class KangarooJumping extends TeamlessMechanic {

	@Override
	public @NotNull String getName() {
		return "Kangaroo Jumping";
	}

	@Override
	public @NotNull String getDescription() {
		return "Jump higher and higher and be the first to the finish!";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.LEATHER_BOOTS);
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		Match match = event.getMatch();
		KangarooJumpingArena arena = match.getArena();
		match.getTasks().wait(TickTime.SECOND.x(5), () -> {
			match.broadcast("Power ups have spawned!");
			for (Location loc : arena.getPowerUpLocations())
				new PowerUpUtils(match, powerUps).spawn(loc, true);
		});
	}

	@EventHandler
	public void onEnterWinningArea(PlayerEnteredRegionEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		if (!minigamer.getMatch().getArena().ownsRegion(event.getRegion().getId(), "win")) return;
		if (hasAnyoneScored(minigamer.getMatch())) return;

		minigamer.scored();
		minigamer.getMatch().broadcast("&e" + minigamer.getColoredName() + " has reached the finish area!");
		minigamer.getMatch().getTasks().wait(TickTime.SECOND.x(5), () -> minigamer.getMatch().end());
	}

	boolean hasAnyoneScored(Match match) {
		for (Minigamer minigamer : match.getMinigamers())
			if (minigamer.getScore() > 0)
				return true;
		return false;
	}

	PowerUpUtils.PowerUp JUMP = new PowerUpUtils.PowerUp("Extra Jump Boost", true,
			new ItemBuilder(Material.LEATHER_BOOTS).glow().build(),
			minigamer -> {
				minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.JUMP_BOOST).duration(TickTime.SECOND.x(10)).amplifier(20));

				minigamer.getMatch().getTasks().wait(TickTime.SECOND.x(10), () ->
					minigamer.getMatch().getAliveTeams().get(0).getLoadout().apply(minigamer));
			});

	PowerUpUtils.PowerUp POSITIVE_BLINDNESS = new PowerUpUtils.PowerUp("Blindness", true,
			new ItemBuilder(Material.POTION).potionEffectColor(ColorType.BLACK.getBukkitColor()).glow().build(),
			minigamer -> {
				for (Minigamer _minigamer : minigamer.getMatch().getMinigamers())
					if (_minigamer != minigamer) {
						_minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).duration(TickTime.SECOND.x(5)));
						_minigamer.tell("You have been trapped!");
					}
			});

	PowerUpUtils.PowerUp NEGATIVE_BLINDNESS = new PowerUpUtils.PowerUp("Blindness", false,
			new ItemBuilder(Material.POTION).potionEffectColor(ColorType.BLACK.getBukkitColor()).build(),
			minigamer -> minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).duration(TickTime.SECOND.x(5))));

	PowerUpUtils.PowerUp SNOWBALL = new PowerUpUtils.PowerUp("Snowball", true, Material.SNOWBALL,
			minigamer ->
				minigamer.getMatch().getMinigamers().forEach(_minigamer -> _minigamer.getOnlinePlayer().getInventory().addItem(
						new ItemBuilder(Material.SNOWBALL)
								.name("&bKnockback Snowball")
								.enchant(Enchantment.KNOCKBACK, 2)
								.amount(3)
								.build()
				)));

	PowerUpUtils.PowerUp LEVITATION = new PowerUpUtils.PowerUp("Levitation", true, Material.ELYTRA,
			minigamer -> {
				for (Minigamer _minigamer : minigamer.getMatch().getMinigamers())
					if (_minigamer != minigamer) {
						_minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.LEVITATION).duration(TickTime.SECOND.x(5)).amplifier(3));
						_minigamer.tell("You have been trapped!");
					}
			});

	List<PowerUpUtils.PowerUp> powerUps = Arrays.asList(JUMP, POSITIVE_BLINDNESS, NEGATIVE_BLINDNESS, SNOWBALL, LEVITATION);

	@EventHandler
	public void onSnowballHit(ProjectileHitEvent event) {
		if (!(event.getHitEntity() instanceof Player player))
			return;

		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		player.setVelocity(event.getEntity().getVelocity().multiply(0.5).add(new Vector(0, .5, 0)));
	}

}

