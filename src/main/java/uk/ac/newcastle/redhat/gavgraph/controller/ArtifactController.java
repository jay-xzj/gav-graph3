package uk.ac.newcastle.redhat.gavgraph.controller;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.newcastle.redhat.gavgraph.domain.nodes.Artifact;
import uk.ac.newcastle.redhat.gavgraph.exception.NotFoundException;
import uk.ac.newcastle.redhat.gavgraph.repository.ArtifactRepository;
import uk.ac.newcastle.redhat.gavgraph.service.ArtifactService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/artifacts")
@Api(value = "Artifact API")
public class ArtifactController {

    @Resource
    private ArtifactService artifactService;

    @Resource
    private ArtifactRepository artifactRepository;

    private final static Logger logger = LoggerFactory.getLogger(ArtifactController.class);

    @PostMapping("/create")
    @ApiOperation(value = "Add a new Artifact to the database")
    public ResponseEntity<Artifact> create(
            @RequestBody @ApiParam(value = "JSON representation of an artifact to be added to the database", required = true)
                    Artifact artifact) {
        if (artifact == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Artifact save = null;
        try{
            save = artifactService.save(artifact);
            logger.info("An artifact created : " + artifact);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("Exception occurs while creating an artifact : " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
        }
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    @GetMapping("/findAllPagination/{pageSize}/{depth}")
    @ApiOperation(value = "fetch all artifacts", notes = "return a list of artifacts")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 302, message = "Found")
    })
    public ResponseEntity<List<Artifact>> findAllPagination(
            @PathVariable @ApiParam(defaultValue = "1000") int pageSize,
            @PathVariable @ApiParam(defaultValue = "0") int depth){
        List<Artifact> artifacts;
        try {
            artifacts = artifactService.findAllPagination(pageSize,depth);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("Exception occurs while retrieving all artifacts : " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(artifacts, HttpStatus.OK);
    }

    /*@GetMapping("/findAll")
    @ApiOperation(value = "fetch all artifacts", notes = "return a list of artifacts")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 302, message = "Found")
    })
    public ResponseEntity<List<Artifact>> findAll(){
        List<Artifact> artifacts;
        try {
            artifacts = artifactService.findAllZeroDepth();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("Exception occurs while retrieving all artifacts : " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(artifacts, HttpStatus.OK);
    }*/

    @GetMapping("findById/{id}")
    @ApiOperation(value = "find artifact by id",notes = "return an artifact with certain id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "no content"),
            @ApiResponse(code = 302, message = "Not found")
    })
    public ResponseEntity<Artifact> findById(@PathVariable @ApiParam Long id){
        Artifact artifact;
        try{
            artifact = artifactRepository.findById(id).orElseThrow(NotFoundException::new);
        }catch (NotFoundException ne){
            ne.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(artifact, HttpStatus.FOUND);
    }

    @GetMapping("findByGroupId/{groupId}")
    public ResponseEntity<List<Artifact>> findByGroupId(@PathVariable String groupId){
        List<Artifact> artifacts = null;
        try{
            artifacts = artifactService.findByGroupId(groupId);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(artifacts, HttpStatus.OK);
    }

    /**
     * finagle-core_2.11
     * @param artifactId
     * @return
     */
    @GetMapping("findArtifactId/{artifactId}")
    public ResponseEntity<List<Artifact>> findArtifactId(@PathVariable @ApiParam(defaultValue = "finagle-core_2.11") String artifactId){
        List<Artifact> artifacts = null;
        try{
            artifacts = artifactService.findArtifactId(artifactId);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(artifacts, HttpStatus.OK);
    }

    @GetMapping("findArtifactIdLike/{artifactId}")
    public ResponseEntity<List<Artifact>> findArtifactIdLike(@PathVariable String artifactId){
        List<Artifact> artifacts = null;
        try{
            artifacts = artifactService.findArtifactIdLike(artifactId);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(artifacts, HttpStatus.OK);
    }

    @GetMapping("findGroupIdLike/{groupId}")
    public ResponseEntity<List<Artifact>> findGroupIdLike(@PathVariable @ApiParam(defaultValue = "redhat-7") String groupId){
        List<Artifact> artifacts = null;
        try{
            artifacts = artifactService.findGroupIdLike(groupId);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(artifacts, HttpStatus.OK);
    }

    @GetMapping("findByGav/{gav}/{depth}")
    public ResponseEntity<List<Artifact>> findByGav(
            @PathVariable @ApiParam(defaultValue = "org.apache.directory.shared:shared-ldap-client-api:1.0.0-M7") String gav,
            @PathVariable @ApiParam(defaultValue = "1") int depth){
        List<Artifact> artifacts = null;
        try{
            artifacts = artifactService.findByGav(gav,depth);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(artifacts, HttpStatus.OK);
    }

    @GetMapping("/findAllDependOnCurrent/{gav}/{limit}")
    @ApiOperation(value = "fetch all artifacts depend on current one", notes = "return a list of artifacts")
    public ResponseEntity<List<Artifact>> findAllDependOnCurrent(
            @PathVariable @ApiParam(defaultValue = "com.twitter:finagle-core_2.11:6.25.0") String gav,
            //@PathVariable @ApiParam(defaultValue = "1") int hop,
            @PathVariable @ApiParam(defaultValue = "5000")int limit) {
        List<Artifact> artifacts = null;
        try{
            artifacts = artifactService.findAllDependOnCurrent(gav,limit);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(artifacts,HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Long id) {
        artifactRepository.deleteById(id);
    }

    @Transactional
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Artifact update(@PathVariable Long id, @RequestBody Artifact update) {
        final Artifact existing = artifactRepository.findById(id).orElseThrow(NotFoundException::new);
        existing.updateFrom(update);
        return artifactRepository.save(existing);
    }


    /**
     * 解决方案:将红帽自由的项目加上标签 organization:red hat
     * 从数据库里先把这些项目找出来
     * 然后遍历这个list 查询上传的pom里面的某个dependency artifact 比如 log4j:1.2 是否被这个list里面的某个项目所直接或者间接依赖。
     * 然后组成一个dependency gav:[project1:used, project2:used,project3:used] 这样的结果。打印出来即可
     * 如果没有used的项目，进一步，可以只查询有没有同一个artifactId的。那么就可以找到是不是稍微修改一下版本号就可以用了。
     */
    @PostMapping(value="/upload-image",headers="content-type=multipart/form-data")
    @ApiOperation(value = "图片上传", notes = "图片上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organization", value = "redhat", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "deviceId", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "mobilePhone", value = "mobilePhone", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "appName", value = "appName名称", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "cardNo", value = "银行卡号,上传银行卡反面图片时必须填", required = false, dataType = "String", paramType = "query"),
    })
    public ResponseEntity<List<Artifact>> imageUpload(HttpServletRequest request,
                                                      HttpServletResponse response,
                                                      @ApiParam(name = "attach",value = "attach", required = true) MultipartFile attach,
                                                      String org,
                                                      String deviceId,
                                                      String mobilePhone,
                                                      String appName,
                                                      String cardNo){
        //organization:red hat 从数据库里先把这些项目找出来
        //List<Artifact> artifacts = artifactRepository.findAllByOrganization(org);

        //解析上传文件的依赖gav，当做参数传入后台去校验
        //List<String> dependencies = readPom();

        //List <> result = checkUsed(artifacts,dependencies);


        return null;
    }


}
