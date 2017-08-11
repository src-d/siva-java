package tech.sourced.siva;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.CRC32;

/**
 * This is a high-level API for extracting bare git repository
 * (content of .git) from a given .siva file.
 * <p>
 * Actual reading is done by SivaReader
 *
 * @see SivaReader#getEntry(IndexEntry)
 */
public class SivaUnpacker {
    SivaReader reader;

    public SivaUnpacker(SivaReader reader) {
        this.reader = reader;
    }

    public SivaUnpacker(String sivaFilePath) throws SivaException {
        try {
            this.reader = new SivaReader(new File(sivaFilePath));
        } catch (FileNotFoundException e) {
            throw new SivaException("Could not read " + sivaFilePath, e);
        }
    }

    public void unpack(String dstDir) throws SivaException {
        File dst = new File(dstDir);
        if (!dst.exists()) {
            System.out.println("Directory " + dstDir + " does not exist, creating " + dst.getAbsolutePath());
            if (!dst.mkdirs()) {
                throw new SivaException("Failed to created a directory: " + dst.getAbsolutePath());
            }
        }

        List<IndexEntry> entries = this.reader.getIndex().getFilteredIndex().getEntries();
        for (IndexEntry entry : entries) {
            this.extractEntry(entry, dstDir);
        }
        this.reader.close();
    }

    private void extractEntry(IndexEntry entry, String dstDir) throws SivaException {
        this.extractEntry(entry, dstDir, null);
    }

    private void extractEntry(IndexEntry entry, String dstDir, CRC32 crc32) throws SivaException {
        InputStream src = this.reader.getEntry(entry);
        try {
            OutputStream dst = createFileFor(entry, dstDir);
            IOUtils.copy(src, dst);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to create " + entry.getName() + " in " + dstDir);
            e.printStackTrace();
            return;
        } catch (IOException e) {
            System.out.println("Failed to copy " + entry.getName() + " to " + dstDir);
            e.printStackTrace();
            return;
        }
        if (crc32 != null) {
            calculateCRC(entry, crc32, src);
        }
    }

    private OutputStream createFileFor(IndexEntry entry, String dstDir) throws FileNotFoundException {
        File dst = Paths.get(dstDir, entry.getName()).toAbsolutePath().toFile();
        if (dst.exists()) { // warn
            System.out.println("File " + dst + " already exist, content will be overwritten");
        } else {
            dst.getParentFile().mkdirs();
        }

        //TODO(bzz) apply permissions: entry.getFileMode() to dst
        return new FileOutputStream(dst);
    }

    private void calculateCRC(IndexEntry entry, CRC32 crc32, InputStream src) throws SivaException {
        crc32.reset();
        try {
            src.reset();
            crc32.update(IOUtils.toByteArray(src));
            if (entry.getCrc32() == crc32.getValue()) {
                throw new SivaException("CRC32 checksum does not match for " + entry.getName() +
                        ": expected " + entry.getCrc32() + " actual " + crc32.getValue());
            }
        } catch (IOException e) {
            throw new SivaException("Failed to calcuate CRC for " + entry.getName(), e);
        }
    }

}
