package ee.taltech.arete.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("arete.dev")
@Component
@Data
public class DevProperties {

    private Boolean debug = true; // if unlock features for debug
    private String developer = "ago.luberg"; // send all submissions
    private String ago = "ago.luberg"; // send only failed submissions
    private String areteMail = "automated_testing_service@taltech.ee";
    private Integer defaultDockerTimeout = 120; // default dockertimeout is 120 seconds
    private String areteBackend = "https://cs.ttu.ee/services/arete/api/admin/job"; // backend url
	private Integer parallelJobs = 16; // Total dockers running same time
	private Double maxCpuUsage = 0.8; // percent that can allow more jobs

}
