package uk.ac.newcastle.redhat.gavgraph.service;

import org.apache.maven.model.Model;
import uk.ac.newcastle.redhat.gavgraph.domain.nodes.Artifact;

import java.util.List;
import java.util.Map;

public interface ArtifactService {

    List<Artifact> findAllPagination(int pageSize, int depth);

    List<Artifact> findAllZeroDepth();

    Artifact save(Artifact artifact);

    List<Artifact> findByGroupId(String groupId);

    List<Artifact> findArtifactId(String artifactId);

    List<Artifact> findArtifactIdLike(String artifactId);

    List<Artifact> findGroupIdLike(String groupId);

    List<Artifact> findByGav(String gav,int depth);

    //List<Artifact> findAllDependOnCurrent(String gav,int hop, int limit);

    List<Artifact> findAllDependOnCurrent(String gav,int pageSize,int pageNo);

    List<Artifact> findAllDependOnCurrentPerformanceTest(String gav,int pageSize,int pageNo);

    List<Map<String,Object>> analysePomDependencies(Model model,String orgName);

    List<Artifact> findAllDependOnCurrentV2(String gav);
}
