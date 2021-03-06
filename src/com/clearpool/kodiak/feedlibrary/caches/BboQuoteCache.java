package com.clearpool.kodiak.feedlibrary.caches;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.clearpool.common.datastractures.MutableInteger;
import com.clearpool.common.symbology.ISymbolConverter;
import com.clearpool.kodiak.feedlibrary.callbacks.IMdQuoteListener;
import com.clearpool.kodiak.feedlibrary.core.MdFeed;
import com.clearpool.kodiak.feedlibrary.utils.symbolconverters.SymbolConverterFactory;
import com.clearpool.messageobjects.marketdata.Exchange;
import com.clearpool.messageobjects.marketdata.MdEntity;
import com.clearpool.messageobjects.marketdata.MdServiceType;
import com.clearpool.messageobjects.marketdata.Quote;

public class BboQuoteCache implements IMdServiceCache
{
	private final IMdQuoteListener quoteListener;
	private final MdFeed feedType;
	private final String range;
	private final int channel;
	private final int index;
	private final MdServiceType mdServiceType;
	private final Map<String, Map<Exchange, Quote>> quotes; // feed symbol -> exchange -> quote
	private final Map<String, MutableInteger> symbolToSequenceNumbers; // CMS symbol -> sequence number

	public BboQuoteCache(IMdQuoteListener quoteListener, MdFeed feedType, String range, int channel, int index)
	{
		this.quoteListener = quoteListener;
		this.feedType = feedType;
		this.range = range;
		this.channel = channel;
		this.index = index;
		this.mdServiceType = MdServiceType.BBO;
		this.quotes = new HashMap<>();
		this.symbolToSequenceNumbers = new HashMap<String, MutableInteger>();
	}

	public void updateBidAndOffer(String symbol, Exchange exchange, double bidPrice, int bidSize, double askPrice, int askSize, long timestamp, long participantTimestamp,
			int conditionCode)
	{
		Map<Exchange, Quote> symbolQuotes = this.quotes.get(symbol);
		if (symbolQuotes == null)
		{
			symbolQuotes = new HashMap<>();
			this.quotes.put(symbol, symbolQuotes);
		}

		Quote quote = symbolQuotes.get(exchange);
		if (quote == null)
		{
			quote = this.createQuote(symbol, exchange);
			symbolQuotes.put(exchange, quote);
		}

		quote.setBidPrice(bidPrice);
		quote.setBidSize(bidSize);
		quote.setBidExchange(exchange);
		quote.setAskPrice(askPrice);
		quote.setAskSize(askSize);
		quote.setAskExchange(exchange);
		quote.setTimestamp(timestamp);
		quote.setParticipantTimestamp(participantTimestamp);
		quote.setConditionCode(conditionCode);
		sendQuote(quote);
	}

	private void sendQuote(Quote quote)
	{
		quote.setMdTimestamp(System.currentTimeMillis());
		quote.setConditionCode(MdEntity.setCondition(quote.getConditionCode(), MdEntity.CONDITION_FRESH));
		quote.setSymbolSequenceNumber(getNextSymbolSequenceNumber(quote.getSymbol()));

		if (this.quoteListener != null)
		{
			quote = quote.clone();
			this.quoteListener.quoteReceived(quote, this.channel, this.index);
		}
	}

	private int getNextSymbolSequenceNumber(String symbol)
	{
		MutableInteger sequenceNumber = this.symbolToSequenceNumbers.get(symbol);
		if (sequenceNumber == null)
		{
			sequenceNumber = new MutableInteger(0);
			this.symbolToSequenceNumbers.put(symbol, sequenceNumber);
		}
		return sequenceNumber.incrementAndGet();
	}

	private Quote createQuote(String symbol, Exchange exchange)
	{
		Quote quote = new Quote();
		quote.setServiceType(MdServiceType.BBO);
		ISymbolConverter converter = SymbolConverterFactory.getConverterInstance(this.feedType);
		if (converter != null) quote.setSymbol(converter.convert(symbol));
		else quote.setSymbol(symbol);
		quote.setBidExchange(exchange);
		quote.setAskExchange(exchange);
		return quote;
	}

	@Override
	public MdServiceType getMdServiceType()
	{
		return this.mdServiceType;
	}

	@Override
	public String[] getAllSymbols()
	{
		return this.quotes.keySet().toArray(new String[0]);
	}

	@Override
	public Map<Exchange, Quote> getData(String symbol)
	{
		Map<Exchange, Quote> symbolQuotes = this.quotes.get(symbol);
		if (symbolQuotes == null) return null;
		return symbolQuotes;
	}

	@Override
	public String getRange()
	{
		return this.range;
	}

	@Override
	public Collection<String> publishAllData()
	{
		for (Map<Exchange, Quote> symbolQuotes : this.quotes.values())
		{
			if (symbolQuotes != null)
			{
				for (Quote quote : symbolQuotes.values())
				{
					if (this.quoteListener != null)
					{
						quote = quote.clone();
						this.quoteListener.quoteReceived(quote, this.channel, this.index);
					}
				}
			}
		}
		return this.quotes.keySet();
	}
}
