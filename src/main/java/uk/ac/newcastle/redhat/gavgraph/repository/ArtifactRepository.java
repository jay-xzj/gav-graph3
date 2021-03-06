package uk.ac.newcastle.redhat.gavgraph.repository;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.springframework.data.domain.Sort;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.newcastle.redhat.gavgraph.domain.nodes.Artifact;

import java.util.List;

@Transactional
@Repository
public interface ArtifactRepository extends CrudRepository<Artifact, Long> {

    @Query("MATCH (a:Artifact) WHERE a.groupId = $groupId RETURN a")
    List<Artifact> getAllByGroupId(String groupId);

    @Query("MATCH (a:Artifact) WHERE a.gav = $gav RETURN a")
    List<Artifact> getAllByGav(String gav);

    @Query("MATCH (a:Artifact) WHERE a.artifactId = $artifactId RETURN a")
    List<Artifact> getAllByArtifactId(String artifactId);

    @Query("MATCH (a:Artifact) WHERE a.artifactId CONTAINS $artifactId RETURN a")
    List<Artifact> getAllByArtifactIdContains(String artifactId);

    @Query("MATCH (a:Artifact) WHERE a.groupId CONTAINS $groupId RETURN a")
    List<Artifact> getALlByGroupIdContains(String groupId);

    /*@Query("MATCH (connected)-[:DEPEND_ON*]->(root:Artifact {gav: $gav})\n" +
            "WHERE root <> connected RETURN distinct connected skip ($pageSize * $pageNo) limit $pageSize")*/
    @Query("MATCH (a:Artifact {gav:$gav})\n" +
            "CALL apoc.path.subgraphNodes(a, {relationshipFilter:'<DEPEND_ON', labelFilter:'>Artifact'}) YIELD node\n" +
            "RETURN node skip ($pageSize * $pageNo) limit $pageSize")
    List<Artifact> findAllDependOnCurrent(String gav,int pageSize,int pageNo);

    @Query("MATCH (a:Artifact {gav:$gav}) MATCH (end:Artifact) WHERE end.version CONTAINS $org WITH a, collect(end) AS endNodes " +
            "CALL apoc.path.subgraphNodes(a, {relationshipFilter:'<DEPEND_ON', labelFilter:'>Artifact', endNodes: endNodes})" +
            "YIELD node RETURN node skip($pageSize * $pageNo) limit $pageSize")
    List<Artifact> findAllDependOnCurrentBelongToOrg(String gav,int pageSize,int pageNo,String org);

    List<Artifact> findDependOnByArtifactId(String artifactId, Sort sort);

    @Query("MATCH (a:Artifact {gav:$gav}) return ID(a)")
    Long getIdByGav(String gav);

    @Query("MATCH (a:Artifact) WHERE a.gav =~ $queryGav RETURN a.gav")
    List<String> findArtifactMatchOrg(String queryGav);

    @Query("MATCH (a:Artifact) RETURN distinct a.gav skip ($pageSize * $pageNo) limit $pageSize")
    List<String> findAllPagination(int pageSize,int pageNo);

    @Query("MATCH (connected)-[:DEPEND_ON*]->(root:Artifact {gav: $gav})\n" +
            "WHERE root <> connected RETURN distinct connected skip ($pageSize * $pageNo) limit $pageSize")
    List<Artifact> findAllDependOnCurrentPerformanceTest(String gav, int pageSize, int pageNo);

    @Query("MATCH (connected)-[:DEPEND_ON*]->(root:Artifact {gav: $gav})\n" +
            "WHERE root <> connected RETURN distinct connected")
    int countDependOnCurrent(String gav);

    /*@Query("MATCH (connected)-[:DEPEND_ON*$hop]->(root:Artifact {gav: $gav})\n" +
            "WHERE root <> connected RETURN distinct connected")
    List<Artifact> findAllDependOnCurrent(String gav,int hop,int limit);*/
}
