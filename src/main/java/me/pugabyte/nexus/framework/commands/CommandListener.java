package me.pugabyte.nexus.framework.commands;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.google.common.base.Strings;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.events.CommandTabEvent;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.trimFirst;

@NoArgsConstructor
public class CommandListener implements Listener {

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		for (Entry<String, String> redirect : Commands.getRedirects().entrySet()) {
			if (!(event.getMessage() + " ").toLowerCase().startsWith(redirect.getKey() + " "))
				continue;

			event.setCancelled(true);
			String command = redirect.getValue() + event.getMessage().substring(redirect.getKey().length());
			PlayerUtils.runCommand(event.getPlayer(), trimFirst(command));
			return;
		}
	}

//	@EventHandler
	@SneakyThrows
	public void onAsyncTabComplete(AsyncTabCompleteEvent event) {
		String buffer = event.getBuffer();
		if ((!event.isCommand() && !buffer.startsWith("/")) || buffer.indexOf(' ') == -1)
			return;

		if (!event.getSender().getName().equals("Pugabyte"))
			return;

		List<String> args = new ArrayList<>(Arrays.asList(buffer.split(" ")));
		String alias = trimFirst(args.get(0));
		CustomCommand customCommand = Commands.get(alias);
		if (customCommand == null)
			return;

		boolean lastIndexIsEmpty = Strings.isNullOrEmpty(args.get(args.size() - 1));
		args.removeIf(Strings::isNullOrEmpty);
		if (lastIndexIsEmpty) args.add("");

		CommandTabEvent tabEvent = new CommandTabEvent(event.getSender(), customCommand, alias, args, Collections.unmodifiableList(args));
//		if (tabEvent.callEvent()) {
			List<String> completions = customCommand.tabComplete(tabEvent);
			if (completions != null) {
				event.setCompletions(completions.stream().distinct().collect(Collectors.toList()));
				event.setHandled(true);
			}
//		}
	}

}
