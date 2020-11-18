package me.pugabyte.bncore.features.holidays.easter20;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.annotations.Disabled;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.ItemUtils;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.ActionGroup;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@Disabled
@NoArgsConstructor
public class Easter20Command extends CustomCommand implements Listener {

	private static String header = StringUtils.colorize("&1[Easter 2020]");
	private static String PREFIX = StringUtils.getPrefix("Easter2020");
	SettingService service = new SettingService();

	public Easter20Command(CommandEvent event) {
		super(event);
	}

	private Sign getTargetSign(Player player) {
		Block targetBlock = player.getTargetBlockExact(10);
		Material material = targetBlock.getType();
		if (ItemUtils.isNullOrAir(material) || !MaterialTag.SIGNS.isTagged(material))
			error("You must be looking at a sign!");
		return (Sign) targetBlock.getState();
	}

	@Permission("group.staff")
	@Path("set <player>")
	void set(OfflinePlayer player) {
		Sign sign = getTargetSign(player());
		sign.setLine(0, header);
		sign.setLine(1, player.getName());
		sign.update();
	}

	@Path("check <player>")
	void check(@Arg("self") OfflinePlayer player) {
		send(PREFIX + "&e" + player.getName() + " &3has found &e" + service.get(player, "easter2020").getValue() + "&3 easter eggs");
	}

	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		if (!ActionGroup.CLICK_BLOCK.applies(event)) return;
		if (!MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())) return;
		if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;

		Sign sign = (Sign) event.getClickedBlock().getState();
		if (!header.equals(sign.getLine(0))) return;

		SettingService service = new SettingService();
		Player player = event.getPlayer();
		Setting setting = service.get(player, "easter2020");
		Setting found = service.get(player, "easter2020Found");

		String name = sign.getLine(1);

		if (found.getValue() != null && found.getValue().contains(name)) {
			send(player, PREFIX + "You have already found this egg. Go search around warps for more");
			return;
		}

		int clicked = 0;
		if (Utils.isInt(setting.getValue()))
			clicked = Integer.parseInt(setting.getValue());
		clicked++;
		if (clicked == 19) {
			send(player, PREFIX + "You have found all the eggs! You have won &e$10,000");
			BNCore.getEcon().depositPlayer(player, 10000);
		} else if (clicked % 3 == 0) {
			ItemStack headPaper = new ItemBuilder(Material.PAPER).name("&3Coupon for 1 HDB head").lore("&eThis coupon is valid for one head from the head database. " +
					"Claim it with a staff member").build();
			ItemUtils.giveItem(player, headPaper);
			send(player, PREFIX + "You have found &e" + name + "'s &3easter egg. You have been given &eone head database coupon");
		} else {
			send(player, PREFIX + "You have found &e" + name + "'s &3easter egg. You have been given &e$500");
			BNCore.getEcon().depositPlayer(player, 500);
		}
		setting.setValue(clicked + "");
		service.save(setting);

		if (found.getValue() == null)
			found.setValue(name);
		else
			found.setValue(found.getValue() + name);
		service.save(found);

	}

}
