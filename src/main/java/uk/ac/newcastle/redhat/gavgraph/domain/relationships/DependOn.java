package uk.ac.newcastle.redhat.gavgraph.domain.relationships;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import uk.ac.newcastle.redhat.gavgraph.domain.Neo4jEntity;
import uk.ac.newcastle.redhat.gavgraph.domain.nodes.Artifact;

import java.io.Serializable;

@Getter
@Setter
@RelationshipEntity(type = "DEPEND_ON")
public class DependOn extends Neo4jEntity implements Serializable {

    @JsonIgnore
    @StartNode
    private Artifact from;

    @EndNode
    private Artifact to;

    private String scope;

}
