package uk.ac.newcastle.redhat.gavgraph.domain.relationships;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import uk.ac.newcastle.redhat.gavgraph.domain.Neo4jEntity;
import uk.ac.newcastle.redhat.gavgraph.domain.nodes.Artifact;
import uk.ac.newcastle.redhat.gavgraph.domain.nodes.Organization;

import java.io.Serializable;

@Setter
@Getter
@RelationshipEntity(type = "OWN")
public class Own extends Neo4jEntity implements Serializable {

    @JsonIgnore
    @StartNode
    private Organization organization;

    @EndNode
    private Artifact artifact;

}
