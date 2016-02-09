package com.clearpool.kodiak.feedlibrary.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clearpool.common.datastractures.Pair;

public class MdSocketSelector extends Thread
{
	private static final Logger LOGGER = Logger.getLogger(MdSocketSelector.class.getName());

	protected final Selector selector;
	private final ByteBuffer udpBuffer;
	protected final int recvBufferSize;
	private final boolean spinning;

	public MdSocketSelector(String name, int recvBufferSize, boolean spinning) throws IOException
	{
		this.recvBufferSize = recvBufferSize;
		this.selector = Selector.open();
		this.udpBuffer = ByteBuffer.allocateDirect(1500);
		this.spinning = spinning;
		this.setName("MdSocketSelector-" + name);
	}

	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				// Do selection
				int selectedKeyCount = (this.spinning) ? this.selector.selectNow() : this.selector.select();
				if (selectedKeyCount > 0)
				{
					Iterator<SelectionKey> selectionKeyIterator = this.selector.selectedKeys().iterator();
					while (selectionKeyIterator.hasNext())
					{
						SelectionKey selectedKey = selectionKeyIterator.next();
						selectionKeyIterator.remove();

						if (selectedKey.channel() instanceof DatagramChannel)
						{
							handleMulticastSelection(selectedKey);
						}
					}
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	protected void handleMulticastSelection(SelectionKey selectedKey)
	{
		try
		{
			if (selectedKey.isReadable())
			{
				try
				{
					this.udpBuffer.clear();
					((DatagramChannel) selectedKey.channel()).receive(this.udpBuffer);
					this.udpBuffer.flip();
					((ISelectable) selectedKey.attachment()).onSelection(selectedKey, this.udpBuffer);
				}
				catch (Exception e)
				{
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
		catch (CancelledKeyException e)
		{
			LOGGER.warning("Unable to process selection because key (" + selectedKey + ") has been cancelled");
		}
	}

	public Pair<SelectionKey, DatagramChannel> registerMulticastChannel(String ip, int port, String interfaceIp, ISelectable attachment)
	{
		try
		{
			DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
			channel.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
			channel.configureBlocking(false);
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName(interfaceIp));
			channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
			InetAddress address = InetAddress.getByName(ip);
			if (System.getProperty("os.name").contains("Windows")) channel.bind(new InetSocketAddress(port));
			else channel.bind(new InetSocketAddress(address, port));
			channel.setOption(StandardSocketOptions.SO_RCVBUF, Integer.valueOf(this.recvBufferSize));
			SelectionKey key = channel.register(this.selector, SelectionKey.OP_READ, attachment);
			channel.join(address, networkInterface);

			// Log values from the channel to confirm they got registered properly
			Object reuse = channel.getOption(StandardSocketOptions.SO_REUSEADDR);
			Object intf = channel.getOption(StandardSocketOptions.IP_MULTICAST_IF);
			Object recvBuf = channel.getOption(StandardSocketOptions.SO_RCVBUF);
			LOGGER.info("Joined channel[" + address + ":" + port + ",SO_REUSEADDR=" + reuse + ",IP_MULTICAST_IF=" + intf + ",SO_RCVBUF=" + recvBuf + "]");

			return new Pair<SelectionKey, DatagramChannel>(key, channel);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
}
