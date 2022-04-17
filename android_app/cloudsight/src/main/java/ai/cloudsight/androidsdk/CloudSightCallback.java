package ai.cloudsight.androidsdk;

public interface CloudSightCallback {

    void imageUploaded(CloudSightResponse response);
    void imageRecognized(CloudSightResponse response);
    void imageRecognitionFailed(String reason);
    void onFailure(Throwable response);

}
