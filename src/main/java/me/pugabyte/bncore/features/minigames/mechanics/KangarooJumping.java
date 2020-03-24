package me.pugabyte.bncore.features.minigames.mechanics;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.KangarooJumpingArena;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.features.minigames.utils.PowerUpUtils;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public final class KangarooJumping extends TeamlessMechanic {

	@Override
	public String getName() {
		return "Kangaroo Jumping";
	}

	@Override
	public String getDescription() {
		return "Jump higher and higher and be the first to the finish!";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.LEATHER_BOOTS);
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		Match match = event.getMatch();
		KangarooJumpingArena arena = match.getArena();
		match.getTasks().wait(5 * 20, () -> {
			match.broadcast("Power ups have spawned!");
			for (Location loc : arena.getPowerUpLocations())
				new PowerUpUtils(match, powerUps).spawn(loc, true);
		});
	}

	@EventHandler
	public void onEnterWinningArea(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		if (!minigamer.getMatch().getArena().ownsRegion(event.getRegion().getId(), "win")) return;
		if (hasAnyoneScored(minigamer.getMatch())) return;
		minigamer.scored();
		minigamer.getMatch().broadcast("&e" + minigamer.getColoredName() + " has reached the finish area!");
		minigamer.getMatch().getTasks().wait(5 * 20, () -> minigamer.getMatch().end());
	}

//	@Override
//	public void kill(Minigamer victim, Minigamer attacker) {
//	}

	boolean hasAnyoneScored(Match match) {
		for (Minigamer minigamer : match.getMinigamers())
			if (minigamer.getScore() > 0)
				return true;
		return false;
	}

	PowerUpUtils.PowerUp JUMP = new PowerUpUtils.PowerUp("Extra Jump Boost", true,
			new ItemBuilder(Material.LEATHER_BOOTS).glow().build(),
			minigamer -> {
				minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10 * 20, 20), true);
				minigamer.getMatch().getTasks().wait(10 * 20, () -> minigamer.getMatch().getTeams().get(0).getLoadout().apply(minigamer));
			});

	PowerUpUtils.PowerUp POSITIVE_BLINDNESS = new PowerUpUtils.PowerUp("Blindness", true,
			new ItemBuilder(Material.POTION).effectColor(ColorType.BLACK.getColor()).glow().build(),
			minigamer -> {
				for (Minigamer _minigamer : minigamer.getMatch().getMinigamers())
					if (_minigamer != minigamer) {
						_minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 1));
						_minigamer.getPlayer().sendMessage(Minigames.PREFIX + "You have been trapped!");
					}
			});

	PowerUpUtils.PowerUp NEGATIVE_BLINDNESS = new PowerUpUtils.PowerUp("Blindness", false,
			new ItemBuilder(Material.POTION).effectColor(ColorType.BLACK.getColor()).build(),
			minigamer -> minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 1)));

	PowerUpUtils.PowerUp SNOWBALL = new PowerUpUtils.PowerUp("Snowball", true, Material.SNOW_BALL,
			minigamer ->
				minigamer.getMatch().getMinigamers().forEach(_minigamer -> _minigamer.getPlayer().getInventory().addItem(
						new ItemBuilder(Material.SNOW_BALL)
								.name("&bKnockback Snowball")
								.enchant(Enchantment.KNOCKBACK, 2)
								.amount(3)
								.build()
				)));

	PowerUpUtils.PowerUp LEVITATION = new PowerUpUtils.PowerUp("Levitation", true, Material.ELYTRA,
			minigamer -> {
				for (Minigamer _minigamer : minigamer.getMatch().getMinigamers())
					if (_minigamer != minigamer) {
						_minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 5 * 20, 3));
						_minigamer.getPlayer().sendMessage(Minigames.PREFIX + "You have been trapped!");
					}
			});

	List<PowerUpUtils.PowerUp> powerUps = Arrays.asList(JUMP, POSITIVE_BLINDNESS, NEGATIVE_BLINDNESS, SNOWBALL, LEVITATION);

	@EventHandler
	public void onSnowballHit(ProjectileHitEvent event) {
		if (!(event.getHitEntity() instanceof Player)) return;
		Player player = (Player) event.getHitEntity();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this)) return;
		player.setVelocity(event.getEntity().getVelocity().multiply(0.5).add(new Vector(0, .5, 0)));
	}


}

