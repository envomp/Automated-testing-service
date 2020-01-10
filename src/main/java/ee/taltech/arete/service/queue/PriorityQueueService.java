package ee.taltech.arete.service.queue;

import ee.taltech.arete.domain.Submission;

import java.util.List;

public interface PriorityQueueService {

	void enqueue(Submission submission);

	void runJob();

	void killThread(Submission submission);

	Integer getJobsRan();

	Integer getQueueSize();

	void halt() throws InterruptedException;

	void go();

	List<Submission> getActiveSubmissions();
}
