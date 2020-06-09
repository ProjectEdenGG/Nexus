package me.pugabyte.bncore.features.holidays.pride20;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.features.holidays.DyeBombCommand;
import me.pugabyte.bncore.framework.exceptions.postconfigured.CooldownException;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Pride20Listener implements Listener {

	@EventHandler
	public void onBalloonNPCClick(NPCRightClickEvent event) {
		if (event.getNPC().getId() != 2771) return;
		Player player = event.getClicker();

		CooldownService cooldownService = new CooldownService();
		try {
			cooldownService.check(player, "prideDyeBomb", Time.MINUTE.x(1));
		} catch (CooldownException ex) {
			return;
		}

		player.sendMessage(StringUtils.colorize("&3Vendor > &eSadly all my balloons have uh... floated away, but I can give you this to play with"));
		DyeBombCommand.giveDyeBomb(player, 5);
	}

	@EventHandler
	public void onSecretCatClick(NPCRightClickEvent event) {
		if (event.getNPC().getId() != 2776) return;
		Player player = event.getClicker();

		SettingService service = new SettingService();
		Setting setting = service.get(player, "pride20Secret");
		if (setting.getBoolean()) {

			CooldownService cooldownService = new CooldownService();
			try {
				cooldownService.check(player, "pride20Cat", Time.SECOND.x(10));
			} catch (CooldownException ex) {
				return;
			}

			player.playSound(player.getLocation(), Sound.ENTITY_CAT_PURREOW, 5f, .08f);
			return;
		}

		player.playSound(player.getLocation(), Sound.ENTITY_CAT_PURREOW, 5f, .08f);
		Utils.giveItem(player, new ItemBuilder(Material.ORANGE_BANNER)
				.pattern(DyeColor.RED, PatternType.STRIPE_TOP)
				.pattern(DyeColor.YELLOW, PatternType.STRIPE_MIDDLE)
				.pattern(DyeColor.LIME, PatternType.HALF_HORIZONTAL_MIRROR)
				.pattern(DyeColor.YELLOW, PatternType.STRIPE_MIDDLE)
				.pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM)
				.build());

		setting.setBoolean(true);
		service.save(setting);
		player.sendMessage(StringUtils.colorize("&eHow did you even get here? I mean.... meow"));
	}


	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase("pride20")) return;
		event.getPlayer().sendMessage(StringUtils.colorize("&eWelcome to the Pride Parade!" +
				" Have a look at all the colorful floats and roam around the city. If you'd like to join the parade, " +
				"type &c/pride20 parade join &3while standing where you want to be in the parade. &eEnjoy and happy pride!"));
	}

}
