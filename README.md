<img src="./docs/images/logo.png" align="left" hspace="1" vspace="1">

<a href='https://play.google.com/store/apps/details?id=org.fossasia.phimpme' target='_blank' align="right"><img align="right" height='36' src='https://s20.postimg.org/muzx3w4jh/google_play_badge.png' /></a>
# Phimp.me Android

Phimp.me is a Photo App for Android that aims to replace proprietary photo applications. It offers features such as taking photos, adding filters, editing images and uploading them to social networks.

| **Master** | **Development** | **Codecov** | **Code Quality** | **Gitter** |
|------------|-----------------|-------------|-----------------|-----------------|
| [![Build Status](https://travis-ci.org/fossasia/phimpme-android.svg?branch=master)](https://travis-ci.org/fossasia/phimpme-android) | [![Build Status](https://travis-ci.org/fossasia/phimpme-android.svg?branch=development)](https://travis-ci.org/fossasia/phimpme-android) | [![codecov](https://codecov.io/gh/fossasia/phimpme-android/branch/master/graph/badge.svg)](https://codecov.io/gh/fossasia/phimpme-android) | [![Codacy Badge](https://api.codacy.com/project/badge/Grade/4584003e734343b3b8ce94bcae6e9ca4)](https://www.codacy.com/app/harshithdwivedi/phimpme-android?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=fossasia/phimpme-android&amp;utm_campaign=Badge_Grade) | [![Gitter Room](https://img.shields.io/badge/gitter-join%20chat%20%E2%86%92-blue.svg)](https://gitter.im/fossasia/phimpme) |

---

## Screenshots

  <table>
    <tr>
     <td><img src="./docs/images/ss_1.png"></td>
     <td><img src="./docs/images/ss_2.png"></td>
     <td><img src="./docs/images/ss_3.png"></td>
     <tr> 
      <td><img src="./docs/images/ss_4.png"></td>
      <td><img src="./docs/images/ss_5.png"></td>
      <td><img src="./docs/images/ss_6.png"></td>
    </tr>
  </table>
  
---


Do you have many different apps on your Android Phone for the purpose of clicking, editing and sharing images? Hey, guess what, now you don't need all of those because `Phimp.me` is here, with all the features packaged into one. So go and clean your phone memory to save your space for other useful purposes.


## Features

* Click beautiful images using the Phimp.me app. Use various advanced scene modes and variety of balance modes.

* You can use your voice actions to invoke Camera, Just say "Ok Google click a picture". You can also select front and rear camera based on voice.

* Browse the local gallery inside the app with folder and all photos mode. Copy, move and add a description to the images.

* Edit images with various cool filters with optimized performance, built using OpenCV library.

* Enhance contrast, hue, Satur, temp, tint, and sharpness of the image.

* Use 'Crop and rotate' features from Transform section to get your perfect image.

* Apply different stickers - facial, express, objects, comments, wishes, emojis, hashtag

* Write anything on the images in your handwriting!

* Don't be afraid to experiment as you can easily go back and forth with 'redo' and 'undo'.

* Finally, after all this editing you can easily share the image to your favourite social media sites with our easy-to-use sharing feature.

* Facebook, Twitter, NextCloud, OwnCloud, Imgur, Dropbox, Box, Flickr, Pinterest, Instagram, Whatsapp, and Tumblr - You name it and we have it covered.

---

## How to Contribute
This is an Open Source project and we would be happy to see contributors who report bugs and file feature requests by submitting pull requests as well.This project adheres to the [Contributor Covenant](https://github.com/fossasia/phimpme-android/blob/development/CONTRIBUTING.md). By participating, you are expected to uphold this code style. Please report issues here https://github.com/fossasia/phimpme-android/issues


---

### Branch Policy
We have the following branches
 * **development**
     All development goes on in this branch. If you're making a contribution,
     you are supposed to make a pull request to _development_.
     Make sure it passes a build check on Travis

     It is advisable to clone only the development branch using the following command:

    `git clone -b <branch> <remote_repo>`

    Example:

    `git clone -b my-branch git@github.com:user/myproject.git`

    Alternative (no public key setup needed):

    `git clone -b my-branch https://git@github.com/username/myproject.git`

    With Git 1.7.10 and later, add --single-branch to prevent fetching of all branches. Example, with development branch:

    `git clone -b development --single-branch https://github.com/username/phimpme-android.git`

 * **master**
   This contains the stable code. After significant features/bugfixes are accumulated on development, we move it to master.

 * **apk**
   This branch contains automatically generated apk file for testing.

 * **gh-pages**
   Hosting the landing page [phimp.me](http://phimp.me)  

---

## Development Setup

Before you begin, you should have already downloaded the Android Studio SDK and set it up correctly. You can find a guide on how to do this here: [Setting up Android Studio](http://developer.android.com/sdk/installing/index.html?pkg=studio)

### Setting up the Android Project

1. Download the *phimpme-android* project source. You can do this either by forking and cloning the repository (recommended if you plan on pushing changes) or by downloading it as a ZIP file and extracting it.

2. Install the NDK in Android Studio.

3. Open Android Studio, you will see a **Welcome to Android** window. Under Quick Start, select *Import Project (Eclipse ADT, Gradle, etc.)*

4. Navigate to the directory where you saved the phimpme-android project, select the root folder of the project (the folder named "phimpme-android"), and hit OK. Android Studio should now begin building the project with Gradle.

5. Once this process is complete and Android Studio opens, check the Console for any build errors.

    - *Note:* If you receive a Gradle sync error titled, "failed to find ...", you should click on the link below the error message (if available) that says *Install missing platform(s) and sync project* and allow Android studio to fetch you what is missing.

6. Download this [OpenCV-android-sdk](https://github.com/opencv/opencv/releases/download/2.4.13.3/opencv-2.4.13.3-android-sdk.zip) zip file and extract it.

     - Copy all the files from *"OpenCV-android-sdk/sdk/native/3rdparty"* to *"phimpme-android/app/src/main/3rdparty"* (create directory if it doesn't exist)
     - Copy all the files from *"OpenCV-android-sdk/sdk/native/libs"* to *"phimpme-android/app/src/main/jniLibs"* (create directory if it doesn't exist)
     - Now build your project. If your build fails then try deleting these build directories *"phimpme-android/app/.externalNativeBuild"* and *"phimpme-android/app/build"*, if they exist and run the build again.

7. Once all build errors have been resolved, you should be all set to build the app and test it.

8. To Build the app, go to *Build > Make Project* (or alternatively press the Make Project icon in the toolbar).

9. If the app was built successfully, you can test it by running it on either a real device or an emulated one by going to *Run > Run 'app'* or pressing the Run icon in the toolbar.

10. **To enable the sharing features to a different account, add API_KEY/ APP_ID associated with accounts**. Add the Keys in `constant.java` and `strings.xml`. Following are the links to developer pages of account. Go there to create apps and get keys.
    * Facebook: https://developers.facebook.com
    * Twitter: https://dev.twitter.com
    * Flickr: https://www.flickr.com/services/developer
    * Pinterest: https://developers.pinterest.com
    * Tumblr: https://www.tumblr.com/developers
    * Imgur: https://api.imgur.com
    * Box: https://developer.box.com
    * Dropbox: https://www.dropbox.com/developers
    
--- 

## License

This project is currently licensed under the GNU General Public License v3. A copy of [LICENSE](LICENSE.md) is to be present along with the source code. To obtain the software under a different license, please contact [FOSSASIA](http://fossasia.com/).

---

## Maintainers
The project is maintained by
- Hon Nguyen ([@vanhonit](https://github.com/vanhonit))
- Mario Behling ([@mariobehling](http://github.com/mariobehling))

---

## Developers
<table>
<tr>
<td>
     <img src="https://avatars2.githubusercontent.com/u/20367966?v=4&s=150" />
     
     Anant Prasad

<p align="center">
<a href = "https://github.com/anantprsd5"><img src = "http://www.iconninja.com/files/241/825/211/round-collaboration-social-github-code-circle-network-icon.svg" width="36" height = "36"/></a>
<a href = "https://twitter.com/Anantprasad3"><img src = "https://www.shareicon.net/download/2016/07/06/107115_media.svg" width="36" height="36"/></a>
<a href = "https://www.linkedin.com/in/anant-prasad-82b7a2119/"><img src = "http://www.iconninja.com/files/863/607/751/network-linkedin-social-connection-circular-circle-media-icon.svg" width="36" height="36"/></a>
</p>
</td>

<td>
     <img src="https://avatars0.githubusercontent.com/u/9361754?v=4&s=150"/>
     
     Mohit Manuja

<p align="center">
<a href = "https://github.com/mohitmanuja"><img src = "http://www.iconninja.com/files/241/825/211/round-collaboration-social-github-code-circle-network-icon.svg" width="36" height = "36"/></a>
<a href = "https://twitter.com/manuja_mohit"><img src = "https://www.shareicon.net/download/2016/07/06/107115_media.svg" width="36" height="36"/></a>
<a href = "https://www.linkedin.com/in/mohitmanuja/"><img src = "http://www.iconninja.com/files/863/607/751/network-linkedin-social-connection-circular-circle-media-icon.svg" width="36" height="36"/></a>
</p>
</td>

<td>
     <img src="https://avatars1.githubusercontent.com/u/6936968?v=4&s=150" />
     
     Pawan Pal

<p align="center">
<a href = "https://github.com/pa1pal"><img src = "http://www.iconninja.com/files/241/825/211/round-collaboration-social-github-code-circle-network-icon.svg" width="36" height = "36"/></a>
<a href = "https://twitter.com/pa1pal"><img src = "https://www.shareicon.net/download/2016/07/06/107115_media.svg" width="36" height="36"/></a>
<a href = "https://www.linkedin.com/in/pa1pal/"><img src = "http://www.iconninja.com/files/863/607/751/network-linkedin-social-connection-circular-circle-media-icon.svg" width="36" height="36"/></a>
</p>
</td>

<td>
     <img src="https://avatars0.githubusercontent.com/u/17337006?v=4&s=150"/>
     
     Subhankar Shah

<p align="center">
<a href = "https://github.com/Subhankar29"><img src = "http://www.iconninja.com/files/241/825/211/round-collaboration-social-github-code-circle-network-icon.svg" width="36" height = "36"/></a>
<a href = "https://twitter.com/sole_subhankar"><img src = "https://www.shareicon.net/download/2016/07/06/107115_media.svg" width="36" height="36"/></a>
<a href = "https://www.linkedin.com/in/subhankar-shah-12245b130/"><img src = "http://www.iconninja.com/files/863/607/751/network-linkedin-social-connection-circular-circle-media-icon.svg" width="36" height="36"/></a>
</p>
</td>

<td>
     <img src="https://avatars2.githubusercontent.com/u/15368561?v=4&s=150"/>
     
     Vinay Sajjanapu
<p align="center">
<a href = "https://github.com/vinaysajjanapu"><img src = "http://www.iconninja.com/files/241/825/211/round-collaboration-social-github-code-circle-network-icon.svg" width="36" height = "36"/></a>
<a href = "https://twitter.com/sajjanapu_vinay"><img src = "https://www.shareicon.net/download/2016/07/06/107115_media.svg" width="36" height="36"/></a>
<a href = "https://www.linkedin.com/in/vinay-sajjanapu-a315ab21/"><img src = "http://www.iconninja.com/files/863/607/751/network-linkedin-social-connection-circular-circle-media-icon.svg" width="36" height="36"/></a>
</p>
</td>

<td>
     <img src="https://avatars1.githubusercontent.com/u/20841578?s=400&u=ccf2c85bac58c92f69c4c69ce2c46dcee4564e23&v=4"/>
     
     Saurav Vishal

<p align="center">
<a href = "https://github.com/sauravvishal8797"><img src = "http://www.iconninja.com/files/241/825/211/round-collaboration-social-github-code-circle-network-icon.svg" width="36" height = "36"/></a>
<a href = "https://twitter.com/vishal_saurav99"><img src = "https://www.shareicon.net/download/2016/07/06/107115_media.svg" width="36" height="36"/></a>
<a href = "https://www.linkedin.com/in/saurav-vishal-517aab144/"><img src = "http://www.iconninja.com/files/863/607/751/network-linkedin-social-connection-circular-circle-media-icon.svg" width="36" height="36"/></a>
</p>
</td>
</tr> 
  </table>
