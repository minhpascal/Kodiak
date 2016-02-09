package com.clearpool.kodiak.feedlibrary.core;

import com.clearpool.common.util.DateUtil;

public class MDSequencerStats
{
	private long totalDropCount;
	private int totalPacketProcessedCount;
	private long totalMessageProcessedCount;
	private long totalProcessTime;

	private long intervalDropCount;
	private int intervalPacketProcessedCount;
	private long intervalMessageProcessedCount;
	private long intervalProcessTime;

	public void updateProcessTime(long time)
	{
		this.totalProcessTime += time;
		this.intervalProcessTime += time;
	}

	public void incrementMessageProcessedCount()
	{
		this.totalMessageProcessedCount++;
		this.intervalMessageProcessedCount++;
	}

	public void incrementPacketProcessedCount()
	{
		this.totalPacketProcessedCount++;
		this.intervalPacketProcessedCount++;
	}

	public void updateDropCount(long count)
	{
		this.totalDropCount += count;
		this.intervalDropCount += count;
	}

	public long getDropCount()
	{
		return this.totalDropCount;
	}

	public String getStats()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("PacketsProcessed=").append(this.intervalPacketProcessedCount).append("(").append(this.totalPacketProcessedCount).append(')');
		builder.append("| MessagesProcessed=").append(this.intervalMessageProcessedCount).append("(").append(this.totalMessageProcessedCount).append(')');
		builder.append("| Drops=").append(this.intervalDropCount).append("(").append(this.totalDropCount).append(')');
		long totalProcessTimeMillis = this.totalProcessTime / DateUtil.NANOS_PER_MILLISECOND;
		long intervalProcessTimeMillis = this.intervalProcessTime / DateUtil.NANOS_PER_MILLISECOND;
		builder.append(" | TotalProcessTime(ms)=").append(intervalProcessTimeMillis).append("(").append(totalProcessTimeMillis).append(')');
		long packetsPerMillis = (totalProcessTimeMillis == 0) ? 0 : this.totalPacketProcessedCount / totalProcessTimeMillis;
		long intervalPacketsPerMillis = (intervalProcessTimeMillis == 0) ? 0 : this.intervalPacketProcessedCount / intervalProcessTimeMillis;
		builder.append(" | packets/ms=").append(intervalPacketsPerMillis).append("(").append(packetsPerMillis).append(')');
		long messagesPerMillis = (this.totalProcessTime == 0) ? 0 : this.totalMessageProcessedCount * DateUtil.NANOS_PER_MILLISECOND / this.totalProcessTime;
		long intervalMessagesPerMillis = (this.intervalProcessTime == 0) ? 0 : this.intervalMessageProcessedCount * DateUtil.NANOS_PER_MILLISECOND / this.intervalProcessTime;
		builder.append(" | messages/ms=").append(intervalMessagesPerMillis).append("(").append(messagesPerMillis).append(')');
		resetInterval();
		return builder.toString();
	}

	private void resetInterval()
	{
		this.intervalDropCount = 0;
		this.intervalPacketProcessedCount = 0;
		this.intervalMessageProcessedCount = 0;
		this.intervalProcessTime = 0;
	}
}