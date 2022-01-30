package gg.projecteden.nexus.models.rainbowarmor;

import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.models.invisiblearmour.InvisibleArmor;
import gg.projecteden.nexus.models.invisiblearmour.InvisibleArmorService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.PlayerUtils.ArmorSlot;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.MathUtils;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

@Data
@Builder
public class RainbowArmorTask {
	@Builder.Default
	private Color color = Color.fromRGB(255, 0, 0);

	private final HumanEntity entity;
	private Set<ArmorSlot> disabledSlots;
	private final int rate;
	private Supplier<Boolean> cancelIf;
	private Runnable onCancel;

	private int taskId;

	public RainbowArmorTask start() {
		taskId = Tasks.repeat(0, 2, () -> {
			if (cancelIf != null && cancelIf.get()) {
				stop();
				return;
			}

			increment();
			sendPackets();
		});
		return this;
	}

	public void stop() {
		Tasks.cancel(taskId);
		if (onCancel != null)
			onCancel.run();

		for (ArmorSlot slot : ArmorSlot.values())
			PacketUtils.sendFakeItem(entity, recipients(), entity.getInventory().getItem(slot.getSlot()), slot.getSlot());
	}

	private List<Player> recipients() {
		return OnlinePlayers.where().radius(entity.getLocation(), 50).get();
	}

	public ItemStack color(ItemStack item) {
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(color);
		item.setItemMeta(meta);
		return item;
	}

	public void sendPackets() {
		final boolean npc = CitizensUtils.isNPC(entity);
		final List<ItemStack> armor = Arrays.stream(armor()).peek(this::color).toList();

		for (ArmorSlot slot : ArmorSlot.values()) {
			if (!isEnabled(slot))
				continue;

			if (npc)
				// NPCs send their own packets which causes bad flickering
				entity.getInventory().setItem(slot.getSlot(), armor.get(slot.ordinal()));
			else {
				final InvisibleArmor invisibleArmor = new InvisibleArmorService().get(entity);
				if (invisibleArmor.isEnabled() && invisibleArmor.isHidden(slot))
					continue;
				else if (slot == ArmorSlot.HELMET && new CostumeUserService().get(entity).hasActiveCostumes())
					continue;
			}

			PacketUtils.sendFakeItem(entity, recipients(), armor.get(slot.ordinal()), slot.getSlot());
		}
	}

	private boolean isEnabled(ArmorSlot slot) {
		return isNullOrEmpty(disabledSlots) || !disabledSlots.contains(slot);
	}

	@NotNull
	private ItemStack[] armor() {
		return new ItemStack[]{
			new ItemStack(Material.LEATHER_HELMET),
			new ItemStack(Material.LEATHER_CHESTPLATE),
			new ItemStack(Material.LEATHER_LEGGINGS),
			new ItemStack(Material.LEATHER_BOOTS)
		};
	}

	public void increment() {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();

		if (r > 0 && b == 0) {
			if (r != 255 || g >= 255)
				r -= rate;
			g += rate;
		}
		if (g > 0 && r == 0) {
			if (g != 255 || b >= 255)
				g -= rate;
			b += rate;
		}
		if (b > 0 && g == 0) {
			if (b != 255 || r >= 255)
				b -= rate;
			r += rate;
		}

		r = MathUtils.clamp(r, 0, 255);
		g = MathUtils.clamp(g, 0, 255);
		b = MathUtils.clamp(b, 0, 255);

		color = Color.fromRGB(r, g, b);
	}

}
