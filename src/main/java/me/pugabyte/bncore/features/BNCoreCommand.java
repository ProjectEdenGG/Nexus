package me.pugabyte.bncore.features;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.skript.SkriptFunctions;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.FireworkLauncher;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.material.Dye;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BNCoreCommand extends CustomCommand {
	public BNCoreCommand(CommandEvent event) {
		super(event);
	}

	// TODO: Move to appropriate place
	@TabCompleterFor({Player.class, OfflinePlayer.class, Nerd.class})
	List<String> playerTabComplete(String filter) {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> player.getName().toLowerCase().startsWith(filter.toLowerCase()))
				.map(Player::getName)
				.collect(Collectors.toList());
	}

	@Path("getPlayer {offlineplayer}")
	void getPlayer(@Arg Nerd nerd) {
		reply(nerd.toString());
	}

	@Path("redtint {double} {double} {player}")
	void redTint(@Arg("0.5") double fadeTime, @Arg("10") double intensity, @Arg("self") Player player) {
		SkriptFunctions.redTint(player, fadeTime, intensity);
	}

	@TabCompleterFor(ColorType.class)
	List<String> colorTypeTabComplete(String filter) {
		return Arrays.stream(ColorType.values())
				.filter(value -> value.name().toLowerCase().startsWith(filter))
				.map(Enum::name)
				.collect(Collectors.toList());
	}

	@Override
	public Object convert(String value, Class<?> type) {
		if (ColorType.class == type)
			try {
				return ColorType.valueOf(value.toUpperCase());
			} catch (IllegalArgumentException ignore) {
				error("ColorType from " + value + " not found");
			}
		return super.convert(value, type);
	}

	@Path("getColor {color}")
	void getColor(@Arg ColorType colorType) {

		Location location = player().getLocation().add(2, 0, 0);

		// Firework (Color)
		new FireworkLauncher(location).color(colorType.getColor()).type(FireworkEffect.Type.BALL).detonateAfter(1).launch();

		// Place block
		location.getBlock().setType(Material.WOOL);
		location.getBlock().setData(colorType.getDurability().byteValue());

		// Give dyed chestplate
		Dye dye = new Dye(Material.LEATHER_CHESTPLATE);
		dye.setColor(colorType.getDyeColor());
		location.getWorld().dropItemNaturally(location.add(0, 1, 0), dye.toItemStack(1));

		// ChatColor
		reply(colorType.getChatColor() + colorType.name());
	}
}
