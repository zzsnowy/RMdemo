package label;

import java.util.Objects;

public class EvolutionaryDependency {

    String node1;
    String node2;

    public EvolutionaryDependency(String node1, String node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(node1, node2);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null || getClass() != obj.getClass()) {
            return false;
        }
        EvolutionaryDependency ed = (EvolutionaryDependency) obj;
        return ed.node1.equals(node1) && ed.node2.equals(node2);
    }
}
