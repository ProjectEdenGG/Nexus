package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CuriosityCakeCommand extends CustomCommand {

	public CuriosityCakeCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void cookies() {
		SettingService service = new SettingService();
		Setting setting = service.get(player(), "curiosityCake");
		if (setting.getBoolean()) return;
		setting.setBoolean(true);
		service.save(setting);
		player().getInventory().addItem(new ItemStack(Material.CAKE));
		line();
		send("&3You earned a &ecake &3for learning!");
	}


}
