package me.pugabyte.nexus.models.perkowner;

import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.features.minigames.models.perks.PerkType;
import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;

public class PerkOwnerTest extends PerkOwner {
	public PerkOwnerTest() {
		uuid = UUID.fromString("f84c6a79-0a4e-45e0-879b-cd49ebd4c4e2");
	}
	private boolean saveCalled = false;

	@Override
	protected void save() {saveCalled = true;}

	@Test
	public void rewardTest() {
		assertTrue(reward("Arena"));
		assertThat(getTokenDate(), not(LocalDate.of(1970, 1, 1)));
		assertThat(getTokens(), not(0));
		assertThat(getDailyTokens(), not(0)); // assert their daily tokens isn't 0
		assertTrue(saveCalled);
	}

	@Test
	public void successfulPurchaseTest() {
		setTokens(100);
		assertTrue(purchase(PerkType.GRASS_BLOCK));
		assertThat(getPurchasedPerks().keySet(), hasItem(PerkType.GRASS_BLOCK));
		assertEquals(getPurchasedPerks().get(PerkType.GRASS_BLOCK), false);
		assertEquals(99, getTokens()); // assuming grass block's price won't change
		assertTrue(saveCalled);
	}

	@Test
	public void duplicatePurchaseTest() {
		successfulPurchaseTest();
		saveCalled = false;
		assertFalse(purchase(PerkType.GRASS_BLOCK));
		assertEquals(99, getTokens());
		assertFalse(saveCalled);
	}

	@Test
	public void insufficientFundsPurchaseTest() {
		assertFalse(purchase(PerkType.GRASS_BLOCK));
		assertEquals(0, getTokens());
		assertFalse(getPurchasedPerks().containsKey(PerkType.GRASS_BLOCK));
		assertFalse(saveCalled);
	}

	@Test
	public void equalsTest() {
		assertTrue(equals(new PerkOwnerTest()));
	}

	@Test
	public void unsuccessfulToggleTest() {
		assertFalse(toggle(PerkType.GRASS_BLOCK));
		assertFalse(saveCalled);
	}

	@Test
	public void successfulToggleTest() {
		getPurchasedPerks().put(PerkType.GRASS_BLOCK, false);
		assertTrue(toggle(PerkType.GRASS_BLOCK));
		assertTrue(getPurchasedPerks().get(PerkType.GRASS_BLOCK));
		assertTrue(saveCalled);
		saveCalled = false;
		assertTrue(toggle(PerkType.GRASS_BLOCK));
		assertFalse(getPurchasedPerks().get(PerkType.GRASS_BLOCK));
		assertTrue(saveCalled);
	}

	@Test
	public void successfulExclusiveToggleTest() {
		getPurchasedPerks().put(PerkType.GRASS_BLOCK, false);
		getPurchasedPerks().put(PerkType.SEA_LANTERN, true);
		getPurchasedPerks().put(PerkType.NOTE_BLOCK, true);
		getPurchasedPerks().put(PerkType.CLOUD, true);
		getPurchasedPerks().put(PerkType.SPRING, true);
		assertTrue(toggle(PerkType.GRASS_BLOCK));
		assertTrue(saveCalled);
		assertTrue(getPurchasedPerks().get(PerkType.GRASS_BLOCK));
		assertFalse(getPurchasedPerks().get(PerkType.SEA_LANTERN));
		assertFalse(getPurchasedPerks().get(PerkType.NOTE_BLOCK));
		assertTrue(getPurchasedPerks().get(PerkType.CLOUD));
		assertTrue(getPurchasedPerks().get(PerkType.SPRING));
		saveCalled = false;
		assertTrue(toggle(PerkType.GRASS_BLOCK));
		assertTrue(saveCalled);
		assertFalse(getPurchasedPerks().get(PerkType.GRASS_BLOCK));
	}

	@Test
	public void enabledPerksTest() {
		getPurchasedPerks().put(PerkType.GRASS_BLOCK, true);
		getPurchasedPerks().put(PerkType.SEA_LANTERN, false);
		assertTrue(getEnabledPerks().contains(PerkType.GRASS_BLOCK));
		assertFalse(getEnabledPerks().contains(PerkType.SEA_LANTERN));
	}

	@Test
	public void enabledPerksByClassTest() {
		getPurchasedPerks().put(PerkType.GRASS_BLOCK, true);
		getPurchasedPerks().put(PerkType.SEA_LANTERN, false);
		getPurchasedPerks().put(PerkType.SPRING, true);
		getPurchasedPerks().put(PerkType.CONCRETE_HAT, true);
		Set<LoadoutPerk> enabledPerks = getEnabledPerksByClass(LoadoutPerk.class);
		assertTrue(enabledPerks.contains(PerkType.GRASS_BLOCK.getPerk()));
		assertFalse(enabledPerks.contains(PerkType.SEA_LANTERN.getPerk()));
		assertFalse(enabledPerks.contains(PerkType.SPRING.getPerk()));
		assertTrue(enabledPerks.contains(PerkType.CONCRETE_HAT.getPerk()));
	}

	@Test
	public void enabledPerksByCategoryTest() {
		getPurchasedPerks().put(PerkType.GRASS_BLOCK, true);
		getPurchasedPerks().put(PerkType.SEA_LANTERN, false);
		getPurchasedPerks().put(PerkType.SPRING, true);
		getPurchasedPerks().put(PerkType.CONCRETE_HAT, true);
		Set<? extends Perk> enabledPerks = getEnabledPerksByCategory(PerkCategory.HAT);
		assertTrue(enabledPerks.contains(PerkType.GRASS_BLOCK.getPerk()));
		assertFalse(enabledPerks.contains(PerkType.SEA_LANTERN.getPerk()));
		assertFalse(enabledPerks.contains(PerkType.SPRING.getPerk()));
		assertFalse(enabledPerks.contains(PerkType.CONCRETE_HAT.getPerk()));
	}
}