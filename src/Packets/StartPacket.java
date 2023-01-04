package Packets;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
                START_SIGNAL.length +
                FILE_LENGTH_LENGTH +
                FILENAME_LENGTH_LENGTH +
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


}
