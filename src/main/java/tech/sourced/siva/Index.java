package tech.sourced.siva;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.*;

/**
 * Index stands for the part of the blocks of the siva files that contains {@link IndexEntry}s and the {@link IndexFooter}.
 *
 * @see <a href="https://github.com/src-d/go-siva/blob/master/SPEC.md">Siva Format Specification</a>
 */
public interface Index {

    /**
     * @return the list of {@link IndexEntry} that the index contains.
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    void endIndexBlock() {
        for (Map.Entry<String, IndexEntry> entry : this.blockEntries.entrySet()) {
            this.entries.putIfAbsent(entry.getKey(), entry.getValue());
        }

        this.blockEntries.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IndexEntry> getEntries() {
        return new ArrayList<>(this.entries.values());
    }
}

class CompleteIndex extends BaseIndex {
    private final List<IndexEntry> entries = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    void add(IndexEntry entry) {
        this.entries.add(entry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endIndexBlock() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IndexEntry> getEntries() {
        return this.entries;
    }
}
