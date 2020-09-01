package uk.ac.newcastle.redhat.gavgraph;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.newcastle.redhat.gavgraph.domain.nodes.Artifact;
import uk.ac.newcastle.redhat.gavgraph.repository.ArtifactRepository;
import uk.ac.newcastle.redhat.gavgraph.service.ArtifactService;

import java.util.List;
import java.util.Optional;

public class SimpleQueryTest extends GavGraphApplicationTests {
    @Autowired
    private ArtifactService service;

    @Autowired
    private ArtifactRepository repository;

    @Test
    public void testFindByGav(){
        Assert.assertEquals(service.findByGav("org.hdrhistogram:HdrHistogram:2.1.4",0).get(0).getArtifactId(),"HdrHistogram");
    }

    @Test
    public void testFindByArtifact(){
        List<Artifact> list = service.findArtifactId("HdrHistogram");
        Assert.assertNotNull(list);
        String artifactId = list.get(0).getArtifactId();
        Assert.assertEquals("HdrHistogram",artifactId);
    }

    @Test
    public void testFindByGroupId(){
        List<Artifact> list = service.findByGroupId("org.hdrhistogram");
        Assert.assertNotNull(list);
        String groupId = list.get(0).getGroupId();
        Assert.assertEquals("org.hdrhistogram",groupId);
    }

    @Test
    public void testFindArtifactIdLike(){
        List<Artifact> list = service.findArtifactIdLike("Histogram");
        Assert.assertNotNull(list);
        String artifactId = list.get(0).getArtifactId();
        Assert.assertTrue(artifactId.contains("Histogram"));
    }

    @Test
    public void testSaveAndDeleteAndUpdate(){
        Artifact artifact = new Artifact();
        artifact.setGroupId("org.test");
        artifact.setArtifactId("test");
        artifact.setVersion("1.1.1");
        artifact.setAvailability(true);
        artifact.setGav("org.test:test:1.1.1");
        Artifact save = service.save(artifact);
        Long id = save.getId();
        save.setVersion("2.2.2");
        service.save(save);
        Optional<Artifact> optional = repository.findById(id);
        Artifact find = optional.get();
        Assert.assertEquals("2.2.2",find.getVersion());
        repository.deleteById(id);
        Optional<Artifact> optional2 = repository.findById(id);
        Assert.assertSame(Optional.empty(),optional2);
    }

}
