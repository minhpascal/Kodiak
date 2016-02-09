package com.clearpool.kodiak.feedlibrary.core.cta;

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
public class CtsNormalizerTest
{
	private CtsNormalizer normalizer;

	@Before
	public void setUp()
	{
		HashMap<String, Double> closePrices = new HashMap<String, Double>();
		closePrices.put("Syed", new Double(100.12));
		closePrices.put("Saad", new Double(99.12));
		MdFeedProps.putInstanceProperty(closePrices, MdFeed.CTS.toString(), "CLOSEPRICES");
		Map<MdServiceType, IMdLibraryCallback> callbacks = new HashMap<MdServiceType, IMdLibraryCallback>();
		this.normalizer = new CtsNormalizer(callbacks, "S-T", 0, 0);
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
		int conditionCode1 = CtsNormalizer.getSaleConditions("O", null, 'N', true);
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_LAST));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_VOLUME));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_VWAP));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_OPEN));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_LOW));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_HIGH));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_LATEST_CLOSE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_OPEN_INTEREST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_LATE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_CANCEL));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_CORRECTION));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_OPEN_AUCTION_SUMMARY));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode1, Sale.CONDITION_CODE_CLOSE_AUCTION_SUMMARY));

		int conditionCode2 = CtsNormalizer.getSaleConditions("O", null, 'N', false);
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

		int conditionCode3 = CtsNormalizer.getSaleConditions("O I", null, 'N', true);
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_LAST));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_VOLUME));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_VWAP));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_OPEN));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_LOW));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_HIGH));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_LATEST_CLOSE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_OPEN_INTEREST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_LATE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_CANCEL));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_CORRECTION));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_OPEN_AUCTION_SUMMARY));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode3, Sale.CONDITION_CODE_CLOSE_AUCTION_SUMMARY));

		int conditionCode4 = CtsNormalizer.getSaleConditions("O I", null, 'N', false);
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

		int conditionCode5 = CtsNormalizer.getSaleConditions("6", null, 'N', true);
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_LAST));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_VOLUME));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_VWAP));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_OPEN));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_LOW));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_HIGH));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_LATEST_CLOSE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_OPEN_INTEREST));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_LATE));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_CANCEL));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_CORRECTION));
		Assert.assertFalse(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_OPEN_AUCTION_SUMMARY));
		Assert.assertTrue(MdEntity.isConditionSet(conditionCode5, Sale.CONDITION_CODE_CLOSE_AUCTION_SUMMARY));

		int conditionCode6 = CtsNormalizer.getSaleConditions("6", null, 'N', false);
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

		int conditionCode7 = CtsNormalizer.getSaleConditions("M", null, 'N', true);
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

		int conditionCode8 = CtsNormalizer.getSaleConditions("9", null, 'N', true);
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

		int conditionCode9 = CtsNormalizer.getSaleConditions("Q", null, 'N', true);
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
	}
}
