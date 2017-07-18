# phimpme-android
<img src="/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" align="left" width="200" hspace="10" vspace="10">
Phimp.me is a Photo App for Android that aims to replace proprietary photo applications. It offers features such as taking photos, adding filters, editing images and uploading them to social networks.

[![Build Status](https://travis-ci.org/fossasia/phimpme-android.svg?branch=master)](https://travis-ci.org/fossasia/phimpme-android)
[![codecov](https://codecov.io/gh/fossasia/phimpme-android/branch/master/graph/badge.svg)](https://codecov.io/gh/fossasia/phimpme-android)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4584003e734343b3b8ce94bcae6e9ca4)](https://www.codacy.com/app/harshithdwivedi/phimpme-android?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=fossasia/phimpme-android&amp;utm_campaign=Badge_Grade)

## Communication
Join Gitter channel: https://gitter.im/fossasia/phimpme

## How to Contribute
This is an Open Source project and we would be happy to see contributors who report bugs and file feature requests submitting pull requests as well.This project adheres to the Contributor Covenant [code of conduct](https://github.com/fossasia/phimpme-android/blob/development/CONTRIBUTING.md). By participating, you are expected to uphold this code style. Please report issues here https://github.com/fossasia/phimpme-android/issues

### Branch Policy
We have the following branches
 * **development**
	 All development goes on in this branch. If you're making a contribution,
	 you are supposed to make a pull request to _development_.
	 Make sure it pass a build check on Travis

	 It is advisable to clone only the development branch using following command:
	
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
   
## Screenshots

  <table>
    <tr>
     <td><img src="/docs/screenshots/screenshot_1.png"></td>
     <td><img src="/docs/screenshots/screenshot_3.png"></td>
     <td><img src="/docs/screenshots/screenshot_5.png"></td>
     <td><img src="/docs/screenshots/screenshot_2.png"></td>
     <td><img src="/docs/screenshots/screenshot_4.png"></td>
     
    
</td>
    </tr>
  </table>
  
   
  
## Features
**Feature**|**Description**|**Status**
-----|-----|-----
Home Screen|Show local captured images/Album wise|Established
 |Grid view of images|Diplaying images in grid|Established
 |Edit, Upload, share option on image click|Working
Camera|Capture Image, Toggle Camera|Working
 |Apply filters, Camera features|Flash on/off, Exposure etc|Working
Upload|Select account on which you want to upload|Not Implemented
 |Select images which you want to upload|To upload photos|Not implemented
Settings|Add Accounts|Not Implemented
 |Photos settings|Set password, choose folder etc|Working
## Development Setup

Before you begin, you should already have the Android Studio SDK downloaded and set up correctly. You can find a guide on how to do this here: [Setting up Android Studio](http://developer.android.com/sdk/installing/index.html?pkg=studio)

**Setting up the Android Project**

1. Download the *phimpme-android* project source. You can do this either by forking and cloning the repository (recommended if you plan on pushing changes) or by downloading it as a ZIP file and extracting it.

2. Open Android Studio, you will see a **Welcome to Android** window. Under Quick Start, select *Import Project (Eclipse ADT, Gradle, etc.)*

3. Navigate to the directory where you saved the phimpme-android project, select the root folder of the project (the folder named "phimpme-android"), and hit OK. Android Studio should now begin building the project with Gradle.

4. Once this process is complete and Android Studio opens, check the Console for any build errors.

    - *Note:* If you recieve a Gradle sync error titled, "failed to find ...", you should click on the link below the error message (if avaliable) that says *Install missing platform(s) and sync project* and allow Android studio to fetch you what is missing.

5. Download this [OpenCV-android-sdk](https://github.com/opencv/opencv/releases/download/3.2.0/opencv-3.2.0-android-sdk.zip) zip file and extract it.
     
     - Copy all the files from *"OpenCV-android-sdk/sdk/native/3rdparty"* to *"phimpme-android/app/src/main/3rdparty"* (create directory if it doesn't exist)
     - Copy all the files from *"OpenCV-android-sdk/sdk/native/libs"* to *"phimpme-android/app/src/main/jniLibs"* (create directory if it doesn't exist)
     - Now build your project. If your build fails then try deleting these build directories *"phimpme-android/app/.externalNativeBuild"* and *"phimpme-android/app/build"*, if they exist and run the build again.

6. Once all build errors have been resolved, you should be all set to build the app and test it.

7. To Build the app, go to *Build>Make Project* (or alternatively press the Make Project icon in the toolbar).

8. If the app was built succesfully, you can test it by running it on either a real device or an emulated one by going to *Run>Run 'app'* or presing the Run icon in the toolbar.

**To login and upload images to Dropbox**

1. Go to the Dropbox App console([Link](https://www.dropbox.com/developers/apps))
2. Create a new application and get the APP_KEY and API_SECRET.
3. In the AndroidManifest.xml file, in the AuthActivity section, replace the APPKEY with your own App key.
4. Go to Utilities/Constants.java file and add replace the APP_KEY and API_SECRET with the key received from step 2.

## License

This project is currently licensed under the GNU General Public License v3. A copy of [LICENSE](LICENSE.md) is to be present along with the source code. To obtain the software under a different license, please contact FOSSASIA.

## Maintainers
The project is maintained by
- Hon Nguyen ([@vanhonit](https://github.com/vanhonit))
- Mario Behling ([@mariobehling](http://github.com/mariobehling))
- Pawan Pal ([@pa1pal](http://github.com/pa1pal))
