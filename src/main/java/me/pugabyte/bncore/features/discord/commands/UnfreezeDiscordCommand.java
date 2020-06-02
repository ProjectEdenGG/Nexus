package me.pugabyte.bncore.features.discord.commands;

import com.google.common.base.Strings;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.discord.Bot;
import me.pugabyte.bncore.features.discord.Bot.HandledBy;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.features.discord.DiscordId.Role;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.NoPermissionException;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

@HandledBy(Bot.RELAY)
public class UnfreezeDiscordCommand extends Command {
	public static final String PREFIX = StringUtils.getPrefix("Freeze");

	public UnfreezeDiscordCommand() {
		this.name = "unfreeze";
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(Channel.STAFF_BRIDGE.getId()))
			return;

		Tasks.sync(() -> {
			try {
				if (Strings.isNullOrEmpty(event.getArgs()))
					throw new InvalidInputException("Correct usage: /unfreeze <players...>");

				DiscordUser user = new DiscordService().getFromUserId(event.getAuthor().getId());
				if (user.getUuid() == null)
					throw new NoPermissionException();

				OfflinePlayer executor = Utils.getPlayer(user.getUuid());
							SettingService service = new SettingService();
				for (String arg : event.getArgs().split(" ")) {
					OfflinePlayer player = Utils.getPlayer(arg);
					if (!player.isOnline() || player.getPlayer() == null)
						throw new PlayerNotOnlineException(player);

					Setting setting = service.get(player, "frozen");
					if (!setting.getBoolean()) throw new InvalidInputException(player.getName() + " is not frozen");
					setting.setBoolean(false);
					service.save(setting);
					if (player.getPlayer().getVehicle() != null)
						player.getPlayer().getVehicle().remove();
					player.getPlayer().sendMessage(colorize("&cYou have been unfrozen."));
					Chat.broadcast(PREFIX + "&e" + executor.getName() + " &3has unfrozen &e" + player.getName(), "Staff");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				event.reply(stripColor(ex.getMessage()));
			}
		});
	}


}
