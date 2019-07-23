package me.pugabyte.bncore.features.dailyrewards;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.dailyrewards.DailyRewards;
import me.pugabyte.bncore.models.dailyrewards.DailyRewardsService;
import me.pugabyte.bncore.models.exceptions.MustBeIngameException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class DailyRewardsCommand implements CommandExecutor {
	final private String PREFIX = BNCore.getPrefix("DailyRewards");

	public DailyRewardsCommand() {
		BNCore.registerCommand("jdailyrewards", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (!(sender instanceof Player)) {
				if (args[0].equalsIgnoreCase("dailyreset")) {
					doDailyReset();
				} else {
					throw new MustBeIngameException();
				}
			} else {
				Player player = (Player) sender;
				DailyRewardsService service = new DailyRewardsService();
				DailyRewards dailyRewards = (DailyRewards) service.get(player);

				switch (args.length != 0 ? args[0] : "") {
					case "streak":
						player.sendMessage(PREFIX + "Your current streak is " + dailyRewards.getStreak());
						break;
					case "today":
						if (args[1].length() > 0) {
							Optional<Player> playerMaybe = BNCore.getPlayer(args[1]);
							if (playerMaybe.isPresent()) {

							}
						}
					case "unclaim":

					case "reset":

					case "confirmreset":

					case "top":

					default:
						DailyRewardsFeature.menu(player);
				}
			}
		} catch (MustBeIngameException ex) {
			sender.sendMessage(ex.getMessage());
		} catch (ArrayIndexOutOfBoundsException ex) {
			// Ignore
		}
		return true;
	}

	private void doDailyReset() {
	}

}
