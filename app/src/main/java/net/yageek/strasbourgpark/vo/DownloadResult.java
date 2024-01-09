package net.yageek.strasbourgpark.vo;

import net.yageek.common.ParkingResult;

import java.util.List;

/**
 * Created by yheinrich on 14.01.18.
 */

public class DownloadResult {

    public enum Status {
        Loading,
        Error,
        Success;
    }

    public final Status status;

    public final List<ParkingResult> results;
    public final String lastRefreshTime;
    public final Throwable t;

    DownloadResult(Status status, List<ParkingResult> results, String lastRefreshTime, Throwable t) {
        this.status = status;
        this.results = results;
        this.lastRefreshTime = lastRefreshTime;
        this.t = t;
    }


    public static DownloadResult error(Throwable t) {
        return new DownloadResult(Status.Error, null, null, t);
    }

    public static DownloadResult loading() {
        return new DownloadResult(Status.Loading, null, null, null);
    }

    public static DownloadResult success(List<ParkingResult> resuls, String lastRefreshTime) {
        return new DownloadResult(Status.Success, resuls, lastRefreshTime, null);
    }
}
