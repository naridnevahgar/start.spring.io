package io.spring.start.site.extension;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;

import io.spring.initializr.generator.project.ResolvedProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.start.site.entity.ApplicationInfo;
import io.spring.start.site.entity.ManifestInfo;

public class ManifestFileContributor implements ProjectContributor {
	
	private final ResolvedProjectDescription resolvedProjectDescription;
	
	private final Map<String, String> files;
	
	public ManifestFileContributor(ResolvedProjectDescription initializrMetadata) {
		this.resolvedProjectDescription = initializrMetadata;
		files = new HashMap<String, String>();
		files.put("default", "manifest.yml");
		files.put("dev", "manifest-dev.yml");
		files.put("sit", "manifest-sit.yml");
		files.put("uat", "manifest-uat.yml");
		files.put("prd", "manifest-prd.yml");
	}
	
	@Override
	public void contribute(Path projectRoot) throws IOException {
		files.forEach((hostSuffix, fileName) -> {
			try {
				Path destinationYml = Files.createFile(projectRoot.resolve(fileName));
				writeBuild(Files.newBufferedWriter(destinationYml), hostSuffix.replace("default", ""));
			} catch (IOException ioEx) {}
		});
	}
	
	private ManifestInfo extractManifestInfo(String hostSuffix) {
		
		String hostSpecificArtifactName = resolvedProjectDescription.getArtifactId();
		if (!hostSuffix.isEmpty()) {
			hostSpecificArtifactName = hostSpecificArtifactName.concat("-").concat(hostSuffix);
		}
		
		ManifestInfo manifestInfo = new ManifestInfo();
		ApplicationInfo application = new ApplicationInfo();
		
		manifestInfo.setApplications(new ArrayList<ApplicationInfo>());
		manifestInfo.getApplications().add(application);

		application.setMemory("1G");
		application.setInstances(3);
		application.setDomain("apps.eas.pcf.manulife.com");
		application.setName(hostSpecificArtifactName);
		application.setPath("target/" + resolvedProjectDescription.getArtifactId() + "-" + resolvedProjectDescription.getVersion() + "." + resolvedProjectDescription.getPackaging().id());
		application.setHost(hostSpecificArtifactName);
		application.setServices(Arrays.asList("registry-service", "config-service", "cb-dashboard-service", "newrelic-service", "scale-service"));
		
		return manifestInfo;
	}
	
	private void writeBuild(Writer out, String hostSuffix) throws IOException {
		YAMLGenerator generator = new YAMLFactory().createGenerator(out);
		generator.configure(Feature.MINIMIZE_QUOTES, true);
		generator.setCodec(new ObjectMapper());
		generator.writeObject(extractManifestInfo(hostSuffix));
		generator.close();		
	}

}
