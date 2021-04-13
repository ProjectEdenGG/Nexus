package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.HandledBy;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.discord.DiscordUserService;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.OfflinePlayer;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class BalanceDiscordCommand extends Command {

	public BalanceDiscordCommand() {
		this.name = "balance";
		this.aliases = new String[]{"bal", "money"};
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {
				DiscordUser user = new DiscordUserService().checkVerified(event.getAuthor().getId());
				OfflinePlayer player = PlayerUtils.getPlayer(user.getUuid());
				ShopGroup shopGroup = ShopGroup.SURVIVAL;

				String[] args = event.getArgs().split(" ");
				if (args.length > 0 && !isNullOrEmpty(args[0]))
					player = PlayerUtils.getPlayer(args[0]);
				if (args.length > 1 && !isNullOrEmpty(args[1]))
					shopGroup = ShopGroup.valueOf(args[1]);

				String formatted = new BankerService().getBalanceFormatted(player, shopGroup);
				boolean isSelf = user.getUuid().equals(player.getUniqueId().toString());
				event.reply(camelCase(shopGroup) + " balance" + (isSelf ? "" : " of " + player.getName()) + ": " + formatted);
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof NexusException))
					ex.printStackTrace();
			}
		});
	}

}
