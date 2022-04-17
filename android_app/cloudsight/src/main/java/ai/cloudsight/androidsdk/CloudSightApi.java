package ai.cloudsight.androidsdk;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface CloudSightApi {

    @Multipart
    @POST("/v1/images")
    Call<CloudSightResponse> recognitionByImageFile(
            @PartMap() Map<String, RequestBody> partMap,
            @Part("image\"; filename=\"myfile.jpg\" ") RequestBody file);

    @Multipart
    @POST("/v1/images")
    Call<CloudSightResponse> recognitionByRemoteImageUrl(
            @PartMap() Map<String, RequestBody> partMap,
            @Part("remote_image_url")
                    RequestBody imageUrl);

    @GET("/v1/images/{token}")
    Call<CloudSightResponse> getInfoByToken(
            @Path("token") String token);
}
