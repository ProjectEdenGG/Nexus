package me.pugabyte.bncore.features.minigames.lobby;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

@Permission("group.staff")
public class BasketballCommand extends CustomCommand {

	public BasketballCommand(CommandEvent event) {
		super(event);
	}

	@Path("save")
	void save() {
		ItemStack basketball = player().getInventory().getItemInMainHand();
		ItemMeta meta = basketball.getItemMeta();
		meta.setDisplayName(colorize("&6&lBasketball"));
		meta.setLore(Collections.singletonList(colorize("&eMinigame Lobby Basketball")));
		basketball.setItemMeta(meta);
		BNCore.getInstance().getConfig().set("minigames.lobby.basketball.item", basketball);
		BNCore.getInstance().saveConfig();
		Basketball.basketball = basketball;
		send(PREFIX + "Basketball saved");
	}

	@Path("give [player]")
	void give(@Arg("self") Player player) {
		Basketball.giveBasketball(player);
	}
}
