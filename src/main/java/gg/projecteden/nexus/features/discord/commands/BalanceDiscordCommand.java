package gg.projecteden.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.OfflinePlayer;

import static com.google.common.base.Strings.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

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
					shopGroup = ShopGroup.valueOf(args[1].toUpperCase());

				String formatted = new BankerService().getBalanceFormatted(player, shopGroup);
				boolean isSelf = user.getUuid().equals(player.getUniqueId());
				event.reply(camelCase(shopGroup) + " balance" + (isSelf ? "" : " of " + Nickname.of(player)) + ": " + formatted);
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}

}
