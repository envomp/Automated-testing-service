package ee.taltech.arete.initializers;

import ee.taltech.arete.api.data.request.AreteRequest;
import ee.taltech.arete.api.data.response.arete.AreteResponse;
import ee.taltech.arete.domain.Submission;
import lombok.SneakyThrows;
import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class SubmissionInitializer {
	private final static String UNIID_GIT = "envomp";

	private static final String STUDENT_REPO_PYTHON = "https://gitlab.cs.ttu.ee/envomp/iti0102-2019.git";
	private static final String STUDENT_REPO_JAVA = "https://gitlab.cs.ttu.ee/envomp/iti0202-2019.git";
	private static final String STUDENT_REPO_EXAM = "https://gitlab.cs.ttu.ee/iti0102-2018/exams/exam2-envomp.git";
	private static final String STUDENT_REPO_GITHUB = "https://github.com/envomp/CV.git";

	private static final String TESTER_REPO_PYTHON = "https://gitlab.cs.ttu.ee/iti0102-2019/ex.git";
	private static final String TESTER_REPO_EXAM = "https://gitlab.cs.ttu.ee/iti0102-2018/ex.git";
	private static final String TESTER_REPO_JAVA = "https://gitlab.cs.ttu.ee/iti0202-2019/ex.git";
	private static final String TESTER_REPO_GITHUB = "https://github.com/envomp/CV.git";

	private static final String PROJECT_PYTHON = "iti0102-2019";
	private static final String PROJECT = "iti0202-2019";
	private final static String TESTING_PLATFORM_JAVA = "java";
	private final static String TESTING_PLATFORM_PYTHON = "python";
	private final static String TESTING_PLATFORM_PROLOG = "prolog";
	private static final String PROJECT_GIT = System.getenv().containsKey("GIT_PASSWORD") ? "https://gitlab.cs.ttu.ee/iti0202-2019/ex.git" : "git@gitlab.cs.ttu.ee:iti0202-2019/ex.git";
	private static final String PROJECT_GIT_PYTHON = System.getenv().containsKey("GIT_PASSWORD") ? "https://gitlab.cs.ttu.ee/iti0102-2019/ex.git" : "git@gitlab.cs.ttu.ee:iti0102-2019/ex.git";
	private final static HashSet<String> EXTRA = new HashSet<>(Collections.singletonList("stylecheck"));
	private final static String home = System.getenv().getOrDefault("ARETE_HOME", System.getenv().getOrDefault("HOME", ".") + "/arete");

	public static Submission getFullSubmissionPython(String base) {
		String hash = "1bf2d711ce9ff944c7c9ffd9def23d312e9c4f9f";
		return Submission.builder()
				.uniid(UNIID_GIT)
				.gitStudentRepo(STUDENT_REPO_PYTHON)
				.gitTestSource(TESTER_REPO_PYTHON)
				.course(PROJECT_PYTHON)
				.folder(PROJECT_PYTHON)
				.hash(hash)
				.testingPlatform(TESTING_PLATFORM_PYTHON)
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.dockerTimeout(120)
				.commitMessage("First commit!!!")
				.hash("12dacy372642hc3642c3v423xd34v5yb534bn7354")
				.result("Everything went ok but in Python. Here are some LOGS LOGS LOGS LOGS LOGS ...")
				.systemExtra((new HashSet<>(Arrays.asList("noMail", "integration_tests"))))
				.dockerExtra(new HashSet<>(Collections.singletonList("stylecheck")))
				.timestamp(System.currentTimeMillis() / 1000)
				.priority(10)
				.build();
	}


	public static Submission getFullSubmissionJava(String base) {
		String hash = "12dacy372642hc3642c3v423xd34v5yb534bn7354";
		return Submission.builder()
				.uniid(UNIID_GIT)
				.gitStudentRepo(STUDENT_REPO_JAVA)
				.gitTestSource(TESTER_REPO_JAVA)
				.course(PROJECT)
				.folder(PROJECT)
				.testingPlatform(TESTING_PLATFORM_JAVA)
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.dockerTimeout(120)
				.commitMessage("First commit!")
				.hash(hash)
				.result("Everything went ok. Here are some LOGS LOGS LOGS LOGS LOGS ...")
				.systemExtra((new HashSet<>(Arrays.asList("noMail", "integration_tests"))))
				.dockerExtra(new HashSet<>(Collections.singletonList("stylecheck")))
				.timestamp(System.currentTimeMillis() / 1000)
				.priority(10)
				.build();
	}


	public static Submission getGitPullEndpointSubmissionGithub(String base) {
		String hash = getRandomHash();
		return Submission.builder()
				.uniid(UNIID_GIT)
				.gitStudentRepo(STUDENT_REPO_GITHUB)
				.gitTestSource(TESTER_REPO_GITHUB)
				.course(UNIID_GIT)
				.folder(UNIID_GIT)
				.slugs(new HashSet<>(Arrays.asList(
						"EX01IdCode/inner/stuff.py"
				)))
				.priority(10)
				.timestamp(1L)
				.dockerTimeout(120)
				.testingPlatform(TESTING_PLATFORM_JAVA)
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.systemExtra((new HashSet<>(Arrays.asList("noMail", "integration_tests"))))
//				.dockerExtra(new HashSet<>(Collections.singletonList("stylecheck")))
//				.hash("a5462dc0377504e79b25ad76c9d0a4c7ce27f7d4")
				.build();
	}

	public static Submission getGitPullEndpointSubmissionGitlab(String base) {
		String hash = getRandomHash();
		return Submission.builder()
				.uniid(UNIID_GIT)
				.gitStudentRepo(STUDENT_REPO_JAVA)
				.gitTestSource(TESTER_REPO_JAVA)
				.course(PROJECT)
				.folder(PROJECT)
				.slugs(new HashSet<>(Arrays.asList(
						"EX01IdCode/inner/stuff.py",
						"TK/tk_tsükkel_1/exam.py"
				)))
				.groupingFolders(new HashSet<>(Collections.singletonList("TK")))
				.priority(10)
				.timestamp(1L)
				.dockerTimeout(120)
				.testingPlatform(TESTING_PLATFORM_JAVA)
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.systemExtra((new HashSet<>(Arrays.asList("noMail", "integration_tests"))))
//				.dockerExtra(new HashSet<>(Collections.singletonList("stylecheck")))
//				.hash("a5462dc0377504e79b25ad76c9d0a4c7ce27f7d4")
				.build();
	}

	public static AreteRequest getFullSubmissionStringControllerEndpoint(String base) {
		String hash = "f951d41763c6b0b6d0def92722241e5746bb0b3c";
		return AreteRequest.builder()
				.gitStudentRepo(STUDENT_REPO_JAVA)
				.gitTestSource(TESTER_REPO_JAVA)
				.hash(hash)
				.testingPlatform(TESTING_PLATFORM_JAVA)
				.systemExtra((new HashSet<>(Arrays.asList("noMail", "integration_tests"))))
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.build();
	}

	public static AreteRequest getFullSubmissionStringControllerEndpointPython(String base) {
		String hash = "ec99e4e12d28fd8ed66aaa26c2c56c53f445444c";
		return AreteRequest.builder()
				.uniid(UNIID_GIT)
				.gitStudentRepo(STUDENT_REPO_PYTHON)
				.gitTestSource(TESTER_REPO_PYTHON)
				.hash(hash)
				.testingPlatform(TESTING_PLATFORM_PYTHON)
				.systemExtra((new HashSet<>(Arrays.asList("noMail", "integration_tests"))))
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.dockerExtra(EXTRA)
				.build();
	}

	public static AreteRequest getFullSubmissionStringControllerEndpointPythonLong(String base) {
		String hash = "1a98082946d55f4479ea22a4ea9fbf58d31e2a89";
		return AreteRequest.builder()
				.uniid(UNIID_GIT)
				.gitStudentRepo(STUDENT_REPO_PYTHON)
				.gitTestSource(TESTER_REPO_PYTHON)
				.hash(hash)
				.testingPlatform(TESTING_PLATFORM_PYTHON)
				.systemExtra((new HashSet<>(Arrays.asList("noMail", "integration_tests"))))
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.dockerExtra(EXTRA)
				.dockerTimeout(1080)
				.build();
	}

	public static AreteRequest getFullSubmissionStringControllerEndpointPythonRecursion(String base) {
		String hash = "5c656f906cdd5c045e0cae99c870e77bd9a3771a";
		return AreteRequest.builder()
				.gitStudentRepo(STUDENT_REPO_PYTHON)
				.gitTestSource(TESTER_REPO_PYTHON)
				.testingPlatform(TESTING_PLATFORM_PYTHON)
				.systemExtra((new HashSet<>(Arrays.asList("noMail", "integration_tests"))))
				.uniid(UNIID_GIT)
				.timestamp(123L)
				.eMail("envomp@ttu.ee")
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.hash(hash)
				.dockerExtra(EXTRA)
				.priority(10)
				.build();
	}

	public static AreteRequest getFullSubmissionStringControllerEndpointPythonCustomConfiguration(String base) {
		String hash = "ec99e4e12d28fd8ed66aaa26c2c56c53f445444c";
		return AreteRequest.builder()
				.uniid(UNIID_GIT)
				.gitStudentRepo(STUDENT_REPO_PYTHON)
				.systemExtra((new HashSet<>(Arrays.asList("noMail", "integration_tests"))))
				.gitTestSource(TESTER_REPO_PYTHON)
				.testingPlatform(TESTING_PLATFORM_PYTHON)
				.hash(hash)
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.priority(10)
				.build();
	}

	public static AreteRequest getFullSubmissionStringExamControllerEndpoint(String base) {
		String hash = "b9a7e15b14d31b8b6af47a5b53af73051e32e38b";
		return AreteRequest.builder()
				.uniid(UNIID_GIT)
				.gitStudentRepo(STUDENT_REPO_EXAM)
				.gitTestSource(TESTER_REPO_EXAM)
				.testingPlatform(TESTING_PLATFORM_PYTHON)
				.systemExtra((new HashSet<>(Arrays.asList("noStd", "noFeedback", "noMail", "integration_tests"))))
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.hash(hash)
				.dockerExtra(EXTRA)
				.build();
	}

	public static AreteRequest getFullSubmissionStringProlog(String base) {
		String hash = getRandomHash();
		return AreteRequest.builder()
				.uniid(UNIID_GIT)
				.gitStudentRepo("https://gitlab.cs.ttu.ee/envomp/iti0211-2019.git")
				.testingPlatform(TESTING_PLATFORM_PROLOG)
				// no test access prolog
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.build();
	}

	@SneakyThrows
	public static AreteRequest getFullSubmissionStringSync(String base) {
		String hash = getRandomHash();
		return AreteRequest.builder()
				.uniid(UNIID_GIT)
				.gitTestSource(TESTER_REPO_JAVA)
				.testingPlatform(TESTING_PLATFORM_JAVA)
				.hash(hash)
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.gitTestSource(PROJECT_GIT)
				.dockerExtra((new HashSet<>(Collections.singletonList("-r ~CHECKSTYLE"))))
				.systemExtra((new HashSet<>(Arrays.asList("noMail", "anonymous"))))
				.source(new ArrayList<>(Collections.singletonList(
						AreteRequest.SourceFile.builder()
								.path("EX01IdCode/src/ee/taltech/iti0202/idcode/IDCode.java")
								.contents(Files.readString(Paths.get(home + "/src/test/java/ee/taltech/arete/initializers/IDCode.java"), StandardCharsets.US_ASCII))
								.build())))
				.build();
	}

	@SneakyThrows
	public static AreteRequest getFullSubmissionStringSyncBadRequest(String base) {
		String hash = getRandomHash();
		return AreteRequest.builder()
				.uniid(UNIID_GIT)
				.gitTestSource(TESTER_REPO_JAVA)
				.testingPlatform(TESTING_PLATFORM_JAVA)
				.hash(hash)
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.gitTestSource(PROJECT_GIT)
				.systemExtra((new HashSet<>(Arrays.asList("integration_tests", "noMail"))))
				.source(new ArrayList<>(Collections.singletonList(
						AreteRequest.SourceFile.builder()
								.path("EX01IdCode/src/ee/taltech/iti0202/idcode/IDCode.java")
								.contents(Files.readString(Paths.get(home + "/src/test/java/ee/taltech/arete/initializers/IDCode.java"), StandardCharsets.US_ASCII))
								.build())))
				.build();
	}

	@SneakyThrows
	public static AreteRequest getFullSubmissionStringPythonSync(String base) {
		String hash = getRandomHash();
		return AreteRequest.builder()
				.uniid(UNIID_GIT)
				.gitTestSource(TESTER_REPO_PYTHON)
				.testingPlatform(TESTING_PLATFORM_PYTHON)
				.dockerExtra(EXTRA)
				.hash(hash)
				.systemExtra((new HashSet<>(Arrays.asList("integration_tests", "noMail"))))
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.gitTestSource(PROJECT_GIT_PYTHON)
				.source(new ArrayList<>(Collections.singletonList(
						AreteRequest.SourceFile.builder()
								.path("ex04_cipher/cipher.py")
								.contents(Files.readString(Paths.get(home + "/src/test/java/ee/taltech/arete/initializers/cipher.py"), StandardCharsets.UTF_8))
								.build())))
				.build();
	}

	@SneakyThrows
	public static AreteRequest getFullSubmissionStringPythonSyncNoStyle(String base) {
		String hash = getRandomHash();
		return AreteRequest.builder()
				.uniid(UNIID_GIT)
				.gitTestSource(TESTER_REPO_PYTHON)
				.testingPlatform(TESTING_PLATFORM_PYTHON)
				.dockerExtra(new HashSet<>())
				.hash(hash)
				.systemExtra((new HashSet<>(Arrays.asList("integration_tests", "noMail"))))
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.gitTestSource(PROJECT_GIT_PYTHON)
				.source(new ArrayList<>(Collections.singletonList(
						AreteRequest.SourceFile.builder()
								.path("ex04_cipher/cipher.py")
								.contents(Files.readString(Paths.get(home + "/src/test/java/ee/taltech/arete/initializers/cipher.py"), StandardCharsets.UTF_8))
								.build())))
				.build();
	}

	@SneakyThrows
	public static AreteRequest getFullSubmissionStringPythonSyncNoStdout(String base) {
		String hash = getRandomHash();
		return AreteRequest.builder()
				.uniid(UNIID_GIT)
				.gitTestSource(TESTER_REPO_PYTHON)
				.testingPlatform(TESTING_PLATFORM_PYTHON)
				.dockerExtra(EXTRA)
				.systemExtra((new HashSet<>(Arrays.asList("noStd", "integration_tests", "noMail"))))
				.hash(hash)
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.gitTestSource(PROJECT_GIT_PYTHON)
				.source(new ArrayList<>(Collections.singletonList(
						AreteRequest.SourceFile.builder()
								.path("ex04_cipher/cipher.py")
								.contents(Files.readString(Paths.get(home + "/src/test/java/ee/taltech/arete/initializers/cipher.py"), StandardCharsets.UTF_8))
								.build())))
				.build();
	}

	@SneakyThrows
	public static AreteRequest getFullSubmissionStringPythonSyncNoTesterFiles(String base) {
		String hash = getRandomHash();
		return AreteRequest.builder()
				.uniid(UNIID_GIT)
				.gitTestSource(TESTER_REPO_PYTHON)
				.testingPlatform(TESTING_PLATFORM_PYTHON)
				.dockerExtra(EXTRA)
				.systemExtra((new HashSet<>(Arrays.asList("noTesterFiles", "integration_tests", "noMail"))))
				.hash(hash)
				.returnUrl(String.format("%s/waitingroom/%s", base, hash))
				.gitTestSource(PROJECT_GIT_PYTHON)
				.source(new ArrayList<>(Collections.singletonList(
						AreteRequest.SourceFile.builder()
								.path("ex04_cipher/cipher.py")
								.contents(Files.readString(Paths.get(home + "/src/test/java/ee/taltech/arete/initializers/cipher.py"), StandardCharsets.UTF_8))
								.build())))
				.build();
	}

	private static String getRandomHash() {
		return RandomStringUtils.random(64, true, true).toLowerCase();
	}


	public static void assertFullSubmission(AreteResponse submission) {
		assert submission.getUniid() != null;
		assert submission.getHash() != null;
		assert submission.getTimestamp() != null;
		assert !submission.getFailed();
		assert submission.getTestSuites().size() > 0;
		assert submission.getTestSuites().get(0).getUnitTests().size() != 0;
		assert submission.getTestSuites().get(0).getUnitTests().get(0).getName() != null;
		assert submission.getTotalGrade() != null;
		assert submission.getType() != null;
		assert submission.getVersion() != null;
//		assert submission.getTestingPlatform().equals(TESTING_PLATFORM);
//		assert !submission.getDockerExtra().isEmpty();
	}
}
