package com.clearpool.kodiak.feedlibrary.utils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.clearpool.kodiak.feedlibrary.core.MdFeed;
import com.clearpool.kodiak.feedlibrary.core.MdProcessor;
import com.clearpool.kodiak.feedlibrary.core.MdSocketSelector;

public class MdPacketLossDetector
{
	static final Logger LOGGER = Logger.getLogger(MdLogger.class.getName());

	private final MdSocketSelector[] selectors;
	private final List<MdProcessor> processors;
	private final Timer timer;

	public MdPacketLossDetector(String feed, String[] lines, String networkAIp, String networkBIp, boolean spinning, int threadCount) throws IOException
	{
		this.selectors = new MdSocketSelector[threadCount];
		this.processors = new LinkedList<MdProcessor>();
		this.timer = new Timer();

		for(int i = 0; i < this.selectors.length; ++i)
			this.selectors[i] = new MdSocketSelector(feed + " Logger Selector Thread", 8388608, spinning);

		registerProcessors(feed, lines, networkAIp, networkBIp);
	}

	public void start()
	{
		this.timer.schedule(new TimerTask() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void run()
			{
				for (MdProcessor processor : MdPacketLossDetector.this.processors)
				{
					StringBuilder builder = new StringBuilder();
					builder.append(" Line#").append(processor.getLine());
					builder.append(" Range=").append(processor.getRange());
					builder.append(" Stats=[").append(processor.getStatistics());
					builder.append("]");
					LOGGER.info(builder.toString());
				}
			}
		}, 60000, 60000);
		
		//Start selectors
		for (int i = 0; i < this.selectors.length; ++i)
			this.selectors[i].start();
	}

	private void registerProcessors(String inputfeed, String[] lines, String networkAIp, String networkBIp)
	{
		String[] feeds = null;
		if (inputfeed.equals("CTA"))
			feeds = new String[] { MdFeed.CQS.toString(), MdFeed.CTS.toString() };
		else if (inputfeed.equals("UTP"))
			feeds = new String[] { MdFeed.UQDF.toString(), MdFeed.UTDF.toString() };
		else
			feeds = new String[] { inputfeed };

		for (String feed : feeds)
		{
			for (int i = 0; i < lines.length; ++i)
			{
				String line = lines[i];
				MdProcessor processor = new MdProcessor(MdFeed.valueOf(feed), line, networkAIp, networkBIp, null);
				this.processors.add(processor);
				processor.registerWithSocketSelector(this.selectors[i % this.selectors.length]);
			}
		}
	}

	public static void main(String[] args) throws IOException
	{
		String feed = args[0];
		String[] lines = args[1].split(",");
		String networkAIp = args[2];
		String networkBIp = args[3];
		boolean spinning = Boolean.valueOf(args[4]).booleanValue();
		int threadCount = Integer.valueOf(args[5]).intValue();
		MdPacketLossDetector detector = new MdPacketLossDetector(feed, lines, networkAIp, networkBIp, spinning, threadCount);
		detector.start();
	}
}
