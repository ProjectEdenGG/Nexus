package me.pugabyte.bncore.features.commands.staff;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@NoArgsConstructor
@Permission("group.staff")
public class LavaCommand extends CustomCommand implements Listener {

	public LavaCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void lava() {
		WorldGroup world = WorldGroup.get(player().getWorld());
		if (world.equals(WorldGroup.SKYBLOCK))
			error("Not allowed in " + world);

		Setting setting = new SettingService().get(player(), "lava");

		if (setting.getBoolean()) {
			new SettingService().delete(player(), "lava");
			send("&3Unlimited lava turned &eoff&3.");
		} else {
			setting.setBoolean(true);
			new SettingService().save(setting);
			send("&3Unlimited lava turned &eon&3.");
		}
	}

	@EventHandler
	public void onPlaceLava(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		if (!new Nerd(player).getRank().isStaff())
			return;

		Material material = event.getBucket();
		if (!material.equals(Material.LAVA_BUCKET))
			return;

		WorldGroup world = WorldGroup.get(player.getWorld());
		if (world.equals(WorldGroup.SKYBLOCK))
			return;

		Setting setting = new SettingService().get(player, "lava");
		if (!setting.getBoolean())
			return;

		PlayerInventory playerInv = player.getInventory();
		Tasks.wait(1, () -> playerInv.setItemInMainHand(new ItemStack(material)));

	}
}
