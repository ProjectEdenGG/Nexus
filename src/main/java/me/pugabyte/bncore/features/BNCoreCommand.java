package me.pugabyte.bncore.features;

import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.models.nerds.NerdService;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.skript.SkriptFunctions;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.FireworkLauncher;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BNCoreCommand extends CustomCommand {
	public BNCoreCommand(CommandEvent event) {
		super(event);
	}

	@Path("getPlayer [player]")
	void getPlayer(@Arg("self") OfflinePlayer player) {
		send(player.getName());
	}

	@Path("redtint [fadeTime] [intensity] [player]")
	void redTint(@Arg("0.5") double fadeTime, @Arg("10") double intensity, @Arg("self") Player player) {
		SkriptFunctions.redTint(player, fadeTime, intensity);
	}

	@SneakyThrows
	@Path("setting <type> [value]")
	void setting(String type, String value) {
		if (!isNullOrEmpty(value))
			new SettingService().save(new Setting(player(), type, value));
		send("Setting: " + new SettingService().get(player(), type));
	}

	@Path("schem save <name>")
	@Permission("group.seniorstaff")
	void schemSave(String name) {
		WorldEditUtils worldEditUtils = new WorldEditUtils(player().getWorld());
		worldEditUtils.save(name, worldEditUtils.getPlayerSelection(player()));
		send("Saved schematic " + name);
	}

	@Path("schem paste <name>")
	@Permission("group.seniorstaff")
	void schemPaste(String name) {
		WorldEditUtils worldEditUtils = new WorldEditUtils(player().getWorld());
		worldEditUtils.paste(name, player().getLocation());
		send("Pasted schematic " + name);
	}

	@ConverterFor(Nerd.class)
	Nerd convertToNerd(String value) {
		return new NerdService().get(convertToOfflinePlayer(value));
	}

	@TabCompleterFor(Nerd.class)
	List<String> tabCompleteNerd(String value) {
		return tabCompletePlayer(value);
	}

	@ConverterFor(Material.class)
	Material convertToMaterial(String value) {
		Material material = Material.matchMaterial(value);
		if (material == null)
			throw new InvalidInputException("Material from " + value + " not found");
		return material;
	}

	@ConverterFor(ColorType.class)
	ColorType convertToColorType(String value) {
		try {
			return ColorType.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException ignore) {
			throw new InvalidInputException("ColorType from " + value + " not found");
		}
	}

	@TabCompleterFor(ColorType.class)
	List<String> tabCompleteColorType(String filter) {
		return Arrays.stream(ColorType.values())
				.filter(value -> value.name().toLowerCase().startsWith(filter))
				.map(Enum::name)
				.collect(Collectors.toList());
	}

	@Path("getColor <color>")
	void getColor(ColorType colorType) {

		Location location = player().getLocation().add(2, 0, 0);

		// Firework (Color)
		new FireworkLauncher(location).color(colorType.getColor()).type(FireworkEffect.Type.BALL).detonateAfter(1).launch();

		// Place block
		location.getBlock().setType(Material.WOOL);
		location.getBlock().setData(colorType.getDurability().byteValue());

		// Give dyed chestplate
		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
		meta.setColor(colorType.getColor());
		chestplate.setItemMeta(meta);
		location.getWorld().dropItemNaturally(location.add(0, 1, 0), chestplate);

		// ChatColor
		send(colorType.getChatColor() + colorType.name());
	}

	@Path("signgui")
	void signgui() {
		BNCore.getInstance().getSignMenuFactory()
				.create("", "1", "2")
				.response((player, lines) -> {
					for (String string : lines)
						send(string);
				})
				.open(player());
	}
}
