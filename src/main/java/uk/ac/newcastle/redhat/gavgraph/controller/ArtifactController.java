package uk.ac.newcastle.redhat.gavgraph.controller;

import io.swagger.annotations.*;
import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.newcastle.redhat.gavgraph.domain.nodes.Artifact;
import uk.ac.newcastle.redhat.gavgraph.exception.NotFoundException;
import uk.ac.newcastle.redhat.gavgraph.pom.util.PomUtil2;
import uk.ac.newcastle.redhat.gavgraph.pom.util.WriteCsvTool2;
import uk.ac.newcastle.redhat.gavgraph.repository.ArtifactRepository;
import uk.ac.newcastle.redhat.gavgraph.service.ArtifactService;
import uk.ac.newcastle.redhat.gavgraph.util.FileDownloadAndUploadUtil;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/findAllDependOnCurrent/{gav}/{pageSize}/{pageNo}")
    @ApiOperation(value = "fetch all artifacts depend on current one", notes = "return a list of artifacts")
    public ResponseEntity<List<Artifact>> findAllDependOnCurrent(
            @PathVariable @ApiParam(defaultValue = "com.twitter:finagle-core_2.11:6.25.0") String gav,
            @PathVariable @ApiParam(defaultValue = "10") int pageSize,
            @PathVariable @ApiParam(defaultValue = "1") int pageNo
            //@PathVariable @ApiParam(defaultValue = "5000")int limit
            ) {
        List<Artifact> artifacts = null;
        try{
            artifacts = artifactService.findAllDependOnCurrent(gav, pageSize, (pageNo >= 1?(pageNo-1):0));
            //artifacts = artifactService.findAllDependOnCurrentPerformanceTest(gav, pageSize, (pageNo >= 1?(pageNo-1):0));
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

    @Autowired
    private Environment environment;

    /**
     * 解决方案:
     * 红帽内部自研版本会在version后面带有redhat字样，
     * 我们只要根据当前的gav找到 相同version并且后面带redhat suffix的即可
     */
    @PostMapping(value="/pomUploadAndAnalyse",headers="content-type=multipart/form-data")
    @ApiOperation(value = "pom uploading", notes = "pom uploading")
    public String pomUploadAndAnalyse(
        HttpServletResponse response,
        HttpServletRequest request,
        @RequestParam("file") @ApiParam(name = "file",value = "file", required = true) MultipartFile file,
        @RequestParam("orgName") @ApiParam(defaultValue = "redhat") String orgName) throws IOException {
        String result = "failed";
        //上传pom的路径，也是下载生成的excel报告的路径
        String uploadDir = environment.getProperty("upload.dir");
        try {
            if (file.isEmpty()) {
                //上传pom文件为空
                return "";
            }

            String uploadRes = FileDownloadAndUploadUtil.upload(uploadDir, file);
            String originalFilename = file.getOriginalFilename();
            File pomFile = null;
            Model model = null;
            if (originalFilename.endsWith("pom")||originalFilename.endsWith("xml")) {
                pomFile = new File(uploadDir + File.separator + file.getOriginalFilename());
                model = PomUtil2.getPomModel(pomFile);
            }else{
                throw new IllegalArgumentException("File with unsupported suffix.");
            }
            List<Map<String,Object>> reportData = artifactService.analysePomDependencies(model,orgName);

            //generate report
            String reportName = "pom_analyse_report_"+orgName+"_"+System.currentTimeMillis()+".csv";
            String filePath = uploadDir+File.separator+reportName;
            File downloadFile = new File(filePath);
            if (!downloadFile.getParentFile().exists()){
                downloadFile.getParentFile().mkdirs();
            }
            //解析上传文件的依赖gav，当做参数传入后台去校验
            String[] displayColNames = {"origin","in-house"};
            String[] fieldNames = {"origin","in-house"};
            WriteCsvTool2.writeCvs(filePath,reportData, displayColNames,fieldNames );

            if (!downloadFile.exists()){
                //没有生成相应的报告
                return "analyse failed!";
            }

            // 设置信息给客户端不解析
            String type = new MimetypesFileTypeMap().getContentType(reportName);
            // 设置contenttype，即告诉客户端所发送的数据属于什么类型
            response.setHeader("Content-type",new MimetypesFileTypeMap().getContentType(reportName));
            response.setContentType("application/octet-stream");
            // 设置编码
            String encoded = new String(reportName.getBytes("utf-8"), "iso-8859-1");
            // 设置扩展头，当Content-Type 的类型为要下载的类型时 , 这个信息头会告诉浏览器这个文件的名字和类型。
            response.setHeader("Content-Disposition", "attachment;filename=" + encoded);
            result = FileDownloadAndUploadUtil.download(downloadFile,response);

        }catch (Exception e){
            e.printStackTrace();
            return result;
        }

        return result;
    }


}
