package me.pugabyte.bncore.features;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.Koda;
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
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.models.task.Task;
import me.pugabyte.bncore.models.task.TaskService;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.SoundUtils.Jingle;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.parseShortDate;

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

	@Path("listTest <player...>")
	void listTest(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		send(players.stream().map(OfflinePlayer::getName).collect(Collectors.joining(", ")));
	}

	static {
		Tasks.repeatAsync(Time.SECOND, Time.SECOND.x(30), () -> {
			TaskService service = new TaskService();
			service.process("command-test").forEach(task ->
				Tasks.wait(Time.MINUTE.x(2), () -> {
					Map<String, Object> data = task.getJson();
					OfflinePlayer player = Utils.getPlayer((String) data.get("uuid"));
					if (player.isOnline() && player.getPlayer() != null)
						player.getPlayer().sendMessage((String) data.get("message"));
					service.complete(task);
				}));
		});
	}

	@Path("taskTest <message...>")
	@Permission("group.seniorstaff")
	void taskTest(String message) {
		new TaskService().save(new Task("command-test", new HashMap<String, Object>() {{
			put("uuid", player().getUniqueId().toString());
			put("message", message);
		}}, LocalDateTime.now().plusMinutes(1)));
	}

	@Path("koda <message...>")
	@Permission("group.seniorstaff")
	void koda(String message) {
		Koda.say(message);
	}

	@Path("getLastColor <message...>")
	void getLastColor(String message) {
		send(StringUtils.getLastColor(message) + "Last color");
	}

	@Path("getRank <player>")
	void getRank(Nerd player) {
		send(player.getRank().withFormat());
	}

	@Path("urlize <message...>")
	void urlize(String message) {
		send(json("Urlized: ").urlize(message));
	}

	@Path("getPlayer [player]")
	void getPlayer(@Arg("self") OfflinePlayer player) {
		send(player.getName());
	}

	@Permission("group.seniorstaff")
	@Path("expCooldown <cooldown>")
	void expCooldown(@Arg("20") int cooldown) {
		Tasks.Countdown.builder()
				.duration(cooldown)
				.onStart(() -> player().setLevel(0))
				.onTick(ticks -> player().setExp((float) ticks / cooldown))
				.onComplete(() -> player().setExp(0))
				.start();
	}

	@Permission("group.seniorstaff")
	@Path("setExp <number>")
	void setExp(float exp) {
		player().setExp(exp);
	}

	@Permission("group.seniorstaff")
	@Path("setTotalExperience <number>")
	void setTotalExperience(int exp) {
		player().setTotalExperience(exp);
	}

	@Permission("group.seniorstaff")
	@Path("setLevel <number>")
	void setLevel(int exp) {
		player().setLevel(exp);
	}

	@Path("actionBar [message] [duration] [fade]")
	void actionBar(@Arg(" ") String message, int duration, @Arg("true") boolean fade) {
		Utils.sendActionBar(player(), message, duration, fade);
	}

	@Permission("group.seniorstaff")
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

	@Permission("group.seniorstaff")
	@Path("getOnlineNerdsWith <permission>")
	void getOnlineNerdsWith(String permission) {
		send(new NerdService().getOnlineNerdsWith(permission).stream().map(Nerd::getName).collect(Collectors.joining(", ")));
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

	@Path("cooldown")
	@Cooldown({
			@Part(value = Time.SECOND, x = 5),
			@Part(value = Time.TICK, x = 5)
	})
	void cooldown() {
		send("Hello!");
	}

	@Path("jingles <jingle>")
	void jingles(Jingle jingle) {
		jingle.play(player());
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
		return tabCompleteEnum(ColorType.class, filter);
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

	@ConverterFor(Rank.class)
	Rank convertToRank(String value) {
		try {
			return Rank.getByString(value);
		} catch (IllegalArgumentException ignore) {
			throw new InvalidInputException("Rank from " + value + " not found");
		}
	}

	@TabCompleterFor(Rank.class)
	List<String> tabCompleteRank(String value) {
		return tabCompleteEnum(Rank.class, value);
	}

	@ConverterFor(LocalDate.class)
	LocalDate convertToLocalDate(String value) {
		try { return parseShortDate(value); } catch (Exception ignore) {}
		throw new InvalidInputException("Could not parse date");
	}

	@ConverterFor(Jingle.class)
	Jingle convertToJingle(String value) {
		try {
			return Jingle.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException ignore) {
			throw new InvalidInputException("Jingle from " + value + " not found");
		}
	}

	@TabCompleterFor(Jingle.class)
	List<String> tabCompleteJingle(String value) {
		return tabCompleteEnum(Jingle.class, value);
	}
}
