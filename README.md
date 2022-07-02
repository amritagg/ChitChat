# ChitChat

In this mobile application a user can chat with other user using Firebase!

## FUNCTIONALITIES

As soon as user opens the application user is asked to login or signup using google authentication. For which user can use any of their google account.
After login/signup, the user is prompted to Home screen where the user can see all person he had a chat with.

<img src="https://github.com/amritagg/ChitChat/blob/main/screenshots/HOME_SCREEN.jpg" height=600 />

On the home screen user is given option to start a new chat using the FAB given at bottom right corner of the screen.

### NEW FRIEND

On this activity user can find other person using their gmail Id, if the user do exists then their EMAIL ID will be shown in the center of the screen, clicking on which user will be prompted to their chat
If any user with that GMAIL ID doesn't exists then it will say <b>User Not Avaiable</b>

<img src="https://github.com/amritagg/ChitChat/blob/main/screenshots/FIND_FRIEND.jpg" height=600 />

### CHAT ACTIVITY

Here 2 users can exchange messages with one another in realtime.
Text Messages will be sent in encrypted form and then they will be decrypted at the other end.

<img src="https://github.com/amritagg/ChitChat/blob/main/screenshots/CHAT_SCREEN.jpg" height=600 />

Also users can share Images with caption Text from their gallery

<img src="https://github.com/amritagg/ChitChat/blob/main/screenshots/SEND_IMAGE.jpg" height=600 />

## WHAT DID I USED

I have used different APIs, Models...
<ul>
    <li>For creation of the application I used JAVA and android studio</li>
    <li>For the purpose of data storage and authentication I used Firebase</li>
    <li>For showing list of users I have used RecyclerView and Adapter</li>
    <li>For finding user I have used query method on firebase data</li>
    <li>Messages are shown in a RecyclerView using adapter</li>
    <li>For sending and receiving messages I have used realtime database</li>
    <li>For the data security messages are sent and received in encrypted form, for which i have used RSA encryption method</li>
    <li>For showing all images I have created a differnet activity which loads all the images of the internal storage of the user.</li>
    <li>For Image loading I have used Glide libraray and ViewPager2</li>
</ul>

Using all these Methods, this app is build successfully

<H2>THANK YOU!!</H2>