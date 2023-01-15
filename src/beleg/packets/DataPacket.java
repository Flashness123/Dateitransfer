package beleg.packets;

import java.nio.ByteBuffer;

public class DataPacket extends Packet {
    public DataPacket(byte[] sessionNumber, byte paketNumber, byte[] data) {
        this.byteBuffer = ByteBuffer.allocate(data.length + SESSION_NUMBER_LENGTH + PAKET_NUMBER_LENGTH);
        byteBuffer.put(sessionNumber);
        byteBuffer.put(paketNumber);
        byteBuffer.put(data);
    }
}
