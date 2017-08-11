package tech.sourced.siva;

class IndexFooter {
    private final int entryCount;
    private final long indexSize;
    private final long blockSize;
    private final int crc32;

    IndexFooter(int entryCount, long indexSize, long blockSize, int crc32) {
        this.entryCount = entryCount;
        this.indexSize = indexSize;
        this.blockSize = blockSize;
        this.crc32 = crc32;
    }

    int getEntryCount() {
        return entryCount;
    }

    long getIndexSize() {
        return indexSize;
    }

    long getBlockSize() {
        return blockSize;
    }

    int getCrc32() {
        return crc32;
    }
}
