package me.pugabyte.nexus.models.purchase;

import me.pugabyte.nexus.models.MySQLService;

import java.util.List;

public class PurchaseService extends MySQLService {

	public List<Purchase> getRecent(int count) {
		return database.sql(
				" select results.* from purchase as results       " +
				"    where id in (                                " +
				"        select id                                " +
				"        from purchase                            " +
				"        where timestamp = (                      " +
				"            select max(timestamp)                " +
				"            from purchase as recent              " +
				"            where purchase.uuid = recent.uuid    " +
				"        )                                        " +
				"        and packageId = (                        " +
				"            select packageId                     " +
				"            from purchase as recent              " +
				"            where purchase.uuid = recent.uuid    " +
				"            order by timestamp desc              " +
				"            limit 1                              " +
				"        )                                        " +
				"    )                                            " +
				" order by timestamp desc                         " +
				" limit " + count
		).results(Purchase.class);
	}

}
