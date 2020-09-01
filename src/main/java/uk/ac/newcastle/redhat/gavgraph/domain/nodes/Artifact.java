package uk.ac.newcastle.redhat.gavgraph.domain.nodes;

import com.sun.istack.internal.NotNull;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import uk.ac.newcastle.redhat.gavgraph.domain.Node;
import uk.ac.newcastle.redhat.gavgraph.domain.relationships.DependOn;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * This class will be like a node in a linked list or a graph,
 * there should be some 前继节点 and 后继节点
 *
 * for example: org.slf4j:slf4j-api:1.6.1
 */
//@Data//@ToString、@EqualsAndHashCode、@Getter、@Setter、@RequiredArgsConstrutor
@NodeEntity
@Setter
@Getter
public class Artifact extends Node implements Serializable {

    @ApiModelProperty(hidden = true)
    private Long id;

    @NotNull
    @ApiModelProperty(example = "uk.neo4j.graph")
    private String groupId;

    @NotNull
    @ApiModelProperty(example = "neo4j-graph")
    private String artifactId;

    @NotNull
    @ApiModelProperty(example = "1.0")
    private String version;

    @NotNull
    @ApiModelProperty(example = "uk.neo4j.graph:neo4j-graph:1.0")
    private String gav;

    @ApiModelProperty(example = "true")
    private Boolean availability;

    @Relationship(type = "DEPEND_ON")
    private Set<DependOn> dependOns;

    /*@ApiModelProperty(example = "runtime")
    private String scope;*/

    //incoming
    /*@ApiModelProperty(hidden = true)
    @Relationship(type = "DEPEND_ON",direction = Relationship.INCOMING)
    private Set<Artifact> artifacts;

    //outgoing
    @ApiModelProperty(hidden = true)
    @Relationship(type = "DEPEND_ON")//Direction of the relationship. Defaults to OUTGOING.
    private Set<Artifact> dependencies;*/

    /*@Relationship(type = "HAS_PARENT")
    @ApiModelProperty(hidden = true)
    private Parent parent;

    @Relationship(type = "HAS_LICENSE")
    @ApiModelProperty(hidden = true)
    private Set<License> licenses;

    @Relationship(type = "HAS_ORG")
    @ApiModelProperty(hidden = true)
    private Organization organization;

    @Relationship(type = "DEVELOPED_BY")
    @ApiModelProperty(hidden = true)
    private Set<Developer> developers;*/

    public Artifact() {
        this.availability = true;
        //this.dependencies = new HashSet<>();
        //this.artifacts = new HashSet<>();
        //this.licenses = new HashSet<>();
        //this.developers = new HashSet<>();
    }

    public void updateFrom(Artifact artifact){
        this.groupId = Optional.ofNullable(artifact.groupId).orElse(this.groupId);;
        this.artifactId = Optional.ofNullable(artifact.artifactId).orElse(this.artifactId);
        this.version = Optional.ofNullable(artifact.version).orElse(this.version);
        this.gav = Optional.ofNullable(artifact.gav).orElse(this.gav);
        //commonly,we can only update the availability
        this.availability = Optional.ofNullable(artifact.availability).orElse(this.availability);
        //this.scope = Optional.ofNullable(artifact.scope).orElse(this.scope);
    }
}