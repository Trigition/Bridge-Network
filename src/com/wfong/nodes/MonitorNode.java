package com.wfong.nodes;

import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wfong.token.STPLPFrame;

/**
 * This class implements Monitor Node behavior and is a subclass of the Node class.
 * @author William
 *
 */
public class MonitorNode extends Node implements Runnable{
	private Map<Integer, Integer> myNetwork;
	private InetAddress myAddress;
	private int port;
	
	public MonitorNode() {
		super();
	}

	/**
	 * This constructor creates a Monitor node with the specified ID (usually 0) and a specified time out period (in milliseconds).
	 * @param NodeName The Node ID.
	 * @param timeOut The time out period in milliseconds.
	 */
	public MonitorNode(int NodeName, int timeOutPeriod) {
		super(NodeName, timeOutPeriod);
		this.myAddress = getLocalAddress();
		this.port = this.addServerSocket(myAddress);
		this.myNetwork = new HashMap<Integer, Integer>();
	}
	
	/**
	 * Places each node into myNetwork. This is to keep track of whom has completed their transfers.
	 * @param myNetwork A list of all the Nodes in the network.
	 */
	public void placeNetwork(List<RelayNode> myNetwork) {
		for (RelayNode node : myNetwork) {
			this.myNetwork.put(node.getNodeID(), 0);
		}
	}


	/**
	 * This method monitors the Network for Garbled and Orphaned Frames. Lost tokens are handled
	 * by the enclosing method.
	 */
	private int MonitorNetwork() {
		STPLPFrame inputFrame = null;
			try {
				inputFrame = readSocket();
				//Check for Garbled Frame
				if (!isFrameHealthy(inputFrame)) {
					//Frame is Garbled
					if (inputFrame == null) {
						writeToSocket(STPLPFrame.generateToken());
						return 0;
					}
					this.drainInputSocket();
					writeToSocket(STPLPFrame.generateToken());
					return 0;
				}
				//Check for Orphan Frame
				if (!inputFrame.isToken() && inputFrame.monitorBit()) {
					System.out.println("MONITOR NODE: Removing orphan frame.");
					inputFrame = null; //'Drain' the frame
					return 0;
				} else if (!inputFrame.isToken()) {
					//This is the Frame's first encounter with Monitor
					//Set the monitor bit
					inputFrame.setMonitorBit();
					writeToSocket(inputFrame);
					return 0;
				}
				//Check for Transmission Completed Signals
				if (inputFrame.isToken() && inputFrame.getFrameStatus() == 0) {
					writeToSocket(STPLPFrame.generateKillSig());
					return 0;
				}
				//Check for Kill Signal
				if (inputFrame.getFrameStatus() == 4) {
					this.closeNode();
					return 1;
				}
				//Check for token
				if (inputFrame.isToken()) {
					writeToSocket(STPLPFrame.generateToken());
					return 0;
				}
				writeToSocket(inputFrame);
				return 0;
				
				} catch (SocketTimeoutException e) {
					System.out.println("MONITOR NODE: Timeout");
					writeToSocket(STPLPFrame.generateToken());
					return 0;
				}
	}
	
	/**
	 * This method checks the validity of the Frame.
	 * @param frame The Frame to be checked.
	 * @return True if the Frame is healthy, false if it is garbled.
	 */
	public static boolean isFrameHealthy(STPLPFrame frame) {
		//Check Frame Control Health
		try {
		if (frame.getFrameControl() > 1) {
			//System.out.println("FRAME Error: Frame Control out of bounds: " + frame.getFrameControl());
			return false; //Values above 5 on Frame Control are not accepted
		}
		//Check if Data Length is correct
		int frameLength = frame.getFrame().length;
		int dataSize = frame.getDataSize();
		//System.out.println("\tFrame Length  " + frameLength + " Frame Data Size + 6: " + (dataSize + 6));
		if (frameLength != dataSize + 6) {
			//System.out.println("FRAME Error: Incorrect Data Size");
			return false;
		}
		if (frame.getFrameStatus() > 0x40) {
			//sSystem.out.println("FRAME Error: Incorrect Frame Status Byte");
			return false;
		}
		return true;
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	/**
	 * Returns the Port this node is listening to.
	 * @return The Port Number.
	 */
	public int getPort() {
		return this.port;
	}
	
	@Override
	public void run(){
		//System.out.println("Initialized Monitor Node");
		long simTime;
		simTime = System.currentTimeMillis();
		this.acceptClient();
		while (true) {
			if(MonitorNetwork() == 1) {
				System.out.println(((System.currentTimeMillis() - simTime) / 1000));
				return;
			}
		}
	}
	
}