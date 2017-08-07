package tech.sourced.siva;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.*;

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
     *
     * @param entry
     */
    abstract void add(IndexEntry entry);

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
    private Map<String, IndexEntry> entries;

    FilteredIndex() {
        this.entries = new HashMap<>();
    }

    @Override
    void add(IndexEntry entry) {
        if (entry.getFlag() == Flag.DELETE) {
            this.entries.remove(entry.getName());

            return;
        }

        this.entries.put(entry.getName(), entry);
    }

    @Override
    public List<IndexEntry> getEntries() {
        return new ArrayList<>(this.entries.values());
    }
}

class CompleteIndex extends BaseIndex {
    private List<IndexEntry> entries;

    CompleteIndex() {
        this.entries = new ArrayList<>();
    }

    @Override
    void add(IndexEntry entry) {
        this.entries.add(entry);
    }

    @Override
    public List<IndexEntry> getEntries() {
        return this.entries;
    }
}
