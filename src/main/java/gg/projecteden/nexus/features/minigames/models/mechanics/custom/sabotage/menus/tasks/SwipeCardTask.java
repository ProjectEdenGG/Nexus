package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.menus.tasks;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Task;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Rows(3)
@Title("Swipe Card")
public class SwipeCardTask extends AbstractTaskMenu {

	private static final Supplier<ItemStack> KEY_CARD = () -> new ItemBuilder(CustomMaterial.SABOTAGE_KEY_CARD)
		.name("Key Card")
		.lore("Grab this key card and place it on the right")
		.build();

	public SwipeCardTask(Task task) {
		super(task);
	}

	@Override
	public void init() {
		Runnable reset = () -> {
			player.setItemOnCursor(null);
			init();
		};
		SlotPos cardPos = SlotPos.of(1, 1);
		contents.fill(ClickableItem.empty(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build()));

		AtomicReference<LocalDateTime> time = new AtomicReference<>();
		contents.set(cardPos, ClickableItem.of(KEY_CARD.get(), click -> {
			if (!click.isLeftClick() || !KEY_CARD.get().isSimilar(click.getItem()) || !(click.getPlayer().getItemOnCursor().getType() == Material.AIR || click.getPlayer().getItemOnCursor().isSimilar(KEY_CARD.get()))) {
				reset.run();
			} else {
				time.set(LocalDateTime.now());
				ClickableItem item = contents.get(cardPos).orElse(null);
				if (item != null)
					contents.set(cardPos, item.clone(new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name(" ").build()));
				click.getPlayer().setItemOnCursor(KEY_CARD.get());
			}
		}));
		SlotPos destination = SlotPos.of(1, 7);
		contents.set(destination, ClickableItem.of(new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name(" ").build(), click -> {
			if (time.get() == null || !KEY_CARD.get().isSimilar(click.getPlayer().getItemOnCursor())) {
				reset.run();
				return;
			}
			Duration duration = Duration.between(time.get(), LocalDateTime.now());
			double sec = duration.getSeconds() + (duration.getNano() / 1000000000d);
			if (!(sec >= .9d && sec <= 1.2d)) {
				reset.run();
				String fmt = sec < .9d ? "fast" : "slow";
				contents.set(destination, contents.get(destination).get().clone(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("Too " + fmt + ", try again").build()));
				new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO).receiver(player).category(SoundCategory.MASTER).volume(2f).play();
			} else {
				getTask().partCompleted(Minigamer.of(player));
				close();
			}
		}));
	}

}
