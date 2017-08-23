package tech.sourced.siva;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Index {
    List<IndexEntry> getEntries();

    /**
     * glob returns all index entries whose name matches pattern or an empty List if there is
     * no matching entry.
     *
     * @param pattern glob pattern
     * @return a list of IndexEntry that matches with the glob pattern provided.
     */
    List<IndexEntry> glob(String pattern);
}

abstract class BaseIndex implements Index {
    /**
     * This method will be called in the same order that the index has been read.
     */
    abstract void add(IndexEntry entry);

    /**
     * This method will be called when an index block is totally read.
     */
    abstract void endIndexBlock();

    @Override
    public List<IndexEntry> glob(String pattern) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:".concat(pattern));
        List<IndexEntry> result = new ArrayList<>();
        for (IndexEntry entry : this.getEntries()) {
            if (matcher.matches(Paths.get(entry.getName()))) {
                result.add(entry);
            }
        }

        return result;
    }
}

class FilteredIndex extends BaseIndex {
    private final Map<String, IndexEntry> entries = new HashMap<>();
    private final Map<String, IndexEntry> blockEntries = new HashMap<>();

    private final Set<String> deleted = new HashSet<>();

    @Override
    void add(IndexEntry entry) {
        String name = entry.getName();

        if (entry.getFlag() == Flag.DELETE) {
            this.deleted.add(name);
            this.blockEntries.remove(name);
            return;
        }

        if (!this.deleted.contains(name)) {
            this.blockEntries.put(entry.getName(), entry);
        }
    }

    @Override
    void endIndexBlock() {
        for (Map.Entry<String, IndexEntry> entry : this.blockEntries.entrySet()) {
            this.entries.putIfAbsent(entry.getKey(), entry.getValue());
        }

        this.blockEntries.clear();
    }

    @Override
    public List<IndexEntry> getEntries() {
        return new ArrayList<>(this.entries.values());
    }
}

class CompleteIndex extends BaseIndex {
    private final List<IndexEntry> entries = new ArrayList<>();

    @Override
    void add(IndexEntry entry) {
        this.entries.add(entry);
    }

    @Override
    void endIndexBlock() {
    }

    @Override
    public List<IndexEntry> getEntries() {
        return this.entries;
    }
}
