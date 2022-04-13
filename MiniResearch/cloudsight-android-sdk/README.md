# CloudSight Android SDK (beta) 

Official Android SDK to support:

 - Recognise Image From File
 - Recognise Image From Url
 - Set Localization To Recognise Image Response

At a minimum, this SDK is designed to work with Android SDK 16.

## Before you begin

You need your API key and secret (if using OAuth1 authentication). They are available on [CloudSight site](https://cloudsightapi.com/) after you sign up and create a project.

## Installation

To use the CloudSight Android SDK, add the compile dependency with the latest version of the CloudSight SDK.
For work you need add `android.permission.INTERNET` and `android.permission.READ_EXTERNAL_STORAGE` permissions in Manifest for your Android project.

### Gradle



## Usage

Create a client instance using simple key-based authentication:

```java
CloudSightClient client = new CloudSightClient().init("your-api-key");

```

Or, using OAuth1 authentication:

```java
CloudSightClient client = new CloudSightClient().init("your-api-key", "your-api-secret");
```

Also, can set localization to responses from CloudSightClient

```java
client.setLocale("en-US");

```

Send the image request using a file:

```java
File file = new File();
client.getImageInformation(file, new CloudSightCallback() {

            @Override
            public void imageUploaded(CloudSightResponse response) {
          		Log.d("CloudSight ", "Recognition process startet");

        	  }

            @Override
            public void imageRecognized(CloudSightResponse response) {
          		Log.d("CloudSight ", "Recognition process finished");
            }

            @Override
            public void imageRecognitionFailed(String reason) {
          		Log.d("CloudSight ", "Recognition process fail by reason");
            }

            @Override
            public void onFailure(Throwable throwable) {
          		Log.d("CloudSight ", "Recognition request fail");
            }
        });
```

Or, you can send the image request using a URL:

```java
String url = "http://www.example.com/image.png"
client.getImageInformation(url, new CloudSightCallback() {

            @Override
            public void imageUploaded(CloudSightResponse response) {
          		Log.d("CloudSight ", "Recognition process startet");

        	  }

            @Override
            public void imageRecognized(CloudSightResponse response) {
          		Log.d("CloudSight ", "Recognition process finished");
            }

            @Override
            public void imageRecognitionFailed(String reason) {
          		Log.d("CloudSight ", "Recognition process fail by reason");
            }

            @Override
            public void onFailure(Throwable throwable) {
          		Log.d("CloudSight ", "Recognition request fail");
            }
        });
```

## Examples

There's a working Android example that you can run by opening `app` in Android Studio.

## License

CloudSight is released under the MIT license.