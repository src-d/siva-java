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
    String dstDir;

    public SivaUnpacker(SivaReader reader, String dstDir) {
        this.reader = reader;
        this.dstDir = dstDir;
    }

    public SivaUnpacker(String sivaFilePath, String dstDir) throws SivaException {
        this.dstDir = dstDir;
        try {
            this.reader = new SivaReader(new File(sivaFilePath));
        } catch (FileNotFoundException e) {
            throw new SivaException("Could not read " + sivaFilePath, e);
        }
    }

    public void unpack() throws SivaException {
        File dst = new File(this.dstDir);
        if (!dst.exists()) {
            System.out.print("Directory " + this.dstDir + " does not exist, creating " + dst.getAbsolutePath());
            dst.mkdir();
        }

        List<IndexEntry> entries = null;
        entries = this.reader.getIndex().getFilteredIndex().getEntries();

        //CRC32 crc32 = new CRC32();
        for (IndexEntry entry : entries) {
            extractEntry(entry);
        }
        this.reader.close();
    }

    private void extractEntry(IndexEntry entry) throws SivaException {
        this.extractEntry(entry, null);
    }

    private void extractEntry(IndexEntry entry, CRC32 crc32) throws SivaException {
        InputStream src = this.reader.getEntry(entry);
        try {
            OutputStream dst = createFileFor(entry);
            IOUtils.copy(src, dst);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to create " + entry.getName() + " in " + dstDir);
            return;
        } catch (IOException e) {
            System.out.println("Failed to copy " + entry.getName() + " to " + dstDir);
            return;
        }
        if (crc32 != null) {
            calculateCRC(entry, crc32, src);
        }
    }

    private OutputStream createFileFor(IndexEntry entry) throws FileNotFoundException {
        File dst = Paths.get(this.dstDir, entry.getName()).toAbsolutePath().toFile();
        if (dst.exists()) { // warn
            System.out.println("File " + dst + " already exist, content will be overwritten");
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
