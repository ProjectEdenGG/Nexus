package gg.projecteden.nexus.models.shop;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ObjectClass(Shop.class)
public class ShopService extends MongoPlayerService<Shop> {
	private final static Map<UUID, Shop> cache = new ConcurrentHashMap<>();

	public Map<UUID, Shop> getCache() {
		return cache;
	}

	public List<Shop> getShops() {
		return new ArrayList<>(cache.values());
	}

	public List<Shop> getShopsSorted(ShopGroup shopGroup) {
		return getShops().stream()
				.filter(shop -> !shop.isMarket() && !shop.getProducts(shopGroup).isEmpty())
				.sorted(Comparator.<Shop>comparingInt(shop -> shop.getInStock(shopGroup).size()).reversed())
				.collect(Collectors.toList());
	}

	public Shop getMarket() {
		return get0();
	}

	@Override
	public void save(Shop shop) {
		if (shop.isMarket())
			return;

		super.save(shop);
	}

}
