package gg.projecteden.nexus.models.shop;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@PlayerClass(Shop.class)
public class ShopService extends MongoService<Shop> {
	private final static Map<UUID, Shop> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, Shop> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	static {
		Tasks.async(() -> database.createQuery(Shop.class).find().forEachRemaining(shop -> cache.put(shop.getUuid(), shop)));
	}

	public List<Shop> getShops() {
		return new ArrayList<>(cache.values());
	}

	public List<Shop> getShopsSorted(ShopGroup shopGroup) {
		return getShops().stream()
				.filter(shop -> !shop.isMarket() && !shop.getProducts(shopGroup).isEmpty())
				.sorted(Comparator.comparing(shop -> shop.getInStock(shopGroup).size(), Comparator.reverseOrder()))
				.collect(Collectors.toList());
	}

	public Shop getMarket() {
		return get0();
	}

	@Override
	public void save(Shop object) {
		if (StringUtils.isUUID0(object.getUuid()))
			return;

		super.save(object);
	}

}
