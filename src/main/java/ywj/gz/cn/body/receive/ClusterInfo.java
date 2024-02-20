package ywj.gz.cn.body.receive;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
public class ClusterInfo {
    @JsonProperty("Alloc")
    private String alloc;
    @JsonProperty("ClientId")
    private String clientId;
    @JsonProperty("ClusterIP")
    private String clusterIP;
    @JsonProperty("CpuNum")
    private Long cpuNum;
    @JsonProperty("FreesTimes")
    private Long freesTimes;
    @JsonProperty("GCTime")
    private String gcTime;
    @JsonProperty("GoArch")
    private String goArch;
    @JsonProperty("GoVersion")
    private String goVersion;
    @JsonProperty("GoroutineNum")
    private Long goroutineNum;
    @JsonProperty("LastGCTime")
    private String lastGCTime;
    @JsonProperty("MacInfo")
    private String macInfo;
    @JsonProperty("MallocsTimes")
    private Long mallocsTimes;
    @JsonProperty("NextGC")
    private String nextGC;
    @JsonProperty("Platform")
    private String platform;
    @JsonProperty("QQUsers")
    private List<QQUser> qqUsers;
    @JsonProperty("QQUsersCounts")
    private Long qqUsersCounts;
    @JsonProperty("ServerRuntime")
    private String serverRuntime;
    @JsonProperty("Sys")
    private String sys;
    @JsonProperty("TotalAlloc")
    private String totalAlloc;
    @JsonProperty("Version")
    private String version;
}
