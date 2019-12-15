package ee.taltech.arete.service.git;

import ee.taltech.arete.domain.Submission;

import java.io.IOException;
import java.util.Optional;

public interface GitPullService {

	void repositoryMaintenance(Submission submission);

	void resetHead(Submission submission);

	void pullOrClone(String pathToFolder, String pathToRepo, Optional<Submission> submission);

	String[] getChangedFolders(String pathToFolder) throws IOException;

}
