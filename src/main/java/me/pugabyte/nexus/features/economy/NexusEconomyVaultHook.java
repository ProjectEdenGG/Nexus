package me.pugabyte.nexus.features.economy;

import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NegativeBalanceException;
import me.pugabyte.nexus.framework.exceptions.preconfigured.NotEnoughMoneyException;
import me.pugabyte.nexus.models.banker.Banker;
import me.pugabyte.nexus.models.banker.BankerService;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class NexusEconomyVaultHook implements Economy {

	private final Nexus plugin;

	private Banker getBanker(String player) {
		return getBanker(Bukkit.getOfflinePlayer(player));
	}

	private Banker getBanker(OfflinePlayer offlinePlayer) {
		return new BankerService().get(offlinePlayer);
	}

	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled(plugin);
	}

	@Override
	public String getName() {
		return plugin.getName();
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public int fractionalDigits() {
		return 2;
	}

	@Override
	public String format(double v) {
		return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_EVEN).toString();
	}

	@Override
	public String currencyNamePlural() {
		return "Dollars";
	}

	@Override
	public String currencyNameSingular() {
		return "Dollar";
	}

	@Override
	public boolean hasAccount(String s) {
		return true;
	}

	@Override
	public boolean hasAccount(OfflinePlayer offlinePlayer) {
		return true;
	}

	@Override
	public boolean hasAccount(String player, String world) {
		return true;
	}

	@Override
	public boolean hasAccount(OfflinePlayer offlinePlayer, String world) {
		return true;
	}

	@Override
	public double getBalance(String player) {
		return getBalance(Bukkit.getOfflinePlayer(player));
	}

	@Override
	public double getBalance(OfflinePlayer offlinePlayer) {
		return getBanker(offlinePlayer).getBalance().doubleValue();
	}

	@Override
	public double getBalance(String player, String world) {
		return getBalance(Bukkit.getOfflinePlayer(player));
	}

	@Override
	public double getBalance(OfflinePlayer offlinePlayer, String s) {
		return getBalance(offlinePlayer);
	}

	@Override
	public boolean has(String player, double value) {
		return has(Bukkit.getOfflinePlayer(player), value);
	}

	@Override
	public boolean has(OfflinePlayer offlinePlayer, double value) {
		return getBanker(offlinePlayer).has(value);
	}

	@Override
	public boolean has(String player, String world, double value) {
		return has(Bukkit.getOfflinePlayer(player), world, value);
	}

	@Override
	public boolean has(OfflinePlayer offlinePlayer, String world, double value) {
		return has(offlinePlayer, value);
	}

	@Override
	public EconomyResponse withdrawPlayer(String player, double value) {
		return withdrawPlayer(Bukkit.getOfflinePlayer(player), value);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double value) {
		Banker banker = getBanker(offlinePlayer);
		try {
			banker.withdraw(value);
			new BankerService().save(banker);
		} catch (NegativeBalanceException ex) {
			return new EconomyResponse(value, banker.getBalance().doubleValue(), EconomyResponse.ResponseType.FAILURE, new NotEnoughMoneyException().getMessage());
		}

		return new EconomyResponse(value, banker.getBalance().doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
	}

	@Override
	public EconomyResponse withdrawPlayer(String player, String world, double value) {
		return withdrawPlayer(Bukkit.getOfflinePlayer(player), world, value);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String world, double value) {
		return withdrawPlayer(offlinePlayer, value);
	}

	@Override
	public EconomyResponse depositPlayer(String player, double value) {
		return depositPlayer(Bukkit.getOfflinePlayer(player), value);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double value) {
		Banker banker = getBanker(offlinePlayer);
		banker.deposit(value);
		new BankerService().save(banker);

		return new EconomyResponse(value, banker.getBalance().doubleValue(), EconomyResponse.ResponseType.SUCCESS, null);
	}

	@Override
	public EconomyResponse depositPlayer(String player, String world, double value) {
		return depositPlayer(Bukkit.getOfflinePlayer(player), world, value);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String world, double value) {
		return depositPlayer(offlinePlayer, value);
	}

	@Override
	public EconomyResponse createBank(String s, String s1) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}

	@Override
	public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}

	@Override
	public EconomyResponse deleteBank(String s) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}

	@Override
	public EconomyResponse bankBalance(String s) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}

	@Override
	public EconomyResponse bankHas(String s, double v) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}

	@Override
	public EconomyResponse bankWithdraw(String s, double v) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}

	@Override
	public EconomyResponse bankDeposit(String s, double v) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}

	@Override
	public EconomyResponse isBankOwner(String s, String s1) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}

	@Override
	public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}

	@Override
	public EconomyResponse isBankMember(String s, String s1) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}

	@Override
	public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
		return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented.");
	}

	@Override
	public List<String> getBanks() {
		return Collections.emptyList();
	}

	@Override
	public boolean createPlayerAccount(String player) {
		return createPlayerAccount(Bukkit.getOfflinePlayer(player));
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
		return offlinePlayer.hasPlayedBefore();
	}

	@Override
	public boolean createPlayerAccount(String player, String world) {
		return createPlayerAccount(Bukkit.getOfflinePlayer(player), world);
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
		return false;
	}
}
