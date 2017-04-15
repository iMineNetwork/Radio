package nl.imine.radio.validator;

import org.springframework.stereotype.Service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class NBSFileValidator {

    /**
     * Validates an NBS stream.
     * Code taken from https://github.com/xxmicloxx/NoteBlockAPI/blob/master/src/main/java/com/xxmicloxx/NoteBlockAPI/NBSDecoder.java
     *
     * @param source The source stream to read
     * @return if the NBS file was valid
     */
    public boolean validate(InputStream source){
        DataInputStream inputStream = new DataInputStream(source);
        try {
            DataInputStream dis = new DataInputStream(inputStream);
            short length = readShort(dis);
            short songHeight = readShort(dis);
            String title = readString(dis);
            if(title == null){
                return false;
            }
            String author = readString(dis);
            if(author == null) {
                return false;
            }
            readString(dis);
            String description = readString(dis);
            if(description == null) {
                return false;
            }
            float speed = readShort(dis) / 100f;
            dis.readBoolean(); // auto-save
            dis.readByte(); // auto-save duration
            dis.readByte(); // x/4ths, time signature
            readInt(dis); // minutes spent on project
            readInt(dis); // left clicks (why?)
            readInt(dis); // right clicks (why?)
            readInt(dis); // blocks added
            readInt(dis); // blocks removed
            readString(dis); // .mid/.schematic file name
            short tick = -1;
            while (true) {
                short jumpTicks = readShort(dis); // jumps till next tick
                //System.out.println("Jumps to next tick: " + jumpTicks);
                if (jumpTicks == 0) {
                    break;
                }
                tick += jumpTicks;
                //System.out.println("Tick: " + tick);
                short layer = -1;
                while (true) {
                    short jumpLayers = readShort(dis); // jumps till next layer
                    if (jumpLayers == 0) {
                        break;
                    }
                    layer += jumpLayers;
                    //System.out.println("Layer: " + layer);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static short readShort(DataInputStream dis) throws IOException {
        int byte1 = dis.readUnsignedByte();
        int byte2 = dis.readUnsignedByte();
        return (short) (byte1 + (byte2 << 8));
    }

    private static int readInt(DataInputStream dis) throws IOException {
        int byte1 = dis.readUnsignedByte();
        int byte2 = dis.readUnsignedByte();
        int byte3 = dis.readUnsignedByte();
        int byte4 = dis.readUnsignedByte();
        return (byte1 + (byte2 << 8) + (byte3 << 16) + (byte4 << 24));
    }

    private static String readString(DataInputStream dis) throws IOException {
        int length = readInt(dis);
        if(length < 1024) {
            StringBuilder sb = new StringBuilder(length);
            for (; length > 0; --length) {
                char c = (char) dis.readByte();
                if (c == (char) 0x0D) {
                    c = ' ';
                }
                sb.append(c);
            }
            return sb.toString();
        }
        return null;
    }
}
