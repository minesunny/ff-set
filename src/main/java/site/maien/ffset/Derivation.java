package site.maien.ffset;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a derivation path from a rule to a token.
 */
public class Derivation {
    private final List<Integer> path;
    private final int targetToken;

    public Derivation(List<Integer> path, int targetToken) {
        this.path = Collections.unmodifiableList(path);
        this.targetToken = targetToken;
    }

    public List<Integer> getPath() {
        return path;
    }

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
