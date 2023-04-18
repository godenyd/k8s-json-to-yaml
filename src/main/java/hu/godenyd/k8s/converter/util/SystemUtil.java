package hu.godenyd.k8s.converter.util;

public class SystemUtil {

    // TODO: Read this from environment
    public static String getSharedVolumePath() {
        return "/data_distributed/volume1";
    }

    private SystemUtil() {

    }
}
