package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Armadillo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;
import static java.util.Objects.requireNonNull;

@HideFromWiki
@NoArgsConstructor
@Permission(Group.STAFF)
public class ArmadilloSoccerCommand extends CustomCommand implements Listener {

	private static final String NAME = "&eArmadillo Soccer Stick";
	private static final ItemBuilder ITEM = new ItemBuilder(Material.STICK)
		.name(NAME)
		.enchant(Enchantment.KNOCKBACK, 6);

	public ArmadilloSoccerCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("stick")
	void stick() {
		if (Minigames.getWorld() != world())
			error("You must be in minigames to use this command");

		giveItem(ITEM);
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player player))
			return;

		boolean inRegion = new WorldGuardUtils(player).isInRegion(player, "soccer");
		if (Rank.of(player).lt(Rank.ADMIN) && !inRegion)
			return;

		// TODO Model?
		ItemStack tool = player.getInventory().getItemInMainHand();
		if (tool.getType() != ITEM.get().getType())
			return;

		String displayName = tool.getItemMeta().getDisplayName();
		if (!stripColor(NAME).equals(stripColor(displayName)))
			return;
		//

		if (!(event.getEntity() instanceof Armadillo armadillo)) {
			if (!(event.getEntity() instanceof Player opponent))
				event.setCancelled(true);
			return;
		}

		event.setDamage(0);
		armadillo.setHealth(requireNonNull(armadillo.getAttribute(Attribute.MAX_HEALTH)).getValue());

		player.addPotionEffect(new PotionEffectBuilder()
			.type(PotionEffectType.SLOWNESS)
			.duration(TickTime.SECOND.x(4))
			.build());
	}
}

