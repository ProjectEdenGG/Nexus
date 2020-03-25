package me.pugabyte.bncore.features;

import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.koda.Koda;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.NerdService;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.skript.SkriptFunctions;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.getLastColor;

@Permission("group.seniorstaff")
public class BNCoreCommand extends CustomCommand {
	public BNCoreCommand(CommandEvent event) {
		super(event);
	}

	static {
		BNCore.registerPlaceholder("vanished", event ->
				String.valueOf(Utils.isVanished(event.getPlayer())));

		BNCore.registerPlaceholder("nerds", event ->
				String.valueOf(Bukkit.getOnlinePlayers().stream().filter(target -> Utils.canSee(event.getPlayer(), target)).count()));
	}

	@Path("koda <message...>")
	void koda(String message) {
		Koda.say(message);
	}

	@Path("getPlayer [player]")
	void getPlayer(@Arg("self") OfflinePlayer player) {
		send(player.getName());
	}

	@Path("redtint [fadeTime] [intensity] [player]")
	void redTint(@Arg("0.5") double fadeTime, @Arg("10") double intensity, @Arg("self") Player player) {
		SkriptFunctions.redTint(player, fadeTime, intensity);
	}

	@Path("expCooldown <cooldown>")
	void expCooldown(@Arg("20") int cooldown) {
		Tasks.Countdown.builder()
				.duration(cooldown)
				.onStart(() -> player().setLevel(0))
				.onTick(ticks -> player().setExp((float) ticks / cooldown))
				.onComplete(() -> player().setExp(0))
				.start();
	}

	@Path("setExp <number>")
	void setExp(float exp) {
		player().setExp(exp);
	}

	@Path("setTotalExperience <number>")
	void setTotalExperience(int exp) {
		player().setTotalExperience(exp);
	}

	@Path("setLevel <number>")
	void setLevel(int exp) {
		player().setLevel(exp);
	}

	@Path("actionBar [message] [duration] [fade]")
	void actionBar(@Arg(" ") String message, int duration, @Arg("true") boolean fade) {
		Utils.sendActionBar(player(), message, duration, fade);
	}

	@SneakyThrows
	@Path("setting <type> [value]")
	void setting(String type, String value) {
		if (!isNullOrEmpty(value))
			new SettingService().save(new Setting(player(), type, value));
		send("Setting: " + new SettingService().get(player(), type));
	}

	@Path("getBlockStandingOn")
	void getBlockStandingOn() {
		Block block = Utils.getBlockStandingOn(player());
		if (block == null)
			send("Nothing");
		else
			send(block.getType().name());
	}

	@Path("getOnlineNerdsWith <permission>")
	void getOnlineNerdsWith(String permission) {
		send(new NerdService().getOnlineNerdsWith(permission).stream().map(Nerd::getName).collect(Collectors.joining(", ")));
	}

	@Path("schem save <name>")
	void schemSave(String name) {
		WorldEditUtils worldEditUtils = new WorldEditUtils(player().getWorld());
		worldEditUtils.save(name, worldEditUtils.getPlayerSelection(player()));
		send("Saved schematic " + name);
	}

	@Path("schem paste <name>")
	void schemPaste(String name) {
		WorldEditUtils worldEditUtils = new WorldEditUtils(player().getWorld());
		worldEditUtils.paste(name, player().getLocation());
		send("Pasted schematic " + name);
	}

	@Path("signgui")
	void signgui() {
		BNCore.getSignMenuFactory()
				.lines("1", "2", "3", "4")
				.response((player, lines) -> {
					for (String string : lines)
						send(string);
				})
				.open(player());
	}

	@Path("lastColor <text...>")
	void lastColor(String text) {
		send(getLastColor(text) + "color");
	}

	@Path("cooldown")
	@Cooldown({
			@Part(value = Time.SECOND, x = 5),
			@Part(value = Time.TICK, x = 5)
	})
	void cooldown() {
		send("Hello!");
	}

	@ConverterFor(Nerd.class)
	Nerd convertToNerd(String value) {
		return new NerdService().get(convertToOfflinePlayer(value));
	}

	@TabCompleterFor(Nerd.class)
	List<String> tabCompleteNerd(String value) {
		return tabCompletePlayer(value);
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

	@ConverterFor(WorldGroup.class)
	WorldGroup convertToWorldGroup(String value) {
		try {
			return WorldGroup.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException ignore) {
			throw new InvalidInputException("WorldGroup from " + value + " not found");
		}
	}

	@TabCompleterFor(WorldGroup.class)
	List<String> tabCompleteWorldGroup(String filter) {
		return Arrays.stream(WorldGroup.values())
				.filter(worldGroup -> worldGroup.name().toLowerCase().startsWith(filter.toLowerCase()))
				.map(WorldGroup::toString)
				.collect(Collectors.toList());
	}
}
