package gg.projecteden.nexus.features.events;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Permission(Group.MODERATOR)
public class DyeBombCommand extends CustomCommand implements Listener {
	private static final List<ColorType> filter = List.of(ColorType.BLACK, ColorType.GRAY, ColorType.LIGHT_GRAY, ColorType.BROWN);
	private static final List<Color> colors = Arrays.stream(ColorType.values())
		.filter(colorType -> !filter.contains(colorType))
		.map(ColorType::getBukkitColor)
		.toList();

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
	@Description("Spawn dye bombs")
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
		if (Nullables.isNullOrAir(item))
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
		if (CooldownService.isOnCooldown(player, "throwDyeBomb", 2 * 20))
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
		snowball.setMetadata(FireworkLauncher.METADATA_KEY_DAMAGE, new FixedMetadataValue(Nexus.getInstance(), false));

		player.playSound(player.getLocation(), Sound.ENTITY_EGG_THROW, 0.5F, 1F);
	}

	@EventHandler
	public void onDyeBombHit(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Snowball snowball)) return;
		if (!snowball.hasMetadata(FireworkLauncher.METADATA_KEY_DAMAGE)) return;
		if (event.getHitBlock() == null && event.getHitEntity() == null) return;

		Vector vector = snowball.getVelocity().normalize().multiply(0.1);
		Location location = snowball.getLocation().subtract(vector);

		FireworkLauncher firework = FireworkLauncher.random(location)
			.detonateAfter(0L)
			.power(0)
			.type(FireworkEffect.Type.BURST)
			.colors(randomColors())
			.fadeColors(randomColors())
			.silent(true);

		if (RandomUtils.chanceOf(50))
			firework.color(randomColor()).fadeColor(randomColor());

		firework.launch();
	}

	private List<Color> randomColors() {
		List<Color> random = new ArrayList<>();
		for (int i = 0; i < colors.size(); i++) {
			Color color = randomColor();
			if (!random.contains(color))
				random.add(color);
		}

		return random;
	}

	private Color randomColor() {
		return RandomUtils.randomElement(colors);
	}
}
