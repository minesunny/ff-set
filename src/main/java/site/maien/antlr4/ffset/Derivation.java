package site.maien.antlr4.ffset;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single derivation path from a parser rule to a terminal token type.
 *
 * <p>The path is an ordered list of invoked rule indices describing the chain of rule
 * references that leads, outermost first, to the rule which directly produces the target
 * token. An empty path means the token is produced directly by the originating rule with no
 * intervening rule reference.
 */
public class Derivation {
    private final List<Integer> path;
    private final int targetToken;

    /**
     * Creates a derivation.
     *
     * @param path        the chain of invoked rule indices (stored as an unmodifiable copy)
     * @param targetToken the terminal token type the path arrives at; {@code -1}
     *                    ({@code Token.EOF}) denotes end-of-input / the empty string (epsilon)
     */
    public Derivation(List<Integer> path, int targetToken) {
        this.path = Collections.unmodifiableList(path);
        this.targetToken = targetToken;
    }

    /**
     * @return the chain of invoked rule indices leading to {@link #getTargetToken()};
     *         empty when the token is produced directly by the originating rule
     */
    public List<Integer> getPath() {
        return path;
    }

    /**
     * @return the terminal token type the path arrives at; {@code -1} ({@code Token.EOF})
     *         denotes end-of-input / the empty string (epsilon)
     */
    public int getTargetToken() {
        return targetToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Derivation that = (Derivation) o;
        return targetToken == that.targetToken && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, targetToken);
    }

    @Override
    public String toString() {
        return "Derivation{" +
                "path=" + path +
                ", targetToken=" + targetToken +
                '}';
    }
}
