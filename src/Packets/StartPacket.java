package Packets;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class StartPacket extends Packet {
    private static final byte[] StartSignal = "Start".getBytes();
    private static final int FileLength_Length = 8;
    private static final int FilenameLength_Length = 2;

    /**
     *
     * @param sessionNumber Session number for each session
     * @param fileName File name of the file to transmit
     * @param fileLength length of file to transmit
     */

    public StartPacket(byte[] sessionNumber, String fileName, long fileLength) {
        /*this.byteBuffer = ByteBuffer.allocate(
            (
                SessionNumberLength +
                PaketNumberLength +
                StartSignal.length +
                FileLength_Length +
                FilenameLength_Length +
                fileName.length()
            )
        );*/
        //Aufpassen, buffer bestehend aus 2 ByteBuffern, mit und ohne payload
        ByteBuffer ByteBufferNonPayload = ByteBuffer.allocate(
                SessionNumberLength +
                        PaketNumberLength +
                        StartSignal.length
        );

        ByteBufferNonPayload.put(sessionNumber);

        byte PacketNumber = 0;
        ByteBufferNonPayload.put(PacketNumber);

        ByteBufferNonPayload.put(StartSignal);

        ByteBuffer ByteBufferPayload = ByteBuffer.allocate(
                FileLength_Length +
                        FilenameLength_Length +
                        fileName.length()
        );
        ByteBufferPayload.putLong(fileLength);  //befullen
        ByteBufferPayload.putShort((short) fileName.length());  //casten auf short da nur 2 bytes
        ByteBufferPayload.put(fileName.getBytes());

        this.byteBuffer = ByteBuffer.allocate(
                ByteBufferNonPayload.capacity() +
                        ByteBufferPayload.capacity() +
                        Crc32Length
        );

        this.byteBuffer.put(ByteBufferNonPayload.array());
        this.byteBuffer.put(ByteBufferPayload.array());

        CRC32 crc32 = new CRC32();
        crc32.update(ByteBufferPayload.array());    //crc soll nur aus payload bestehen

        this.byteBuffer.putInt((int) crc32.getValue());
    }

}
