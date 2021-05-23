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

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

@NoArgsConstructor
@Permission("group.moderator")
public class DyeBombCommand extends CustomCommand implements Listener {
	public static ItemStack getDyeBomb() {
		return new ItemBuilder(Material.MAGMA_CREAM).name("Dye Bomb").lore("&bEvent Item").unbreakable().itemFlags(ItemFlag.HIDE_UNBREAKABLE).build();
	}

	public DyeBombCommand(CommandEvent event) {
		super(event);
	}

	public static boolean isDyeBomb(ItemStack itemStack) {
		return getDyeBomb().isSimilar(itemStack);
	}

	@Path("give <amount> [player]")
	public void give(@Arg int amount, @Arg("self") Player player) {
		giveDyeBomb(player, amount);
	}

	public static void giveDyeBomb(Player player, int amount) {
		ItemStack item = getDyeBomb();
		item.setAmount(amount);
		PlayerUtils.giveItem(player, item);
	}

	@EventHandler
	public void throwDyeBomb(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;
		if (!ActionGroup.RIGHT_CLICK.applies(event))
			return;
		if (!event.getMaterial().equals(Material.MAGMA_CREAM))
			return;

		ItemStack item = event.getItem();
		if (isNullOrAir(item))
			return;

		// WhiteWolfKnight
		if (event.getPlayer().getUniqueId().toString().equals("f325c439-02c2-4043-995e-668113c7eb9f"))
			return;

		ItemMeta meta = item.getItemMeta();
		String originalName = StringUtils.stripColor(getDyeBomb().getItemMeta().getDisplayName());
		String itemName = StringUtils.stripColor(meta.getDisplayName());

		if (!originalName.equals(itemName))
			return;
		if (!meta.isUnbreakable())
			return;

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
		fw.colors(removeUgly(fw.colors()));
		fw.fadeColors(removeUgly(fw.fadeColors()));

		if (RandomUtils.chanceOf(50))
			fw.colors(Collections.singletonList(randomColor())).fadeColors(Collections.singletonList(randomColor()));
		fw.launch();
	}

	private Color randomColor() {
		List<Color> colors = new ArrayList<>() {{
			for (ColorType colortype : ColorType.values())
				add(colortype.getBukkitColor());
		}};

		return RandomUtils.randomElement(removeUgly(colors));
	}

	private static final List<ColorType> ugly = List.of(ColorType.BLACK, ColorType.GRAY, ColorType.LIGHT_GRAY, ColorType.BROWN);

	private List<Color> removeUgly(List<Color> oldColors) {
		List<Color> newColors = new ArrayList<>();
		for (Color color : oldColors) {
			ColorType type = ColorType.of(color);
			if (type != null && !ugly.contains(type))
				newColors.add(color);
		}

		if (newColors.isEmpty())
			newColors.add(randomColor());

		return newColors;
	}
}
