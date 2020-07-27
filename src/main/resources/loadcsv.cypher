//MERGE效率很低，直接用create rTrim如果不是必要也不用，效率也低
:auto USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM 'file:///release_all.csv' AS art
CREATE (:Artifact {gav: art.artifact, groupId: split(art.artifact,":")[0], artifactId: split(art.artifact,":")[1],
                   version: split(art.artifact,":")[2], packaging: art.packaging})

//在gav上创建索引，方便建立relationship不然创建速度很慢
CREATE INDEX ON :Artifact(gav)

:auto USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM "file:///links_all.csv" AS row
MATCH (f:Artifact {gav: row.source}),(t:Artifact {gav: row.target})
MERGE (f)-[:DEPEND_ON {scope: row.scope}]->(t);

:auto USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM "file:///next_all.csv" AS row
MATCH (f:Artifact {gav: rTrim(row.source)}), (t:Artifact {gav: rTrim(row.target)})
MERGE (f)-[:NEXT]->(t);