package com.clearpool.kodiak.feedlibrary.core.cta;

import java.nio.ByteBuffer;

import com.clearpool.common.util.DateUtil;
import com.clearpool.kodiak.feedlibrary.core.MdFeedPacket;
import com.clearpool.kodiak.feedlibrary.utils.ByteBufferUtil;
import com.clearpool.kodiak.feedlibrary.utils.MdDateUtil;

public class CtaPacket extends MdFeedPacket
{
	private static final long TODAY = MdDateUtil.TODAY_EST.getTime();

	private char messageCategory;
	private char messageType;
	private char messageNetwork;
	private char participantId;
	private long timestamp;
	private long participantTimestamp;

	public CtaPacket(long selectionTimeNanos)
	{
		super(true, selectionTimeNanos);
	}

	@Override
	public void parseHeader()
	{
		this.messageCategory = (char) this.buffer.get();
		this.messageType = (char) this.buffer.get();
		this.messageNetwork = (char) this.buffer.get();
		ByteBufferUtil.advancePosition(this.buffer, 2); // retrans requester
		char headerIdentifier = (char) this.buffer.get();
		ByteBufferUtil.advancePosition(this.buffer, 2); // transaction id part A
		this.sequenceNumber = ByteBufferUtil.readAsciiLong(this.buffer, 9);
		this.participantId = (char) this.buffer.get();
		if (headerIdentifier == 'B')
		{
			this.timestamp = readMicroSecondsSinceMidnight(this.buffer);
			this.participantTimestamp = readMicroSecondsSinceMidnight(this.buffer); // timestamp1
			ByteBufferUtil.advancePosition(this.buffer, 6); // timestamp2
			ByteBufferUtil.advancePosition(this.buffer, 9); // transaction id part B
		}
		else
		{
			this.timestamp = readTimestamp(this.buffer);
		}

		this.messageCount = 1;
	}

	private static long readTimestamp(ByteBuffer buffer)
	{
		int hours = buffer.get() - '0';
		int mins = buffer.get() - '0';
		int seconds = buffer.get() - '0';
		long millis = ByteBufferUtil.readAsciiLong(buffer, 3);
		return TODAY + (hours * DateUtil.MILLIS_PER_HOUR) + (mins * DateUtil.MILLIS_PER_MINUTE) + (seconds * DateUtil.MILLIS_PER_SECOND) + millis;
	}

	private static long readMicroSecondsSinceMidnight(ByteBuffer buffer)
	{
		long microsSinceMidnight = ByteBufferUtil.readBase95Long(buffer, 6);
		long millisSinceMidnight = microsSinceMidnight / 1000;
		return TODAY + millisSinceMidnight;
	}

	public char getMessageCategory()
	{
		return this.messageCategory;
	}

	public char getMessageType()
	{
		return this.messageType;
	}

	public char getMessageNetwork()
	{
		return this.messageNetwork;
	}

	public char getParticipantId()
	{
		return this.participantId;
	}

	public long getTimestamp()
	{
		return this.timestamp;
	}

	public long getParticipantTimestamp()
	{
		return this.participantTimestamp;
	}

	@Override
	public boolean isSequenceNumberReset()
	{
		return this.messageCategory == 'C' && this.messageType == 'L';
	}

	@Override
	public boolean isEndOfTransmission()
	{
		return this.messageCategory == 'C' && this.messageType == 'Z';
	}
}