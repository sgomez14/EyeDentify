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


