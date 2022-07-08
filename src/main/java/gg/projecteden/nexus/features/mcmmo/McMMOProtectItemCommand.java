package gg.projecteden.nexus.features.mcmmo;

import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@NoArgsConstructor
@Redirect(from = "/mcmmo protectItem", to = "/mcmmoProtectItem")
public class McMMOProtectItemCommand extends CustomCommand implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("mcMMO");

	public McMMOProtectItemCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		final EquipmentSlot hand = getHandWithToolRequired();
		final ItemBuilder tool = new ItemBuilder(inventory().getItem(hand));
		final boolean newState = !ItemSetting.MCMMOABLE.of(tool);
		inventory().setItem(hand, tool.setting(ItemSetting.MCMMOABLE, newState).build());

		if (newState)
			send(PREFIX + "&cRemoved repair/salvage protection");
		else
			send(PREFIX + "&aProtected item from repair/salvage");
	}

	@EventHandler
	public void on(McMMOPlayerSalvageCheckEvent event) {
		if (isNullOrAir(event.getSalvageItem()))
			return;

		if (ItemSetting.MCMMOABLE.of(new ItemBuilder(event.getSalvageItem())))
			return;

		event.setCancelled(true);
		PlayerUtils.send(event.getPlayer(), PREFIX + "&cThat &e" + camelCase(event.getSalvageItem().getType()) + " &cis protected from mcMMO salvage");
	}

	@EventHandler
	public void on(McMMOPlayerRepairCheckEvent event) {
		if (isNullOrAir(event.getRepairedObject()))
			return;

		if (ItemSetting.MCMMOABLE.of(new ItemBuilder(event.getRepairedObject())))
			return;

		event.setCancelled(true);
		PlayerUtils.send(event.getPlayer(), PREFIX + "&cThat &e" + camelCase(event.getRepairedObject().getType()) + " &cis protected from mcMMO repair");
	}
}
