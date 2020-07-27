package uk.ac.newcastle.redhat.gavgraph.impl;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import uk.ac.newcastle.redhat.gavgraph.domain.nodes.Artifact;
import uk.ac.newcastle.redhat.gavgraph.repository.ArtifactRepository;
import uk.ac.newcastle.redhat.gavgraph.repository.MyNeo4jRepository;
import uk.ac.newcastle.redhat.gavgraph.service.ArtifactService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArtifactServiceImpl extends MyNeo4jRepository<Artifact> implements ArtifactService {

    @Resource
    private ArtifactRepository artifactRepository;

    private final String LABEL = "Artifact";

    public ArtifactServiceImpl() {
        super(Artifact.class);
    }

    /**
     * pagination is
     * @return
     */
    @Override
    public List<Artifact> findAllPagination(int pageSize,int depth){
        //final int pageSize = 10;
        final int count = getCount();
        int pageCount = (count % pageSize) == 0 ? (count/pageSize) : ((count/pageSize)+1);
        List<Artifact> list = new ArrayList<>();

        //pageNumber = i
        //query all
        /*for (int i = 0; i < pageCount; i++) {
            int pageNumber = i;
            //int skip = (i-1)*pageSize;//在pagination的方法里会去计算
            //The offset, if present, determines how many records to skip.
            // Otherwise, pageNumber * pageSize records are skipped.
            Iterable<Artifact> page = getPage(pageNumber, pageSize,0);
            List<Artifact> artifacts = Lists.newArrayList(page);
            list.addAll(artifacts);
        }*/

        //query One page（OOM：因为把所有的depend on也查出来了）
        Iterable<Artifact> page = getPage(0, pageSize,depth);
        List<Artifact> artifacts = Lists.newArrayList(page);
        list.addAll(artifacts);


        /*Map<String, Integer> params = new HashMap<>();
        params.put("skip", 0);
        params.put("limit", pageSize);

        Result result = neo4jSession.query("MATCH (a:" + LABEL + ") return a.gav as gav SKIP $skip Limit $limit", params);
        Iterator<Map<String, Object>> it = result.iterator();
        while(it.hasNext()){
            Map<String, Object> next = it.next();
            System.out.println(next.get("gav"));
        }*/
        return list;
    }

    public static void main(String[] args) {
        int a = 65;
        int b = 10;
        System.out.println(a/b);
    }

    @Override
    public List<Artifact> findAllZeroDepth() {
        return (List<Artifact>) artifactRepository.findAll();
    }

    @Override
    public Artifact save(Artifact artifact) {
        return artifactRepository.save(artifact);
    }

    @Override
    public List<Artifact> findByGroupId(String groupId) {
        return artifactRepository.getAllByGroupId(groupId);
    }

    @Override
    public List<Artifact> findArtifactId(String artifactId) {
        List<Artifact> artifacts = artifactRepository.getAllByArtifactId(artifactId);
        return artifacts;
    }

    @Override
    public List<Artifact> findArtifactIdLike(String artifactId) {
        return artifactRepository.getAllByArtifactIdContains(artifactId);
    }

    @Override
    public List<Artifact> findGroupIdLike(String groupId) {
        return artifactRepository.getALlByGroupIdContains(groupId);
    }

    @Override
    public List<Artifact> findByGav(String gav,int depth) {
        Artifact entity = neo4jSession.load(Artifact.class, getIdByGav(gav),depth);
        //return artifactRepository.getAllByGav(gav);
        //Artifact entity = getSingleEntity(gav);
        List<Artifact> artifacts = new ArrayList<>();
        artifacts.add(entity);
        return artifacts;
    }

    private Long getIdByGav(String gav) {
        return artifactRepository.getIdByGav(gav);
    }

    /*@Override
    public List<Artifact> findAllDependOnCurrent(String gav, int hop, int limit) {
        return artifactRepository.findAllDependOnCurrent(gav,hop,limit);
    }*/

    @Override
    public List<Artifact> findAllDependOnCurrent(String gav,int limit) {
        return artifactRepository.findAllDependOnCurrent(gav,limit);
    }
}
