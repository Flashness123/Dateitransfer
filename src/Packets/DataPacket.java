package Packets;

import java.nio.ByteBuffer;

public class DataPacket extends Packet{
    public DataPacket(byte[] sessionNumber, byte paketNumber, byte[] data){
        this.byteBuffer = ByteBuffer.allocate(data.length + SessionNumberLength + PaketNumberLength);   //vorerst nur allocaten und befullen
        byteBuffer.put(sessionNumber);
        byteBuffer.put(paketNumber);
        byteBuffer.put(data);
    }
}
