# EyeDentify
- EyeDentify is an app that allows users to identify objects in their environment by taking pictures. 

- Our app uses computer vision algorithms to generate a vibrant description of the object scanned. 

- It also employs optical character recognition to detect words on the object. 

- A user can also provide a voice memo that gets attached to the results of that object. 

- This information is paired with a near field communication (NFC) tag so that a user can easily access that object’s description at a later timer by scanning the tag. 

- This app is designed for users that are blind or have other visual impairments. But, it is useful to both sighted and non-sighted users alike.


### Dependencies

TODO: dependenceis here

### Authors
- Santiago Gomez
- Jason Mayfield
- Spandana Patil
- Mandy Yao
- Ze Yu

# Testing Instructions
## On First App Launch

You need to be connected to the internet.
You will be prompted to login. Test username is 'hello@gmail.com' and password is 'password.' You can also create your own account. The login screen is below. Toast will appear if there are any errors with logging in.


After logging, you will be prompted for several persmissions, such as connecting to the internet, using the camera, recording audio, and saving to the device's memory. Grant these permission requests as they pop up during the app launch.


![Image of EyeDentify Login Screen](https://user-images.githubusercontent.com/30096097/166729639-fc56aefd-f992-4d40-8f7b-870dd25161fe.jpg)


## Scanning Your First Item

You will need an NFC tag and any item you wish to tag.


After logging in, the MainActivity is the screen that prompts a user to either "Scan Item with Camera" or to "Tag Item." The screen is show below. Tap "Scan Item with Camera."

![Image Asking User to Scan Item with Camera or to Tag Item](https://user-images.githubusercontent.com/30096097/166740093-512b6e42-268e-466f-ae52-5ce52f73be9f.jpg)


Next will bring up the camera, at which point you can take a picture of your item. The camera interface will prompt to confirm if you want to use the photo. Tap the button that indicates you want to use the picture.


The loading results screen will appear and remain there until the app receives the results from its computer vision algorithms. 

![Image of the Loading Results Screen](https://user-images.githubusercontent.com/30096097/166741597-da1743a0-994b-4fbc-94aa-328785e387b2.jpg)


The TagAcitivty screen will display the results from our computer vision algorithm. The screen is shown below. CloudSight provides results for the "Item Description" and Google MLKit provides OCR results for "Item Keywords."


![tag_activity](https://user-images.githubusercontent.com/30096097/166743890-18c77d8d-b45c-4d41-bb95-1dfb52ba04dc.jpg)


You can edit the "Item Description" and "Item Keywords" text fields by tapping on them an using the phone's keyword. Alternatively, you can edit the fields with Speech-to-Text (STT). You activate STT by tapping and holding on the field. This will make the phone vibrate and you speak the words you want in the field.


The "Scan Item with Camera" button allows you to retake the photo of the item.


The "Add Voice Memo" button enables a user to record a voice memo to pair with the tag. Activate recording by tapping and holding onto the "Add Voice Memo" button. The phone will vibrate and you can commence recording. Letting go of the buttons stops the recording. A Toast will appear to signal that recording ended. To remove the voice recording, simply shake the phone. A Toast will to show on screen to state the memo was removed.


The "Pair with Tag" button initiates the tag pairing process. Tap this button to pair information with an NFC tag.


Now the "Listening to a Tag" screen will show. A Toast will also appear warning that information that exists on an NFC tag will be overwritten. You can now tap your NFC tag to the back of the phone, which is the typically location of the NFC sensor. Another Toast shows up if there is an error reading the tag. Ultiamtely, this screen will time out after a minute and return to the previous tag editing screen if a tag is not detected.


![Image of Listening to a Tag Screen](https://user-images.githubusercontent.com/30096097/166747533-8e797a26-02f9-4076-a205-f51877b85126.jpg)






