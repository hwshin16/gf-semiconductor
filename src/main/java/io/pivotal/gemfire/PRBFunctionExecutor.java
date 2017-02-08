package io.pivotal.gemfire;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;

import io.pivotal.gemfire.domain.Master;
import io.pivotal.gemfire.domain.MasterKey;
import io.pivotal.gemfire.domain.Trace;
import io.pivotal.gemfire.functions.PRBResultCollector;

public class PRBFunctionExecutor {
	private ClientCache cache;
	private Region<?, ?> traces;
	private Region<?, ?> masters;

	public static void main(String[] args) {
		PRBFunctionExecutor executor = new PRBFunctionExecutor();
		executor.getCache();
		executor.getRegions();
		executor.printBuckets();
		executor.closeCache();
	}

	public void closeCache() {
		cache.close();
	}

	public void getCache() {
		this.cache = new ClientCacheFactory().set("name", "ClientWorker").set("cache-xml-file", "client.xml")
				.create();
	}

	public void getRegions() {
		masters = cache.getRegion("Master");
		System.out.println("Got the Master Region: " + masters);
		traces = cache.getRegion("Trace");
		System.out.println("Got the Trace Region: " + traces);
	}

	public void printBuckets() {
		System.out.println("\nTrace buckets");
		executePRB(traces);
		System.out.println("\nMaster buckets");
		executePRB(masters);
	}

	public void executePRB(Region r) {
		Execution execution = FunctionService.onRegion(r).withCollector(new PRBResultCollector());
		ResultCollector collector = execution.execute("PRBFunction");
		String result = (String) collector.getResult();
		System.out.println(result);
	}

}