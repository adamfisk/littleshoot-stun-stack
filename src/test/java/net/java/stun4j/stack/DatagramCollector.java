package net.java.stun4j.stack;

import java.net.*;
import java.io.IOException;

public class DatagramCollector
    implements Runnable
{
    DatagramPacket receivedPacket = null;
    DatagramSocket sock           = null;

    public DatagramCollector()
    {
    }

    public void run()
    {
        try
        {
            sock.receive(receivedPacket);
        }
        catch (IOException ex)
        {
            receivedPacket = null;
        }

    }

    public void startListening(DatagramSocket sock)
    {
        this.sock = sock;
        receivedPacket = new DatagramPacket(new byte[4096], 4096);

        new Thread(this).start();

        //give the guy a chance to start
        try
        {
            Thread.sleep(200);
        }
        catch (InterruptedException ex)
        {
        }
    }

    public DatagramPacket collectPacket()
    {
        //recycle
        DatagramPacket returnValue = receivedPacket;
        receivedPacket = null;
        sock           = null;

        //return
        return returnValue;
    }
}
