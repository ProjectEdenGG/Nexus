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
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.models.task.Task;
import me.pugabyte.bncore.models.task.TaskService;
import me.pugabyte.bncore.utils.SoundUtils.Jingle;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

	@Path("actionBar <duration> <message...>")
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
				.response(lines -> {
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

	@ConverterFor(LocalDate.class)
	LocalDate convertToLocalDate(String value) {
		try { return parseShortDate(value); } catch (Exception ignore) {}
		throw new InvalidInputException("Could not parse date");
	}
}
