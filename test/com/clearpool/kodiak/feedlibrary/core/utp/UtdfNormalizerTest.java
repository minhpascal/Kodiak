package com.clearpool.kodiak.feedlibrary.core.utp;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.clearpool.kodiak.feedlibrary.caches.SaleCache;
import com.clearpool.kodiak.feedlibrary.callbacks.IMdLibraryCallback;
import com.clearpool.kodiak.feedlibrary.core.MdFeed;
import com.clearpool.kodiak.feedlibrary.core.MdFeedProps;
import com.clearpool.messageobjects.marketdata.MdEntity;
import com.clearpool.messageobjects.marketdata.MdServiceType;
import com.clearpool.messageobjects.marketdata.Sale;

@SuppressWarnings("static-method")
public class UtdfNormalizerTest
{
	private UtdfNormalizer normalizer;

	@Before
	public void setUp()
	{
		HashMap<String, Double> closePrices = new HashMap<String, Double>();
		closePrices.put("Syed", new Double(100.12));
		closePrices.put("Saad", new Double(99.12));
		MdFeedProps.putInstanceProperty(closePrices, MdFeed.UTDF.toString(), "CLOSEPRICES");
		Map<MdServiceType, IMdLibraryCallback> callbacks = new HashMap<MdServiceType, IMdLibraryCallback>();
		this.normalizer = new UtdfNormalizer(callbacks, "S-T", 0, 0);
	}

	@Test
	public void testClosePrices()
	{
		SaleCache cache = this.normalizer.getSalesCache();
		assertEquals(cache.getData("Syed").getLatestClosePrice(), 100.12, 1e-15);
		assertEquals(cache.getData("Saad").getLatestClosePrice(), 99.12, 1e-15);
	}

	@Test
	public void testGetSaleConditions()
	{
		int conditionCode1 = UtdfNormalizer.getSaleConditions("O", null, true, false);
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_LAST));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_VOLUME));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_VWAP));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_OPEN));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_LOW));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_HIGH));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_LATEST_CLOSE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_OPEN_INTEREST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_LATE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_CANCEL));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_CORRECTION));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_OPEN_AUCTION_SUMMARY));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_CLOSE_AUCTION_SUMMARY));

		int conditionCode2 = UtdfNormalizer.getSaleConditions("O", null, false, false);
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode2, Sale.CONDITION_CODE_LAST));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode2, Sale.CONDITION_CODE_VOLUME));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode2, Sale.CONDITION_CODE_VWAP));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode2, Sale.CONDITION_CODE_OPEN));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode2, Sale.CONDITION_CODE_LOW));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode2, Sale.CONDITION_CODE_HIGH));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode2, Sale.CONDITION_CODE_LATEST_CLOSE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode2, Sale.CONDITION_CODE_OPEN_INTEREST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode2, Sale.CONDITION_CODE_LATE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode2, Sale.CONDITION_CODE_CANCEL));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode2, Sale.CONDITION_CODE_CORRECTION));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode2, Sale.CONDITION_CODE_OPEN_AUCTION_SUMMARY));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode2, Sale.CONDITION_CODE_CLOSE_AUCTION_SUMMARY));

		int conditionCode3 = UtdfNormalizer.getSaleConditions("O I", null, true, false);
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_LAST));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_VOLUME));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_VWAP));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_OPEN));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_LOW));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_HIGH));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_LATEST_CLOSE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_OPEN_INTEREST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_LATE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_CANCEL));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_CORRECTION));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_OPEN_AUCTION_SUMMARY));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_CLOSE_AUCTION_SUMMARY));

		int conditionCode4 = UtdfNormalizer.getSaleConditions("O I", null, false, false);
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode4, Sale.CONDITION_CODE_LAST));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode4, Sale.CONDITION_CODE_VOLUME));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode4, Sale.CONDITION_CODE_VWAP));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode4, Sale.CONDITION_CODE_OPEN));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode4, Sale.CONDITION_CODE_LOW));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode4, Sale.CONDITION_CODE_HIGH));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode4, Sale.CONDITION_CODE_LATEST_CLOSE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode4, Sale.CONDITION_CODE_OPEN_INTEREST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode4, Sale.CONDITION_CODE_LATE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode4, Sale.CONDITION_CODE_CANCEL));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode4, Sale.CONDITION_CODE_CORRECTION));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode4, Sale.CONDITION_CODE_OPEN_AUCTION_SUMMARY));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode4, Sale.CONDITION_CODE_CLOSE_AUCTION_SUMMARY));

		int conditionCode5 = UtdfNormalizer.getSaleConditions("6", null, true, false);
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_LAST));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_VOLUME));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_VWAP));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_OPEN));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_LOW));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_HIGH));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_LATEST_CLOSE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_OPEN_INTEREST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_LATE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_CANCEL));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_CORRECTION));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_OPEN_AUCTION_SUMMARY));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_CLOSE_AUCTION_SUMMARY));

		int conditionCode6 = UtdfNormalizer.getSaleConditions("6", null, false, false);
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode6, Sale.CONDITION_CODE_LAST));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode6, Sale.CONDITION_CODE_VOLUME));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode6, Sale.CONDITION_CODE_VWAP));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode6, Sale.CONDITION_CODE_OPEN));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode6, Sale.CONDITION_CODE_LOW));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode6, Sale.CONDITION_CODE_HIGH));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode6, Sale.CONDITION_CODE_LATEST_CLOSE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode6, Sale.CONDITION_CODE_OPEN_INTEREST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode6, Sale.CONDITION_CODE_LATE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode6, Sale.CONDITION_CODE_CANCEL));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode6, Sale.CONDITION_CODE_CORRECTION));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode6, Sale.CONDITION_CODE_OPEN_AUCTION_SUMMARY));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode6, Sale.CONDITION_CODE_CLOSE_AUCTION_SUMMARY));

		int conditionCode7 = UtdfNormalizer.getSaleConditions("M", null, true, false);
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode7, Sale.CONDITION_CODE_LAST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode7, Sale.CONDITION_CODE_VOLUME));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode7, Sale.CONDITION_CODE_VWAP));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode7, Sale.CONDITION_CODE_OPEN));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode7, Sale.CONDITION_CODE_LOW));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode7, Sale.CONDITION_CODE_HIGH));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode7, Sale.CONDITION_CODE_LATEST_CLOSE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode7, Sale.CONDITION_CODE_OPEN_INTEREST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode7, Sale.CONDITION_CODE_LATE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode7, Sale.CONDITION_CODE_CANCEL));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode7, Sale.CONDITION_CODE_CORRECTION));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode7, Sale.CONDITION_CODE_OPEN_AUCTION_SUMMARY));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode7, Sale.CONDITION_CODE_CLOSE_AUCTION_SUMMARY));

		int conditionCode8 = UtdfNormalizer.getSaleConditions("9", null, true, false);
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode8, Sale.CONDITION_CODE_LAST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode8, Sale.CONDITION_CODE_VOLUME));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode8, Sale.CONDITION_CODE_VWAP));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode8, Sale.CONDITION_CODE_OPEN));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode8, Sale.CONDITION_CODE_LOW));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode8, Sale.CONDITION_CODE_HIGH));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode8, Sale.CONDITION_CODE_LATEST_CLOSE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode8, Sale.CONDITION_CODE_OPEN_INTEREST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode8, Sale.CONDITION_CODE_LATE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode8, Sale.CONDITION_CODE_CANCEL));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode8, Sale.CONDITION_CODE_CORRECTION));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode8, Sale.CONDITION_CODE_OPEN_AUCTION_SUMMARY));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode8, Sale.CONDITION_CODE_CLOSE_AUCTION_SUMMARY));

		int conditionCode9 = UtdfNormalizer.getSaleConditions("Q", null, true, false);
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode9, Sale.CONDITION_CODE_LAST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode9, Sale.CONDITION_CODE_VOLUME));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode9, Sale.CONDITION_CODE_VWAP));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode9, Sale.CONDITION_CODE_OPEN));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode9, Sale.CONDITION_CODE_LOW));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode9, Sale.CONDITION_CODE_HIGH));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode9, Sale.CONDITION_CODE_LATEST_CLOSE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode9, Sale.CONDITION_CODE_OPEN_INTEREST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode9, Sale.CONDITION_CODE_LATE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode9, Sale.CONDITION_CODE_CANCEL));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode9, Sale.CONDITION_CODE_CORRECTION));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode9, Sale.CONDITION_CODE_OPEN_AUCTION_SUMMARY));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode9, Sale.CONDITION_CODE_CLOSE_AUCTION_SUMMARY));

		int conditionCode10 = UtdfNormalizer.getSaleConditions("@6 X", null, false, false);
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode10, Sale.CONDITION_CODE_LAST));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode10, Sale.CONDITION_CODE_VOLUME));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode10, Sale.CONDITION_CODE_VWAP));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode10, Sale.CONDITION_CODE_OPEN));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode10, Sale.CONDITION_CODE_LOW));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode10, Sale.CONDITION_CODE_HIGH));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode10, Sale.CONDITION_CODE_LATEST_CLOSE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode10, Sale.CONDITION_CODE_OPEN_INTEREST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode10, Sale.CONDITION_CODE_LATE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode10, Sale.CONDITION_CODE_CANCEL));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode10, Sale.CONDITION_CODE_CORRECTION));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode10, Sale.CONDITION_CODE_OPEN_AUCTION_SUMMARY));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode10, Sale.CONDITION_CODE_CLOSE_AUCTION_SUMMARY));

		int conditionCode11 = UtdfNormalizer.getSaleConditions("@6 X", null, true, false);
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode11, Sale.CONDITION_CODE_LAST));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode11, Sale.CONDITION_CODE_VOLUME));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode11, Sale.CONDITION_CODE_VWAP));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode11, Sale.CONDITION_CODE_OPEN));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode11, Sale.CONDITION_CODE_LOW));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode11, Sale.CONDITION_CODE_HIGH));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode11, Sale.CONDITION_CODE_LATEST_CLOSE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode11, Sale.CONDITION_CODE_OPEN_INTEREST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode11, Sale.CONDITION_CODE_LATE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode11, Sale.CONDITION_CODE_CANCEL));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode11, Sale.CONDITION_CODE_CORRECTION));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode11, Sale.CONDITION_CODE_OPEN_AUCTION_SUMMARY));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode11, Sale.CONDITION_CODE_CLOSE_AUCTION_SUMMARY));
	}
}
