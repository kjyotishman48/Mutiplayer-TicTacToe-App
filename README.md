# Tic Tac Toe (CS F214 Assignment 5)
 
## A) Submission Details
   Name of Project : Assignment 5 - Tic Tac Toe <br />
   Name : Jyotishman Kashyap <br />
   ID Number : 2019B2A70911G <br />
   Email ID : f20190911@goa.bits-pilani.ac.in
   
 
## B) Description of the App and Known Bugs
### Description of the App
 
This app is a tic tac toe game app, where the user can sign into the app using thier email and then can play either single player or multiplayer tic tac toe game. Every time they play a game their stats - wins, loses and draws are recorded and is displayed on the dashboard. The dashboard also shows the list of mutiplayer tic tac toe games which are available to play (i.e host has created a game and is looking for an opponent), which the user can join and play. Authentication is implemented using Firebase and the multiplayer game is implemented using firebase realtime database.
 
### Screenshots from App
<p align="center">
   <img width="220" height="430" src="https://user-images.githubusercontent.com/68853069/206180903-5e5089f1-6833-46f7-a954-e012231a7271.png"> &nbsp; 
   <img width="220" height="430" src="https://user-images.githubusercontent.com/68853069/206181015-f33c52b3-a52f-43c4-918f-14226885ad3e.png"> &nbsp; 
   <img width="220" height="430" src="https://user-images.githubusercontent.com/68853069/206184239-ada3c8e5-8031-4293-a09d-b6b6d3daad28.jpg"> &nbsp;
   <img width="220" height="430" src="https://user-images.githubusercontent.com/68853069/206184816-c271da0c-7ece-427d-a09f-6d55195cf2b0.png"> &nbsp;
   <img width="220" height="430" src="https://user-images.githubusercontent.com/68853069/206185636-25d652a0-1682-4b02-8b1a-100085f93a07.png"> &nbsp;
   <img width="220" height="430" src="https://user-images.githubusercontent.com/68853069/206185883-c283d696-91b2-45db-b570-e1f1811ae067.png"> &nbsp;
   <img width="220" height="430" src="https://user-images.githubusercontent.com/68853069/206186298-1d32d1e6-f483-4db8-a83f-e0864978f30e.png"> &nbsp;
   <img width="220" height="430" src="https://user-images.githubusercontent.com/68853069/206186510-ba7bf39a-151e-4804-90e5-6b7051573623.png"> &nbsp;
   <img width="220" height="430" src="https://user-images.githubusercontent.com/68853069/206186789-8e94eb99-d090-4cb2-9578-c8aa0cfdca49.png"> &nbsp;
   <img width="220" height="430" src="https://user-images.githubusercontent.com/68853069/206187085-d3740c32-656a-4f09-b5fc-9362df74414d.png"> &nbsp;
   <img width="220" height="430" src="https://user-images.githubusercontent.com/68853069/206187255-feb88b0f-36a7-42e9-a1c1-b0bbc31f857f.png"> &nbsp;
   <img width="220" height="430" src="https://user-images.githubusercontent.com/68853069/206187461-13bf997d-c4b3-4d7c-bfa2-03cbf920046d.png">
</p>
 
### Known bugs
 
When the user is in dashboard fragment and if they press back button of the device, the app navigates to the login fragment, although the user is still logged in. This issue happens occasionally, for example after playing a multiplayer game.
 
## C) Brief Description of how the task was completed
 
### Task 1 : Authentication
 
Signed in to firebase account, in the firebase console created a project and added an android application with the name androidsamples.java.tictactoe in the project. Downloaded the google-services.json file generated and placed it at the correct path in the src folder of the project. In the firebase console enabled authentication and enabled sign in with email and password. Also crated a firebase realtime database for the app. 
 
The entry point in the app is the dashbord fragment, a FirebaseAuth object is created and initializing it with FirebaseAuth.getInstance() and if the object is null we navigate to the login fragment. In the login fragment we are performing authentication - we register the user if the email id dostnot exist in our firebase database otherwise we perform login by matching the email with its password. After the user has authenticated and logged in the app is navigated to the dashboard fragment.
 
### Task 2 : Single Player mode
 
In the navigation graph two arguments has been specified for the gaame fragment which is gameType and the gameId, both of which are string object. When the user selects single player option on the dialog box which appears after clicking the floating action button, the user is navigated into the game fragment and using safe args the arguments gameType and gameId with the values "One-player" and "Single Game ID".
 
Inside the game fragment the value of gameType is checked if it is "One-player" a boolean argument isSinglePlayer is set to true.
 
Inside the game fragment gameArray of size 9 is present which is a string array of size 9 and contains the state of the 3x3 grid of the tic tac toe. Every time the user taps on the grid of buttons, the gameArray is accordingly updated with the text "X" at the correct index and that perticular button is disabled. Every time the gameArray is updated we check if the user has won or loss using checkWin function (which returns 1 if the user won and -1 if the user lost respectively), otherwise it return zero, after the user has made his move, we call the function automateSinglePlayerGame() which is responsible for the computer opponent move, inside this function we are generating a random number between 0-8 (Format - rand.nextInt((max - min) + 1) + min) and we are checking if the gameArray at that position is empty or not inside a while loop, if it is empty, it is a valid move and that position of the array is updated with O. After this again checkWin and check draw function is called to check for win, lose or draw.
 
If it is a win, lose or draw the endGame function is invoked with the parameter as 1 (in case of win), -1 (in case of lose) and 0 (in case of a draw) and depending on the value of this parameter in the database corresponding the perticular user, his wins, losses, draw is updated accordingly and also an alert dialog box is created with the appropriate message and with a OK button, on pressing the okay button the app navigates to the dashboard fragment and the stats is updated since those values are fetched from the realtime database.
 
### Task 3 : Multiplayer mode
 
When the user selects single player option on the dialog box which appears after clicking the floating action button, the user is navigated into the game fragment and using safe args the arguments gameType and gameId with the values "Two-player" and game id generated using gamesRef.push().getKey(). A entry in created in the database with label "games" and inside the game label a new GameModel object is created with the uid of the host, email id of the host, gameId and some other parameters which is set on creating the object and calling the constructor of GameModel class.
 
Inside the host moves to gameFragment and isSinglePlayer veriable is set true and its wait for the other player.
 
For all the other players (potential guests) - inside the dashboard I have created a gamesRef which is a object of FirebaseDatabase under the reference "games". And inside the onViewCreated callback of the fragment using a ValueEventListener and inside its onDataChange function taking a snapshot of the database and iterating through the GameModel objects present. I am a using gameList which is a ArrayList of GameModel, in which I will store all the list of available games. Also inside the onDataChange, gameList must be cleared first (since multiplayer games which were previously open will eventually become close). Before adding GameMoodel object into the gameList ArrayList I am checking it the state of the perticular game is open or not (using isOpen state) and checking if the user is not the host of the game, if these conditions are satisfied I add the perticular GameModel object into our gameList and the details of the this GameModel object is then displayed inside the recyler view with the help of the adapter class. When the guest clicks on this item in the recyler view they are navigated to gameFragment with the gameID and they join as host and the "turn" in realtime database is set to 1 for the host to play the move, when the hosts plays the mode the turn is updated and the state of the game object is updated with it continuously as such the turn is altered between 1 and 2 for the host and the gues to play the game and set their move. 
 
### Task 4 : Accessibility
 
Yes I tested my app using accessibility scanner:
 
- Item label
androidsamples.java.tictactoe:id/list
This item may not have a label readable by screen readers. <br>
Fix : set contentDiscription attribute for the recyler view
 
- Image contrast
androidsamples.java.tictactoe:id/fab_new_game
The image's contrast ratio is 1.77. This ratio is based on an estimated foreground color of #FFFFFF and an estimated background color of #03DAC5. Consider increasing this ratio to 3.00 or greater. <br>
Fix : Added a color in the colors.xml res file <color name="green">#05F340</color> and use this as the background tint of the floating action button
 
- Item descriptions
androidsamples.java.tictactoe:id/won_score
This non-clickable item's speakable text: "5" is identical to that of 1 other item(s). <br>
Fix : set distinct description for won_score, lost_score and draw_score, and updated it when ever the user score statistics change.
 
- Item label
androidsamples.java.tictactoe:id/button1
This item may not have a label readable by screen readers.<br>
Item label
androidsamples.java.tictactoe:id/button2
This item may not have a label readable by screen readers.<br>
Item label
androidsamples.java.tictactoe:id/button3
This item may not have a label readable by screen readers.<br>
Item label
androidsamples.java.tictactoe:id/button5
This item may not have a label readable by screen readers.<br>
Item label
androidsamples.java.tictactoe:id/button6
This item may not have a label readable by screen readers.<br>
Item label
androidsamples.java.tictactoe:id/button7
This item may not have a label readable by screen readers.<br>
Item label
androidsamples.java.tictactoe:id/button8
This item may not have a label readable by screen readers.<br>
Fix : Set sepearate distinct description for each of the 9 buttons and updated them when ever gameArray is updated
 
- Touch target
androidsamples.java.tictactoe:id/edit_email
This item's height is 45dp. Consider making the height of this touch target 48dp or larger.<br>
Touch target
androidsamples.java.tictactoe:id/edit_password
This item's height is 45dp. Consider making the height of this touch target 48dp or larger.<br>
Fix : set minHeight attribute to 48dp
 
- Text contrast
androidsamples.java.tictactoe:id/edit_password
The item's text contrast ratio is 2.68. This ratio is based on an estimated foreground color of #9E9E9E and an estimated background color of #FFFFFF. Consider increasing this item's text contrast ratio to 3.00 or greater.<br>
Fix : Added a color in the colors.xml res file <color name="hint_text_color">#757575</color> and used this for the hint text color.
 
### Talkback
 
The talkback expecience was good because the content description of various UI elements are dynamically updated with the change in the content/text of the UI elements and a readable and easily understandable text was set, this provides a good expericence for any user who is using the app by talk back, as they can undertand the updated state of the game.
 
## D) Hosting and Running Aap
 
Clone this repository,Since I have linked the app with my firebase console it will work properly and all the functionality should work.
 
But if someone want to host the app and see the firebase database, they need to sign into their firebase account and do the following steps (basically need to change the database to their created database and link the project with their own firebase console). -
- Create a project in firebase
- Inside that project add an android application with the name androidsamples.java.tictactoe and download the google-services.json file and place it correctly in the given path inside the src folder.
- In the firebase console enable authentication using email and password.
- In the firebase console create a realtime test database and copy the link of the link database and replace the link in the code with the new link in the code where-ever there is a dependency or where-ever a DatabaseReference object is created.
- After these changes the app will be hosted in the firebase.
 
## E) Testing
 
Yes I did use the Monkey Tool (with 2000 events)</br>
I ran monkey in my emulator it ran succesfully without any crash <br>
<p align="center">
   <img width="680" height="450" src="https://user-images.githubusercontent.com/68853069/206180100-c9c88dd8-749d-4bae-9b9b-ad6dcecad452.png"> 
</p>
<p align="center">
   <img width="680" height="450" src="https://user-images.githubusercontent.com/68853069/206180292-e302acd1-b96e-4484-a5d4-9ca54b5800bf.png"> 
</p>
 
## F) Time taken to finish the Assignment
 
I took me 48 hours to finish the assignment.
 
## G) The difficulty level of the Assignment
 
For me the difficulty level of the assignment 9/10. 

## Acknowledgement

I referred the following documentations
https://firebase.google.com/docs/auth/where-to-start
https://firebase.google.com/docs/database/android/start

I would like to thank my friends Ricky Patel, Hitarth Kothari, Puru Narayan and Dhruv Chovatiya who help me with the app logic, the idea behind the multiplayer logic and reported certain bugs and issues in my app.
