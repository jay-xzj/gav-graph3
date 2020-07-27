package uk.ac.newcastle.redhat.gavgraph.pom.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import uk.ac.newcastle.redhat.gavgraph.pom.Constant;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

//SWITCH PATH CHECK √ path -> gav不用管 gav -> path 要管
public class PomUtil2 {

    public static void recur(File file, List<File> pomFiles) {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~ START 从 ["+file.getAbsolutePath()+"]中递归寻找文件 extractData ~~~~~~~~~~~~~~~~~~~~~");
        File[] files = file.listFiles();
        if (files != null && files.length != 0) {
            for (File f : files) {
                if (f.isDirectory()) {
                    recur(f,pomFiles);
                } else if (f.isFile() && f.getName().endsWith(".pom")) {
                    pomFiles.add(f);
                }
            }
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~ END 从 ["+file.getAbsolutePath()+"]中递归寻找文件 extractData ~~~~~~~~~~~~~~~~~~~~~");
    }
    public static void recur2(File file, Queue<File> queue) {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~ START 从 ["+file.getAbsolutePath()+"]中递归寻找文件 extractData ~~~~~~~~~~~~~~~~~~~~~");
        File[] files = file.listFiles();
        if (files != null && files.length != 0) {
            for (File f : files) {
                if (f.isDirectory()) {
                    recur2(f,queue);
                } else if (f.isFile() && f.getName().endsWith(".pom")) {
                    queue.offer(f);
                }
            }
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~ END 从 ["+file.getAbsolutePath()+"]中递归寻找文件 extractData ~~~~~~~~~~~~~~~~~~~~~");
    }

    /**
     * artfactId 也有可能包含EL表达式，并且不止一个
     * artifactId 有可能是${A}.${B}的形式
     * A和B可能又是EL表达式，也需要去properties里面查找。
     *
     * @param file
     * @return
     */
    public static String getArtifactId(File file, Model model){
        System.out.println("------------START getArtifactId ------------");
        //Model model = getPomModel(file);
        String artifactId = model.getArtifactId();
        if(artifactId == null||artifactId.contains("$")) {
            System.err.println("~~~~~~~~~~~~~~artifactId is null~~~~~~~~~~~~~~~");
            //version
            File parentFile1 = file.getParentFile();
            //artifactId
            File parentFile2 = parentFile1.getParentFile();
            String absolutePath = parentFile2.getAbsolutePath();
            File parentFile3 = parentFile2.getParentFile();
            artifactId = absolutePath.substring(parentFile3.getAbsolutePath().length() + 1);
        }

        System.out.println("------------END getArtifactId: artifactId = "+artifactId+" ------------");
        return artifactId;
    }

    /**
     * get groupId
     * @param file
     * @return
     */

    public static String getGroupId(File file,Model model,String rootPath){
        System.out.println("------------START getGroupId ------------");

        //String path = Constant.ALL_POMS_PATH;
        //Model model = getPomModel(file);
        String groupId = model.getGroupId();
        if(groupId==null||groupId.contains("$")) {
            System.err.println("~~~~~~~~~~~~~~~~groupId is null~~~~~~~~~~~~~~~~~");
            //version
            File parentFile1 = file.getParentFile();
            //artifactId
            File parentFile2 = parentFile1.getParentFile();
            //groupId
            File parentFile3 = parentFile2.getParentFile();
            String absolutePath = parentFile3.getAbsolutePath();
            groupId = absolutePath.substring(rootPath.length() + 1).replaceAll("\\\\","\\.");
        }
        System.out.println("------------END getGroupId : groupId = "+groupId+"------------");
        return groupId;
    }

    /**
     * ${project.groupId}
     * 发现又groupId也是${}
     * @param model
     * @return
     */
    //TODO
    /*public static String resolveGroupId(Model model) {
        String groupId = model.getGroupId();
        System.err.println("============= START resolveGroupId  model = "+model+", GroupId = "+groupId+"==============");
        //System.out.println(artifactId);
        *//*if (artifactId == null){
            return null;
        }*//*
        String[] split = null;
        try {
            split = artifactId.split("\\$");
        }catch (Exception e) {
            System.out.println(model);
        }
        Map<String, Map<String,String>> keyMaps = new HashMap<>();
        //List<Map<String, String>> list = new ArrayList<>();
        //Map<String, String> keyMaps = new HashMap<>();
        for (String str : split) {
            if (str.contains("{")) {
                int b = str.indexOf("{");
                int e = str.indexOf("}");
                String key = str.substring(b + 1, e);
                Map<String,String> map = new HashMap<String, String>();
                String ELKey = "${" + key + "}";
                map.put(ELKey, key);
                //list.add(map);
                map.put(ELKey, key);
                keyMaps.put(ELKey, map);
            }
        }

        //去寻找key的值
        for(Map.Entry<String,Map<String,String>> en: keyMaps.entrySet()){
            String ELkey = en.getKey();
            //String key = en.getValue();
            Map<String, String> value = en.getValue();
            String key = value.get(ELkey);
            //调用查找version的方法
            String version = getVersionWithELExpression(model, key);
            //value.put(ELkey,version);
            //value.remove(ELkey);
            if (version == null){
                return null;
            }else{
                artifactId = artifactId.replace(ELkey, version);
            }
        }
        // ${key}:key -> value -> ${key}:value
        return groupId;
    }*/

    /**
     *  special version value： project.version, project.parent.version, parent.version, version
     * get version
     * @param file
     * @return
     */
    public static String getVersion(File file,Model model){
        System.out.println("------------START getVersion ------------");
        String version = model.getVersion();
        if(version == null||version.contains("$")) {
            System.err.println("~~~~~~~~~~~~~~~~version is null~~~~~~~~~~~~~~~~~");
            //version
            File parentFile1 = file.getParentFile();
            String absolutePath = parentFile1.getAbsolutePath();
            //artifactId
            File parentFile2 = parentFile1.getParentFile();
            version = absolutePath.substring(parentFile2.getAbsolutePath().length() + 1).replaceAll("\\\\","\\.");
        }
        System.out.println("------------END getVersion = "+version+"------------");
        return version;
    }

    /**
     * 如果找不到就要去父项目里面找，有可能在另一个路径下
     */
    public static String resolveArtifactId(Model model) {
        String artifactId = model.getArtifactId();
        System.err.println("============= START resolveArtifactId  model = "+model+", artifactId = "+artifactId+"==============");
        //System.out.println(artifactId);
        /*if (artifactId == null){
            return null;
        }*/
        String[] split = null;
        try {
            split = artifactId.split("\\$");
        }catch (Exception e) {
            System.out.println(model);
        }
        Map<String, Map<String,String>> keyMaps = new HashMap<>();
        //List<Map<String, String>> list = new ArrayList<>();
        //Map<String, String> keyMaps = new HashMap<>();
        for (String str : split) {
            if (str.contains("{")) {
                int b = str.indexOf("{");
                int e = str.indexOf("}");
                String key = str.substring(b + 1, e);
                Map<String,String> map = new HashMap<String, String>();
                String ELKey = "${" + key + "}";
                map.put(ELKey, key);
                //list.add(map);
                map.put(ELKey, key);
                keyMaps.put(ELKey, map);
            }
        }

        //去寻找key的值
        for(Map.Entry<String,Map<String,String>> en: keyMaps.entrySet()){
            String ELkey = en.getKey();
            //String key = en.getValue();
            Map<String, String> value = en.getValue();
            String key = value.get(ELkey);
            //调用查找version的方法
            String version = getVersionWithELExpression(model, key);
            //value.put(ELkey,version);
            //value.remove(ELkey);
            if (version == null){
                return null;
            }else{
                artifactId = artifactId.replace(ELkey, version);
            }
        }

        System.err.println("============= END resolveArtifactId  ==============");
        // ${key}:key -> value -> ${key}:value
        return artifactId;
    }

    /**
     * get version from other tags in this pom which is represented by EL Expression
     * @param model
     * @param key
     * @return
     */
    private static String getVersionWithELExpression(Model model, String key) {
        System.out.println("============= START getVersionWithELExpression Model = "+model+" , key = "+key+"==============");

        //用EL表达式表示的
        //String key = getELKey(version);
        String version = null;
        if (key.equalsIgnoreCase("project.version") || key.equalsIgnoreCase("version")) {
            String pversion = model.getVersion();
            if (pversion != null) {
                version = pversion;
            }else{
                org.apache.maven.model.Parent parent = model.getParent();
                if (parent != null) {
                    version = parent.getVersion();
                }
            }
        }if (key.equalsIgnoreCase("project.parent.version") || key.equalsIgnoreCase("parent.version")) {
            org.apache.maven.model.Parent parent = model.getParent();
            if (parent != null) {
                version = parent.getVersion();
            }
        } else{
            //get version from properties

            /*Properties properties = model.getProperties();
            if (properties != null&&properties.size() > 0){
                version = (String) properties.get(key);
            }
            if(version == null)
                //version is managed in parent's properties
                version = getELVersionByQueryingParentPom(model,key);*/

            //EL表示的版本号可能被管理在properties里面
            //get version from properties
            Properties properties = model.getProperties();
            if (properties != null&&properties.size() > 0){
                version = (String) properties.get(key);
                //找不到properties里的就要去parentPom里面去找
                if(version == null) {
                    //version is managed in parent's properties
                    //SWITCH PATH CHECK √
                    version = getELVersionByQueryingParentPom(model, key);
                } else {
                    //递归调用该方法，直到version里面不包含EL表达式
                    //if (version.contains("$")){
                    /*
                    <scala.major.version>2</scala.major.version>
                    <scala.minor.version>12</scala.minor.version>
                    <scala.maintenance.version>2</scala.maintenance.version>
                    <scala.major.minor.version>${scala.major.version}.${scala.minor.version}</scala.major.minor.version>
                    <scala-library.version>${scala.major.minor.version}.${scala.maintenance.version}</scala-library.version>
                     */
                    version = resolveEmbeddedVersionInProperties(model,properties, version);
                    //}
                }
            }
        }
        System.out.println("============= END getVersionWithELExpression ==============");
        return version;
    }

    //SWITCH PATH CHECK √
    private static String getELVersionByQueryingParentPom(Model model,String key) {
        System.out.println("============= START getELVersionByQueryingParentPom Model = "+model+" , key = "+key+"==============");
        //SWITCH PATH CHECK √
        List<Model> models = getParentModelList(model,new ArrayList<Model>());

        if (models.isEmpty()){
            return null;
        }

        //list的头部是当前pom的parent，第二个是parent的parent；所以在parent里面拿到了version就不用去parent的parent里面再去遍历了
        for (int i = 0; i < models.size(); i++) {
            Model pmodel = models.get(i);
            //因为这边直接是EL表达式，所以肯定是维护在properties里面了
            Properties properties = pmodel.getProperties();
            if (properties != null){
                String version = (String)properties.get(key);
                if(version != null) {
                    return version;
                }
            }
        }
        System.out.println("============= START getELVersionByQueryingParentPom ==============");

        //代码走到这，表示没有找到
        return null;
    }

    private static String resolveEmbeddedVersionInProperties(Model model,Properties properties, String version) {
        if (version.contains("$")) {
            String[] split = version.split("\\$");
            Map<String, Map<String, String>> keyMaps = new HashMap<>();
            for (String str : split) {
                if (str.contains("{")) {
                    int b = str.indexOf("{");
                    int e = str.indexOf("}");
                    String k1 = str.substring(b + 1, e);
                    Map<String, String> map = new HashMap<String, String>();
                    String ELKey = "${" + k1 + "}";
                    map.put(ELKey, k1);
                    //list.add(map);
                    keyMaps.put(ELKey, map);
                }
            }

            //去寻找key的值
            for (Map.Entry<String, Map<String, String>> en : keyMaps.entrySet()) {
                String ELkey = en.getKey();
                Map<String, String> value = en.getValue();
                String k2 = value.get(ELkey);
                System.out.println(k2);
                //调用查找version的方法
                String v2 = (String) properties.get(k2);
                if(k2.equalsIgnoreCase("project.parent.version")){
                    org.apache.maven.model.Parent parent = model.getParent();
                    if (parent != null) {
                        v2 = parent.getVersion();
                    }
                }
                if (v2 == null) {
                    return null;
                } else {
                    version = version.replace(ELkey, v2);
                }
            }
            version = resolveEmbeddedVersionInProperties(model,properties, version);
        }
        return version;
    }

    /**
     * recursion to find the parentModel List
     * @param model
     * @param models
     * @return
     */
    //SWITCH PATH CHECK √
    private static List<Model> getParentModelList(Model model, List<Model> models) {
        System.out.println("============= START getELVersionByQueryingParentPom Model = "+model+" , models = "+models+"==============");

        //SWITCH PATH CHECK √
        String patentAbsPath = combineParentPath(model);
        if (patentAbsPath == null){
            return models;
        }
        File file = new File(patentAbsPath);
        if (!file.exists()) {
            return models;
        }
        Model parentModel = getPomModel(file);
        models.add(parentModel);
        getParentModelList(parentModel,models);
        System.out.println("============= END getELVersionByQueryingParentPom Model = "+model+" , models = "+models+"==============");
        return models;
    }

    public static void main(String[] args) {
        String gav = "E:\\poms\\com\\microsoft\\msr\\malmo\\MalmoJavaJar\\0.30.0\\MalmoJavaJar-0.30.0.pom";
        /*String groupId = getGroupId(new File(gav));
        String version = getVersion(new File(gav));
        Model pomModel = getPomModel(new File(gav));
        String parent = combineParentPath(pomModel);
        System.out.println(parent);
        System.out.println(groupId);
        System.out.println(version);*/
        Model pomModel = getPomModel(new File(gav));
        //String artifactId = pomModel.getArtifactId();
        String artifactId = getArtifactId(new File(gav),pomModel);
        String[] split = artifactId.split("\\$");
        for (String string : split){
            System.out.println(string);
        }
    }

    public static Model getPomModel(File pom) {
        Model model = null;
        try {
            MavenXpp3Reader pomReader = new MavenXpp3Reader();
            FileReader fileReader = new FileReader(pom);
            model = pomReader.read(fileReader);//XmlPullParserException: only whitespace content allowed before start tag and not \u9518 (position: START_DOCUMENT seen \u9518... @1:1)
            fileReader.close();
        } catch (IOException | org.codehaus.plexus.util.xml.pull.XmlPullParserException xe) {
            System.out.println("报错XmlPullParserException的路径是："+pom.getAbsolutePath());
            xe.printStackTrace();
        }
        return model;
    }

    //SWITCH PATH CHECK √
    private static String combineParentPath(Model model) {
        //这边就不能纯粹的去直接拼接了，有可能切换的
        //String path = "C:\\Users\\jayxu\\Desktop\\test";
        org.apache.maven.model.Parent parent = model.getParent();
        String groupId = "";
        String artifactId = "";
        String version = "";
        String ga = "";
        //证明存在parent pom，要去拼接查找
        if(parent != null) {
            groupId = parent.getGroupId();
            artifactId = parent.getArtifactId();
            version = parent.getVersion();
            ga = groupId.replaceAll("\\.","\\\\") + "\\" + artifactId;
        }
        String pathStr1 = Constant.ROOT_PATH_1 + "\\" + ga + "\\" + version + "\\" + artifactId + "-" + version + ".pom";
        String pathStr2 = Constant.ROOT_PATH_2 + "\\" + ga + "\\" + version + "\\" + artifactId + "-" + version + ".pom";
        File file1 = new File(pathStr1);
        return file1.exists() ? pathStr1:pathStr2;
    }

    public static boolean filterConditions(String artifactId, String groupId, String version) {
        return StringUtils.isNotBlank(artifactId) && StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(version);
    }


    //public static List<Map<String, Object>> extractData(File file,String type) {
    public static List<Map<String,Object>> extractData(File file,String type) {
        System.out.println("~~~~~~~~~~~~~~~~ START extractData file = " + file + "~~~~~~~~~~~~~~~~~~~ ");

        List<Map<String,Object>> datas = new ArrayList<>();

        System.err.println("=== START 开始获取文件中的数据，当前处理的文件是 === " + file.getAbsolutePath());
        Model model = getPomModel(file);
        if (model != null) {
            String artifactId = getArtifactId(file,model);
            //SWITCH PATH CHECK √ ROOT_PATH_1 2长度一样
            String groupId = getGroupId(file,model,Constant.ROOT_PATH_1);
            String version = getVersion(file,model);
            //去除packaging是bundle的,我可以认为bundle的是OSGI的内容，而不去管它么？如果可以，那应该要去除;还是只要jar war这种
            //String packaging = model.getPackaging();
            String packaging = getPackaging(model);
            String combination = groupId + ":" + artifactId + ":" + version;
            if (filterConditions(artifactId, groupId, version)) {
                try {
                    switch (type) {
                        case Constant.ARTIFACT_ALL:
                            List<Map<String,Object>> subdatas1 = generateArtifactFile(groupId, artifactId, version, packaging);
                            System.err.println("datas.size = " + subdatas1.size());
                            if (subdatas1.size() > 0) {
                                datas.addAll(subdatas1);
                            }
                            break;
                        case Constant.DEPEND_ON_ALL:
                            List<Map<String,Object>> subdatas2 = generateDependOnDatas(file, model, combination, "C:\\Users\\jayxu\\Desktop\\test\\" + Thread.currentThread().getId() + ".csv");
                            System.err.println("datas.size = " + subdatas2.size());
                            if (subdatas2.size() > 0) {
                                datas.addAll(subdatas2);
                            }
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    model = null;
                }
            }
        }
        System.err.println("=== END 结束获取文件中的数据，处理结束 === ");

        System.out.println("~~~~~~~~~~~~~~~~ END extractData file = " + file + "~~~~~~~~~~~~~~~~~~~ ");
        return datas;
    }

    /**
     * generate dependOn relationships' File
     * @param file
     * @param model
     * @param acom
     * @param fileName
     * @throws Exception
     */
    private static List<Map<String,Object>> generateDependOnDatas(File file, Model model, String acom, String fileName) throws Exception {
        System.out.println("===========  START generateDependOnDatas 【"+file.getAbsolutePath()+"】 ==============");
        //dependencies
        List<Dependency> dependencies = model.getDependencies();
        //Properties properties = model.getProperties();
        List<Map<String,Object>> dataList = new ArrayList<>();

        Optional<List<Dependency>> dopt = Optional.ofNullable(dependencies);
        if(dopt.isPresent()&& dependencies.size()>0){
            //List<Map<String, Object>> datas = new ArrayList<>();
            //List<Map<String, Object>> incompletedatas = new ArrayList<>();

            //poms' paths which is without version
            //artifact's gav_dependency's artifactId
            Set<String> paths = new HashSet<>();

            for (Dependency d:dependencies) {
                String groupId = d.getGroupId();
                String artifactId = d.getArtifactId();
                String version = d.getVersion();
                //version is non-null : represented by EL expression; directly showed; omitted in current pom due to managed in parent pom
                try{
                    if (artifactId.contains("$")) {
                        artifactId = resolveArtifactId(model);
                    }
                }catch (Exception e) {
                    System.out.println("空指针==="+ file.getAbsolutePath()+""+ groupId+""+artifactId+""+version);
                }
                if (artifactId != null) {
                    version = resolveVersion(model, artifactId, version);
                }else{
                    //System.out.println(file.getAbsolutePath());
                    //将其加入未找到版本的csv里面
                    continue;
                }
                //if the version is not found, then give up on this dependency and store the path into txt file.
                if (version == null){
                    //prepare the data for recode
                    /*Map<String,Object> map = new HashMap<>();
                    map.put("pom_gav",pathToGav(file.getAbsolutePath()));
                    map.put("pom_path",file.getAbsolutePath());
                    map.put("dependency_aid",d.getArtifactId());
                    incompletedatas.add(map);*/
                    //System.out.println(file.getAbsolutePath());
                }else {
                    //替换逗号
                    if (version.contains(",")){
                        version = version.replaceAll(",","--");
                    }
                    if (artifactId.contains(",")){
                        artifactId = artifactId.replaceAll(",","--");
                    }

                    String dcom = groupId + ":" + artifactId + ":" + version;
                    Map<String,Object> map = new HashMap<>();
                    map.put("from",acom);
                    map.put("to", dcom);
                    /*map.put("groupId", groupId);
                    map.put("artifactId", artifactId);
                    map.put("version", "s_"+version);*/
                    dataList.add(map);

                    /*List<Object> rowList = new ArrayList<Object>();
                    String dcom = groupId + ":" + artifactId + ":" + version;
                    Object[] row = new Object[5];
                    row[0] = acom;
                    row[1] = dcom;
                    row[2] = groupId;
                    row[3] = artifactId;
                    row[4] = "s_"+version;
                    for(int j=0;j<row.length;j++){
                        rowList.add(row[j]);
                    }
                    dataList.add(rowList);*/

                    /*String dcom = groupId + ":" + artifactId + ":" + version;
                    Map<String, Object> map = new HashMap<>();
                    map.put("from", acom);
                    map.put("to", dcom);
                    map.put("groupId", groupId);
                    map.put("artifactId", artifactId);
                    map.put("version", "s_" + version);
                    datas.add(map);*/
                }
            }

            /*String[] displayColNames = {"from","to","groupId","artifactId","version"};
            String[] fieldNames = {"from","to","groupId","artifactId","version"};

            System.err.println("datas.size = " + datas.size());*/

            //WriteCsv.writeCvsV2(fileName, "name", datas, displayColNames, fieldNames);
            //save the paths into the database
            /*String[] displayColNames1 = {"pom_gav","pom_path","dependency_aid"};
            String[] fieldNames1 = {"pom_gav","pom_path","dependency_aid"};
            WriteCsv.writeCvs(incomplete_dependency, incompletedatas, displayColNames1, fieldNames1, true, true);*/
        }
        System.out.println("===========  START generateDependOnDatas 【"+file.getAbsolutePath()+"】, datas.size 【"+dataList.size()+"】 ==============");
        return dataList;
    }

    public static String resolveVersion(Model model, String artifactId, String version) throws SQLException {
        if (version != null) {
            /*
            1. 直接有值直接拿
            2. 如果是EL表达式，里面是project.version就去取当前artifact的version，如果当前artifact没有version就去拿它的路径的版本号。
            3. 如果EL表达式里面不是project.version，就去properties里面去取
            1. 如果是用EL表达式在dependencyManagement里面或者直接在dependencies里面的话，那直接去properties里面取值即可
             */
            //当前的版本号可以从properties里面取，或者是等同于project的version
            if (version.contains("$")) {//this means version should be found in properties
                String key = getELKey(version);
                version = getVersionWithELExpression(model, key);
            }else if(version.contains(",")){
                //替换逗号
                version = version.replaceAll(",","--");
            }
        }else {
            //if the current pom's dependencies don't have versions, store the path into the txt file.
            /*
            如果dependency在当前pom里面没有版本号，被管理在parent pom里，通过下面几个途径去查找：
            1. 直接在dependencyManagement或者dependencies里面找，可能直接写了版本号的。
            2. 如果在以上两个里面也是用EL表达式表示的话，就直接去properties里面通过这个EL的key去查找版本号
             */
            //拿到version不写的依赖的版本号
            version = getVersionByQueryingParentPom(model,artifactId);
        }
        return version;
    }

    /**
     * find the version through parent or parent's parent
     * 在子项目的pom里面没有version的值，为null的时候才到parent pom里面去找
     * query mysql database to get the path of the parent pom,
     * get the dependency version in the parent pom file
     * 如果dependency在当前pom里面没有版本号，被管理在parent pom里，通过下面几个途径去查找：
     1. 直接在dependencyManagement或者dependencies里面找，可能直接写了版本号的。
     2. 如果在以上两个里面也是用EL表达式表示的话，就直接去properties里面通过这个EL的key去查找版本号
     * @param model
     * @param artifactId
     * @return the version of the dependency in parent pom
     */
    private static String getVersionByQueryingParentPom(Model model, String artifactId) throws SQLException {
        //拿到parent pom的绝对路径,如果没有则返回null
        //String patentAbsPath = combineParentPath(model);

        //查找到parent，以及可能的parent的parent的list去查找version
        List<Model> models = getParentModelList(model,new ArrayList<Model>());


        if (models.isEmpty()){
            return null;
        }

        String version = null;
        //list的头部是当前pom的parent，第二个是parent的parent；所以在parent里面拿到了version就不用去parent的parent里面再去遍历了
        for (int i = 0; i < models.size(); i++) {
            Model pmodel = models.get(i);
            List<Dependency> dependencies = getDependenciesDirectlyOrInDirectly(pmodel);

            //如果dependencies不为空
            if(dependencies!=null && dependencies.size()>0){
                //存在和子pom里面同样的artifactId
                //if(searchForDependency(dependencies,artifactId)){
                //在parent pom的其他标签中查找version信息
                //version = queryVersionCrossPomModel(parentModel,dependency);
                //通过artifactId查找到在dependencies里面的对应的version的值，可能是直接显示的，也可能是EL表达式表示的
                //1. 直接在dependencyManagement或者dependencies里面找，可能直接写了版本号的。
                //2. 如果在以上两个里面也是用EL表达式表示的话，就直接去properties里面通过这个EL的key去查找版本号
                version = queryVersionCrossPomModel(pmodel,dependencies,artifactId);
                //}
                /*else{
                    //can not found the dependency artifact in parent pom
                    //may be in the parent's parent pom
                    //version = getVersionByQueryingParentPom(parentModel,dependency);
                }*/
                //当取到了version的值的时候，直接结束方法
                if (version != null){
                    return version;
                }
            }
        }
        //代码走到这，表示没有找到
        return null;
    }

    /**
     * get dependencies directly from parent pom or indirectly from dependencyManagement in parent pom
     * @param parentModel
     * @return
     */
    private static List<Dependency> getDependenciesDirectlyOrInDirectly(Model parentModel) {
        List<Dependency> dependencies = null;
        List<Dependency> dependencies1 = parentModel.getDependencies();
        DependencyManagement dependencyManagement = parentModel.getDependencyManagement();

        if (dependencyManagement != null){
            dependencies = dependencyManagement.getDependencies();
        }

        if (dependencies != null) {
            dependencies = dependencies1;
        }

        return dependencies;
    }

    /**
     * 1. 直接在dependencyManagement或者dependencies里面找，可能直接写了版本号的。
     * 2. 如果在以上两个里面也是用EL表达式表示的话，就直接去properties里面通过这个EL的key去查找版本号
     * @param dependencies
     * @param artifactId
     * @return
     */
    private static String queryVersionCrossPomModel(Model parentModel, List<Dependency> dependencies, String artifactId) {
        String version = null;
        //1. 直接在dependencyManagement或者dependencies里面找
        for (int i = 0; i < dependencies.size(); i++) {
            Dependency dependency = dependencies.get(i);
            String artifactId1 = dependency.getArtifactId();
            if (artifactId.equals(artifactId1)) {
                version = dependency.getVersion();
                if (version != null){
                    if (version.contains("$")){
                        //用EL表达式表示的
                        String key = getELKey(version);
                        if (key.equalsIgnoreCase("project.version") || key.equalsIgnoreCase("version")) {
                            String pversion = parentModel.getVersion();
                            if (pversion != null) {
                                version = pversion;
                            }else{
                                Parent parent = parentModel.getParent();
                                if (parent != null) {
                                    version = parent.getVersion();
                                }
                            }
                        }if (key.equalsIgnoreCase("project.parent.version") || key.equalsIgnoreCase("parent.version")) {
                            org.apache.maven.model.Parent parent = parentModel.getParent();
                            if (parent != null) {
                                version = parent.getVersion();
                            }
                        }else{
                            //get version from properties
                            Properties properties = parentModel.getProperties();
                            if (properties != null)
                                version = (String) properties.get(key);
                        }
                    }
                }
            }
        }
        return version;
    }

    private static String getELKey(String version) {
        return version.replaceAll("\\$", "").replaceAll("\\{", "").replaceAll("\\}", "");
    }

    public static List<Map<String,Object>> generateArtifactFile(String groupId,String artifactId,String version,String packaging) throws Exception {

        /*If the value of artifact or version is taken from another tag, such as the properties in the profile,
                                        or directly taken from the properties, then the value of artifact or version is directly determined from the folder path*/
        /*File parent = file.getParentFile();
        Optional<File> opt = Optional.ofNullable(parent);
        if(version.contains("$")&&opt.isPresent()){
            version = parent.getName();
        }
        Optional<File> opt1 = Optional.ofNullable(Objects.requireNonNull(parent).getParentFile());
        if (artifactId.contains("$")&&opt1.isPresent()){
            artifactId = parent.getParentFile().getName();
        }*/

        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("gav",groupId+":"+artifactId+":"+version);
        map.put("groupId", groupId);
        map.put("artifactId", artifactId);
        map.put("version", "s_"+version);
        map.put("packaging",packaging);
        dataList.add(map);


        //List<List<Object>> dataList = new ArrayList<>();

        /*List<Object> rowList = new ArrayList<Object>();
        Object[] row = new Object[5];
        row[0] = groupId+":"+artifactId+":"+version;
        row[1] = groupId;
        row[2] = artifactId;
        row[3] = "s_"+version;
        row[4] = packaging;
        for(int j=0;j<row.length;j++){
            rowList.add(row[j]);
        }
        dataList.add(rowList);
*/
        /*Map<String,Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("artifactId", artifactId);
        map.put("version", "s_"+version);
        map.put("combination",groupId+":"+artifactId+":"+version);
        List<Map<String, Object>> datas = new ArrayList<>();
        datas.add(map);
        String[] displayColNames = {"combination","groupId","artifactId","version"};
        String[] fieldNames = {"combination","groupId","artifactId","version"};*/

        return dataList;
        //WriteCsv.writeCvs(fileName, datas, displayColNames, fieldNames, true, false);
    }


    public static String getPackaging(Model model) {
        String packaging = model.getPackaging();
        if (packaging == null){
            packaging = "jar";
        }else if (packaging.contains("$")){
            String pkgKey = getELKey(packaging);
            Properties properties = model.getProperties();
            if (properties != null) {
                packaging = properties.getProperty(pkgKey);
            }else{
                packaging = "jar";
            }
        }
        return packaging;
    }
}
