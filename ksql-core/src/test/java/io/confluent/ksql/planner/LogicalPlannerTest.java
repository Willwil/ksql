package io.confluent.ksql.planner;


import io.confluent.ksql.analyzer.Analysis;
import io.confluent.ksql.analyzer.AnalysisContext;
import io.confluent.ksql.analyzer.Analyzer;
import io.confluent.ksql.metastore.MetaStore;
import io.confluent.ksql.parser.KSQLParser;
import io.confluent.ksql.parser.rewrite.SqlFormatterQueryRewrite;
import io.confluent.ksql.parser.tree.Join;
import io.confluent.ksql.parser.tree.Statement;
import io.confluent.ksql.planner.plan.*;
import io.confluent.ksql.util.KSQLTestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class LogicalPlannerTest {

    private static final KSQLParser ksqlParser = new KSQLParser();

    private MetaStore metaStore;

    @Before
    public void init() {
        metaStore = KSQLTestUtil.getNewMetaStore();
    }

    private PlanNode buildLogicalPlan(String queryStr) {
        List<Statement> statements = ksqlParser.buildAST(queryStr, metaStore);
        // Analyze the query to resolve the references and extract oeprations
        Analysis analysis = new Analysis();
        Analyzer analyzer = new Analyzer(analysis,metaStore);
        analyzer.process(statements.get(0), new AnalysisContext(null, null));
        // Build a logical plan
        PlanNode logicalPlan = new LogicalPlanner(analysis).buildPlan();
        return logicalPlan;
    }

    @Test
    public void testSimpleQueryLogicalPlan() throws Exception {
        String simpleQuery = "SELECT col0, col2, col3 INTO testOutput FROM test1 WHERE col0 > 100;";
        PlanNode logicalPlan = buildLogicalPlan(simpleQuery);

        Assert.assertTrue(logicalPlan instanceof OutputKafkaTopicNode);
        Assert.assertTrue(logicalPlan.getSources().get(0) instanceof ProjectNode);
        Assert.assertTrue(logicalPlan.getSources().get(0).getSources().get(0) instanceof FilterNode);
        Assert.assertTrue(logicalPlan.getSources().get(0).getSources().get(0).getSources().get(0) instanceof SourceKafkaTopicNode);

        Assert.assertTrue(logicalPlan.getSchema().fields().size() == 3);
        Assert.assertNotNull(((FilterNode)logicalPlan.getSources().get(0).getSources().get(0)).getPredicate());

        Assert.assertTrue(logicalPlan.getKeyField().name().equalsIgnoreCase("col0"));

    }

    @Test
    public void testSimpleLeftJoinLogicalPlan() throws Exception {
        String simpleQuery = "SELECT t1.col1, t2.col1, col4, t2.col2 INTO testOutput FROM test1 t1 LEFT JOIN test2 t2 ON t1.col1 = t2.col1;";
        PlanNode logicalPlan = buildLogicalPlan(simpleQuery);

        Assert.assertTrue(logicalPlan instanceof OutputKafkaTopicNode);
        Assert.assertTrue(logicalPlan.getSources().get(0) instanceof ProjectNode);
        Assert.assertTrue(logicalPlan.getSources().get(0).getSources().get(0) instanceof JoinNode);
        Assert.assertTrue(logicalPlan.getSources().get(0).getSources().get(0).getSources().get(0) instanceof SourceKafkaTopicNode);
        Assert.assertTrue(logicalPlan.getSources().get(0).getSources().get(0).getSources().get(1) instanceof SourceKafkaTopicNode);

        Assert.assertTrue(logicalPlan.getSchema().fields().size() == 4);

        Assert.assertTrue(logicalPlan.getKeyField().name().equalsIgnoreCase("t1.col1"));

    }

    @Test
    public void testSimpleLeftJoinFilterLogicalPlan() throws Exception {
        String simpleQuery = "SELECT t1.col1, t2.col1, col4, t2.col2 INTO testOutput FROM test1 t1 LEFT JOIN test2 t2 ON t1.col1 = t2.col1 WHERE t1.col1 > 10 AND t2.col4 = 10.8;";
        PlanNode logicalPlan = buildLogicalPlan(simpleQuery);

        Assert.assertTrue(logicalPlan instanceof OutputKafkaTopicNode);
        Assert.assertTrue(logicalPlan.getSources().get(0) instanceof ProjectNode);
        ProjectNode projectNode = (ProjectNode) logicalPlan.getSources().get(0);

        Assert.assertTrue(projectNode.getKeyField().name().equalsIgnoreCase("t1.col1"));
        Assert.assertTrue(projectNode.getSchema().fields().size() == 4);

        Assert.assertTrue(projectNode.getSources().get(0) instanceof FilterNode);
        FilterNode filterNode = (FilterNode) projectNode.getSources().get(0);
        Assert.assertTrue(filterNode.getPredicate().toString().equalsIgnoreCase("((T1.COL1 > 10) AND (T2.COL4 = 10.8))"));

        Assert.assertTrue(filterNode.getSources().get(0) instanceof JoinNode);
        JoinNode joinNode = (JoinNode) filterNode.getSources().get(0);
        Assert.assertTrue(joinNode.getSources().get(0) instanceof SourceKafkaTopicNode);
        Assert.assertTrue(joinNode.getSources().get(1) instanceof SourceKafkaTopicNode);

    }

}