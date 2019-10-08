package com.ors.interview.mariner.access.time;

import java.time.ZoneOffset;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;
import java.time.zone.ZoneRulesProvider;
import java.util.Collections;
import java.util.NavigableMap;
import java.util.Set;

import com.google.common.collect.ImmutableSortedMap;

public class AdtTimeZoneContainer {
	static boolean registered = false;
	static {
		registerAdtTimeZoneRules();
	}

	synchronized public static void registerAdtTimeZoneRules() {
		if (!registered) {
			ZoneRulesProvider adtProvider = new ZoneRulesProvider() {
				ZoneRules rules = ZoneRules.of(ZoneOffset.ofHours(-3));
				Set<String> ids = Collections.singleton("ADT");
				NavigableMap<String, ZoneRules> map = ImmutableSortedMap.<String, ZoneRules>of("ADT", rules);

				@Override
				protected Set<String> provideZoneIds() {
					return ids;
				}

				@Override
				protected NavigableMap<String, ZoneRules> provideVersions(String zoneId) {
					return map;
				}

				@Override
				protected ZoneRules provideRules(String zoneId, boolean forCaching) {
					if (zoneId.endsWith("ADT")) {
						return rules;
					}
					throw new ZoneRulesException(zoneId + " not a valid timezone for this provider");
				}
			};

			ZoneRulesProvider.registerProvider(adtProvider);
			registered = true;
		}
	}

}
