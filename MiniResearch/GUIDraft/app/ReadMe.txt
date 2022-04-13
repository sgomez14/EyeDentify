ReadMe for EyeDentify GUI Draft

The purpose of this app is to explore accessibility design principles.

In the layout design, you will find:
    - big buttons that increase the touch area
    - organization optimized for screen readers
    - predictable layout, which enables users to create muscle memory for the app
    - content descriptions for image views
    - textview acting as labels are linked to another view using the labelFor attribute
    - editable text fields have hints so that screen reader can relay to user what the field is for
    - text fields editable via speech-to-text when user long-presses on an edittext view
    - this design will function great for both sighted and non-sighted users

To simulate how a blind user will interact with the design, you will need to turn on the Android
TalkBack feature.

1) Explore the UI by first clicking on the "Scan Item with Button Camera." This will not open up the
camera, but it will take you directly to the screen that users will have once the snap their picture.

The results screen demonstrates the principles listed above.

Take note that the "Add Voice Memo" and "Pair with Tag" buttons are not connected to any actions.
But, the TalkBack feature will announce to a user the purpose of these buttons.

2) Back on the main activity screen, click on the "Tag Item" button. This will bring you to a screen
that resembles the "Scan Item with Camera" result screen. The difference here is that the image view
is replaced with a "Scan Item with Camera" button. The edit text fields have hints that TalkBack will
read. In our final implementation, long-press on a edit text field will activate speech-to-text.
Editing a field with speech-to-text is for the benefit for blind users, but it will also be handy for
sighed users.
