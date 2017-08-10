package tech.sourced.siva;

import java.io.File;
import java.io.FileNotFoundException;

public class SivaCLI {

    public static void main(String args[]) {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: ./siva-unpack <file.siva> [dst dir]");
            System.exit(1);
        }

        String srcSivaFile = args[0];
        String dstDir = args.length == 2 ? args[1] : new File(srcSivaFile).getParent() + "/unpacked";

        try {
            SivaReader reader = null;
            reader = new SivaReader(new File(srcSivaFile));
            SivaUnpacker unpacker = new SivaUnpacker(reader, dstDir);
            System.out.println("Unpacking " +srcSivaFile + " to " + dstDir);
            unpacker.unpack();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SivaException e) {
            e.printStackTrace();
        }

    }

}
