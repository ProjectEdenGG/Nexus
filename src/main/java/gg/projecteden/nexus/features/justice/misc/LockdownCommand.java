package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.justice.Justice;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.lockdown.LockdownConfig;
import gg.projecteden.nexus.models.lockdown.LockdownConfigService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Aliases("ld")
@NoArgsConstructor
@Permission(Group.MODERATOR)
public class LockdownCommand extends CustomCommand implements Listener {
	private static final LockdownConfigService service = new LockdownConfigService();
	private static final LockdownConfig lockdown = service.get0();

	public LockdownCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Justice.PREFIX;
		DISCORD_PREFIX = Justice.DISCORD_PREFIX;
	}

	static {
		Tasks.repeat(TickTime.SECOND, TickTime.SECOND, () -> {
			if (!lockdown.isEnabled() || lockdown.getEnd() == null)
				return;

			if (lockdown.getEnd().isBefore(LocalDateTime.now())) {
				lockdown.end();
				service.save(lockdown);
				lockdown.broadcast("Lockdown expired");
			}
		});
	}

	@Path("start <time/reason...>")
	@Description("Enable lockdown mode")
	void start(String input) {
		if (lockdown.isEnabled()) {
			send(PREFIX + "Overriding previous lockdown: &c" + lockdown.getReason());
			lockdown.end();
		}

		lockdown.setEnabled(true);
		Timespan timespan = Timespan.find(input);
		lockdown.setReason(timespan.getRest());
		if (timespan.getOriginal() > 0)
			lockdown.setEnd(timespan.fromNow());
		service.save(lockdown);

		String message = "&c" + name() + " initiated lockdown for &e" + (timespan.isNull() ? "" : timespan.format(FormatType.LONG) + "&c for &e") + timespan.getRest();
		lockdown.broadcast(message);

		for (Player player : OnlinePlayers.getAll())
			if (!canBypass(player.getUniqueId())) {
				player.kick(getLockdownReason());
				lockdown.broadcast("Removed " + player.getName() + " from server");
			}
	}

	@Path("end")
	@Description("End lockdown mode")
	void end() {
		if (!lockdown.isEnabled())
			error("Lockdown not enabled");

		lockdown.end();
		service.save(lockdown);

		lockdown.broadcast(name() + " ended lockdown");
	}

	@Path("bypass add <player>")
	@Description("Allow a player to bypass the current lockdown")
	void bypassAdd(OfflinePlayer player) {
		lockdown.getBypass().add(player.getUniqueId());
		service.save(lockdown);
		send(PREFIX + "Added " + player.getName() + " to bypass list");
	}

	@Path("bypass remove <player>")
	@Description("Remove a player's bypass to the current lockdown")
	void bypassRemove(OfflinePlayer player) {
		lockdown.getBypass().remove(player.getUniqueId());
		service.save(lockdown);
		send(PREFIX + "Removed " + player.getName() + " from bypass list");
	}

	@EventHandler
	public void onConnect(AsyncPlayerPreLoginEvent event) {
		if (!lockdown.isEnabled())
			return;
		if (event.getLoginResult() != Result.ALLOWED)
			return;
		if (canBypass(event.getUniqueId()))
			return;

		event.disallow(Result.KICK_OTHER, getLockdownReason());
		lockdown.broadcast("Prevented " + event.getName() + " from joining the server");
	}

	public boolean canBypass(UUID player) {
		if (lockdown.getBypass().contains(player))
			return true;

		Hours hours = new HoursService().get(player);
		return hours.has(TickTime.MINUTE.x(30));
	}

	private Component getLockdownReason() {
		return new JsonBuilder()
				.next("&e&lProject Eden &3&lis in &4&llockdown &3&lmode")
				.newline()
				.newline()
				.next("&eReason: &c" + lockdown.getReason())
				.newline()
				.newline()
				.next("&3Please check back soon!")
				.build();
	}

}
