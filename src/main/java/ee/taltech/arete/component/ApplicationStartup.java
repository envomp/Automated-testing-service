package ee.taltech.arete.component;

import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import ee.taltech.arete.service.docker.ImageCheck;
import ee.taltech.arete.service.git.GitPullService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class ApplicationStartup implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(ApplicationStartup.class);

	@Autowired
	private GitPullService gitPullService;

	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		log.info("setting up temp folders.");
		if (System.getenv().containsKey("ARETE_HOME") && System.getenv().get("ARETE_HOME").equals("/arete")) {
			log.info("Build phase detected. Aborting.");
			return;
		}

		createDirectory("input_and_output");
		createDirectory("students");
		createDirectory("tests");

		for (int i = 0; i < 16; i++) {

			createDirectory(String.format("input_and_output/%s", i));
			createDirectory(String.format("input_and_output/%s/tester", i));
			createDirectory(String.format("input_and_output/%s/student", i));
			createDirectory(String.format("input_and_output/%s/host", i));

			try {
				new File(String.format("input_and_output/%s/host/input.json", i)).createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				new File(String.format("input_and_output/%s/host/output.json", i)).createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		try {
			String dockerHost = System.getenv().getOrDefault("DOCKER_HOST", "unix:///var/run/docker.sock");

			DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
					.withDockerHost(dockerHost)
					.withDockerTlsVerify(false)
					.build();

			new ImageCheck(DockerClientBuilder.getInstance(config).build(), "automatedtestingservice/java-tester:latest").pull();
			new ImageCheck(DockerClientBuilder.getInstance(config).build(), "automatedtestingservice/python-tester:latest").pull();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			List<String> projects = Arrays.asList("iti0102-2019/ex", "iti0202-2019/ex");
			List<String> projectsFolders = Arrays.asList("iti0102-2019", "iti0202-2019");

			for (int i = 0; i < projects.size(); i++) {
				String project = projects.get(i);
				String projectsFolder = projectsFolders.get(i);
				String pathToTesterFolder = String.format("tests/%s/", projectsFolder);
				String pathToTesterRepo = String.format("https://gitlab.cs.ttu.ee/%s.git", project);
				gitPullService.pullOrClone(pathToTesterFolder, pathToTesterRepo, Optional.empty());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void createDirectory(String home) {
		File file = new File(home);
		if (!file.exists()) {
			if (!file.exists()) {
				file.mkdir();
			}
		}
	}

}
