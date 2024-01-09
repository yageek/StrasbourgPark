package net.yageek.strasbourgpark.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.yageek.common.ParkingResult;
import net.yageek.common.repository.ParkingRepository;
import net.yageek.strasbourgpark.vo.DownloadResult;
import net.yageek.common.APIClient;

import java.util.List;

/**
 * Created by yheinrich on 14.01.18.
 */

public class ParkingModel extends ViewModel {

    public static final String TAG = "ParkingModel";
    private MutableLiveData<DownloadResult> downloadStatus = new MutableLiveData<>();

    private ParkingRepository repository = new ParkingRepository(new APIClient());

    public void fetchData() {

        downloadStatus.setValue(DownloadResult.loading());
        repository.getParkingResults(new ParkingRepository.Callback() {
            @Override
            public void onResponse(List<ParkingResult> result, String lastRefresh) {
                downloadStatus.setValue(DownloadResult.success(result, lastRefresh));

            }

            @Override
            public void onFailure(Throwable t) {
                downloadStatus.setValue(DownloadResult.error(t));
            }
        });
    }

    public LiveData<DownloadResult> getDownloadStatus() {
        return downloadStatus;

    }

}
