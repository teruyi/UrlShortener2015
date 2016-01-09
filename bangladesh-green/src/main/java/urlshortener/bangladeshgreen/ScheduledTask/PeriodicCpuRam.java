package urlshortener.bangladeshgreen.ScheduledTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import urlshortener.bangladeshgreen.domain.UsageCpu;
import urlshortener.bangladeshgreen.domain.UsageRam;
import urlshortener.bangladeshgreen.repository.CPURepository;
import urlshortener.bangladeshgreen.repository.RamRepository;

/**
 * Class for periodic check of the cpu and ram usage
 * It's save the cpu and ram usage every 30 seconds
 */
public class PeriodicCpuRam {

	@Autowired
	private CPURepository cpuRepository;

	@Autowired
	private RamRepository ramRepository;

	// One minute of delay (for checking cpu and ram)
	@Scheduled(fixedRate = 30000)
	public void send() {
		com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
				java.lang.management.ManagementFactory.getOperatingSystemMXBean();

		double cpu = 0.0;
		double physicalMemorySize = os.getTotalPhysicalMemorySize();
		double physicalfreeMemorySize = os.getFreePhysicalMemorySize();
		double physicalfreeMemorySize2 = ((physicalMemorySize-physicalfreeMemorySize)/physicalMemorySize)*100;
		cpu = os.getSystemCpuLoad()*100;
		UsageCpu usageCpuCpu = new UsageCpu(System.currentTimeMillis(),cpu);
		cpuRepository.save(usageCpuCpu);
		UsageRam usageCpuRam = new UsageRam(System.currentTimeMillis(),physicalfreeMemorySize2);
		ramRepository.save(usageCpuRam);
	}

}