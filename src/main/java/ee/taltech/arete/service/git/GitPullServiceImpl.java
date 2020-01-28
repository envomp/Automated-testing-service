package ee.taltech.arete.service.git;

import ee.taltech.arete.domain.Submission;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

//Return true = success, false = failure.

@Service
public class GitPullServiceImpl implements GitPullService {

	private static final List<String> TESTABLES = List.of("ADD", "MODIFY");
	private static Logger LOGGER = LoggerFactory.getLogger(GitPullService.class);
	private static TransportConfigCallback transportConfigCallback = new SshTransportConfigCallback(); // failed ssh will fallback onto password


	@Override
	public boolean repositoryMaintenance(Submission submission) {

		String pathToStudentFolder = String.format("students/%s/%s/", submission.getUniid(), submission.getFolder());

		try {
			if (!pullOrClone(pathToStudentFolder, submission.getGitStudentRepo(), Optional.of(submission))) {
				return false;
			}
			if (submission.getSlugs() == null) {
				submission.setSlugs(getChangedFolders(pathToStudentFolder));
			}

		} catch (IOException | GitAPIException e) {
			LOGGER.error("Failed to read student repository.");
		}

		return true;
	}

	private boolean resetHard(String pathToFolder, String pathToRepo) {

		try {
			FileUtils.deleteDirectory(new File(pathToFolder));
			return true;
		} catch (Exception e1) {
			return false;
		}

	}

	@Override
	public boolean pullOrClone(String pathToFolder, String pathToRepo, Optional<Submission> submission) throws GitAPIException, IOException {

		if (!SafePullAndClone(pathToFolder, pathToRepo, submission)) {
			LOGGER.error("Defaulting to reset hard");
			if (!resetHard(pathToFolder, pathToRepo)) {
				return false;
			}

			if (!SafePullAndClone(pathToFolder, pathToRepo, submission)) {
				LOGGER.error("Completely failed to pull or clone.");
				return false;
			}
		}
		return true;
	}


	private boolean SafePullAndClone(String pathToFolder, String pathToRepo, Optional<Submission> submission) throws GitAPIException, IOException {
		Path path = Paths.get(pathToFolder);

		if (Files.exists(path)) {
			LOGGER.info("Checking for update for project: {}", pathToFolder);


			if (!SafePull(pathToFolder, submission)) {
				LOGGER.error("Checking failed for update. Trying to reset head and pull again");
				Git.open(new File(pathToFolder)).reset().setMode(ResetCommand.ResetType.HARD).call();
				return SafePull(pathToFolder, submission);
			}

		} else {

			if (!SafeClone(pathToFolder, pathToRepo, submission)) {
				return false;
			}
			LOGGER.info("Cloned to folder: {}", pathToFolder);

			if (submission.isPresent()) { // verify and fill fields
				return SafePull(pathToFolder, submission);
			}

		}
		return true;
	}

	private boolean SafeClone(String pathToFolder, String pathToRepo, Optional<Submission> submission) {
		try {
			if (System.getenv().containsKey("GITLAB_PASSWORD")) {
				Git git = Git.cloneRepository()
						.setCredentialsProvider(
								new UsernamePasswordCredentialsProvider(
										"envomp", System.getenv().get("GITLAB_PASSWORD")))
						.setURI(pathToRepo)
						.setDirectory(new File(pathToFolder))
						.call();
				git.close();

			} else {
				Git git = Git.cloneRepository()
						.setTransportConfigCallback(transportConfigCallback)
						.setURI(pathToRepo)
						.setDirectory(new File(pathToFolder))
						.call();
				git.close();
			}
			return true;
		} catch (Exception e) {
			submission.ifPresent(value -> value.setResult(e.getMessage()));
			LOGGER.error("Cloning failed with message: {}", e.getMessage());
			return false;
		}
	}

	@Override
	public boolean resetHead(Submission submission) {

		String pathToStudentFolder = String.format("students/%s/%s/", submission.getUniid(), submission.getFolder());
		try {
			Git.open(new File(pathToStudentFolder)).reset().setMode(ResetCommand.ResetType.HARD).call();
		} catch (Exception e) {
			LOGGER.error("Failed to reset HEAD for student. Defaulting to hard reset: {}", e.getMessage());
			if (!resetHard(pathToStudentFolder, submission.getGitStudentRepo())) {
				return false;
			}
		}
		return true;
	}

	private boolean SafePull(String pathToFolder, Optional<Submission> submission) {
		Git git = null;
		try {
			git = Git.open(new File(pathToFolder));
			if (submission.isPresent()) {
				Submission user = submission.get();

				if (submission.get().getHash() != null) {

					try {
						fetch(git);
						reset(git, user);
						RevCommit latest = getLatestCommit(git);
						user.setCommitMessage(latest.getFullMessage());
						LOGGER.info("Pulled specific hash {} for user {}", user.getHash(), user.getUniid());
					} catch (Exception e) {
						try {
							fixHash(git, user);
							fetch(git);
							reset(git, user);
						} catch (Exception e1) {
							LOGGER.info("Failed to fetch and reset.");
							git.close();
							return false;
						}
					}

				} else {

					SafePull(git);
					RevCommit latest = getLatestCommit(git);
					user.setHash(latest.name());
					user.setCommitMessage(latest.getFullMessage());
					LOGGER.info("Pulled for user {} and set hash {}", user.getUniid(), user.getHash());
				}

			} else {

				SafePull(git);
				LOGGER.info("Pulled to {} with hash {}", pathToFolder, getLatestCommit(git).name());
			}
			git.close();
			return true;
		} catch (Exception e) {
			if (git != null) {
				git.close();
			}
			submission.ifPresent(value -> value.setResult(e.getMessage()));
			LOGGER.error("Pull failed with message: {}", e.getMessage());
			return false;
		}
	}

	private void reset(Git git, Submission user) throws GitAPIException {
		Ref command = git.reset().setMode(ResetCommand.ResetType.HARD).setRef(user.getHash()).call();
	}

	private void SafePull(Git git) throws GitAPIException {
		PullResult result;
		if (System.getenv().containsKey("GITLAB_PASSWORD")) {
			result = git.pull()
					.setCredentialsProvider(new UsernamePasswordCredentialsProvider(
							"envomp", System.getenv().get("GITLAB_PASSWORD")))
					.call();

		} else {
			result = git.pull()
					.setTransportConfigCallback(transportConfigCallback)
					.call();
		}

		assert result.isSuccessful();
	}

	private void fetch(Git git) throws GitAPIException {
		if (System.getenv().containsKey("GITLAB_PASSWORD")) {
			FetchResult result = git.fetch()
					.setCredentialsProvider(new UsernamePasswordCredentialsProvider(
							"envomp", System.getenv().get("GITLAB_PASSWORD")))
					.call();

		} else {
			FetchResult result = git.fetch()
					.setTransportConfigCallback(transportConfigCallback)
					.call();
		}
	}

	private RevCommit getLatestCommit(Git git) throws GitAPIException, IOException {
		RevCommit youngestCommit = null;
		List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
		try (RevWalk walk = new RevWalk(git.getRepository())) {
			for (Ref branch : branches) {
				RevCommit commit = walk.parseCommit(branch.getObjectId());
				if (youngestCommit == null || commit.getAuthorIdent().getWhen().compareTo(
						youngestCommit.getAuthorIdent().getWhen()) > 0)
					youngestCommit = commit;
			}
		}

		assert youngestCommit != null;
		return youngestCommit;
	}

	private void fixHash(Git git, Submission user) throws GitAPIException, IOException {
		RevCommit youngestCommit = null;
		List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
		try (RevWalk walk = new RevWalk(git.getRepository())) {
			for (Ref branch : branches) {
				RevCommit commit = walk.parseCommit(branch.getObjectId());
				if (youngestCommit == null || commit.getAuthorIdent().getWhen().compareTo(youngestCommit.getAuthorIdent().getWhen()) > 0) {
					if (commit.name().equals(user.getHash())) {
						return;
					}
					youngestCommit = commit;
				}
			}
		}
		assert youngestCommit != null;
		LOGGER.error("Detected faulty hash {}, replaced it with a correct one {}", user.getHash(), youngestCommit.name());
		user.setHash(youngestCommit.name());
	}

	@Override
	public HashSet<String> getChangedFolders(String pathToStudentFolder) throws IOException {
		HashSet<String> repoMainFolders = new HashSet<>();
		Repository repository = new FileRepository(pathToStudentFolder + ".git");
		RevWalk rw = new RevWalk(repository);
		ObjectId head = repository.resolve(Constants.HEAD);
		RevCommit commit = rw.parseCommit(head);
		try {
			RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
			DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
			df.setRepository(repository);
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(true);
			List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
			for (DiffEntry diff : diffs) {

				if (TESTABLES.contains(diff.getChangeType().name())) {
					String potentialSlug = diff.getNewPath().split("/")[0];
					if (potentialSlug.matches("[a-zA-Z0-9_]*")) {
						repoMainFolders.add(potentialSlug);
					}
				}

			}
			return repoMainFolders;
		} catch (Exception e) { // first commit, no parent. Get all slugs
			for (File file : Objects.requireNonNull(new File(pathToStudentFolder).listFiles())) {
				try {
					String potentialSlug = file.getPath().split("/")[0];
					if (potentialSlug.matches("[a-zA-Z0-9_]*")) {
						repoMainFolders.add(potentialSlug);
					}
				} catch (Exception ignored) {
				}
			}
			return repoMainFolders;
		}
	}
}
