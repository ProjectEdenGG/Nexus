package me.pugabyte.nexus.features.events;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.FireworkLauncher;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils.ActionGroup;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Permission("group.moderator")
public class DyeBombCommand extends CustomCommand implements Listener {
	public static final ItemStack dyeBomb = new ItemBuilder(Material.MAGMA_CREAM).name("Dye Bomb").lore("&bEvent Item").unbreakable().itemFlags(ItemFlag.HIDE_UNBREAKABLE).build();

	public DyeBombCommand(CommandEvent event) {
		super(event);
	}

	@Path("give <amount> [player]")
	public void give(@Arg int amount, @Arg("self") Player player) {
		giveDyeBomb(player, amount);
	}

	public static void giveDyeBomb(Player player, int amount) {
		ItemStack item = dyeBomb.clone();
		item.setAmount(amount);
		PlayerUtils.giveItem(player, item);
	}

	@EventHandler
	public void throwDyeBomb(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!ActionGroup.RIGHT_CLICK.applies(event))
			return;
		if (!event.getMaterial().equals(Material.MAGMA_CREAM)) return;

		ItemStack item = event.getItem();
		if (item == null || item.getItemMeta() == null) return;
		ItemMeta meta = item.getItemMeta();

		String itemName = StringUtils.stripColor(meta.getDisplayName());
		if (!"Dye Bomb".equalsIgnoreCase(itemName)) return;
		if (!((meta.getLore() != null && meta.getLore().contains(StringUtils.colorize("&bEvent Item"))) || meta.isUnbreakable())) return;

		Player player = event.getPlayer();
		CooldownService cooldownService = new CooldownService();
		if (!cooldownService.check(player, "throwDyeBomb", 2 * 20))
			return;

		if (player.getGameMode() != GameMode.CREATIVE) {
			int amount = event.getItem().getAmount();
			event.getItem().setAmount(amount - 1);
		}

		Location location = player.getLocation().add(0, 1.5, 0);
		location.add(player.getLocation().getDirection());

		Snowball snowball = (Snowball) player.getWorld().spawnEntity(location, EntityType.SNOWBALL);
		snowball.setVelocity(location.getDirection().multiply(1.2));
		snowball.setShooter(player);
		snowball.setCustomName("DyeBomb");

		player.playSound(player.getLocation(), Sound.ENTITY_EGG_THROW, 0.5F, 1F);
	}

	@EventHandler
	public void onDyeBombHit(ProjectileHitEvent event) {
		Entity entity = event.getEntity();
		EntityType entityType = entity.getType();
		if (!entityType.equals(EntityType.SNOWBALL)) return;
		if (entity.getCustomName() == null) return;
		if (!entity.getCustomName().equalsIgnoreCase("DyeBomb")) return;
		if (event.getHitBlock() == null) {
			if (event.getHitEntity() == null)
				return;
		}

		Vector vel = event.getEntity().getVelocity().normalize().multiply(0.1);
		Location hitLoc = event.getEntity().getLocation().subtract(vel);

		FireworkLauncher fw = FireworkLauncher.random(hitLoc).detonateAfter(0).power(0).type(FireworkEffect.Type.BURST);
		fw.colors(removeUglies(fw.colors()));
		fw.fadeColors(removeUglies(fw.fadeColors()));

		if (RandomUtils.chanceOf(50))
			fw.colors(Collections.singletonList(randomColor())).fadeColors(Collections.singletonList(randomColor()));
		fw.launch();
	}

	private Color randomColor() {
		ColorType[] colorTypes = ColorType.values();
		List<Color> colors = new ArrayList<>();
		for (ColorType colortype : colorTypes) {
			colors.add(colortype.getBukkitColor());
		}
		return RandomUtils.randomElement(removeUglies(colors));
	}

	private List<Color> removeUglies(List<Color> oldColors) {
		List<Color> newColors = new ArrayList<>();
		for (Color fwColor : oldColors) {
			ColorType type = ColorType.of(fwColor);
			if (!type.equals(ColorType.BLACK)
					&& !type.equals(ColorType.GRAY)
					&& !type.equals(ColorType.LIGHT_GRAY)
					&& !type.equals(ColorType.BROWN))
				newColors.add(fwColor);
		}
		return newColors;
	}
}
