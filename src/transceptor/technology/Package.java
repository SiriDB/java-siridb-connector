package transceptor.technology;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 *
 * @author Tristan Nottelman
 */
public class Package {

    private short pid;
    private int length;
    private byte tipe;
    private byte checkbit;
    private byte[] body;

    public Package(byte[] payload) {
        this.pid = convertByteToNumber(Arrays.copyOfRange(payload, 0, 1)).shortValue();
        this.length = convertByteToNumber(Arrays.copyOfRange(payload, 2, 5)).intValue();
        this.tipe = payload[6];
        this.checkbit = payload[7];
        this.body = null;
    }
    
    public void setBody(byte[] body) {
        this.body = body;
    }
    
    /**
     * Converts array of bytes to number
     * 
     * @param b byte array
     * @return 
     */
    private Number convertByteToNumber(byte[] b) {
        ByteBuffer wrapped = ByteBuffer.wrap(b);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        switch (b.length) {
                case 1:
                    return (int) wrapped.get(0);
                case 2:
                    return (int) wrapped.getShort();
                case 4:
                    return wrapped.getInt();
                case 8:
                    return wrapped.getLong();
            }
            return 0;
    }
}
