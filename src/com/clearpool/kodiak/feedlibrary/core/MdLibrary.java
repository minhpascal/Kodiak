package com.clearpool.kodiak.feedlibrary.core;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clearpool.kodiak.feedlibrary.callbacks.IMdLibraryCallback;
import com.clearpool.messageobjects.marketdata.MdServiceType;

public class MdLibrary
{
	private static final Logger LOGGER = Logger.getLogger(MdLibrary.class.getName());

	private final MdLibraryContext context;
	private final MdFeed feed;
	private final String[] lines;
	private final String interfaceA;
	private final String interfaceB;
	private final String readFromDir;
	private final MdProcessor[] mdProcessors;
	private final Map<MdServiceType, IMdLibraryCallback> callbacks;

	public MdLibrary(MdLibraryContext context, MdFeed feed, String[] lines, String interfaceA, String interfaceB, String readFromDir) throws Exception
	{
		this.context = context;
		this.feed = feed;
		this.lines = lines;
		this.interfaceA = interfaceA;
		this.interfaceB = interfaceB;
		this.readFromDir = readFromDir;
		this.mdProcessors = new MdProcessor[this.lines.length];
		this.callbacks = new HashMap<MdServiceType, IMdLibraryCallback>();
	}

	public void registerService(MdServiceType serviceType, IMdLibraryCallback callback)
	{
		this.callbacks.put(serviceType, callback);
	}

	public void initProcessors()
	{
		try
		{
			for (int i = 0; i < this.lines.length; i++)
			{
				String line = this.lines[i];
				int lineInt = Integer.parseInt(line);
				MdProcessor processor = new MdProcessor(this.feed, line, this.interfaceA, this.interfaceB, createNormalizer(this.feed,
						MdFeedProps.getProperty(this.feed.toString(), line, MdFeedProps.RANGE), lineInt, i));
				this.mdProcessors[i] = processor;
				if (this.context.readFromSocket()) processor.registerWithSocketSelector(this.context.getSocketSelectorForLine(lineInt));
				else processor.registerWithFileSelector(this.readFromDir, this.context.getFileSelectorForLine(lineInt));
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	// Helper
	private IMdNormalizer createNormalizer(MdFeed normalizerFeed, String range, int channel, int index)
	{
		String className = MdFeedProps.getProperty(normalizerFeed.toString(), MdFeedProps.NORMALIZER);
		if (className == null || className.isEmpty()) return null;
		try
		{
			return (IMdNormalizer) Class.forName(className).getConstructor(Map.class, String.class, int.class, int.class)
					.newInstance(this.callbacks, range, Integer.valueOf(channel), Integer.valueOf(index));
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	public MdFeed getMdFeed()
	{
		return this.feed;
	}

	public String[] getLines()
	{
		return this.lines;
	}

	public String getInterfaceA()
	{
		return this.interfaceA;
	}

	public String getInterfaceB()
	{
		return this.interfaceB;
	}

	public MdProcessor[] getMdProcessors()
	{
		return this.mdProcessors;
	}

	public int getSelectorThreadCount()
	{
		return this.context.getSelectorThreadCount();
	}
}