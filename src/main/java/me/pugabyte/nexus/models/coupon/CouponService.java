package me.pugabyte.nexus.models.coupon;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.coupon.Coupons.Coupon;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static eden.utils.Utils.isNullOrEmpty;

@PlayerClass(Coupons.class)
public class CouponService extends MongoService<Coupons> {
	private final static Map<UUID, Coupons> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, Coupons> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	protected boolean deleteIf(Coupons object) {
		return isNullOrEmpty(object.getCoupons());
	}

	@Override
	protected void beforeSave(Coupons coupons) {
		coupons.getCoupons().sort(Comparator.comparing(Coupon::getId));
	}

}
