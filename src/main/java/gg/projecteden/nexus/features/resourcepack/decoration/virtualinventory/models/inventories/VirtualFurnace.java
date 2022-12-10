package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories;

import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.VirtualInventoryUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.events.VirtualFurnaceCookEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.events.VirtualFurnaceFuelBurnEvent;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public class VirtualFurnace extends VirtualInventory {
	private final FurnaceProperties furnaceProperties;
	private ItemStack fuel = null;
	private ItemStack input = null;
	private ItemStack output = null;
	private int cookTime = 0;
	private int cookTimeTotal = 0;
	private int fuelTime = 0;
	private int fuelTimeTotal = 0;

	private float experience = 0f;
	private boolean isLit = false;

	public VirtualFurnace(String title, FurnaceProperties properties) {
		super(VirtualInventoryType.FURNACE, title, UUID.randomUUID(),
			Bukkit.createInventory(null, InventoryType.FURNACE, StringUtils.colorize(title)));

		this.furnaceProperties = properties;

		this.updateInventory();
	}

	public float extractExperience() {
		float exp = this.experience;
		this.experience = 0.0f;
		return exp;
	}

	@Override
	public void openInventory(Player player) {
		super.openInventory(player);

		updateInventory();
	}

	@Override
	public void updateInventory() {
		Inventory inv = getInventory();

		inv.setItem(0, this.input);
		inv.setItem(1, this.fuel);
		inv.setItem(2, this.output);
	}

	private void updateInventoryView() {
		if (!this.isOpened())
			return;

		Inventory inv = getInventory();

		ItemStack input = inv.getItem(0);
		if (this.input != input)
			this.input = input;

		ItemStack fuel = inv.getItem(1);
		if (this.fuel != fuel)
			this.fuel = fuel;

		ItemStack output = inv.getItem(2);
		if (this.output != output)
			this.output = output;

		for (HumanEntity entity : inv.getViewers()) {
			InventoryView view = entity.getOpenInventory();
			view.setProperty(InventoryView.Property.COOK_TIME, this.cookTime);
			view.setProperty(InventoryView.Property.TICKS_FOR_CURRENT_SMELTING, this.cookTimeTotal);
			view.setProperty(InventoryView.Property.BURN_TIME, this.fuelTime);
			view.setProperty(InventoryView.Property.TICKS_FOR_CURRENT_FUEL, this.fuelTimeTotal);
		}
	}

	@Override
	public void tick() {
		super.tick();

		if (this.fuelTime > 0) {
			this.fuelTime--;

			if (canCook()) {
				this.isLit = true;
				this.cookTime++;
				if (this.cookTime >= this.cookTimeTotal) {
					this.cookTime = 0;
					processCook();
				}
			} else {
				this.cookTime = 0;
			}

		} else if (canBurn() && canCook()) {
			processBurn();

		} else if (this.cookTime > 0) {
			if (canCook())
				this.cookTime -= 5;
			else
				this.cookTime = 0;
		} else {
			this.isLit = false;
		}

		if (this.isOpened())
			updateInventoryView();
	}

	private boolean canBurn() {
		if (this.fuel == null) {
			return false;
		}

		return ItemUtils.getBurnTime(fuel) > 0;
	}

	private void processBurn() {
		VirtualFurnaceFuelBurnEvent burnEvent = new VirtualFurnaceFuelBurnEvent(this, this.fuel);
		if (!burnEvent.callEvent()) {
			return;
		}

		this.fuel.subtract();

		int burn = (int) (burnEvent.getBurnTime() / furnaceProperties.getFuelMultiplier());
		this.fuelTime = burn;
		this.fuelTimeTotal = burn;

		updateInventory();
	}

	private boolean canCook() {
		if (Nullables.isNullOrAir(this.input)) {
			return false;
		}

		FurnaceRecipe furnaceRecipe = VirtualInventoryUtils.getFurnaceRecipe(this.input);
		if (furnaceRecipe == null) {
			return false;
		}

		this.cookTimeTotal = (int) (furnaceRecipe.getCookingTime() / furnaceProperties.getCookMultiplier());
		if (this.output == null) {
			return true;
		}

		Material type = this.output.getType();
		if (type == furnaceRecipe.getResult().getType()) {
			return this.output.getAmount() < type.getMaxStackSize();
		}

		return false;
	}

	private void processCook() {
		FurnaceRecipe furnaceRecipe = VirtualInventoryUtils.getFurnaceRecipe(this.input);
		if (furnaceRecipe == null) {
			return;
		}

		ItemStack out;
		if (this.output == null) {
			out = furnaceRecipe.getResult().clone();
		} else {
			out = this.output.clone();
			out.add();
		}

		this.experience += furnaceRecipe.getExperience();

		VirtualFurnaceCookEvent cookEvent = new VirtualFurnaceCookEvent(this, this.input, out);
		if (!cookEvent.callEvent())
			return;

		this.output = cookEvent.getResult();
		this.input.subtract();

		updateInventory();
	}

	@Override
	public String toString() {
		return "Furnace{" +
			"name=" + getTitle() +
			", uuid=" + getUuid().toString() +
			", properties=" + furnaceProperties.toString() +
			", fuel=" + (fuel == null ? "null" : fuel) +
			", input=" + (input == null ? "null" : input) +
			", output=" + (output == null ? "null" : output) +
			", cookTime=" + cookTime +
			", fuelTime=" + fuelTime +
			'}';
	}


}
