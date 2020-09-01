//2895
MATCH (a:Artifact) where a.gav=~".*redhat.*" return a.gav

//2103（导出了与redhat相关的项目的Excel）
MATCH (a:Artifact) where a.gav=~".*redhat.*"
and exists((a)-[:DEPEND_ON]->())
return a.gav

MATCH (a:Artifact {gav: "com.redhat.lightblue.rest:lightblue-rest-common:1.8.1"})-[:DEPEND_ON*]->(some)
where a<>some return distinct some


MATCH p = (a:Artifact {gav: "com.redhat.lightblue.rest:lightblue-rest-common:1.8.1"})
  -[:DEPEND_ON*]-> (b:Artifact {gav: "org.hdrhistogram:HdrHistogram:2.1.4"})
RETURN p

/*解决方案:将红帽自由的项目加上标签 organization:red hat
从数据库里先把这些项目找出来
然后遍历这个list 查询上传的pom里面的某个dependency artifact 比如 log4j:1.2 是否被这个list里面的某个项目所直接或者间接依赖。
然后组成一个dependency gav:[project1gav:used, project2gav:used,project3gav:used] 这样的结果。打印出来即可
如果没有used的项目，进一步，可以只查询有没有同一个artifactId的。那么就可以找到是不是稍微修改一下版本号就可以用了。*/

//最短路径
MATCH p=(root)-[*]->(leaf)
  WHERE NOT ()-->(root) AND NOT (leaf)-->()
RETURN MIN(length(p))


//Input org.hdrhistogram:HdrHistogram:2.1.4  Output: com.redhat.lightblue.rest:lightblue-rest-common:1.8.1:used
//到依赖的最短距离大于等于1就说明直接或间接依赖了
//找到即可不必全部找到（将来可以限制内部项目的发布时间来选取从发布时间近的里面筛选）
MATCH p = (a:Artifact {gav: "com.redhat.lightblue.rest:lightblue-rest-common:1.8.1"})
  -[:DEPEND_ON*]-> (b:Artifact {gav: "org.hdrhistogram:HdrHistogram:2.1.4"})
RETURN  MIN(length(p)) as min

MATCH (a:Artifact {gav:"org.slf4j:slf4j-api:1.7.21"})
CALL apoc.path.subgraphNodes(a, {relationshipFilter:'<DEPEND_ON', labelFilter:'>Artifact'}) YIELD node RETURN node



match (a)<-[:DEPEND_ON*]-(b:Artifact {gav:"org.apache.activemq:activemq-spring:5.8.0.redhat-60024"}) where a<>b return distinct a

MATCH (a:Artifact {gav:"org.apache.activemq:activemq-spring:5.8.0.redhat-60024"})
CALL apoc.path.subgraphNodes(a, {relationshipFilter:'>DEPEND_ON', labelFilter:'>Artifact'}) YIELD node RETURN node

//one of the dependencies that org.apache.activemq:activemq-spring:5.8.0.redhat-60024 depends on

MATCH (a:Artifact {gav:"org.apache.directory.shared:shared-ldap-model:1.0.0-M5"})
MATCH (end:Artifact)
  WHERE end.version contains "redhat"
WITH a, collect(end) AS endNodes
CALL apoc.path.subgraphNodes(a, {
  relationshipFilter:'<DEPEND_ON',
  labelFilter:'>Artifact',
  endNodes: endNodes
})
YIELD node
RETURN node;

MATCH (a:Artifact {gav:"org.apache.directory.shared:shared-ldap-model:1.0.0-M5"})
MATCH (end:Artifact)
  WHERE end.version = "5.8.0.redhat-60024"
WITH a, collect(end) AS endNodes
CALL apoc.path.subgraphNodes(a, {
  relationshipFilter:'<DEPEND_ON',
  labelFilter:'>Artifact',
  endNodes: endNodes
})
YIELD node
RETURN node;

MATCH (a:Artifact {artifactId:"log4j", groupId:"log4j"})
MATCH (end:Artifact) where end.version contains "redhat"
WITH a, collect(end) AS endNodes
CALL apoc.path.subgraphAll(a, {
  relationshipFilter:'<DEPEND_ON',
  labelFilter:'>Artifact',
  endNodes: endNodes
})
YIELD nodes,relationships
RETURN a,nodes,relationships;

MATCH (a:Artifact {gav:"org.apache.directory.shared:shared-ldap-model:1.0.0-M5"})
MATCH (end:Artifact {version:"5.8.0.redhat-60024"})
WITH a, collect(end) AS endNodes
CALL apoc.path.subgraphAll(a, {
  relationshipFilter:'<DEPEND_ON',
  labelFilter:'>Artifact',
  endNodes: endNodes
})
YIELD nodes,relationships
RETURN a,nodes,relationships;

PROFILE MATCH (a:Artifact {gav:"log4j:log4j:1.2.16"})
MATCH (end:Artifact) where end.version contains "redhat"
WITH a, collect(end) AS endNodes
CALL apoc.path.subgraphNodes(a, {
  relationshipFilter:'<DEPEND_ON',
  labelFilter:'>Artifact',
  endNodes: endNodes
})
YIELD node
RETURN node;