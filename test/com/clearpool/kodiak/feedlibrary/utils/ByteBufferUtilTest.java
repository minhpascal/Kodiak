package com.clearpool.kodiak.feedlibrary.utils;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.Date;

import org.junit.Test;

import com.clearpool.common.util.DateUtil;

@SuppressWarnings("static-method")
public class ByteBufferUtilTest
{
	@Test
	public void testGetUnsignedLong()
	{
		ByteBuffer buffer = ByteBuffer.allocate(8);
		long nanosSinceMidnight = (new Date().getTime() - DateUtil.TODAY_MIDNIGHT_EST.getTime()) * DateUtil.NANOS_PER_MILLISECOND;
		buffer.putLong(nanosSinceMidnight);
		buffer.flip();
		ByteBufferUtil.advancePosition(buffer, 2);
		assertEquals(nanosSinceMidnight, ByteBufferUtil.getUnsignedLong(buffer, 6));
	}

	@Test
	public void testReadBase95Long()
	{
		ByteBuffer buffer = ByteBuffer.allocate(6);
		buffer.put((byte) '!');
		buffer.put((byte) 'q');
		buffer.put((byte) 'k');
		buffer.put((byte) 'J');
		buffer.put((byte) 'r');
		buffer.put((byte) 'C');
		buffer.flip();
		assertEquals(14400000000l, ByteBufferUtil.readBase95Long(buffer, 6));
	}
}