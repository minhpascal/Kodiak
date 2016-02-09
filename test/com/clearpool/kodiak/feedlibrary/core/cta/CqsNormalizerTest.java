package com.clearpool.kodiak.feedlibrary.core.cta;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.clearpool.kodiak.feedlibrary.callbacks.IMdLibraryCallback;
import com.clearpool.kodiak.feedlibrary.core.MdFeed;
import com.clearpool.kodiak.feedlibrary.core.MdFeedProps;
import com.clearpool.kodiak.feedlibrary.core.TestMDQuoteListener;
import com.clearpool.kodiak.feedlibrary.core.TestMDStateListener;
import com.clearpool.kodiak.feedlibrary.utils.ByteBufferUtil;
import com.clearpool.messageobjects.marketdata.Exchange;
import com.clearpool.messageobjects.marketdata.MarketSession;
import com.clearpool.messageobjects.marketdata.MarketState;
import com.clearpool.messageobjects.marketdata.MdEntity;
import com.clearpool.messageobjects.marketdata.MdServiceType;
import com.clearpool.messageobjects.marketdata.Quote;
import com.clearpool.messageobjects.marketdata.TradingState;

@SuppressWarnings("static-method")
public class CqsNormalizerTest
{
	private final TestMDQuoteListener bboListener = new TestMDQuoteListener();
	private final TestMDQuoteListener nbboListener = new TestMDQuoteListener();
	private final TestMDStateListener stateListener = new TestMDStateListener();
	private CqsNormalizer normalizer;
	private long sequenceNumber;

	@Before
	public void setUp()
	{
		HashMap<String, Integer> lotsize = new HashMap<String, Integer>();
		lotsize.put("SYED", new Integer(10));
		lotsize.put("BRK A", new Integer(1));
		HashSet<String> IPOS = new HashSet<String>();
		IPOS.add("SYED");
		MdFeedProps.putInstanceProperty(lotsize, MdFeed.CQS.toString(), "LOTSIZES");
		MdFeedProps.putInstanceProperty(IPOS, MdFeed.CQS.toString(), "IPOS");
		Map<MdServiceType, IMdLibraryCallback> callbacks = new HashMap<MdServiceType, IMdLibraryCallback>();
		callbacks.put(MdServiceType.BBO, this.bboListener);
		callbacks.put(MdServiceType.NBBO, this.nbboListener);
		callbacks.put(MdServiceType.STATE, this.stateListener);
		this.normalizer = new CqsNormalizer(callbacks, "", 0, 0) {
			@Override
			public MarketSession getMarketSession(char primaryListing, boolean isPrimaryListing, long timestamp)
			{
				return MarketSession.NORMAL;
			}
		};
	}

	@Test
	public void testIgnore()
	{
		this.normalizer.processMessage("TEST", createCtaPacket(createBondLongQuote()), true);
		assertSizes(0, 0, 0);
	}

	@Test
	public void testBondLongQuote()
	{
		this.normalizer.processMessage("TEST", createCtaPacket(createBondLongQuote()), false);
		assertSizes(0, 0, 0);
	}

	@Test
	public void testLocalIssue()
	{
		this.normalizer.processMessage("TEST", createCtaPacket(createLocalIssueLongQuote()), false);
		assertSizes(0, 0, 0);
		this.normalizer.processMessage("TEST", createCtaPacket(createLocalIssueShortQuote()), false);
		assertSizes(0, 0, 0);
	}

	@Test
	public void testAdministrative()
	{
		this.normalizer
				.processMessage(
						"TEST",
						createCtaPacket(createAdministrative("ALERT ALERT ALERT THE CONSOLIDATED QUOTE SYSTEM IS EXPERIENCING A REPORTING DELAY STANDBY FOR FURTHER UPDATES AS INFORMATION BECOMES AVAILABLE")),
						false);
		assertSizes(0, 0, 0);
	}

	@Test
	public void testMWCBDeclineLevel()
	{
		this.normalizer.processMessage("TEST", createCtaPacket(createMWCBDeclineLevel(1, 2, 3)), false);
		assertSizes(0, 0, 0);
	}

	@Test
	public void testMWCBStatus()
	{
		this.normalizer.processMessage("TEST", createCtaPacket(createMWCBStatus('1')), false);
		assertSizes(0, 0, 0);
	}

	@Test
	public void testControl()
	{
		this.normalizer.processMessage("TEST", createCtaPacket(createControl('I')), false);
		assertSizes(0, 0, 0);
	}

	@Test
	public void testLongQuoteLongNBBO()
	{
		this.normalizer.processMessage("TEST",
				createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', '0', ' ', ' ', ' ', '4', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 0, 0);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);

		char[] quoteConditions1 = { 'R', 'W' };
		for (char c : quoteConditions1)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '4', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		char[] quoteConditions2 = { 'I', 'L', 'N', 'S', 'T', 'U', 'X', 'Y', '9' };
		for (char c : quoteConditions2)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '4', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		char[] quoteConditions25 = { 'C' };
		for (char c : quoteConditions25)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '4', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 0, 0, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		this.normalizer.processMessage("TEST",
				createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', 'D', ' ', ' ', ' ', '4', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.HALTED, 190, 191);
		assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, 'D');

		char[] quoteConditions3 = { 'J', 'Q', 'V', 'Z', '1', '2', '3' };
		for (char c : quoteConditions3)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '4', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		this.normalizer.processMessage("TEST",
				createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', 'R', ' ', ' ', ' ', '4', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);
		assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, 'R');
	}

	@Test
	public void testLongQuoteShortNBBO()
	{
		this.normalizer.processMessage("TEST",
				createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', '0', ' ', ' ', ' ', '6', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 0, 0);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);

		char[] quoteConditions1 = { 'R', 'W' };
		for (char c : quoteConditions1)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '6', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		char[] quoteConditions2 = { 'I', 'L', 'N', 'S', 'T', 'U', 'X', 'Y', '9' };
		for (char c : quoteConditions2)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '6', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		char[] quoteConditions25 = { 'C' };
		for (char c : quoteConditions25)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '6', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 0, 0, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		this.normalizer.processMessage("TEST",
				createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', 'D', ' ', ' ', ' ', '6', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.HALTED, 190, 191);
		assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, 'D');

		char[] quoteConditions3 = { 'J', 'Q', 'V', 'Z', '1', '2', '3' };
		for (char c : quoteConditions3)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '6', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		this.normalizer.processMessage("TEST",
				createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', 'R', ' ', ' ', ' ', '6', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);
		assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, 'R');
	}

	@Test
	public void testLongQuoteContainedNBBO()
	{
		this.normalizer.processMessage("TEST",
				createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', '0', ' ', ' ', ' ', '1', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 0, 0);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);

		char[] quoteConditions1 = { 'R', 'W' };
		for (char c : quoteConditions1)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '1', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, c);
		}

		char[] quoteConditions2 = { 'I', 'L', 'N', 'S', 'T', 'U', 'X', 'Y', '9' };
		for (char c : quoteConditions2)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '1', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, c);
		}

		char[] quoteConditions25 = { 'C' };
		for (char c : quoteConditions25)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '1', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 0, 0, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, c);
		}

		this.normalizer.processMessage("TEST",
				createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', 'D', ' ', ' ', ' ', '1', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.HALTED, 190, 191);
		assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, 'D');

		char[] quoteConditions3 = { 'J', 'Q', 'V', 'Z', '1', '2', '3' };
		for (char c : quoteConditions3)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '1', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, c);
		}

		this.normalizer.processMessage("TEST",
				createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', 'R', ' ', ' ', ' ', '1', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);
		assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, 'R');
	}

	@Test
	public void test_NewIssueConditionSet()
	{
		this.normalizer.processMessage("SYED",
				createCtaPacket(createEquityLongQuote("SYED", 'N', 190, 191, 2, 3, 'E', 'N', 'R', ' ', ' ', ' ', '4', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		MarketState state = this.stateListener.getState();
		Assert.assertTrue(MdEntity.isConditionSet(state.getConditionCode(), MarketState.CONDITION_NEW_ISSUE));
	}

	@Test
	public void testLongQuoteNoneNBBO()
	{
		this.normalizer.processMessage("TEST",
				createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', '0', ' ', ' ', ' ', '2', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 0, 0);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);

		char[] quoteConditions1 = { 'R', 'W' };
		for (char c : quoteConditions1)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '2', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 0, 0, 0, 0, null, null, c);
		}

		char[] quoteConditions2 = { 'I', 'L', 'N', 'S', 'T', 'U', 'X', 'Y', '9' };
		for (char c : quoteConditions2)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '2', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 0, 0, 0, 0, null, null, c);
		}

		char[] quoteConditions25 = { 'C' };
		for (char c : quoteConditions25)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '2', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 0, 0, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 0, 0, 0, 0, null, null, c);
		}

		this.normalizer.processMessage("TEST",
				createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', 'D', ' ', ' ', ' ', '2', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.HALTED, 190, 191);
		assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 0, 0, 0, 0, null, null, 'D');

		char[] quoteConditions3 = { 'J', 'Q', 'V', 'Z', '1', '2', '3' };
		for (char c : quoteConditions3)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '2', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 0, 0, 0, 0, null, null, c);
		}

		this.normalizer.processMessage("TEST",
				createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', 'R', ' ', ' ', ' ', '2', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);
		assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 0, 0, 0, 0, null, null, 'R');
	}

	@Test
	public void testLongQuoteUnchangedNBBO()
	{
		this.normalizer.processMessage("TEST",
				createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', '0', ' ', ' ', ' ', '0', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 0, 0);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);

		char[] quoteConditions1 = { 'R', 'W' };
		for (char c : quoteConditions1)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '0', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 0);
			assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		}

		char[] quoteConditions2 = { 'I', 'L', 'N', 'S', 'T', 'U', 'X', 'Y', '9' };
		for (char c : quoteConditions2)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '0', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 0);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		}

		char[] quoteConditions25 = { 'C' };
		for (char c : quoteConditions25)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '0', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 0);
			assertBBO("IBM", 8194, 190, 191, 0, 0, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		}

		this.normalizer.processMessage("TEST",
				createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', 'D', ' ', ' ', ' ', '0', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 0);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.HALTED, 190, 191);
		assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');

		char[] quoteConditions3 = { 'J', 'Q', 'V', 'Z', '1', '2', '3' };
		for (char c : quoteConditions3)
		{
			this.normalizer.processMessage("TEST",
					createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', c, ' ', ' ', ' ', '0', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 0);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		}

		this.normalizer.processMessage("TEST",
				createCtaPacket(createEquityLongQuote("IBM", 'N', 190, 191, 2, 3, 'E', 'N', 'R', ' ', ' ', ' ', '0', ' ', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 0);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);
		assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
	}

	@Test
	public void testShortQuoteLongNBBO()
	{
		this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', '0', ' ', '4', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 0, 0);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);

		char[] quoteConditions1 = { 'R', 'W' };
		for (char c : quoteConditions1)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '4', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		char[] quoteConditions2 = { 'I', 'L', 'N', 'S', 'T', 'U', 'X', 'Y', '9' };
		for (char c : quoteConditions2)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '4', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		char[] quoteConditions25 = { 'C' };
		for (char c : quoteConditions25)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '4', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 0, 0, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', 'D', ' ', '4', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.HALTED, 190, 191);
		assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, 'D');

		char[] quoteConditions3 = { 'J', 'Q', 'V', 'Z', '1', '2', '3' };
		for (char c : quoteConditions3)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '4', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', 'R', ' ', '4', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);
		assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, 'R');
	}

	@Test
	public void testShortQuoteShortNBBO()
	{
		this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', '0', ' ', '6', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 0, 0);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);

		char[] quoteConditions1 = { 'R', 'W' };
		for (char c : quoteConditions1)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '6', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		char[] quoteConditions2 = { 'I', 'L', 'N', 'S', 'T', 'U', 'X', 'Y', '9' };
		for (char c : quoteConditions2)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '6', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		char[] quoteConditions25 = { 'C' };
		for (char c : quoteConditions25)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '6', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 0, 0, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', 'D', ' ', '6', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.HALTED, 190, 191);
		assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, 'D');

		char[] quoteConditions3 = { 'J', 'Q', 'V', 'Z', '1', '2', '3' };
		for (char c : quoteConditions3)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '6', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, c);
		}

		this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', 'R', ' ', '6', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);
		assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 190.01, 190.99, 100, 400, Exchange.USEQ_BATS_Y_EXCHANGE, Exchange.USEQ_BATS_Y_EXCHANGE, 'R');

	}

	@Test
	public void testShortQuoteContainedNBBO()
	{
		this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', '0', ' ', '1', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 0, 0);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);

		char[] quoteConditions1 = { 'R', 'W' };
		for (char c : quoteConditions1)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '1', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, c);
		}

		char[] quoteConditions2 = { 'I', 'L', 'N', 'S', 'T', 'U', 'X', 'Y', '9' };
		for (char c : quoteConditions2)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '1', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, c);
		}

		char[] quoteConditions25 = { 'C' };
		for (char c : quoteConditions25)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '1', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 0, 0, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, c);
		}

		this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', 'D', ' ', '1', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.HALTED, 190, 191);
		assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, 'D');

		char[] quoteConditions3 = { 'J', 'Q', 'V', 'Z', '1', '2', '3' };
		for (char c : quoteConditions3)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '1', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, c);
		}

		this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', 'R', ' ', '1', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);
		assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, 'R');
	}

	@Test
	public void testShortQuoteNoneNBBO()
	{
		this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', '0', ' ', '2', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 0, 0);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);

		char[] quoteConditions1 = { 'R', 'W' };
		for (char c : quoteConditions1)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '2', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 0, 0, 0, 0, null, null, c);
		}

		char[] quoteConditions2 = { 'I', 'L', 'N', 'S', 'T', 'U', 'X', 'Y', '9' };
		for (char c : quoteConditions2)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '2', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 0, 0, 0, 0, null, null, c);
		}

		char[] quoteConditions25 = { 'C' };
		for (char c : quoteConditions25)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '2', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 0, 0, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 0, 0, 0, 0, null, null, c);
		}

		this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', 'D', ' ', '2', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.HALTED, 190, 191);
		assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 0, 0, 0, 0, null, null, 'D');

		char[] quoteConditions3 = { 'J', 'Q', 'V', 'Z', '1', '2', '3' };
		for (char c : quoteConditions3)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '2', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 1);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
			assertNBBO("IBM", 2, 0, 0, 0, 0, null, null, c);
		}

		this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', 'R', ' ', '2', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 1);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);
		assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		assertNBBO("IBM", 2, 0, 0, 0, 0, null, null, 'R');
	}

	@Test
	public void testShortQuoteUnchangedNBBO()
	{
		this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', '0', ' ', '0', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 0, 0);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);

		char[] quoteConditions1 = { 'R', 'W' };
		for (char c : quoteConditions1)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '0', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 0);
			assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		}

		char[] quoteConditions2 = { 'I', 'L', 'N', 'S', 'T', 'U', 'X', 'Y', '9' };
		for (char c : quoteConditions2)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '0', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 0);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		}

		char[] quoteConditions25 = { 'C' };
		for (char c : quoteConditions25)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '0', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 0);
			assertBBO("IBM", 8194, 190, 191, 0, 0, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		}

		this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', 'D', ' ', '0', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 0);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.HALTED, 190, 191);
		assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');

		char[] quoteConditions3 = { 'J', 'Q', 'V', 'Z', '1', '2', '3' };
		for (char c : quoteConditions3)
		{
			this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', c, ' ', '0', 190.01, 190.99, 1, 4, 'Y')), false);
			assertSizes(0, 1, 0);
			assertBBO("IBM", 8194, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
		}

		this.normalizer.processMessage("TEST", createCtaPacket(createEquityShortQuote("IBM", 190, 191, 2, 3, 'E', 'N', 'R', ' ', '0', 190.01, 190.99, 1, 4, 'Y')), false);
		assertSizes(1, 1, 0);
		assertState("IBM", 2, MarketSession.NORMAL, TradingState.TRADING, 190, 191);
		assertBBO("IBM", 2, 190, 191, 200, 300, Exchange.USEQ_NYSE_EURONEXT, Exchange.USEQ_NYSE_EURONEXT, '\u0000');
	}

	private static CtaPacket createCtaPacket(ByteBuffer buffer)
	{
		CtaPacket packet = new CtaPacket(System.nanoTime());
		packet.setBuffer(buffer);
		packet.parseHeader();
		return packet;
	}

	private void assertState(String symbol, int conditionCode, MarketSession marketSession, TradingState tradingState, double lowerBand, double upperBand)
	{
		MarketState state = this.stateListener.getState();
		assertEquals(symbol, state.getSymbol());
		assertEquals(MdServiceType.STATE, state.getServiceType());
		assertEquals(conditionCode, state.getConditionCode());
		assertEquals(marketSession, state.getMarketSession());
		assertEquals(tradingState, state.getTradingState());
		assertEquals(lowerBand, state.getLowerBand(), 0.0000001);
		assertEquals(upperBand, state.getUpperBand(), 0.0000001);
	}

	private void assertSizes(int stateSize, int bboSize, int nbboSize)
	{
		assertEquals(stateSize, this.stateListener.size());
		assertEquals(bboSize, this.bboListener.size());
		assertEquals(nbboSize, this.nbboListener.size());
	}

	private void assertBBO(String symbol, int conditionCode, double bidPrice, double askPrice, int bidSize, int askSize, Exchange bidExchange, Exchange askExchange, char condition)
	{
		Quote quote = this.bboListener.getQuote();
		assertEquals(MdServiceType.BBO, quote.getServiceType());
		assertQuoteFields(quote, symbol, conditionCode, bidPrice, askPrice, bidSize, askSize, bidExchange, askExchange, condition);
	}

	private void assertNBBO(String symbol, int conditionCode, double bidPrice, double askPrice, int bidSize, int askSize, Exchange bidExchange, Exchange askExchange, char condition)
	{
		Quote quote = this.nbboListener.getQuote();
		assertEquals(MdServiceType.NBBO, quote.getServiceType());
		assertQuoteFields(quote, symbol, conditionCode, bidPrice, askPrice, bidSize, askSize, bidExchange, askExchange, condition);
	}

	private static void assertQuoteFields(Quote quote, String symbol, int conditionCode, double bidPrice, double askPrice, int bidSize, int askSize, Exchange bidExchange,
			Exchange askExchange, char condition)
	{
		assertEquals(symbol, quote.getSymbol());
		assertEquals(conditionCode, quote.getConditionCode());
		assertEquals(bidPrice, quote.getBidPrice(), 0.0000001);
		assertEquals(askPrice, quote.getAskPrice(), 0.0000001);
		assertEquals(bidSize, quote.getBidSize());
		assertEquals(askSize, quote.getAskSize());
		assertEquals(bidExchange, quote.getBidExchange());
		assertEquals(askExchange, quote.getAskExchange());

		if (condition == '\u0000') Assert.assertNull(quote.getCondition());
		else assertEquals(String.valueOf(condition), quote.getCondition());
	}

	private void populateHeader(ByteBuffer buffer, char msgCategory, char msgType, char msgNetwork, char participantId)
	{
		ByteBufferUtil.putChar(buffer, msgCategory);
		ByteBufferUtil.putChar(buffer, msgType);
		ByteBufferUtil.putChar(buffer, msgNetwork);
		buffer.position(buffer.position() + 5); // retrans requester, header id, reserved
		ByteBufferUtil.putLong(buffer, this.sequenceNumber++, 9);
		ByteBufferUtil.putChar(buffer, participantId);
		buffer.position(buffer.position() + 6); // timestamp
	}

	private ByteBuffer createBondLongQuote()
	{
		ByteBuffer buffer = ByteBuffer.allocate(102);
		populateHeader(buffer, 'B', 'B', 'E', ' ');
		buffer.flip();
		return buffer;
	}

	private ByteBuffer createLocalIssueLongQuote()
	{
		ByteBuffer buffer = ByteBuffer.allocate(102);
		populateHeader(buffer, 'L', 'B', 'E', ' ');
		buffer.flip();
		return buffer;
	}

	private ByteBuffer createLocalIssueShortQuote()
	{
		ByteBuffer buffer = ByteBuffer.allocate(58);
		populateHeader(buffer, 'L', 'D', 'E', ' ');
		buffer.flip();
		return buffer;
	}

	private ByteBuffer createEquityLongQuote(String symbol, char primaryListing, double bidPrice, double askPrice, long bidSizeLots, long askSizeLots, char messageNetwork,
			char participantId, char quoteCondition, char luldIndicator, char retailInterestIndicator, char shortSaleRestrictionIndicator, char nbboIndicator,
			char nbboLuldIndicator, double nbboBidPrice, double nbboAskPrice, long nbboBidSizeLots, long nbboAskSizeLots, char nbboParticipantId)
	{
		ByteBuffer buffer = ByteBuffer.allocate(160);
		populateHeader(buffer, 'E', 'B', messageNetwork, participantId);
		ByteBufferUtil.putString(buffer, symbol, 11);
		buffer.position(buffer.position() + 2); // suffix, test message indicator
		ByteBufferUtil.putChar(buffer, primaryListing);
		buffer.position(buffer.position() + 7); // SIP generated message ID, reserved, financial status, currency, instrument type
		buffer.position(buffer.position() + 3); // cancel correction, settlement condition, market condition
		ByteBufferUtil.putChar(buffer, quoteCondition);
		ByteBufferUtil.putChar(buffer, luldIndicator);
		ByteBufferUtil.putChar(buffer, retailInterestIndicator);
		ByteBufferUtil.putChar(buffer, 'B');
		ByteBufferUtil.putLong(buffer, (long) (bidPrice * 100), 12);
		ByteBufferUtil.putLong(buffer, bidSizeLots, 7);
		ByteBufferUtil.putChar(buffer, 'B');
		ByteBufferUtil.putLong(buffer, (long) (askPrice * 100), 12);
		ByteBufferUtil.putLong(buffer, askSizeLots, 7);
		buffer.position(buffer.position() + 4); // finraMarketMakerId
		buffer.position(buffer.position() + 1); // reserved
		ByteBufferUtil.putChar(buffer, nbboLuldIndicator);
		buffer.position(buffer.position() + 1); // finra bbo luld indicator
		ByteBufferUtil.putChar(buffer, shortSaleRestrictionIndicator);
		buffer.position(buffer.position() + 1); // reserved
		ByteBufferUtil.putChar(buffer, nbboIndicator);
		buffer.position(buffer.position() + 1); // finra bbo indicator
		populateNbbo(buffer, nbboIndicator, nbboBidPrice, nbboAskPrice, nbboBidSizeLots, nbboAskSizeLots, nbboParticipantId);
		buffer.flip();
		return buffer;
	}

	private ByteBuffer createEquityShortQuote(String symbol, double bidPrice, double askPrice, long bidSizeLots, long askSizeLots, char messageNetwork, char participantId,
			char quoteCondition, char luldIndicator, char nbboIndicator, double nbboBidPrice, double nbboAskPrice, long nbboBidSizeLots, long nbboAskSizeLots,
			char nbboParticipantId)
	{
		ByteBuffer buffer = ByteBuffer.allocate(116);
		populateHeader(buffer, 'E', 'D', messageNetwork, participantId);
		ByteBufferUtil.putString(buffer, symbol, 3);
		ByteBufferUtil.putChar(buffer, quoteCondition);
		ByteBufferUtil.putChar(buffer, luldIndicator);
		buffer.position(buffer.position() + 1); // reserved
		ByteBufferUtil.putChar(buffer, 'B');
		ByteBufferUtil.putLong(buffer, (long) (bidPrice * 100), 8);
		ByteBufferUtil.putLong(buffer, bidSizeLots, 3);
		buffer.position(buffer.position() + 1); // reserved
		ByteBufferUtil.putChar(buffer, 'B');
		ByteBufferUtil.putLong(buffer, (long) (askPrice * 100), 8);
		ByteBufferUtil.putLong(buffer, askSizeLots, 3);
		buffer.position(buffer.position() + 1); // reserved
		ByteBufferUtil.putChar(buffer, nbboIndicator);
		buffer.position(buffer.position() + 1); // finra bbo indicator
		populateNbbo(buffer, nbboIndicator, nbboBidPrice, nbboAskPrice, nbboBidSizeLots, nbboAskSizeLots, nbboParticipantId);
		buffer.flip();
		return buffer;
	}

	private ByteBuffer createAdministrative(String message)
	{
		ByteBuffer buffer = ByteBuffer.allocate(300);
		populateHeader(buffer, 'A', 'H', 'E', ' ');
		ByteBufferUtil.putString(buffer, message, message.length());
		buffer.flip();
		return buffer;
	}

	private ByteBuffer createMWCBDeclineLevel(double l1Price, double l2Price, double l3Price)
	{
		ByteBuffer buffer = ByteBuffer.allocate(70);
		populateHeader(buffer, 'M', 'K', 'E', ' ');
		ByteBufferUtil.putChar(buffer, 'B');
		ByteBufferUtil.putLong(buffer, (long) (l1Price * 100), 12);
		buffer.position(buffer.position() + 3);
		ByteBufferUtil.putLong(buffer, (long) (l2Price * 100), 12);
		buffer.position(buffer.position() + 3);
		ByteBufferUtil.putLong(buffer, (long) (l3Price * 100), 12);
		buffer.position(buffer.position() + 3);
		buffer.flip();
		return buffer;
	}

	private ByteBuffer createMWCBStatus(char levelIndicator)
	{
		ByteBuffer buffer = ByteBuffer.allocate(28);
		populateHeader(buffer, 'M', 'L', 'E', ' ');
		ByteBufferUtil.putChar(buffer, levelIndicator);
		buffer.position(buffer.position() + 3);
		buffer.flip();
		return buffer;
	}

	private ByteBuffer createControl(char msgType)
	{
		ByteBuffer buffer = ByteBuffer.allocate(24);
		populateHeader(buffer, 'C', msgType, 'E', ' ');
		buffer.flip();
		return buffer;
	}

	private static void populateNbbo(ByteBuffer buffer, char nbboIndicator, double bidPrice, double askPrice, long bidSizeLots, long askSizeLots, char participantId)
	{
		switch (nbboIndicator)
		{
			case '0': // No National BBO change
			case '1': // Quote contains all National BBO information
			case '2': // No National BBO
				break;
			case '4': // Long Format of National BBO Appendage
				buffer.position(buffer.position() + 2); // reserved
				ByteBufferUtil.putChar(buffer, participantId);
				ByteBufferUtil.putChar(buffer, 'B');
				ByteBufferUtil.putLong(buffer, (long) (bidPrice * 100), 12);
				ByteBufferUtil.putLong(buffer, bidSizeLots, 7);
				buffer.position(buffer.position() + 4); // finraBestBidMarketMaker
				buffer.position(buffer.position() + 3); // reserved
				ByteBufferUtil.putChar(buffer, participantId);
				ByteBufferUtil.putChar(buffer, 'B');
				ByteBufferUtil.putLong(buffer, (long) (askPrice * 100), 12);
				ByteBufferUtil.putLong(buffer, askSizeLots, 7);
				buffer.position(buffer.position() + 4); // finraBestAskMarketMaker
				buffer.position(buffer.position() + 3); // reserved
				break;
			case '6': // Short Format of National BBO Appendage
				ByteBufferUtil.putChar(buffer, participantId);
				ByteBufferUtil.putChar(buffer, 'B');
				ByteBufferUtil.putLong(buffer, (long) (bidPrice * 100), 8);
				ByteBufferUtil.putLong(buffer, bidSizeLots, 3);
				buffer.position(buffer.position() + 1); // reserved
				ByteBufferUtil.putChar(buffer, participantId);
				ByteBufferUtil.putChar(buffer, 'B');
				ByteBufferUtil.putLong(buffer, (long) (askPrice * 100), 8);
				ByteBufferUtil.putLong(buffer, askSizeLots, 3);
				buffer.position(buffer.position() + 1); // reserved
				break;

			default:
				break;

		}
	}

	@Test
	public void test_GetLotSize()
	{
		Assert.assertEquals(new Integer(10), new Integer(this.normalizer.getLotSize("SYED")));
		Assert.assertEquals(new Integer(1), new Integer(this.normalizer.getLotSize("BRK A")));
		Assert.assertEquals(100, this.normalizer.getLotSize("NONE"));
	}

	@Test
	public void testGetTradingState()
	{
		// normal market
		Assert.assertEquals(TradingState.HALTED, CqsNormalizer.getTradingState('P', true, false));
		Assert.assertEquals(TradingState.HALTED, CqsNormalizer.getTradingState('K', true, false));
		// pre market
		Assert.assertEquals(TradingState.TRADING, CqsNormalizer.getTradingState('P', true, true));
		Assert.assertEquals(TradingState.TRADING, CqsNormalizer.getTradingState('K', true, true));

		Assert.assertEquals(TradingState.HALTED, CqsNormalizer.getTradingState('D', true, false));
		Assert.assertEquals(TradingState.HALTED, CqsNormalizer.getTradingState('J', true, false));
		Assert.assertEquals(TradingState.HALTED, CqsNormalizer.getTradingState('Q', true, false));
		Assert.assertEquals(TradingState.HALTED, CqsNormalizer.getTradingState('V', true, false));
		Assert.assertEquals(TradingState.HALTED, CqsNormalizer.getTradingState('Z', true, false));
		Assert.assertEquals(TradingState.HALTED, CqsNormalizer.getTradingState('1', true, false));
		Assert.assertEquals(TradingState.HALTED, CqsNormalizer.getTradingState('2', true, false));
		Assert.assertEquals(TradingState.HALTED, CqsNormalizer.getTradingState('3', true, false));

		Assert.assertEquals(TradingState.HALTED, CqsNormalizer.getTradingState('M', true, false));
		Assert.assertEquals(TradingState.AUCTION, CqsNormalizer.getTradingState('G', true, false));
		Assert.assertEquals(TradingState.TRADING, CqsNormalizer.getTradingState('B', true, false));
		Assert.assertEquals(TradingState.AUCTION, CqsNormalizer.getTradingState('G', true, true));
	}

	@Test
	public void testGetMarketSession()
	{
		MarketState previousMarketState = new MarketState();
		previousMarketState.setMarketSession(MarketSession.PREMARKET);
		Assert.assertEquals(MarketSession.NORMAL, this.normalizer.getMarketSession(previousMarketState, 'N', true, CqsNormalizer.getMarketOpenTime() + 1000, 'T', "RSH"));
		Assert.assertEquals(MarketSession.PREMARKET, this.normalizer.getMarketSession(previousMarketState, 'N', false, CqsNormalizer.getMarketOpenTime() + 1000, 'T', "RSH"));
		Assert.assertEquals(MarketSession.PREMARKET, this.normalizer.getMarketSession(previousMarketState, 'N', true, CqsNormalizer.getMarketOpenTime() + 1000, 'I', "RSH"));
		previousMarketState.setMarketSession(MarketSession.CLOSED);
		previousMarketState.setSymbol("SYED");
		Assert.assertEquals(MarketSession.CLOSED, this.normalizer.getMarketSession(null, 'N', true, CqsNormalizer.getMarketOpenTime() - 1000, 'R', "SYED")); // ipo closed
		Assert.assertEquals(MarketSession.CLOSED, this.normalizer.getMarketSession(previousMarketState, 'N', true, CqsNormalizer.getMarketOpenTime() + 1000, 'R', "SYED")); // ipo closed even when past market time
		Assert.assertEquals(MarketSession.PREMARKET, this.normalizer.getMarketSession(previousMarketState, 'N', true, CqsNormalizer.getMarketOpenTime() + 1000, 'G', "SYED"));
		Assert.assertEquals(MarketSession.CLOSED, this.normalizer.getMarketSession(previousMarketState, 'N', true, CqsNormalizer.getMarketOpenTime() + 1000, 'O', "SYED"));
		previousMarketState.setMarketSession(MarketSession.PREMARKET);
		Assert.assertEquals(MarketSession.NORMAL, this.normalizer.getMarketSession(previousMarketState, 'N', true, CqsNormalizer.getMarketOpenTime() + 1000, 'O', "SYED"));
	}

	@Test
	public void testSsrInAndOut()
	{
		// Not in SSR - remains not in SSR
		Assert.assertEquals(0, CqsNormalizer.getStateConditionCode(' ', ' ', 0, new HashSet<String>(), "AA"));
		Assert.assertEquals(0, CqsNormalizer.getStateConditionCode(' ', 'D', 0, new HashSet<String>(), "AA"));

		// Not in SSR - changes to SSR
		Assert.assertEquals(2048, CqsNormalizer.getStateConditionCode(' ', 'A', 0, new HashSet<String>(), "AA"));
		Assert.assertEquals(2048, CqsNormalizer.getStateConditionCode(' ', 'C', 0, new HashSet<String>(), "AA"));
		Assert.assertEquals(2048, CqsNormalizer.getStateConditionCode(' ', 'E', 0, new HashSet<String>(), "AA"));

		// In SSR - stays in SSR
		Assert.assertEquals(2048, CqsNormalizer.getStateConditionCode(' ', 'A', 2048, new HashSet<String>(), "AA"));
		Assert.assertEquals(2048, CqsNormalizer.getStateConditionCode(' ', 'C', 2048, new HashSet<String>(), "AA"));
		Assert.assertEquals(2048, CqsNormalizer.getStateConditionCode(' ', 'E', 2048, new HashSet<String>(), "AA"));

		// In SSR - changes to not in SSR
		Assert.assertEquals(0, CqsNormalizer.getStateConditionCode(' ', ' ', 2048, new HashSet<String>(), "AA"));
		Assert.assertEquals(0, CqsNormalizer.getStateConditionCode(' ', 'D', 2048, new HashSet<String>(), "AA"));

		// Short message does not impact SSR value
		Assert.assertEquals(0, CqsNormalizer.getStateConditionCode(' ', (char) 0, 0, new HashSet<String>(), "AA"));
		Assert.assertEquals(2048, CqsNormalizer.getStateConditionCode(' ', (char) 0, 2048, new HashSet<String>(), "AA"));
	}
}
