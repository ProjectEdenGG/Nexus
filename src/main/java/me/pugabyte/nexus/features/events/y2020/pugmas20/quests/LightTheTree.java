package me.pugabyte.nexus.features.events.y2020.pugmas20.quests;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

@NoArgsConstructor
public class LightTheTree implements Listener {
	private static final String lighterRg = Pugmas20.getRegion() + "_lighter";
	private static final String torchRg = Pugmas20.getRegion() + "_torch_";
	private static final String treeTorchRg = Pugmas20.getRegion() + "_treetorch_";
	//
	public static final ItemStack lighter_broken = Pugmas20.questItem(Material.FLINT_AND_STEEL).name("Broken Ceremonial Lighter").build();
	public static final ItemStack lighter = Pugmas20.questItem(Material.FLINT_AND_STEEL).name("Ceremonial Lighter").glow().build();
	public static final ItemStack steel_nugget = Pugmas20.questItem(Material.IRON_NUGGET).name("Steel Nugget").glow().build();

	@EventHandler
	public void onFindBrokenLighter(PlayerInteractEntityEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Player player = event.getPlayer();
		if (!Pugmas20.isAtPugmas(player)) return;

		Entity entity = event.getRightClicked();
		if (!entity.getType().equals(EntityType.ITEM_FRAME)) return;
		if (!Pugmas20.WGUtils.isInRegion(entity.getLocation(), lighterRg)) return;

		Pugmas20Service service = new Pugmas20Service();
		Pugmas20User user = service.get(player);
		if (!user.getLightTreeStage().equals(QuestStage.STEP_ONE)) return;

		event.setCancelled(true);
		ItemUtils.giveItem(player, lighter_broken);
		user.setLightTreeStage(QuestStage.STEP_TWO);
		service.save(user);
	}

	@EventHandler
	public void onLightTorch(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (!Pugmas20.isAtPugmas(player)) return;
		if (block == null) return;

		Pugmas20Service service = new Pugmas20Service();
		Pugmas20User user = service.get(player);
		if (!user.getLightTreeStage().equals(QuestStage.STEPS_DONE)) return;
		if (!lighter.equals(player.getInventory().getItemInMainHand())) return;

		event.setCancelled(true);

		Block placed = block.getType() == Material.NETHERRACK ? block.getRelative(event.getBlockFace()) : block;

		Set<ProtectedRegion> regions = Pugmas20.WGUtils.getRegionsLikeAt("pugmas20_torch_[0-9]+", placed.getLocation());
		if (regions.isEmpty()) return;

		int torch = Integer.parseInt(regions.iterator().next().getId().split("_")[2]);
		user.send("Torch #" + torch);
		if (torch > user.getTorchesLit() + 1) {
			user.send("You missed one!");
			return;
		} else if (torch == user.getTorchesLit() + 1) {
			user.send("Found next torch");
			user.setTorchesLit(torch);
			service.save(user);
		}

		Tasks.wait(1, () -> player.sendBlockChange(placed.getLocation(), Bukkit.createBlockData(Material.FIRE)));

		if (torch == 9)
			user.send("Done");
			// TODO PUGMAS Animation
	}
}
