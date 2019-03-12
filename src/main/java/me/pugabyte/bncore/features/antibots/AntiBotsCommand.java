package me.pugabyte.bncore.features.antibots;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.antibots.models.AntiBotsDatabase;
import me.pugabyte.bncore.features.antibots.models.AntiBotsResultType;
import me.pugabyte.bncore.features.antibots.models.DeniedEntry;
import me.pugabyte.bncore.models.exceptions.InvalidInputException;
import me.pugabyte.bncore.models.exceptions.NoPermissionException;
import me.pugabyte.bncore.skript.SkriptFunctions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AntiBotsCommand implements CommandExecutor {
	private final static String PREFIX = BNCore.getPrefix("AntiBots");
	private static AntiBotsDatabase.AllowedReader allowedReader = new AntiBotsDatabase.AllowedReader();
	private static AntiBotsDatabase.DeniedReader deniedReader = new AntiBotsDatabase.DeniedReader();
	private static Set<String> allowed = (Set<String>) allowedReader.read();
	private static Set<DeniedEntry> denied = (Set<DeniedEntry>) deniedReader.read();
	private static Map<AntiBotsResultType, Boolean> dirty = new HashMap<>();

	AntiBotsCommand() {
		BNCore.registerCommand("antibots", this);
	}

	public static Set<String> getAllowed() {
		return allowed;
	}

	public static Set<DeniedEntry> getDenied() {
		return denied;
	}

	static void addAllowed(String ip) {
		if (!allowed.contains(ip)) {
			allowed.add(ip);
			setDirty(AntiBotsResultType.ALLOWED, true);
		}
	}

	static void addDenied(DeniedEntry entry) {
		denied.add(entry);
		setDirty(AntiBotsResultType.DENIED, true);

		checkForIpBan();
	}

	private static void checkForIpBan() {
		Map<String, Integer> duplicates = new HashMap<>();

		for (DeniedEntry entry : denied) {
			String ip = entry.getIp();
			int count = 0;
			if (duplicates.containsKey(ip)) {
				count = duplicates.get(ip);
			}
			duplicates.put(ip, count + 1);
		}

		for (Map.Entry<String, Integer> duplicate : duplicates.entrySet()) {
			if (duplicate.getValue() == 5) {
				String ip = duplicate.getKey();
				Set<String> matches = denied.stream()
						.filter(denial -> denial.getIp().equals(ip))
						.map(DeniedEntry::getName)
						.collect(Collectors.toSet());
				String names = String.join(", ", matches);
				LocalDate localDate = LocalDate.now();//For reference
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
				String formatted = localDate.format(formatter);
				SkriptFunctions.adminLog("Detected 5 denied attempts from " + ip + " (" + names + ")" +
						"```sudo ufw insert 1 deny from " + ip + " comment '" + names + " (" + formatted + ")'```");
			}
		}
	}

	static void setDirty(AntiBotsResultType resultType, boolean isDirty) {
		dirty.put(resultType, isDirty);
	}

	static boolean isDirty(AntiBotsResultType resultType) {
		try {
			return dirty.get(resultType);
		} catch (NullPointerException ex) {
			return false;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
		try {
			if (sender instanceof Player && !sender.hasPermission("skriptrank.staff")) {
				throw new NoPermissionException();
			}

			if (args.length > 0) {
				switch (args[0].toLowerCase()) {
					case "on":
					case "true":
					case "enable":
						BNCore.antiBots.setEnabled(true);
						sender.sendMessage(PREFIX + "Enabled");
						break;
					case "off":
					case "false":
					case "disable":
						BNCore.antiBots.setEnabled(false);
						sender.sendMessage(PREFIX + "Disabled");
						break;
					default:
						throw new InvalidInputException(ChatColor.RED + "/antibots [on|off]");
				}
			} else {
				sender.sendMessage(PREFIX + "Allowed IPs: " + allowed.size());
				sender.sendMessage(PREFIX + "Denied IPs: " + denied.size());
			}
		} catch (InvalidInputException | NoPermissionException ex) {
			sender.sendMessage(PREFIX + ex.getMessage());
		}
		return true;
	}
}
