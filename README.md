# EyeDentify

<img width="375" alt="eyedentify_logo_black_gold" src="https://user-images.githubusercontent.com/30096097/166806175-25b392e6-c6b6-4f66-aa5b-a37543e48221.PNG">


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


The "Tag Editing" screen will display the results from our computer vision algorithm. The screen is shown below. CloudSight provides results for the "Item Description" and Google MLKit provides OCR results for "Item Keywords."


![Image of Tag Editing Screen](https://user-images.githubusercontent.com/30096097/166743890-18c77d8d-b45c-4d41-bb95-1dfb52ba04dc.jpg)


You can edit the "Item Description" and "Item Keywords" text fields by tapping on them an using the phone's keyword. Alternatively, you can edit the fields with Speech-to-Text (STT). You activate STT by tapping and holding on the field. This will make the phone vibrate and you speak the words you want in the field.


The "Scan Item with Camera" button allows you to retake the photo of the item.


The "Add Voice Memo" button enables a user to record a voice memo to pair with the tag. Activate recording by tapping and holding onto the "Add Voice Memo" button. The phone will vibrate and you can commence recording. Letting go of the buttons stops the recording. A Toast will appear to signal that recording ended. To remove the voice recording, simply shake the phone. A Toast will to show on screen to state the memo was removed.


The "Pair with Tag" button initiates the tag pairing process. Tap this button to pair information with an NFC tag.


Now the "Listening to a Tag" screen will show. A Toast will also appear warning that information that exists on an NFC tag will be overwritten. You can now tap your NFC tag to the back of the phone, which is the typically location of the NFC sensor. Another Toast shows up if there is an error reading the tag. Ultiamtely, this screen will time out after a minute and return to the previous tag editing screen if a tag is not detected.


![Image of Listening to a Tag Screen](https://user-images.githubusercontent.com/30096097/166747533-8e797a26-02f9-4076-a205-f51877b85126.jpg)


When the app pairs the information with the tag, a Toast will appear to indicate the pairing is sucessful. Afterwards, the "Tag Info Results" screen will show.

This screen displays a "Play Voice Memo" button if there a voice memo associated with the tag. Otherwise, the button will not be shown. When TalkBack is not activate and there is no voice memo, then the app will use Text-to-Speech (TTS) to annouce what is in the "Item Description" and "Item Keywords" fields. You can edit the information paired with the tag by tapping on the "Edit Tag" button. This will bring you back to the editing screen, where you will need to tap the "Pair with Tag: button again. Place an NFC tag on the phone, and the new information is paired with the tag.

![Image of Tag Info Results](https://user-images.githubusercontent.com/30096097/166755315-8c20257b-0a54-4df2-ab36-b60b988171b1.jpg)


This completes the app flow when starting with "Scan Item with Camera" button on MainActivity.



## Create Tag without Scanning Item

You can create a tag for an item without first scanning the item with the camera. On the MainActivity screen, tap on "Tag Item."

![Image Asking User to Scan Item with Camera or to Tag Item](https://user-images.githubusercontent.com/30096097/166740093-512b6e42-268e-466f-ae52-5ce52f73be9f.jpg)


You will now see the "Tag Editing" screen. It is the same editing screen as when you get results from the computer vision alogrithms, except there is no image. You edit the information and pair with tag in the same manner as outlined in ["Scanning Your First Item"](https://github.com/sgomez14/EyeDentify/edit/main/README.md#scanning-your-first-item).

![Image of Tag Editing Screen](https://user-images.githubusercontent.com/30096097/166801246-0362c723-8fea-450d-b39a-ea6610c6e937.jpg)


## Reading an NFC Tag with Information
A user can open the app by tapping their phone's NFC sensor area on a tag that already has information. The app will open to the MainActivity screen, and then they can tap the tag again to get information associated with that tag. The "Tag Info Results" screen will resemble the image below.

If the user is already on the MainActivity screen, then they can scan a tag from there.

Lastly, users can scan another tag while they are on the "Tag Info Results" screen.

![Image of Tag Info Results](https://user-images.githubusercontent.com/30096097/166755315-8c20257b-0a54-4df2-ab36-b60b988171b1.jpg)


Users cannot scan a tag when they are on the "Tag Editing " screen, shown below. Doing so displays a Toast prompting the user to go back to the MainActivity screen and scan their tag there.

![Image of Tag Editing Screen](https://user-images.githubusercontent.com/30096097/166743890-18c77d8d-b45c-4d41-bb95-1dfb52ba04dc.jpg)


## Changing Languages
EyeDentify translates the app into Simplified Chinese, Spanish, Korean, and Hindi.

To switch languages, change the language on the device. Then close the app and restart it. This helps reset the locale settings in the app.

The app's Optical Character Recognition (OCR) algorithm currently only detects Latin alphabet based languages. Therefore, OCR will not work for Chinese, Korean, or Hindi. But, the rest of the app will by translated into these languages.  






